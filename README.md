async-servlet-examples
======================

Examples of asynchronous servlets using different technologies. Also uses asynchronous HTTP clients.

The modules called something with "sleep-server" exposes an HTTP service at http://localhost:8001/ that responds after a certain time. The optional parameter replyAfterMillis determines after how long time (default 1000 ms) the service responds. The response contains a statement about after how long time the response was actually sent.

The modules whose names end with "-client" are command line applications that call the service described above. They take two arguments. The first argument is the number of requests to make and the second corresponds to the parameter replyAfterMillis. They make the calls to the service, receive the responses and prints out how long time it took.

Modules with "forward-server" in the name exposes an HTTP service at http://localhost:8002/ that calls the service in the sleep-servers. The two parameters request, which influences how many requests are made, and replyAfterMillis, which is explained above, are used.

## Building

    mvn clean install

## License

Eclipse Public License.

