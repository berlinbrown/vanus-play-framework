import java.util.concurrent.CountDownLatch
import java.util.logging.Logger

object VanusRuntime:
  private val log = Logger.getLogger("vanus.lifecycle")

  def run(server: WebServer): Unit =
    val stopped = new CountDownLatch(1)
    val shutdown = new Thread(() =>
      try server.stop()
      finally stopped.countDown()
    , "vanus-shutdown")
    Runtime.getRuntime.addShutdownHook(shutdown)
    try
      server.start(server.getLimits.idleTimeoutMillis(), false)
      log.info(s"Listening on ${server.getHostname}:${server.getListeningPort}")
      stopped.await()
    catch
      case _: InterruptedException => Thread.currentThread().interrupt()
    finally
      server.stop()
      try Runtime.getRuntime.removeShutdownHook(shutdown)
      catch case _: IllegalStateException => () // JVM shutdown is already in progress.
