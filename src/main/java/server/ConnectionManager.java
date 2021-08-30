package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles incomming socket connections.
 */
class ConnectionManager {
  private final ServerSocket serverSocket;
  private final GamesManager gamesManager;
  private final ExecutorService executorService;

  ConnectionManager(ServerSocket serverSocket, GamesManager gamesManager) {
    this.serverSocket = serverSocket;
    this.gamesManager = gamesManager;
    executorService = Executors.newFixedThreadPool(5);
  }

  /**
   * Start awaiting new connections. A new connection will be handled in a new thread. Handling a
   * connection means passing it to {@link GamesManager}.
   */
  void start() {
    try {
      acceptConnections();
    } catch (IOException e) {
      // Something went wrong - Needs to be fixed
      throw new AssertionError(e);
    }
  }

  private void acceptConnections() throws IOException {
    while (true) {
      Socket connectionSocket = serverSocket.accept();
      System.out.println("new connection");
      executorService.execute(() -> {
        try {
          gamesManager.handleNewPlayerConnection(connectionSocket);
        } catch (IOException e) {
          // Ignore - the client disconnected before it could properly connect
        }
      });
    }
  }
}
