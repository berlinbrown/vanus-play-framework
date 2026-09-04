object VanusConstants:
  val DefaultPort = 8080

  val ArgPort = "-p"
  val ArgPortLong = "--port"
  val ArgHost = "-h"
  val ArgHostLong = "--host"
  val ArgQuiet = "-q"
  val ArgQuietLong = "--quiet"
  val ArgDir = "-d"
  val ArgDirLong = "--dir"
  val ArgCors = "--cors"

  val OptionHost = "host"
  val OptionPort = "port"
  val OptionQuiet = "quiet"
  val OptionHome = "home"

  val DefaultIndexHtml = "index.html"
  val DefaultIndexHtm = "index.htm"

  val HeaderAcceptRanges = "Accept-Ranges"
  val HeaderLocation = "Location"
  val HeaderContentLength = "Content-Length"
  val HeaderContentRange = "Content-Range"
  val HeaderETag = "ETag"
  val HeaderIfNoneMatch = "if-none-match"
  val HeaderRange = "range"
  val HeaderXServerVanusInfo = "X-Server-Vanus-Info"
  val HeaderContentSecurityPolicy = "Content-Security-Policy"
  val HeaderAccessControlAllowOrigin = "Access-Control-Allow-Origin"
  val HeaderAccessControlAllowHeaders = "Access-Control-Allow-Headers"
  val HeaderAccessControlAllowCredentials = "Access-Control-Allow-Credentials"
  val HeaderAccessControlAllowMethods = "Access-Control-Allow-Methods"
  val HeaderAccessControlMaxAge = "Access-Control-Max-Age"

  val CorsAllowedHeadersProperty = "AccessControlAllowHeader"
  val CorsAllowedHeadersDefault = "origin,accept,content-type"
  val CorsAllowCredentialsValue = "true"
  val CorsAllowMethodsValue = "GET, POST, PUT, DELETE, OPTIONS, HEAD"
  val CorsWildcard = "*"
  val CorsMaxAgeSeconds = 151200

  val VanusVersionValue = "\"Version 1.0\""
  val ContentSecurityPolicyValue = "script-src 'none'"

  val ErrorForbiddenTraversal = "Won't serve ../ for security reasons."
  val ErrorNoDirectoryListing = "No directory listing."
  val ErrorReadingFileFailed = "Reading file failed."
  val ErrorForbiddenPrefix = "FORBIDDEN: "
  val ErrorInternalPrefix = "INTERNAL ERROR: "
  val ErrorNotFound = "Error 404, file not found."

  val DirectoryPrefix = "Directory "
