import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadWorker {


  private final int port;
  private Socket socket;

  public MultiThreadWorker(int port) {
    this.port = port;
  }

  public void start() throws IOException {

    //Create a socket
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("Started socket at port " + port);

    while (true) {
      System.out.println("........");
      socket = serverSocket.accept();
      System.out.println("Client connected : " + socket.getInetAddress().getCanonicalHostName());

      Thread thread = new Thread(new ResponseHandler(socket));
      thread.run();
    }

  }

}