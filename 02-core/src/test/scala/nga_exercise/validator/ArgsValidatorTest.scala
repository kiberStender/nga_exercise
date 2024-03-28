package nga_exercise.validator

import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ArgsValidatorTest extends AnyFlatSpec with Matchers with GivenWhenThen {
  private type F[A] = Either[Throwable, A]
  private val argsValidator: ArgsValidator[F] = ArgsValidator.dsl[F]

  behavior of "Argument validator"

  it should "return the argument when it is valid" in {
    Given("A valid argument list")
    val expected = "/some/address/file.txt"
    val args: List[String] = List("/some/address/file.txt")

    When("Validating")
    val actualE = argsValidator.validate(args)

    Then("It should return the valid argument")
    actualE.isRight shouldBe true
    actualE.map(_ shouldBe expected)
  }

  it should "return and error when provided an empty list" in {
    Given("An empty argument list")
    val expected = "There must be only one argument"
    val args: List[String] = List()

    When("Validating")
    val actualE = argsValidator.validate(args)

    Then("It should return an exception")
    actualE.isRight shouldBe false
    actualE.left.map(_.getMessage shouldBe expected)
  }
}
