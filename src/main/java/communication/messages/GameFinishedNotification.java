package communication.messages;

import server.model.GameState;

/**
 * Message send to client if the game is over.
 */
public class GameFinishedNotification {
  private final GameState finishedGameState;
  private final String messageType = "GameFinishedNotification";

  public GameFinishedNotification(GameState finishedGameState) {
    this.finishedGameState = finishedGameState;
  }

  public GameState getFinishedGameState() {
    return finishedGameState;
  }

  public String getMessageType() {
    return messageType;
  }
}
