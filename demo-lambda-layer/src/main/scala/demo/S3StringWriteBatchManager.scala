package demo

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import simplebatchmanager.SimpleBatchManager

import java.util.UUID

object S3StringWriteBatchManager extends SimpleBatchManager[String](maxCount=5,maxBytes=512) {

  val bmId = UUID.randomUUID().toString
  val bucketName = "s3-write-lambda-demo"

  override def getObjectSize(item: String): BigInt = item.getBytes.length

  override def handleBatch(items: List[String]): List[S3StringWriteBatchManager.BatchItemFailure] = {
    val s3Client = AmazonS3Client.builder()
      .withRegion(Regions.US_WEST_2)
      .build()

    val key = s"bulk_write-${bmId}"
    val contents = items.filter(_.length>0).mkString("\n")

    try {
      s3Client.putObject(bucketName, key, contents)
      List.empty[S3StringWriteBatchManager.BatchItemFailure]
    } catch {
      case e:Exception => items.map(i => S3StringWriteBatchManager.BatchItemFailure(i,e.getMessage))
    }
  }

  override def handleFailures(failedRecords: List[S3StringWriteBatchManager.BatchItemFailure]): Unit = {
    failedRecords.foreach(fr => log("ERROR WRITING RECORD: "+fr.item+"; ERROR("+fr.error+")"))
  }
}
