package nga_exercise.ops

import cats.ApplicativeError
import cats.syntax.all.{
  catsSyntaxApplicativeError,
  catsSyntaxApplicativeErrorId,
  catsSyntaxApplicativeId,
  toFunctorOps
}
import nga_exercise.model.{Humidity, IncorrectReading, NaN, Sensor, Val}

import java.io.File

/** An object containing [[String]] operations
  */
object StringOps {

  /** An operation to convert an [[String]] into an [[Int]] in a safer way
    * @param str
    *   The [[String]] that will be converted to an [[Int]]
    */
  implicit class StringToIntOps(str: String) {
    def toIntSafe[F[*]: Lambda[F[*] => ApplicativeError[F, Throwable]]]: F[Int] = try {
      str.toInt.pure[F]
    } catch {
      case _: Throwable =>
        new Exception("Not a number").raiseError[F, Int]
    }
  }

  /** An operation to convert an [[String]] into an [[Humidity]] instance
    * @param humidityStr
    *   the [[String]] to be converted to an [[Humidity]]
    */
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

  /** An operation to convert a [[String]] to [[Sensor]]
    * @param line
    *   The [[String]] to be converted to a [[Sensor]]
    */
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

  /** An operation to convert a [[String]] into a list of [[String]]s representing a lis tof files
    * in a directory were the files are all csv
    * @param dirUrl
    *   The [[String]] to be converted
    */
  implicit class StringToFilesOps(dirUrl: String) {
    def toListOfFiles: List[String] = (new File(dirUrl))
      .listFiles()
      .filter(f => f.isFile && f.getPath.endsWith(".csv"))
      .map(_.getPath)
      .toList
  }
}
