package communication.messages;

/**
 * Message to be sent to the server if a client wants to join a game with the given id as player
 * with the given player name.
 */
public class JoinGameRequest {

  private final int gameId;
  private final String playerName;
  private final String messageType = "JoinGameRequest";

  public JoinGameRequest(int gameId, String playerName) {
    this.gameId = gameId;
    this.playerName = playerName;
  }

  public int getGameId() {
    return gameId;
  }

  public String getPlayerName() {
    return playerName;
  }

  public String getMessageType() {
    return messageType;
  }

}


