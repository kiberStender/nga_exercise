package nga_exercise.stream.filestreamer

import cats.effect.Async
import fs2.text
import nga_exercise.model.Sensor
import nga_exercise.ops.StringOps.StringToSensorOps

trait FileStreamer[F[*]] {
  def stream(path: String): fs2.Stream[F, Sensor]
}

object FileStreamer {
  def dsl[F[*]: Async]: F[FileStreamer[F]] = Async[F].delay {
    new FileStreamer[F] {
      def stream(path: String): fs2.Stream[F, Sensor] = {
        fs2.io.file
          .Files[F]
          .readAll(fs2.io.file.Path(path))
          .through(text.utf8.decode)
          .through(text.lines)
          .drop(1) // Removing Headers
          .filter(_.trim.nonEmpty) // Removing empty lines
          .flatMap(_.toSensor[fs2.Stream[F, *]](path))
      }
    }
  }
}
