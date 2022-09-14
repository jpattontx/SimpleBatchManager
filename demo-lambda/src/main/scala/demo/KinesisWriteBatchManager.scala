package demo

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.{KinesisAsyncClient}
import simplebatchmanager.SimpleBatchManager
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient
import software.amazon.awssdk.services.kinesis.model.{PutRecordsRequest, PutRecordsRequestEntry}

import java.util.UUID

object KinesisWriteBatchManager  extends SimpleBatchManager[PutRecordsRequestEntry](maxCount=5,maxBytes=512) {


  log("[runtime] KinesisWriteBatchManager initializing kinesis client")
  val awsRegion = sys.env.get("AWS_REGION") match {
    case Some(v) => Region.of(v)
    case _ => Region.US_WEST_2
  }

  val kinesisClient = KinesisAsyncClient.builder()
    .httpClient(AwsCrtAsyncHttpClient.create())
    .region(awsRegion)
    .build();
  log("[runtime] KinesisWriteBatchManager kinesis client initialized")


  val streamName = sys.env.get("KINESIS_STREAM").getOrElse("kinesis-write-lambda-demo")

  var batchId = UUID.randomUUID().toString

  override def getObjectSize(item: PutRecordsRequestEntry): BigInt = item.data().asByteArray().length

  override def handleBatch(items: List[PutRecordsRequestEntry]): List[BatchItemFailure] = {
    try {
      val req = PutRecordsRequest.builder()
        .records(items:_*)
        .streamName(streamName)
        .build();
      val res = kinesisClient.putRecords(req)
      log(s"Kinesis response: ${res.get()}")

      // Reset batch ID
      batchId = UUID.randomUUID().toString

      List.empty[BatchItemFailure]

    } catch {
      case e:Exception => items.map(i => BatchItemFailure(i,e.getMessage))
    }
  }

  override def handleFailures(failedRecords: List[BatchItemFailure]): Unit = {
    failedRecords.foreach(fr => log("ERROR WRITING RECORD: "+fr.item+"; ERROR("+fr.error+")"))
  }
}
