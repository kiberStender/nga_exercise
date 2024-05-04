package nga_exercise.stream.folder

import cats.effect.Async
import cats.syntax.all.{catsSyntaxApplicativeId, catsSyntaxApplicativeErrorId}

import java.io.File

/** Trait describing how to process a folder and list its internal files that are csv
  *
  * @tparam F
  *   The bound type
  */
trait FolderProcessor[F[_]] {

  /** Method to check if a given folder url exists and return a list with possible csv files inside
    * it
    * @param folderUrl
    *   The folder address
    * @return
    */
  def process(folderUrl: String): F[List[String]]
}

object FolderProcessor {
  def dsl[F[*]: Async]: F[FolderProcessor[F]] = Async[F].delay {
    new FolderProcessor[F] {
      override def process(folderUrl: String): F[List[String]] = try {
        val list = (new File(folderUrl))
          .listFiles()
          .filter(f => f.isFile && f.getPath.endsWith(".csv"))
          .map(_.getPath)

        if (list.nonEmpty) list.toList.pure[F]
        else
          new Exception("No csv files were found in the given directory")
            .raiseError[F, List[String]]
      } catch {
        case _: NullPointerException =>
          new Exception("Please provide a valid folder url").raiseError[F, List[String]]
        case e: Throwable => e.raiseError[F, List[String]]
      }

    }
  }
}
