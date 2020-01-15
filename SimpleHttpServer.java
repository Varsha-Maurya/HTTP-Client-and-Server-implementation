import java.io.IOException;

public class SimpleHttpServer {

  public static void main(String[] args) throws IOException {

    int port = Integer.valueOf(args[0]);

    MultiThreadWorker multiThreadWorker = new MultiThreadWorker(port);
    multiThreadWorker.start();

  }

}