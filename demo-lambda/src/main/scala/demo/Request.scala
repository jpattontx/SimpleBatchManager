package demo

import scala.beans.BeanProperty;

case class Request(
                    @BeanProperty var body: RequestBody
                  ) {
  /**
   * Empty constructor needed by jackson (for AWS Lambda)
   *
   * @return
   */
  def this() = this(body = RequestBody(""))

  def this(str: String) = this(body = RequestBody(str))

}
