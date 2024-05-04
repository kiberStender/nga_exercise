package nga_exercise.stream.appstream

import cats.effect.Async
import cats.syntax.all.catsSyntaxEq
import nga_exercise.model.{IncorrectReading, NaN, Output, Sensor, SensorStatistic, Val}
import nga_exercise.stream.filestreamer.FileStreamer

/** A trait describing how to get a [[fs2.Stream]] of [[Sensor]] and convert it into a stream of
  * [[Output]]
  * @tparam F
  *   The bound type
  */
trait AppStream[F[*]] {

  /** Method that takes a path to a folder containing the csv files to be streamed
    * @param csvFileList
    *   List of csv files in a given directory
    * @return
    */
  def start(csvFileList: List[String]): fs2.Stream[F, Output]
}

object AppStream {
  def dsl[F[_]: Async](chunk: Int)(fileStreamer: FileStreamer[F]): F[AppStream[F]] =
    Async[F].delay {
      new AppStream[F] {

        private def minOrNaN(newMin: Int)(prevMin: Int) =
          if (prevMin === newMin) prevMin
          else if (prevMin === -1 && newMin > 0) newMin
          else if (newMin > prevMin) prevMin
          else newMin

        private def maxOrNaN(newMax: Int)(prevMax: Int) =
          if (prevMax === newMax) prevMax
          else if (newMax < prevMax) prevMax
          else newMax

        def start(csvFileList: List[String]): fs2.Stream[F, Output] = {
          csvFileList
            .grouped(chunk)
            .foldLeft(fs2.Stream[F, String]()) { case (accumulator, item) =>
              accumulator.merge(fs2.Stream[F, String](item: _*))
            }
            .flatMap(fileStreamer.stream)
            .fold(Output.default) {
              case (Output(fileSet, map), Sensor(filename, id, Val(value))) =>
                val statistic: SensorStatistic = map
                  .get(id)
                  .map(
                    SensorStatistic._sum
                      .modify(_ + value)
                      .andThen(SensorStatistic._updates.modify(_ + 1))
                      .andThen(SensorStatistic._min.modify(minOrNaN(value)))
                      .andThen(SensorStatistic._max.modify(maxOrNaN(value)))
                      .andThen(s => SensorStatistic._avg.replace((s.sum / s.updates).toInt)(s))
                  )
                  .getOrElse(SensorStatistic(0, 0, 1, value, value, value, value))
                Output(fileSet + filename, map + (id -> statistic))
              case (Output(fileSet, map), Sensor(filename, id, IncorrectReading)) =>
                val statistic: SensorStatistic = map
                  .get(id)
                  .map(SensorStatistic._incorrectReadings.modify(_ + 1))
                  .getOrElse(SensorStatistic(0, 1, 0, 0, -1, -1, -1))
                Output(fileSet + filename, map + (id -> statistic))
              case (Output(fileSet, map), Sensor(filename, id, NaN)) =>
                val statistic: SensorStatistic = map
                  .get(id)
                  .map(SensorStatistic._nans.modify(_ + 1))
                  .getOrElse(SensorStatistic(1, 0, 0, 0, -1, -1, -1))
                Output(fileSet + filename, map + (id -> statistic))
            }
        }
      }
    }
}
