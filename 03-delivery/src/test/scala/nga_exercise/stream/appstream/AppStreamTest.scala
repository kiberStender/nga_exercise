package nga_exercise.stream.appstream

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import nga_exercise.model.{NaN, Sensor, SensorStatistic, Val}
import nga_exercise.stream.filestreamer.FileStreamer
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

class AppStreamTest extends AsyncFlatSpec with Matchers with GivenWhenThen {
  private type F[A] = IO[A]
  private implicit val IoRuntime: IORuntime = cats.effect.unsafe.IORuntime.global

  behavior of "App Stream"

  it should "correct summarize 2 streams of sensor" in {
    Given("a valid stream of sensors")
    val dirUrl: String = getClass.getClassLoader.getResource("test_csv").getPath
    val leader_1_path: String =
      getClass.getClassLoader.getResource("test_csv/leader-1.csv").getPath
    val leader_2_path: String =
      getClass.getClassLoader.getResource("test_csv/leader-2.csv").getPath

    val leader_1_1 = Sensor(leader_1_path, "s1", Val(10))
    val leader_1_2 = Sensor(leader_1_path, "s2", Val(88))
    val leader_1_3 = Sensor(leader_1_path, "s1", NaN)
    val leader_2_1 = Sensor(leader_2_path, "s2", Val(80))
    val leader_2_2 = Sensor(leader_2_path, "s3", NaN)
    val leader_2_3 = Sensor(leader_2_path, "s2", Val(78))
    val leader_2_4 = Sensor(leader_2_path, "s1", Val(98))

    val fileStreamer: FileStreamer[F] = {
      case `leader_1_path` => fs2.Stream(leader_1_1, leader_1_2, leader_1_3)
      case `leader_2_path` => fs2.Stream(leader_2_1, leader_2_2, leader_2_3, leader_2_4)
      case _               => throw new Exception("Wrong parameter")
    }
    val appStreamF: F[AppStream[F]] = AppStream.dsl(fileStreamer)

    val expectedSensorMap = Map(
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

    When("summarizing it")
    appStreamF
      .flatMap { appStream =>
        val actual = appStream.start(dirUrl)

        Then("it should work")

        actual.compile.toList.map { list =>
          list.length shouldBe 1
          list.head.fileSet shouldBe Set(leader_1_path, leader_2_path)
          list.head.sensorStatMap shouldBe expectedSensorMap
        }
      }
      .unsafeToFuture()
  }

  it should "correct summarize a stream that starts with NaN" in {
    Given("a valid stream of sensors")
    val dirUrl: String = getClass.getClassLoader.getResource("test_csv").getPath
    val leader_1_path: String =
      getClass.getClassLoader.getResource("test_csv/leader-1.csv").getPath
    val leader_2_path: String =
      getClass.getClassLoader.getResource("test_csv/leader-2.csv").getPath

    val leader_1_1 = Sensor(leader_1_path, "s1", NaN)
    val leader_1_2 = Sensor(leader_1_path, "s2", Val(88))
    val leader_1_3 = Sensor(leader_1_path, "s1", Val(10))
    val leader_1_4 = Sensor(leader_1_path, "s1", Val(98))

    val fileStreamer: FileStreamer[F] = {
      case `leader_1_path` => fs2.Stream(leader_1_1, leader_1_2, leader_1_3, leader_1_4)
      case `leader_2_path` => fs2.Stream.empty
      case _               => throw new Exception("Wrong parameter")
    }
    val appStreamF: F[AppStream[F]] = AppStream.dsl(fileStreamer)

    val expectedSensorMap = Map(
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
        updates = 1,
        sum = 88,
        min = 88,
        max = 88,
        avg = 88
      )
    )

    When("summarizing it")
    appStreamF
      .flatMap { appStream =>
        val actual = appStream.start(dirUrl)

        Then("it should work")

        actual.compile.toList.map { list =>
          list.length shouldBe 1
          list.head.fileSet shouldBe Set(leader_1_path)
          list.head.sensorStatMap shouldBe expectedSensorMap
        }
      }
      .unsafeToFuture()
  }

}
