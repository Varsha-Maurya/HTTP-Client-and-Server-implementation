import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SimpleHttpClient {

  private static String hostName;
  private static int portName;
  private static String resourceName;
  private static final String USER_AGENT = "Mozilla/5.0";
  private static Socket client;
  private static DataOutputStream request;
  private static DataInputStream response;

  public static void main(String[] args) throws IOException {


    hostName = args[0];
    portName = Integer.valueOf(args[1]);
    String requestType = args[2];
    resourceName = args[3];
    client = new Socket(hostName, portName);
    // Declare a writer to this url
    request = new DataOutputStream(client.getOutputStream());
    response = new DataInputStream(client.getInputStream());


    if (requestType.equals("GET")) {
      getRequest();
    }
    else if (requestType.equals("PUT")) {
      putRequest();
    }
    else {
      System.out.println("Request type " + requestType + "is not supported");
    }

  }

  private static void putRequest() throws IOException {


    StringBuilder requestHeader = new StringBuilder();
    appendLine(requestHeader,"PUT /" + resourceName + " HTTP/1.1");
    appendLine(requestHeader,"Host: " + hostName);
    appendLine(requestHeader,"Accept-Language: en-us");
    appendLine(requestHeader,"Connection: Keep-Alive");
    appendLine(requestHeader,"User-Agent:"+USER_AGENT);
    appendLine(requestHeader,"Content-type: text/html");
    appendLine(requestHeader,"");
    request.writeUTF(requestHeader.toString());
    request.writeUTF(getResource());

    request.flush();

    System.out.println("PUT request sent....");
    System.out.println("Waiting for response ....");

    String responseBody = response.readUTF();

    System.out.println(responseBody);

    close();

  }

  public static void getRequest() throws IOException {

    StringBuilder requestHeader = new StringBuilder();
    appendLine(requestHeader,"GET /" + resourceName + " HTTP/1.1");
    appendLine(requestHeader,"Host: " + hostName);
    appendLine(requestHeader,"Connection: close");
    appendLine(requestHeader,"User-Agent:"+USER_AGENT);
    appendLine(requestHeader,"");
    request.writeUTF(requestHeader.toString());
    request.flush();

    System.out.println("GET request sent....");
    System.out.println("Waiting for response ....");

    String responseBody = response.readUTF();

    System.out.println(responseBody);

    close();

  }

  private static String getResource() {
    return new Scanner(SimpleHttpClient.class.getResourceAsStream(resourceName), "UTF-8")
        .useDelimiter("\\A")
        .next();
  }

  private static void appendLine(StringBuilder builder, String value){
    builder.append(value).append(System.lineSeparator());
  }

  private static void close() throws IOException {
    request.close();
    response.close();
    client.close();
  }
}

