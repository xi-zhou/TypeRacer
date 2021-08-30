package communication.messages;

/**
 * Message to be sent to the server if a client request to create a new game and join it with the
 * given name.
 */
public class NewGameRequest {
  private final String playerName;
  private final String messageType = "NewGameRequest";

  public NewGameRequest(String playerName) {
    this.playerName = playerName;
  }

  public String getPlayerName() {
    return playerName;
  }

  public String getMessageType() {
    return messageType;
  }

}
