# Multi-Threaded Web Server in Java #

This project is an implementation of a Java based multi-threaded Web Server, capable of processing multiple simultaneous service requests in parallel. 

* Maintains a thread pool & manages the incoming connections in a multithreaded fashion
* Accepts only HTTP GET requests. 
* Other HTTP Methods like HEAD, POST, DELETE etc are not implemented & an unsupported method exception is thrown when requested
* Keep Alive request headers are handled as per the HTTP Version.
* Proper HTTP/1.1 keep-alive behavior is implemented based
  on the http-client's capabilities exposed through it`s request headers.


