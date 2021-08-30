package communication.messages;

/**
 * Message send to server if player want to start a new game.
 */
public class StartGameRequest {
  private final String messageType = "StartGameRequest";

  public String getMessageType() {
    return messageType;
  }
}
