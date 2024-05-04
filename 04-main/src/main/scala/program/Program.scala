package program

import cats.effect.Async
import cats.syntax.all.{toFlatMapOps, toFunctorOps}
import nga_exercise.stream.appstream.AppStream
import nga_exercise.stream.filestreamer.FileStreamer
import nga_exercise.stream.folder.FolderProcessor
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
      chunk = 5
      _ <- logger.info(s"Chunk value = $chunk")
      directoryName <- argsValidator.validate(args)
      folderProcessor <- FolderProcessor.dsl
      fileStreamer <- FileStreamer.dsl
      appStreamer <- AppStream.dsl(chunk)(fileStreamer)
      summarizer <- StreamSummarizer.stringRepr
      csvFileList <- folderProcessor.process(directoryName)
      list <- appStreamer
        .start(csvFileList)
        .flatMap(summarizer.summarize)
        .compile
        .toList
      _ = if (list.length == 1) println(list.head) else println("Error in the stream")
      _ <- logger.info("End of the application")
    } yield ()
  }
}
