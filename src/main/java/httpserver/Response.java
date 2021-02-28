package httpserver;

import httpserver.enums.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {

    /*
    Using trim() instead of strip() because of early java version
    This is going to be changed when i correct versions by maven
     */

    private int statusCode;
    private String statusMessage;
    private Map<String,String> headers;
    private String body;
    private byte[] binaryBody;

    // empty ctor
    Response() {
        setStatus(Status.NotImplemented_501);
        headers = new HashMap<String, String>();
        body = "";
        binaryBody = new byte[0];
    }

    public Response(String rawHTTP) throws Exception {
        if(rawHTTP.trim().length() == 0) {
            throw new Exception("Empty response");
        }

        int i = 0;
        String[] lines = rawHTTP.split("\n");

        // Ignore empty leading lines
        while(i < lines.length && lines[i].trim().length() == 0) {
            i++;
        }

        // status-line
        String[] statusLine = lines[i++].split(" ", 3);
        if (statusLine.length != 3) {
            throw new Exception(String.format("Invalid Status-line:\n%s\n", lines[i-1]));
        }
        else {
            this.statusCode = Integer.parseInt(statusLine[1].trim());
            this.statusMessage = statusLine[2].trim();
        }

        // general-header
        // request-header
        // CRLF
        this.headers = new HashMap<String,String>();
        while(i < lines.length && lines[i].trim().length() != 0) {
            String[] header = lines[i].split(":", 2);
            if (header.length != 2 || header[0].trim().length() == 0) {
                // invalid header
                continue;
            }
            headers.put(header[0].trim(), header[1].trim());
            i++;
        }

        // lines[i] should be a CRLF here. Nest line is the boyd of the request body.
        i++;

        StringBuilder bodyBuilder = new StringBuilder();
        while(i < lines.length) {
            bodyBuilder.append(lines[i++].trim());
            bodyBuilder.append("\r\n");
        }
        this.body = bodyBuilder.toString().trim();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public StatusType getStatusType() {
        return Status.getStatusType(this.getStatusCode());
    }

    public void setStatus(Status newStatus) {
        setStatus(newStatus.code, newStatus.message);
    }

    public void setStatus(int code, String message) {
        statusCode = code;
        statusMessage = message;
    }

    public String getHeader(String key) {
        return headers.getOrDefault(key, null);     // default value is set to null
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void removeHeader(String key) {
        headers.remove(key);    // this can cause exception possibly (?)
    }

    public void setContentType(String mime) {
        setHeader("Content-Type", String.format("%s; charset=utf-8", mime));
    }

    public enum BodyType {
        Text,
        HTML,
        Path
    }

    public String getBody() {
        return body;
    }

    public void setBody(String value, BodyType type) {
        switch (type) {
            case Text:
                setContentType("text/plain");
                body = value;
                break;
            case HTML:
                setContentType("text/html");
                body = value;
                break;
            case Path:
                body = null;
                setStatus(Status.NotImplemented_501);
                System.out.println("Path bodies are not implemented yet");
                break;
        }
    }

    public byte[] toByteArray() {
        String output = this.toString();

        // Body
        if(body != null) {
            // Text body
            return output.getBytes(StandardCharsets.UTF_8);
        }
        else {
            // Binary body
            byte[] headerBytes = output.getBytes(StandardCharsets.UTF_8);
            byte[] responseBytes = new byte[headerBytes.length + binaryBody.length];
            System.arraycopy(headerBytes, 0, responseBytes, 0, headerBytes.length);
            System.arraycopy(binaryBody, 0, responseBytes, headerBytes.length, binaryBody.length);
            return responseBytes;
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        // Status-line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
        output.append("HTTP/1.1");
        output.append(" ");
        output.append(statusCode);
        output.append(" ");
        if(statusMessage != null && statusMessage.length() > 0) {
            output.append(statusMessage);
        }
        else {
            output.append("status message not set");
        }
        output.append("\r\n");

        // Body
        if(body != null) {
            // text body
            output.append(body);
        }

        return output.toString();
    }


}
