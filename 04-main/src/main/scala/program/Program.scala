package program

import cats.effect.Async
import cats.syntax.all.{toFlatMapOps, toFunctorOps}
import nga_exercise.stream.appstream.AppStream
import nga_exercise.stream.filestreamer.FileStreamer
import nga_exercise.stream.summary.StreamSummarizer
import nga_exercise.validator.ArgsValidator
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
object Program {
  def dsl[F[*]: Async](args: List[String]): F[Unit] = {
    implicit lazy val logger: Logger[F] = Slf4jLogger.getLogger

    val argsValidator: ArgsValidator[F] = ArgsValidator.dsl[F]

    for {
      _ <- logger.info("Started application")
      directoryName <- argsValidator.validate(args)
      fileStreamer <- FileStreamer.dsl
      appStreamer <- AppStream.dsl(fileStreamer)
      summarizer <- StreamSummarizer.stringRepr
      list <- appStreamer
        .start(directoryName)
        .flatMap(summarizer.summarize)
        .compile
        .toList
      _ = if (list.length == 1) println(list.head) else println("Error in the stream")
      _ <- logger.info("End of the application")
    } yield ()
  }
}
