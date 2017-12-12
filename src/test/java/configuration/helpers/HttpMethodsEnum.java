package configuration.helpers;

/**
 * Created by kgr on 11/15/2016.
 */
public enum HttpMethodsEnum {
    CONNECT, // Converts the request connection to a transparent TCP/IP tunnel
    DELETE,  // Deletes the specified resource
    GET,
    HEAD,    // Same as GET but returns only HTTP headers and no document body
    OPTIONS, // Returns the HTTP methods that the server supports
    PATCH,   // The PATCH method requests that a set of changes described in the request entity be applied to the resource identified by the Request-URI.
    POST,
    PUT ,    // Uploads a representation of the specified URI
    TRACE    // Performs a message loop-back test along the path to the target resource.
}
