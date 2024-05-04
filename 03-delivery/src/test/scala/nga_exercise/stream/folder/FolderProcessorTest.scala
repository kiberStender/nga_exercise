package nga_exercise.stream.folder

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.syntax.all.{catsSyntaxApplicativeError, catsSyntaxApplicativeErrorId}
import org.scalatest.{Assertion, GivenWhenThen}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class FolderProcessorTest extends AsyncFlatSpec with Matchers with GivenWhenThen {
  private type F[A] = IO[A]
  private implicit val IoRuntime: IORuntime = cats.effect.unsafe.IORuntime.global

  private val folderProcessorF: F[FolderProcessor[F]] = FolderProcessor.dsl[F]

  behavior of "Folder processor"

  it should "return a list of all csv file inside test_files" in {
    Given("path to test-files")
    val dirUrl: String = getClass.getClassLoader.getResource("test_files").getPath

    When("converting the string to list of file names")
    folderProcessorF
      .flatMap { folderProcessor =>
        val actualF: F[List[String]] = folderProcessor.process(dirUrl)

        Then("it should return 2 files only")
        actualF map { actual =>
          actual.length shouldBe 2
          actual(0) should endWith("abcd.csv")
          actual(1) should endWith("123.csv")
        }
      }
      .unsafeToFuture()
  }

  it should "return an error when the folder does not exist" in {
    Given("wrong path")
    val dirUrl: String = "/non_existing_url"

    When("converting the string to list of file names")
    folderProcessorF
      .flatMap { folderProcessor =>
        val actualF: F[List[String]] = folderProcessor.process(dirUrl)
        val expected = "Please provide a valid folder url"

        Then("it should return an error")
        actualF.handleError(_.getMessage shouldBe expected)
      }
      .unsafeToFuture()
      .asInstanceOf[Future[Assertion]]
  }

  it should "return an error when the folder does not have csv files in it" in {
    Given("a folder with no csv in it")
    val dirUrl: String = getClass.getClassLoader.getResource("test_files_2").getPath

    When("converting the string to list of file names")
    folderProcessorF
      .flatMap { folderProcessor =>
        val actualF: F[List[String]] = folderProcessor.process(dirUrl)
        val expected = "No csv files were found in the given directory"

        Then("it should return an error")
        actualF.handleError(_.getMessage shouldBe expected)
      }
      .unsafeToFuture()
      .asInstanceOf[Future[Assertion]]
  }

}
