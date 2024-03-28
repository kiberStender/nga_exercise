package nga_exercise.model

import monocle.macros.Lenses

/** The representation of a sensor after all its updates being read by the stream
  * @param nans
  *   How many NaNs were caught
  * @param incorrectReadings
  *   How many incorrect readings were caught
  * @param updates
  *   how many valid values were caught
  * @param sum
  *   the sum of all the valid values
  * @param min
  *   the min of all the valid values
  * @param max
  *   the max of all the valid numbers
  * @param avg
  *   the average of all the valid numbers
  */
@Lenses("_")
case class SensorStatistic(
    nans: Long,
    incorrectReadings: Long,
    updates: Long,
    sum: Long,
    min: Int,
    max: Int,
    avg: Int
)
