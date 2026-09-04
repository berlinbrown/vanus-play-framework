import java.util.{HashMap, Map}

object VanusRoutes:
  val systemInfoPath = "/_vanus-ops-manage/_vanus-system-info"

  val nanoletRoutes: Map[String, NanoletHandler] = new HashMap[String, NanoletHandler]()

  def addRoute(path: String, handler: NanoletHandler): Unit =
    if path != null && handler != null then
      nanoletRoutes.put(path.trim, handler)

  def find(path: String): Option[NanoletHandler] =
    Option(nanoletRoutes.get(path))
