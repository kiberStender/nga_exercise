package nga_exercise.stream.filestreamer

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import nga_exercise.model.{NaN, Sensor, Val}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

class FileStreamerTest extends AsyncFlatSpec with Matchers with GivenWhenThen {
  private type F[A] = IO[A]
  private implicit val IoRuntime: IORuntime = cats.effect.unsafe.IORuntime.global

  private val fileStreamerF: F[FileStreamer[F]] = FileStreamer.dsl[F]

  behavior of "File Streamer"

  it should "have 3 sensors lines when provided file leader-1.csv" in {
    Given("Given valid file")
    val filePath: String = getClass.getClassLoader.getResource("test_csv/leader-1.csv").getPath
    val expectedS1 = Sensor(filePath, "s1", Val(10))
    val expectedS2 = Sensor(filePath, "s2", Val(88))
    val expectedS3 = Sensor(filePath, "s1", NaN)

    When("streaming it")
    fileStreamerF
      .flatMap { fileStreamer =>
        val actual: fs2.Stream[F, Sensor] = fileStreamer.stream(filePath)

        Then("it should have 3 sensors")

        actual.compile.toList.map { list =>
          list should contain(expectedS1)
          list should contain(expectedS2)
          list should contain(expectedS3)
        }
      }
      .unsafeToFuture()
  }

  it should "have 4 sensors lines when provided file leader-2.csv" in {
    Given("Given valid file")
    val filePath: String = getClass.getClassLoader.getResource("test_csv/leader-2.csv").getPath
    val expectedS1 = Sensor(filePath, "s2", Val(80))
    val expectedS2 = Sensor(filePath, "s3", NaN)
    val expectedS3 = Sensor(filePath, "s2", Val(78))
    val expectedS4 = Sensor(filePath, "s1", Val(98))

    When("streaming it")
    fileStreamerF
      .flatMap { fileStreamer =>
        val actual: fs2.Stream[F, Sensor] = fileStreamer.stream(filePath)

        Then("it should have 3 sensors")

        actual.compile.toList.map { list =>
          list should contain(expectedS1)
          list should contain(expectedS2)
          list should contain(expectedS3)
          list should contain(expectedS4)
        }
      }
      .unsafeToFuture()
  }
}
