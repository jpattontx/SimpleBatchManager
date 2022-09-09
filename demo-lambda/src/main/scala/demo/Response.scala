package demo;


import scala.beans.BeanProperty;

case class Response(@BeanProperty var message: String, @BeanProperty var queued: Boolean, @BeanProperty batchSize: Int) {
  /**
   * Empty constructor needed by jackson (for AWS Lambda)
   *
   * @return
   */
  def this() = this(message = "none", queued = false, batchSize = 0)

}
