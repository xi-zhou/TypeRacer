package communication.messages;

/**
 * Message to be sent to the client if the client sent a {@link JoinGameRequest} with a non-existend
 * game id.
 */
public class GameDoesNotExistResponse {
  private final String messageType = "GameDoesNotExistsResponse";

  public String getMessageType() {
    return messageType;
  }

}
