package nga_exercise.stream.summary

import cats.effect.Async
import cats.implicits.catsSyntaxEq
import nga_exercise.model.{IncorrectReading, NaN, Output, Sensor, SensorStatistic, Val}

trait StreamSummarizer[F[*]] {
  def summarize(output: Output): fs2.Stream[F, String]
}

object StreamSummarizer {
  def stringRepr[F[*]: Async]: F[StreamSummarizer[F]] = Async[F].delay {
    new StreamSummarizer[F] {
      private def countMeasurements(sensorStatMap: Map[String, SensorStatistic]): (Long, Long) =
        sensorStatMap.values.foldLeft((0L, 0L)) {
          case ((processedMeasurements, failedMeasurements), sensorStatistic) =>
            val fm = sensorStatistic.incorrectReadings + sensorStatistic.nans
            val pm = sensorStatistic.updates + fm
            (processedMeasurements + pm, failedMeasurements + fm)
        }

      private def buildStatistics(sensorStatMap: Map[String, SensorStatistic]): String =
        sensorStatMap
          .map { case (id, SensorStatistic(_, _, _, _, min, max, avg)) =>
            val nMin = if (min === -1) "NaN" else s"$min"
            val nMax = if (max === -1) "NaN" else s"$max"
            val nAvg = if (avg === -1) "NaN" else s"$avg"

            s"""$id,$nMin,$nAvg,$nMax"""
          }
          .foldLeft("")((acc, item) => if (acc.isEmpty) item else s"$acc\n$item")

      def summarize(output: Output): fs2.Stream[F, String] = {
        val (processedMeasurements, failedMeasurements) = countMeasurements(output.sensorStatMap)

        fs2.Stream {
          s"""Num of processed files: ${output.fileSet.size}
              |Num of processed measurements: $processedMeasurements
              |Num of failed measurements: $failedMeasurements
              |
              |Sensors with highest avg humidity:
              |
              |sensor-id,min,avg,max
              |${buildStatistics(output.sensorStatMap)}""".stripMargin
        }
      }
    }
  }
}
