package nga_exercise.ops

import cats.implicits.catsSyntaxApplicativeId
import nga_exercise.model.{IncorrectReading, NaN, Sensor, Val}
import nga_exercise.ops.StringOps.{
  StringToFilesOps,
  StringToHumidity,
  StringToIntOps,
  StringToSensorOps
}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StringOpsTest extends AnyFlatSpec with Matchers with GivenWhenThen {
  type F[A] = Either[Throwable, A]

  behavior of "String to Int"

  it should "return a valid integer when provided 15" in {
    Given("A valid number integer 15")

    val str: String = "15"
    val expected = 15.pure[F]

    When("converting the string to integer")
    val actual = str.toIntSafe[F]

    Then("It should return a valid integer value")
    actual shouldBe expected
  }

  it should "return an error when provided s16" in {
    Given("An invalid number integer s16")

    val str: String = "s16"
    val expected = "Not a number"

    When("converting the string to integer")
    val actual = str.toIntSafe[F]

    Then("It should return an error")
    actual.isLeft shouldBe true
    actual.swap.getOrElse(new Exception("Not the expected error")).getMessage shouldBe expected
  }

  behavior of "String to Humidity Ops"

  it should "instantiate as Val(15) when given proper value" in {
    Given("A valid number between 0 to 100")
    val value = "15"
    val expected = Val(15).pure[F]

    When("instantiating")
    val actual = value.toHumidity[F]

    Then("it should return Val(number)")
    actual shouldBe expected
  }

  it should "instantiate as NaN when given invalid value" in {
    Given("An invalid alphanumeric number")
    val value = "s15"
    val expected = NaN.pure[F]

    When("instantiating")
    val actual = value.toHumidity[F]

    Then("it should return NaN")
    actual shouldBe expected
  }

  it should "instantiate as NaN when given value below 0" in {
    Given("An invalid number below 0")
    val value = "-5"

    When("instantiating")
    val actual = value.toHumidity[F]

    Then("it should return NaN")
    actual shouldBe IncorrectReading.pure[F]
  }

  it should "instantiate as NaN when given value above 100" in {
    Given("An invalid number above 100")
    val value = "120"

    When("instantiating")
    val actual = value.toHumidity[F]

    Then("it should return NaN")
    actual shouldBe IncorrectReading.pure[F]
  }

  behavior of "String to Sensor Ops"

  it should "return a valid Sensor with non NaN humidity when provided s2,80" in {
    Given("line s2,80")
    val line = "s2,80"
    val expected = Sensor("filename", "s2", Val(80)).pure[F]

    When("converting to Sensor")
    val actual = line.toSensor[F]("filename")

    Then("it should return a valid Sensor instance with humidity not NaN")
    actual shouldBe expected
  }

  it should "return a valid Sensor with non NaN humidity when provided s2,80,junk" in {
    Given("line s2,80,junk")
    val line = "s2,80,junk"
    val expected = Sensor("filename", "s2", Val(80)).pure[F]

    When("converting to Sensor")
    val actual = line.toSensor[F]("filename")

    Then("it should return a valid Sensor instance with humidity not NaN")
    actual shouldBe expected
  }

  it should "return a valid Sensor with NaN humidity when provided s3,NaN" in {
    Given("line s3,NaN")
    val line = "s3,NaN"
    val expected = Sensor("filename", "s3", NaN).pure[F]

    When("converting to Sensor")
    val actual = line.toSensor[F]("filename")

    Then("it should return a valid Sensor instance with humidity being NaN")
    actual shouldBe expected
  }

  it should "return a valid Sensor with NaN humidity when provided s3,105" in {
    Given("line s3,105")
    val line = "s3,105"
    val expected = Sensor("filename", "s3", IncorrectReading).pure[F]

    When("converting to Sensor")
    val actual = line.toSensor[F]("filename")

    Then("it should return a valid Sensor instance with humidity being IncorrectReading")
    actual shouldBe expected
  }

  it should "return a valid Sensor with NaN humidity when provided s3,-5" in {
    Given("line s3,-5")
    val line = "s3,-5"
    val expected = Sensor("filename", "s3", IncorrectReading).pure[F]

    When("converting to Sensor")
    val actual = line.toSensor[F]("filename")

    Then("it should return a valid Sensor instance with humidity being IncorrectReading")
    actual shouldBe expected
  }

  it should "return an error when provided s3" in {
    Given("line s3")
    val line = "s3"
    val expected = "Each line is supposed to have at least 2 items separated by comma"

    When("converting to Sensor")
    val actual = line.toSensor[F]("filename")

    actual.isLeft shouldBe true
    actual.swap.getOrElse(new Exception("Not the expected error")).getMessage shouldBe expected
  }

  it should "return an error when provided 80" in {
    Given("line 80")
    val line = "80"
    val expected = "Each line is supposed to have at least 2 items separated by comma"

    When("converting to Sensor")
    val actual = line.toSensor[F]("filename")

    actual.isLeft shouldBe true
    actual.swap.getOrElse(new Exception("Not the expected error")).getMessage shouldBe expected
  }

  behavior of "String to File Ops"

  it should "return a list of all csv file inside test_files" in {
    Given("path to test-files")
    val dirUrl: String = getClass.getClassLoader.getResource("test_files").getPath

    When("converting the string to list of file names")
    val actual = dirUrl.toListOfFiles

    Then("it should return 2 files only")
    actual.length shouldBe 2
    actual(0) should endWith("abcd.csv")
    actual(1) should endWith("123.csv")
  }
}
