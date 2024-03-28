package nga_exercise.model

import monocle.macros.Lenses

/** class to manipulate an output after the stream ends
  * @param fileSet
  *   The list of files that were procesed
  * @param sensorStatMap
  *   The list of statistics about a given sensor
  */
@Lenses("_")
case class Output(fileSet: Set[String], sensorStatMap: Map[String, SensorStatistic])

object Output {
  def default: Output = Output(Set(), Map())
}
