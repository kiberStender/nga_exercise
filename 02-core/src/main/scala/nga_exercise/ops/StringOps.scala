package nga_exercise.ops

import cats.{ApplicativeError, MonadError}
import cats.syntax.all.{
  catsSyntaxApplicativeError,
  catsSyntaxApplicativeErrorId,
  catsSyntaxApplicativeId,
  toFlatMapOps,
  toFunctorOps
}
import nga_exercise.model.{Humidity, IncorrectReading, NaN, Sensor, Val}

import java.io.File

object StringOps {
  implicit class StringToIntOps(str: String) {
    def toIntSafe[F[*]: Lambda[F[*] => ApplicativeError[F, Throwable]]]: F[Int] = try {
      str.toInt.pure[F]
    } catch {
      case _: Throwable =>
        new Exception("Not a number").raiseError[F, Int]
    }
  }

  implicit class StringToHumidity(humidityStr: String) {
    def toHumidity[F[*]: Lambda[F[*] => ApplicativeError[F, Throwable]]]: F[Humidity] =
      humidityStr.trim.toLowerCase match {
        case "nan" => ApplicativeError[F, Throwable].pure(NaN)
        case numberStr =>
          numberStr.toIntSafe
            .map {
              case h if h < 0 || h > 100 => IncorrectReading
              case humidity              => Val(humidity)
            }
            .handleError(_ => NaN)

      }
  }

  implicit class StringToSensorOps(line: String) {
    def toSensor[F[*]: Lambda[F[*] => ApplicativeError[F, Throwable]]](
        fileName: String
    ): F[Sensor] =
      line.split(",").toList match {
        case sensorId :: humidityStr :: _ =>
          humidityStr.toHumidity.map(Sensor(fileName, sensorId.trim, _))

        case _ =>
          new Exception("Each line is supposed to have at least 2 items separated by comma")
            .raiseError[F, Sensor]
      }
  }

  implicit class StringToFilesOps(dirUrl: String) {
    def toListOfFiles: List[String] = (new File(dirUrl))
      .listFiles()
      .filter(f => f.isFile && f.getPath.endsWith(".csv"))
      .map(_.getPath)
      .toList
  }
}
