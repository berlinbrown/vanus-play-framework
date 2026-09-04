import org.nanohttpd.protocols.http.response.Response

object VanusCors:
  def apply(response: Response, cors: Option[String]): Response =
    cors match
      case None => response
      case Some(origin) =>
        response.addHeader(VanusConstants.HeaderAccessControlAllowOrigin, origin)
        response.addHeader(VanusConstants.HeaderAccessControlAllowHeaders, System.getProperty(VanusConstants.CorsAllowedHeadersProperty, VanusConstants.CorsAllowedHeadersDefault))
        response.addHeader(VanusConstants.HeaderAccessControlAllowCredentials, VanusConstants.CorsAllowCredentialsValue)
        response.addHeader(VanusConstants.HeaderAccessControlAllowMethods, VanusConstants.CorsAllowMethodsValue)
        response.addHeader(VanusConstants.HeaderAccessControlMaxAge, VanusConstants.CorsMaxAgeSeconds.toString)
        response
