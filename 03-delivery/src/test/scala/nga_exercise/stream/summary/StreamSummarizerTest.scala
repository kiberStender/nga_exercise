package nga_exercise.stream.summary

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import nga_exercise.model.{Output, SensorStatistic}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

class StreamSummarizerTest extends AsyncFlatSpec with Matchers with GivenWhenThen {
  private type F[A] = IO[A]
  private implicit val IoRuntime: IORuntime = cats.effect.unsafe.IORuntime.global

  private val streamSummarizerF: F[StreamSummarizer[F]] = StreamSummarizer.stringRepr

  behavior of "Stream Summarizer"

  it should "correct print the summarization of an output" in {
    Given("a valid Output object")
    val leader_1_path: String =
      getClass.getClassLoader.getResource("test_csv/leader-1.csv").getPath
    val leader_2_path: String =
      getClass.getClassLoader.getResource("test_csv/leader-2.csv").getPath
    val fileset = Set(leader_1_path, leader_2_path)
    val sensorMap = Map(
      "s1" -> SensorStatistic(
        nans = 1,
        incorrectReadings = 0L,
        updates = 2,
        sum = 10 + 98,
        min = 10,
        max = 98,
        avg = 54
      ),
      "s2" -> SensorStatistic(
        nans = 0L,
        incorrectReadings = 0L,
        updates = 3,
        sum = 88 + 80 + 78,
        min = 78,
        max = 88,
        avg = 82
      ),
      "s3" -> SensorStatistic(
        nans = 1,
        incorrectReadings = 0L,
        updates = 0L,
        sum = 0L,
        min = -1,
        max = -1,
        avg = -1
      )
    )

    val output: Output = Output(fileset, sensorMap)

    When("summarizing it")
    streamSummarizerF
      .flatMap { streamSummarizer =>
        val actual = streamSummarizer.summarize(output)

        Then("it should correctly convert it to the string representation")
        actual.compile.toList.map { list =>
          list.length shouldBe 1
          list.head should include("Num of processed files: 2")
          list.head should include("Num of processed measurements: 7")
          list.head should include("Num of failed measurements: 2")
          list.head should include("s1,10,54,98")
          list.head should include("s2,78,82,88")
          list.head should include("s3,NaN,NaN,NaN")
        }
      }
      .unsafeToFuture()
  }
}
