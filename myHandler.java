import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class myHandler implements HttpHandler {
    private final Map<String, String> data;


    public myHandler(Map<String, String> data) {
        this.data = data;
    }
    
    public void handle(HttpExchange httpExchange) throws IOException{
        String response = "Request Received";
        String method = httpExchange.getRequestMethod(); //get request
        
        //handling different requests & get response
        try {
         if (method.equals("GET")) {
           response = handleGet(httpExchange);
         } else {
           throw new Exception("Not Valid Request Method");
         }
       } catch (Exception e) {
         System.out.println("An erroneous request");
         response = e.toString();
         e.printStackTrace();
       }
     
       //Sending back response to the client
       httpExchange.sendResponseHeaders(200, response.length());
       OutputStream outStream = httpExchange.getResponseBody();
       outStream.write(response.getBytes());
       outStream.close();
    }

    private String handleGet(HttpExchange httpExchange) throws IOException {
        String response = "Invalid GET request";
        URI uri = httpExchange.getRequestURI();
        String query = uri.getRawQuery();
          if (query != null) {
          String name = query.substring(query.indexOf("=") + 1);
          String web = data.get(name); // Retrieve data from hashmap
            if (web != null) {
              response = web;
              return web;
            } else {
                StringBuilder htmlBuilder = new StringBuilder();
                htmlBuilder
                  .append("<html>")
                  .append("<body>")
                  .append("<h1>")
                  .append("Hello ")
                  .append(name)
                  .append("</h1>")
                  .append("</body>")
                  .append("</html>");
           
                // encode HTML content
                response = htmlBuilder.toString();
            }
          }
        return response;
    }
}   
