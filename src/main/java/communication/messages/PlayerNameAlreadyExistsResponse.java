package communication.messages;

/**
 * Message to be sent to the client as a response to {@link JoinGameRequest} or
 * {@link NewGameRequest} if the player name already exists in the game.
 */
public class PlayerNameAlreadyExistsResponse {

  private final String messageType = "PlayerNameAlreadyExistsResponse";


  public String getMessageType() {
    return messageType;
  }

}
