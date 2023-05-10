import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;


public class RequestHandler implements HttpHandler {


 private final Map<String, String> data;


 public RequestHandler(Map<String, String> data) {
   this.data = data;
 }

 /*
  * This implemented method handles all requestes by delegation
  */
 public void handle(HttpExchange httpExchange) throws IOException {
   String response = "Request Received";
   String method = httpExchange.getRequestMethod(); //get request
   
   //handling different requests & get response
   try {
    if (method.equals("GET")) {
      response = handleGet(httpExchange);
    } else if (method.equals("POST")) {
      response = handlePost(httpExchange);
    } else if (method.equals("PUT")) {
      response = handlePut(httpExchange);
    } else if (method.equals("DELETE")){
      response = handleDelete(httpExchange);
    }else {
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
 
 /*
  * This method is called when an HTTP GET request is 
  * received by the server. 
  * Our queries will be the names of the programming languages, 
  * and the response will be the release year of the corresponding 
  * programming language.
  */
  private String handleGet(HttpExchange httpExchange) throws IOException {
    String response = "Invalid GET request";
    URI uri = httpExchange.getRequestURI();
    String query = uri.getRawQuery();
      if (query != null) {
      String value = query.substring(query.indexOf("=") + 1);
      String year = data.get(value); // Retrieve data from hashmap
        if (year != null) {
          response = year;
          System.out.println("Queried for " + value + " and found " + year);
        } else {
          response = "No data found for " + value;
        }
      }
    return response;
  }

  /* 
   * It handles POST requests to the server by retrieving the data 
   * from the request body, parsing it, storing it in a HashMap, 
   * and sending a response back to the client.
  */
  private String handlePost(HttpExchange httpExchange) throws IOException {
    InputStream inStream = httpExchange.getRequestBody();
    Scanner scanner = new Scanner(inStream);
    String postData = scanner.nextLine();
    String language = postData.substring(
      0,
      postData.indexOf(",")
    ), year = postData.substring(postData.indexOf(",") + 1);
 
 
    // Store data in hashmap
    data.put(language, year);
 
 
    String response = "Posted entry {" + language + ", " + year + "}";
    System.out.println(response);
    scanner.close();
  
    return response;
  }

  /*
   * Handl put request, and print different information about wheter
   * the new entries exists or not
   */
  private String handlePut(HttpExchange httpExchange) throws IOException {
    InputStream inStream = httpExchange.getRequestBody();
    Scanner scanner = new Scanner(inStream);
    String postData = scanner.nextLine();
    String language = postData.substring(
      0,
      postData.indexOf(",")
    ), year = postData.substring(postData.indexOf(",") + 1);
 
 
    // Store data in hashmap
    String oldyear = data.get(language);
    if(oldyear == null){
      data.put(language, year);
 
      String response = "Added entry {" + language + ", " + year + "}";
      System.out.println(response);
      scanner.close();
      return response;
    }else{
      data.put(language, year);
 
      String response = "Updated entry {" + language + ", " + year + 
      "} (previous year:" + oldyear +")";
      System.out.println(response);
      scanner.close();
      return response;
    }
  }

/*
   * Handl put request, and print different information about wheter
   * the new entries exists or not
   */
  private String handleDelete(HttpExchange httpExchange) throws IOException {
    String response = "No data found for language";
    URI uri = httpExchange.getRequestURI();
    String query = uri.getRawQuery();
      if (query != null) {
      String value = query.substring(query.indexOf("=") + 1);
      String year = data.remove(value); // Retrieve data from hashmap
        if (year != null) {
          response = "Delete entry {" + value + "," + year + "}";
        } else {
        }
      }
    return response;
  }
}
