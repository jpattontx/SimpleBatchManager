package demo;

import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger, RequestHandler}
import com.google.gson.{Gson, GsonBuilder}

class S3WriteLambdaDemo extends RequestHandler[Request, Response] {

  override def handleRequest(request: Request, context: Context): Response = {

    implicit val logger: LambdaLogger = context.getLogger();
    val added = try {
      S3StringWriteBatchManager.add(request.body.message)
      true
    } catch {
      case e: Exception =>
        logger.log(s"Error adding message to batch ${e.getMessage}")
        false
    }
    Response(request.body.message, added, S3StringWriteBatchManager.getBatchedItemCount())
  }
}
