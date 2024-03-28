package nga_exercise.model

import monocle.macros.Lenses

@Lenses("_")
case class Output(fileSet: Set[String], sensorStatMap: Map[String, SensorStatistic])

object Output {
  def default: Output = Output(Set(), Map())
}
