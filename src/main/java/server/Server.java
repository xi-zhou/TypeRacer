package server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Server main class which is used to run the game.
 */
public class Server implements Closeable {
  private final int port;
  private ServerSocket serverSocket;
  private GamesManager gamesManager;

  /**
   * Main class for the game server. The class starts the server sockets and delegates connection
   * and game handling to {@link ConnectionManager} and {@link GamesManager}
   */
  Server(int port) {
    this.port = port;
  }

  /**
   * Main method for the server.
   *
   * @param args Commandline arguments
   */
  public static void main(final String[] args) throws IOException {
    Server server = new Server(4441);
    server.start();
    server.close();
  }

  private void start() throws IOException {
    System.out.println("Start Server...");
    gamesManager = new GamesManager();

    serverSocket = new ServerSocket(port);
    ConnectionManager connectionManager = new ConnectionManager(serverSocket, gamesManager);

    connectionManager.start();
  }

  @Override
  public void close() throws IOException {
    if (gamesManager != null) {
      gamesManager.close();
    }

    if (serverSocket != null) {
      serverSocket.close();
    }
  }
}
