package nga_exercise.validator

import cats.ApplicativeError
import cats.syntax.all.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId}

/** Trait describing how to validate a list of arguments
  * @tparam F
  *   the return type
  */
trait ArgsValidator[F[*]] {

  /** Method to validate the args passed when starting the application
    * @param args
    *   The list of arguments provided by the user
    * @return
    *   The directory url in the first item of the list case it is not empty or an Exception
    */
  def validate(args: List[String]): F[String]
}

object ArgsValidator {

  /** Factory method to instantiate an [[ArgsValidator]]
    * @tparam F
    *   The dynamic return type
    * @return
    *   The instance of [[ArgsValidator]]
    */
  def dsl[F[*]: Lambda[F[*] => ApplicativeError[F, Throwable]]]: ArgsValidator[F] =
    (args: List[String]) =>
      if (args.nonEmpty) {
        args.head.pure[F]
      } else {
        new Exception("There must be only one argument").raiseError[F, String]
      }
}
