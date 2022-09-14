package simplebatchmanager

import com.google.gson.{Gson, GsonBuilder}

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

abstract class SimpleBatchManager[T](maxCount:Int, maxBytes:BigInt) {

  val gson: Gson = new GsonBuilder().setPrettyPrinting().create()
  private val batchedItems = ArrayBuffer.empty[T]

  def add(i:T):Unit = {
    log(s"Adding item to batch: ${gson.toJson(i)}")
    batchedItems.addOne(i)
    flushIfLimitsHit()
  }

  def approachingByteLimit(): Boolean = {
    val items = getItems
    val bytes = items.map(getObjectSize).sum
    val avgBytes = bytes/items.size
    (bytes + avgBytes) >= maxBytes
  }

  def getBatchedItemCount(): Int = batchedItems.size

  def getObjectSize(item:T):BigInt

  def flushIfLimitsHit(): Unit = {
    if (batchedItems.size>=maxCount) {
      log(s"Hit record limit of ${maxCount}")
      flush()
    } else if (approachingByteLimit()) {
      log(s"Approaching byte limit of ${maxBytes}")
      flush()
    }
  }

  def getItems[T]()(implicit ev: ClassTag[T]) = {
    batchedItems.toList
  }

  def flush():Unit = {
    if (batchedItems.size>0) {
      log(s"Flushing ${batchedItems.size} batched items")
      val items = getItems
      try {
        val failures = handleBatch(items)
        if (failures.size>0) {
          handleFailures(failures)
        }
        batchedItems.clear();

      } catch {
        case e:Exception => log("ERROR FLUSHING BATCH: "+e.getMessage)
          e.printStackTrace()
      } finally {
      }
    } else {
      log("No items to flush")
    }
  }
  
  def log(msg:String) = {
    println(msg)
  }

  def handleBatch(items:List[T]):List[BatchItemFailure]
  def handleFailures(failedRecords:List[BatchItemFailure]):Unit

  case class BatchItemFailure (item:T, error:String)

  Runtime.getRuntime().addShutdownHook(new Thread() {
    override def run(): Unit = {
      log("[runtime] SimpleBatchManager shutdownHook triggered")

      try {
        flush()
        Thread.sleep(200)
      } catch {
        case e:Exception => log("[runtime] SimpleBatchManager shutdown error: "+e.getMessage);
      }

      log("[runtime] SimpleBatchManager exiting")
      System.exit(0)
    }
  })
}
