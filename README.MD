# Multi-Threaded Web Server in Java #

This project is an implementation of a Java based multi-threaded Web Server, capable of processing multiple simultaneous service requests in parallel. 

* Maintains a thread pool & manages the incoming connections in a multithreaded fashion
* Accepts only HTTP GET requests. 
* Other HTTP Methods like HEAD, POST, DELETE etc are not implemented & an unsupported method exception is thrown when requested
* Keep Alive request headers are handled as per the HTTP Version.
    * Proper HTTP/1.1 keep-alive behavior is implemented based
  on the http-client's capabilities exposed through it`s request headers.


### How to Run the WebServer
* git clone https://github.com/rakeshkonda/java-multithreaded-webserver.git
    * or download it from the source control github `https://github.com/rakeshkonda/java-multithreaded-webserver`
* cd `<source folder>`
* Run the command `mvn exec:java`
* Go to a browser and hit the url 
    * http://localhost:8080
       

### References
 * http://www.cs.bu.edu/fac/matta/Teaching/CS552/F99/proj4/
 * https://www.shubhsblog.com/programming/multithreaded-webserver-java.html
 * https://blog.insightdatascience.com/learning-about-the-http-connection-keep-alive-header-7ebe0efa209d
 * https://en.wikipedia.org/wiki/HTTP_persistent_connection