import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class ResponseHandler implements Runnable {

  private final Socket client;
  private ArrayList<String> header;
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
  private DataOutputStream response;
  //private BufferedWriter response;

  ResponseHandler(Socket client) {
    this.client = client;
  }

  @Override
  public void run() {

    try {
      DataInputStream request = new DataInputStream(client.getInputStream());

      response = new DataOutputStream(client.getOutputStream());

      String requestBody = request.readUTF();

      header = new ArrayList<>(Arrays.asList(requestBody.split("\n")));

      if (header.get(0).contains("GET")) {
        getResponse();
      }
      else if (header.get(0).contains("PUT")) {
        putResponse(request.readUTF());
      }
      else {
        System.out.println("Not a valid request from the client");
      }

      System.out.println("Response sent");
      request.close();
      response.close();
      client.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void putResponse(String responseBondy) throws IOException {

    //write to reosurce
    String resource = header.get(0).split(" ")[1];
    boolean hasResourceUploaded = hasResourceUploaded(resource,responseBondy);
    if(hasResourceUploaded) {

      StringBuilder stringBuilder = receivedOk();
      response.writeUTF(stringBuilder.toString());
      stringBuilder.setLength(0);
      response.flush();
    }

  }

  private void getResponse() throws IOException {
    String resource = header.get(0).split(" ")[1];
    boolean doesResourceExist = doesResourceExist(resource);
    if(doesResourceExist) {
      StringBuilder stringBuilder = receivedOk();
      appendLine(stringBuilder,getResource(resource));
      response.writeUTF(stringBuilder.toString());
      stringBuilder.setLength(0);
      response.flush();

    }
    else {
      //404
      resourceNotFound();
    }

  }


  private StringBuilder receivedOk() {
    System.out.println("Received a 200 OK");
    StringBuilder stringBuilder = new StringBuilder();
    appendLine(stringBuilder,"HTTP/1.1 200 OK");
    appendLine(stringBuilder,"Server:localhost"); // Not nice..
    appendLine(stringBuilder,"Date:" + DATE_FORMAT.format(new Date()));
    appendLine(stringBuilder, "Content-Type: text/html");
    appendLine(stringBuilder,"Connection: Closed");
    appendLine(stringBuilder,"");
    return stringBuilder;
  }


  private void resourceNotFound() throws IOException {
    System.out.println("Resource Not Found : 404");
    StringBuilder stringBuilder = new StringBuilder();
    appendLine(stringBuilder, "HTTP/1.1 404 Not Found");
    appendLine(stringBuilder, "Server:localhost"); // Not nice..
    appendLine(stringBuilder, "Date:" + DATE_FORMAT.format(new Date()));
    appendLine(stringBuilder, "");
    response.writeUTF(stringBuilder.toString());
    response.flush();
  }

  private void appendLine(StringBuilder builder, String value){
    builder.append(value).append(System.lineSeparator());
  }

  private boolean doesResourceExist(String resource) {
    return ResponseHandler.class.getResource(resource) != null;
  }

  private boolean hasResourceUploaded(String resource,String responseBody)
      throws FileNotFoundException {

    PrintWriter writer = new PrintWriter(
            new File(ResponseHandler.class.getResource(resource).getPath()));
    writer.write(responseBody);
    writer.close();
    return true;

  }

  private String getResource(String resource) {
    return new Scanner(ResponseHandler.class.getResourceAsStream(resource), "UTF-8")
        .useDelimiter("\\A")
        .next();
  }

}
