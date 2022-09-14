package demo

import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger, RequestHandler}
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry

import java.nio.charset.Charset

class KinesisWriteDemo extends RequestHandler[Request, Response] {

  override def handleRequest(request: Request, context: Context): Response = {

    implicit val logger: LambdaLogger = context.getLogger();
    val added = try {
      val msg = request.body.message
      val req = PutRecordsRequestEntry.builder()
        .partitionKey(msg)
        .data(SdkBytes.fromString(msg,Charset.defaultCharset()))
        .build()
      KinesisWriteBatchManager.add(req)
      true
    } catch {
      case e: Exception =>
        logger.log(s"Error adding message to batch ${e.getMessage}")
        false
    }
    Response(request.body.message, added, S3StringWriteBatchManager.getBatchedItemCount())
  }
}

