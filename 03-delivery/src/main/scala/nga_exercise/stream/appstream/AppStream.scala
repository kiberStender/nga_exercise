package nga_exercise.stream.appstream

import cats.effect.Async
import cats.syntax.all.catsSyntaxEq
import nga_exercise.model.{IncorrectReading, NaN, Output, Sensor, SensorStatistic, Val}
import nga_exercise.ops.StringOps.StringToFilesOps
import nga_exercise.stream.filestreamer.FileStreamer

trait AppStream[F[*]] {
  def start(dirUrl: String): fs2.Stream[F, Output]
}

object AppStream {
  def dsl[F[_]: Async](fileStreamer: FileStreamer[F]): F[AppStream[F]] = Async[F].delay {
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

      def start(dirUrl: String): fs2.Stream[F, Output] = {
        fs2
          .Stream[F, String](dirUrl.toListOfFiles: _*)
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
