package nga_exercise.model

import monocle.macros.Lenses

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
