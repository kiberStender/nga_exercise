package nga_exercise.model

sealed trait Humidity extends Product with Serializable

case class Val(value: Int) extends Humidity
case object NaN extends Humidity
case object IncorrectReading extends Humidity
