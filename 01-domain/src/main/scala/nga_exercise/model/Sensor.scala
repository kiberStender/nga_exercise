package nga_exercise.model

/** A representation of a sensor being read in the stream
  * @param filename
  *   the name of the file this representation was found on
  * @param id
  *   the id of the sensor
  * @param humidity
  *   the value of the humidity, that can be NaN, an integer between 0-100 and IncorrectReading in
  *   case the number is below 0 or above 100
  */
case class Sensor(filename: String, id: String, humidity: Humidity)
