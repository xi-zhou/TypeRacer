package server;

import com.google.gson.Gson;
import communication.Connection;
import communication.messages.FinishedWordRequest;
import communication.messages.GameFinishedNotification;
import communication.messages.PlayerFinishedWord;
import communication.messages.PlayerJoinedNotification;
import communication.messages.PlayerLeftNotification;
import communication.messages.UpdateCountdownNotification;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import server.model.GameState;
import server.model.MultiplayerTypeRacerServer;

class PlayerConnection implements Closeable {
  private final Connection connection;
  private final GamesManager gamesManager;
  private final String playerName;
  private final ExecutorService readingPool;
  private final ExecutorService writingPool;
  private boolean connected;

  PlayerConnection(String playerName, GamesManager gamesManager, Connection connection) {
    this.gamesManager = gamesManager;
    this.connection = connection;
    this.playerName = playerName;
    connected = true;

    readingPool = Executors.newSingleThreadExecutor();
    writingPool = Executors.newSingleThreadExecutor();
  }

  /**
   * Get the name of the player that is connected through this object.
   *
   * @return The name
   */
  String getPlayerName() {
    return playerName;
  }

  /**
   * Notifies the client in a new thread that some player joined.
   *
   * @param gameState is the new state of the game.
   */
  void handlePlayerJoined(GameState gameState) {
    PlayerJoinedNotification message = new PlayerJoinedNotification(gameState);

    sendOrLeaveGame(message);
  }

  /**
   * Notifies the client in a new thread that theres an update for the countdown.
   *
   * @param countdown the new countdown value
   */
  void handleUpdateCountdown(long countdown) {
    UpdateCountdownNotification message = new UpdateCountdownNotification(countdown);

    sendOrLeaveGame(message);
  }

  /**
   * Notifies the client in a new thread that some player left.
   *
   * @param model The model after the player left
   */
  void handlePlayerLeft(MultiplayerTypeRacerServer model) {
    PlayerLeftNotification message = new PlayerLeftNotification(model.getState());

    sendOrLeaveGame(message);
  }

  void handleGameFinishedNotification(MultiplayerTypeRacerServer model) {
    GameFinishedNotification message = new GameFinishedNotification(model.getState());

    sendOrLeaveGame(message);
  }

  /**
   * Notifies the client in a new thread that a client finished a word.
   *
   * @param model The model after updating the progress .
   */
  void handlePlayerFinishedWord(MultiplayerTypeRacerServer model) {
    PlayerFinishedWord message = new PlayerFinishedWord(model.getState());

    sendOrLeaveGame(message);

    awaitClientAction();
  }

  /**
   * Listens for messages from the connected client in a new thread.
   */
  void awaitClientAction() {
    if (!connected) {
      return;
    }

    readingPool.execute(() -> {
      try {
        String json = connection.readJson();
        Gson gson = new Gson();
        if (json.contains("StartGameRequest")) {
          gamesManager.handleStartGameRequest(this);
          awaitClientAction();
        } else if (json.contains("FinishedWordRequest")) {
          FinishedWordRequest finishedWordRequest = gson.fromJson(json, FinishedWordRequest.class);
          gamesManager.playerFinishedWord(this, (finishedWordRequest).getNewWpmEntry());
        } else {
          throw new AssertionError("Invalid Communication");
        }
      } catch (IOException e) {
        connected = false;
        gamesManager.playerLeft(this);
      }
    });
  }

  private void sendOrLeaveGame(Object message) {
    if (!connected) {
      return;
    }

    writingPool.execute(() -> {
      try {

        String json = new Gson().toJson(message);
        connection.writeJson(json);
      } catch (IOException e) {
        connected = false;
        gamesManager.playerLeft(this);
      }
    });
  }

  /**
   * Closes all running threads in the class.
   *
   * @throws IOException if it's already closed.
   */
  @Override
  public void close() throws IOException {
    connection.close();
    readingPool.shutdownNow();
    writingPool.shutdownNow();
  }
}
