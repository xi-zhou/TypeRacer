package server;

import com.google.gson.Gson;
import communication.Connection;
import communication.messages.GameDoesNotExistResponse;
import communication.messages.JoinGameRequest;
import communication.messages.JoinGameResponse;
import communication.messages.NewGameRequest;
import communication.messages.PlayerNameAlreadyExistsResponse;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import server.model.MultiplayerTypeRacerServer;


/**
 * This is the 'controller' of the server. It manages games (i.e. the model) and handles the
 * communication between players (i.e. 'view') and the respective game.
 */
class GamesManager implements Closeable {
  private final Map<Integer, MultiplayerTypeRacerServer> games;
  private final Map<Integer, ExecutorService> gameThreadPools;
  private final Map<PlayerConnection, Integer> playerToGameId;

  GamesManager() {
    games = new ConcurrentHashMap<>();
    playerToGameId = new ConcurrentHashMap<>();
    gameThreadPools = new ConcurrentHashMap<>();
  }

  private static int getRandomGameId() {
    return (int) (System.currentTimeMillis() % 100000);
  }

  /**
   * Registers an incoming connection. That is, either a {@link NewGameRequest} or a
   * {@link JoinGameRequest} is expected to be received on the given Socket's input stream. The
   * requests are then handled accordingly. This means that possibly a new game is created and the
   * connection is added as a new player. The respective request messages get validated and the
   * client is notified if the validation fails.
   *
   * @param connectionSocket Socket connected to the client
   * @throws IOException If connection breaks
   */
  void handleNewPlayerConnection(Socket connectionSocket) throws IOException {
    Connection connection = new Connection(connectionSocket);
    try {
      // Initially expect a message from the clients to know what they want.
      String json = connection.readJson();
      Gson gson = new Gson();

      if (json.contains("NewGameRequest")) {
        NewGameRequest newGameRequest = gson.fromJson(json, NewGameRequest.class);
        handleNewGameRequest(connection, newGameRequest);
      } else if (json.contains("JoinGameRequest")) {
        JoinGameRequest joinGameRequest = gson.fromJson(json, JoinGameRequest.class);
        handleJoinGameRequest(connection, joinGameRequest);
      }
    } catch (IOException e) {
      // Attempt to close
      // (Probably useless because connection is already broken)
      connection.close();
      throw e;
    }


  }

  private void handleJoinGameRequest(Connection connection, JoinGameRequest joinGameRequest)
      throws IOException {
    String playerName = joinGameRequest.getPlayerName();
    int gameId = joinGameRequest.getGameId();

    if (!games.containsKey(gameId)) {
      handleGameIdDoesNotExist(connection);
    } else {
      addNewPlayerToGame(playerName, connection, gameId);
    }
  }

  void handleStartGameRequest(PlayerConnection player) {
    int gameId = playerToGameId.get(player);
    gameThreadPools.get(gameId).execute(() -> {
      MultiplayerTypeRacerServer game = games.get(gameId);
      game.prepareCountdown();
      long countdown = game.getCountdownValue();
      while (0 <= countdown) {

        Set<PlayerConnection> players = getPlayersByGameId(game.getId());
        for (PlayerConnection otherPlayer : players) {
          otherPlayer.handleUpdateCountdown(countdown);
        }
        countdown = game.getCountdownValue();
        try {
          Thread.sleep(250);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void handleNewGameRequest(Connection connection, NewGameRequest newGameRequest) {
    String playerName = newGameRequest.getPlayerName();

    int gameId = startNewGame();
    addNewPlayerToGame(playerName, connection, gameId);
  }

  private void handleGameIdDoesNotExist(Connection connection) throws IOException {
    GameDoesNotExistResponse response = new GameDoesNotExistResponse();
    String json = new Gson().toJson(response);
    connection.writeJson(json);
    connection.close();
  }

  private void addNewPlayerToGame(String playerName, Connection connection, int gameId) {
    MultiplayerTypeRacerServer game = games.get(gameId);
    gameThreadPools.get(gameId).execute(() -> {
      try {

        String actualPlayerName = playerName;
        if (playerName == null || playerName.isBlank()) {
          actualPlayerName = "unnamed";
        }

        if (isDuplicatePlayerName(actualPlayerName, gameId)) {
          handleDuplicatePlayerName(connection);
          return;
        }
        game.addNewPlayer(actualPlayerName);
        try {
          sendJoinGameResponse(connection, game, actualPlayerName);
        } catch (IOException e) {
          game.removePlayer(actualPlayerName);
          if (game.getNumPlayers() == 0) {
            removeGame(gameId);
          }
          return;
        }

        // Send notification to others
        Set<PlayerConnection> players = getPlayersByGameId(gameId);
        for (PlayerConnection player : players) {
          player.handlePlayerJoined(game.getState());
        }

        PlayerConnection playerConnection =
            new PlayerConnection(actualPlayerName, this, connection);
        playerToGameId.put(playerConnection, gameId);
        playerConnection.awaitClientAction();
      } catch (IOException e) {
        // Connection is gone - do nothing
      }
    });
  }

  private boolean isDuplicatePlayerName(String playerName, int gameId) {
    for (Map.Entry<PlayerConnection, Integer> playerAndGameId : playerToGameId.entrySet()) {
      String otherPlayerName = playerAndGameId.getKey().getPlayerName();
      if (playerAndGameId.getValue() == gameId && otherPlayerName.equals(playerName)) {
        return true;
      }
    }
    return false;
  }

  private void handleDuplicatePlayerName(Connection connection) throws IOException {
    connection.writeJson(new Gson().toJson(new PlayerNameAlreadyExistsResponse()));
    connection.close();
  }

  private int startNewGame() {
    int gameId = getRandomGameId();
    MultiplayerTypeRacerServer newGame = new MultiplayerTypeRacerServer(gameId);
    games.put(gameId, newGame);
    gameThreadPools.put(gameId, Executors.newSingleThreadExecutor());

    return gameId;
  }

  private void sendJoinGameResponse(Connection connection, MultiplayerTypeRacerServer initialModel,
      String playerName) throws IOException {
    JoinGameResponse response =
        new JoinGameResponse(initialModel.getId(), playerName, initialModel.getState());
    String json = new Gson().toJson(response);
    connection.writeJson(json);
  }

  /**
   * Notifies the model and other players of a game that a player left. Each player is notified
   * through the respective method in {@link PlayerConnection}.
   *
   * @param connectionThatLeft Connecion to the player who left.
   */
  void playerLeft(PlayerConnection connectionThatLeft) {
    int gameId = playerToGameId.get(connectionThatLeft);
    gameThreadPools.get(gameId).execute(() -> {
      MultiplayerTypeRacerServer game = games.get(gameId);
      game.getState().getPlayerStateMap().remove(connectionThatLeft.getPlayerName());
      Set<PlayerConnection> players = getPlayersByGameId(game.getId());
      for (PlayerConnection otherPlayer : players) {
        otherPlayer.handlePlayerLeft(game);
      }
    });
  }

  /**
   * Notifies the model and other players of a game that a player has finished a word. Each player
   * is notified through the respective method in {@link PlayerConnection}.
   *
   * @param player The connection of the player who finished a word.
   * @param newWpmEntry The wpmScore from the finished word.
   */
  void playerFinishedWord(PlayerConnection player, int newWpmEntry) {
    int gameId = playerToGameId.get(player);
    gameThreadPools.get(gameId).execute(() -> {
      MultiplayerTypeRacerServer game = games.get(gameId);
      game.updatePlayerState(player.getPlayerName(), newWpmEntry);

      int textlength = game.getState().getTextToType().textLength() - 1;
      int playerprogress =
          game.getState().getPlayerStateMap().get(player.getPlayerName()).getWordProgress();
      boolean isGameFinished = textlength == playerprogress;
      Set<PlayerConnection> players = getPlayersByGameId(game.getId());
      for (PlayerConnection otherPlayer : players) {

        if (isGameFinished) {
          otherPlayer.handleGameFinishedNotification(game);
        } else {
          otherPlayer.handlePlayerFinishedWord(game);
        }
      }
    });
  }


  private void removeGame(int gameId) {
    games.remove(gameId);
    gameThreadPools.get(gameId).shutdownNow();
    gameThreadPools.remove(gameId);
  }

  private Set<PlayerConnection> getPlayersByGameId(int gameId) {
    Set<PlayerConnection> players = new HashSet<>();
    for (Map.Entry<PlayerConnection, Integer> entry : playerToGameId.entrySet()) {
      if (entry.getValue() == gameId) {
        players.add(entry.getKey());
      }
    }

    return players;
  }

  /**
   * Closes all Threads of this class.
   *
   * @throws IOException If the connection id interrupted
   */
  @Override
  public void close() throws IOException {
    for (PlayerConnection connection : playerToGameId.keySet()) {
      try {
        connection.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    for (ExecutorService executorService : gameThreadPools.values()) {
      executorService.shutdownNow();
    }
  }
}
