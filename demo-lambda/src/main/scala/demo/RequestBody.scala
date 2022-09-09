package demo;


import scala.beans.BeanProperty;

case class RequestBody(
                        @BeanProperty var message: String
                      ) {
  /**
   * Empty constructor needed by jackson (for AWS Lambda)
   *
   * @return
   */
  def this() = this(message = "")

}
