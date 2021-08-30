package communication.messages;

import server.model.GameState;

/**
 * Message to be sent to the clients if some player joined the game.
 */
public class PlayerJoinedNotification {

  private final String messageType = "PlayerJoinedNotification";
  private final GameState gameState;

  public PlayerJoinedNotification(GameState gameState) {
    this.gameState = gameState;
  }

  public GameState getCurrentGameState() {
    return gameState;
  }

  public String getMessageType() {
    return messageType;
  }
}
