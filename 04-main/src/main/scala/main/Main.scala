package main

import cats.effect.{ExitCode, IO, IOApp}
import program.Program

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = (for {
    _ <- Program.dsl[IO](args)
  } yield ExitCode.Success)
    .handleError { error =>
      println(s"An error has happened: [$error]")
      ExitCode.Error
    }
}
