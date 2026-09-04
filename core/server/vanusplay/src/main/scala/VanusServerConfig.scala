import java.io.File
import scala.jdk.CollectionConverters.*

final case class VanusServerConfig(
    host: String,
    port: Int,
    quiet: Boolean,
    cors: Option[String],
    rootDirs: Vector[File]
):
  def options: Map[String, String] =
    Map(
      VanusConstants.OptionHost -> host,
      VanusConstants.OptionPort -> port.toString,
      VanusConstants.OptionQuiet -> quiet.toString,
      VanusConstants.OptionHome -> rootDirs.map(_.getCanonicalPath).mkString(":")
    )

object VanusServerConfig:
  def fromArgs(args: Array[String]): VanusServerConfig =
    val port = optionValue(args, VanusConstants.ArgPort, VanusConstants.ArgPortLong).flatMap(_.toIntOption).getOrElse(VanusConstants.DefaultPort)
    val host = optionValue(args, VanusConstants.ArgHost, VanusConstants.ArgHostLong).orNull
    val quiet = args.exists(arg => arg.equalsIgnoreCase(VanusConstants.ArgQuiet) || arg.equalsIgnoreCase(VanusConstants.ArgQuietLong))
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
      cors = cors,
      rootDirs = if rootDirs.isEmpty then Vector(new File(".").getAbsoluteFile) else rootDirs
    )

  private def optionValue(args: Array[String], shortName: String, longName: String): Option[String] =
    args.sliding(2).collectFirst {
      case Array(flag, value) if flag.equalsIgnoreCase(shortName) || flag.equalsIgnoreCase(longName) => value
    }
