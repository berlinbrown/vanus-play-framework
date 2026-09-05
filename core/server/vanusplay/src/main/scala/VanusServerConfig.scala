import java.io.File
import scala.jdk.CollectionConverters.*

final case class VanusServerConfig(
    host: String,
    port: Int,
    quiet: Boolean,
    dirListing: Boolean,
    rateLimit: Option[Int],
    cors: Option[String],
    rootDirs: Vector[File]
):
  def options: Map[String, String] =
    Map(
      VanusConstants.OptionHost -> host,
      VanusConstants.OptionPort -> port.toString,
      VanusConstants.OptionQuiet -> quiet.toString,
      VanusConstants.OptionDirListing -> dirListing.toString,
      VanusConstants.OptionRateLimit -> rateLimit.map(_.toString).getOrElse(""),
      VanusConstants.OptionHome -> rootDirs.map(_.getCanonicalPath).mkString(":")
    )

object VanusServerConfig:
  def fromArgs(args: Array[String]): VanusServerConfig =
    val port = optionValue(args, VanusConstants.ArgPort, VanusConstants.ArgPortLong).flatMap(_.toIntOption).getOrElse(VanusConstants.DefaultPort)
    val host = optionValue(args, VanusConstants.ArgHost, VanusConstants.ArgHostLong).getOrElse(VanusConstants.DefaultHost)
    val quiet = args.exists(arg => arg.equalsIgnoreCase(VanusConstants.ArgQuiet) || arg.equalsIgnoreCase(VanusConstants.ArgQuietLong))
    val dirListing = args.exists(_.equalsIgnoreCase(VanusConstants.ArgDirListing))
    val rateLimit = optionValue(args, VanusConstants.ArgRateLimit, VanusConstants.ArgRateLimit).flatMap(_.toIntOption).filter(_ > 0)
    val cors = args.find(_.startsWith(VanusConstants.ArgCors)).map { arg =>
      val equalIndex = arg.indexOf('=')
      if equalIndex > 0 then arg.substring(equalIndex + 1) else VanusConstants.CorsWildcard
    }
    val rootDirs = args.sliding(2).collect {
      case Array(flag, path) if flag.equalsIgnoreCase(VanusConstants.ArgDir) || flag.equalsIgnoreCase(VanusConstants.ArgDirLong) =>
        new File(path).getAbsoluteFile
    }.toVector

    VanusServerConfig(
      host = host,
      port = port,
      quiet = quiet,
      dirListing = dirListing,
      rateLimit = rateLimit,
      cors = cors,
      rootDirs = if rootDirs.isEmpty then Vector(new File(".").getAbsoluteFile) else rootDirs
    )

  private def optionValue(args: Array[String], shortName: String, longName: String): Option[String] =
    args.sliding(2).collectFirst {
      case Array(flag, value) if flag.equalsIgnoreCase(shortName) || flag.equalsIgnoreCase(longName) => value
    }
