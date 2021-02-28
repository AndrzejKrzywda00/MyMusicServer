package httpserver;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Request {

    /*
    These are parts of standard http request
     */

    public String method;               // this is type of method for instance PUT, GET, TRACE
    public String URI;                  // unique id for certain data
    public String HTTPVersion;          // just version
    public Map<String,String> headers;  // headers, to make use of them
    public String body;                 // data, what is going to be calculated

    // filling with empty elements
    public Request() {
        method = "";
        URI = "";
        HTTPVersion = "HTTP/1.1";
        headers = new HashMap<String,String>();
        body = "";
    }

    /***
     * Constructor for a request
     * @param rawHTTP is a string to put not edited http readed from socket
     * @throws Exception is thrown when either request is empty or there are invalid headers (to implement)
     */
    public Request(String rawHTTP) throws Exception {
        if (rawHTTP.trim().length() == 0) {    // basicly no rawHTTP
            throw new Exception("Empty request");
        }

        // counting the lines
        int i = 0;
        String[] lines = rawHTTP.split("\n");
        while (i < lines.length && lines[i].trim().length() == 0) {
            i++;
        }

        // request line
        String[] requestLine = lines[i++].split(" ");
        if (requestLine.length != 3) {      // this HAS to be like METHOD URL VERSION
            throw new Exception(String.format("Invalid Request-line:\n%s\n",lines[i - 1]));
        }
        else {
            this.method = requestLine[0].trim();        // key factor on deciding WHAT to do
            this.URI = requestLine[1].trim();           // tells server what data is going to be changed
            this.HTTPVersion = requestLine[2].trim();   // just version that has to match
        }

        // general-header
        // request-header
        // CRLF
        this.headers = new HashMap<String,String>();
        while(i < lines.length && lines[i].trim().length() != 0) {
            String[] header = lines[i].split(":",2);
            if ( header.length != 2 || header[0].trim().length() == 0) {
                // invalid header
                continue;
            }
            headers.put(header[0].trim(), header[1].trim());  // putting cut headers
            i++;
        }

        // lines[i] should be a CRLF here. Next line is the beginning of request's body
        i++;

        StringBuilder bodyBuilder = new StringBuilder();
        while(i < lines.length) {
            bodyBuilder.append(lines[i++].trim());
            bodyBuilder.append("\r\n");
        }
        this.body = bodyBuilder.toString().trim();
    }

    /***
     *
     * @return Request in a form of a UTF-8 encoded byte array
     */
    public byte[] toByteArray() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }

    /***
     *
     * @return Request in a form of a raw string
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        // request-line
        output.append(method);
        output.append(" ");
        output.append(URI);
        output.append(" ");
        output.append(HTTPVersion);

        // headers
        for (Map.Entry<String,String> header : headers.entrySet()) {
            output.append(header.getKey());
            output.append(": ");
            output.append(header.getValue());
            output.append("\r\n");
        }

        output.append("\r\n");
        output.append(body);

        return output.toString().trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;   // reference to the same object
        if (obj == null || getClass() != obj.getClass()) return false;
        Request request = (Request) obj;
        return Objects.equals(method, request.method) &&
                Objects.equals(URI, request.URI) &&
                Objects.equals(HTTPVersion, request.HTTPVersion) &&
                Objects.equals(headers, request.headers) &&
                Objects.equals(body, request.body);
        // just all fields will have to match
    }

    @Override
    public int hashCode() {
        // hashes this as a list based on the types
        return Objects.hash(method, URI, HTTPVersion, headers, body);
    }

}
