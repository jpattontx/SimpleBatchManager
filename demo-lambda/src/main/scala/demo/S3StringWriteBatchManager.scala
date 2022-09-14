package demo

import simplebatchmanager.SimpleBatchManager
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest

import java.util.UUID

object S3StringWriteBatchManager extends SimpleBatchManager[String](maxCount = 5, maxBytes = 512) {

  log("[runtime] S3StringWriteBatchManager initializing S3 client")
  val awsRegion = sys.env.get("AWS_REGION") match {
    case Some(v) => Region.of(v)
    case _ => Region.US_WEST_2
  }

  val s3Client = S3AsyncClient.builder()
    .httpClient(AwsCrtAsyncHttpClient.create())
    .region(awsRegion)
    .build()
  log("[runtime] S3StringWriteBatchManager S3 client initialized")

  val keyPrefix = sys.env.get("S3_KEY_PREFIX").getOrElse("bulk-write-")
  val bucketName = sys.env.get("S3_BUCKET").getOrElse("s3-write-lambda-demo")

  var batchId = UUID.randomUUID().toString

  override def getObjectSize(item: String): BigInt = item.getBytes.length

  override def handleBatch(items: List[String]): List[S3StringWriteBatchManager.BatchItemFailure] = {

    val key = s"${keyPrefix}-${batchId}"
    val contents = items.filter(_.length > 0).mkString("\n")

    try {
      val req = PutObjectRequest.builder().bucket(bucketName).key(key).build()
      s3Client.putObject(req, AsyncRequestBody.fromString(contents))

      // Reset batch ID.
      batchId = UUID.randomUUID().toString

      List.empty[S3StringWriteBatchManager.BatchItemFailure]

    } catch {
      case e: Exception => items.map(i => S3StringWriteBatchManager.BatchItemFailure(i, e.getMessage))
    }
  }

  override def handleFailures(failedRecords: List[S3StringWriteBatchManager.BatchItemFailure]): Unit = {
    failedRecords.foreach(fr => log("ERROR WRITING RECORD: " + fr.item + "; ERROR(" + fr.error + ")"))
  }
}
