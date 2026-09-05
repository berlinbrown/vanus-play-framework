import java.util.concurrent.ConcurrentHashMap

/**
 * Minimal fixed-window rate limiter, per client key.
 * Each client may make at most maxRequests within each windowMillis window.
 */
class VanusRateLimiter(maxRequests: Int, windowMillis: Long):

  private final case class Window(var startMillis: Long, var count: Int)

  private val windows = new ConcurrentHashMap[String, Window]()

  /** Returns true if the request is allowed, false if the limit is exceeded. */
  def allow(key: String, nowMillis: Long = System.currentTimeMillis()): Boolean =
    val window = windows.computeIfAbsent(key, _ => Window(nowMillis, 0))
    window.synchronized {
      if nowMillis - window.startMillis >= windowMillis then
        window.startMillis = nowMillis
        window.count = 0
      window.count += 1
      window.count <= maxRequests
    }
