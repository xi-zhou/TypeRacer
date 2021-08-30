package communication.messages;

import server.model.GameState;

/**
 * Message to be sent to the clients if some player left the game.
 */
public class PlayerLeftNotification {


  private final GameState currentGameState;
  private final String messageType = "PlayerLeftNotification";

  /**
   * Creates a PlayerLeftNotification object.
   *
   * @param currentGameState Current gamestate.
   */
  public PlayerLeftNotification(GameState currentGameState) {
    this.currentGameState = currentGameState;
  }

  public GameState getCurrentGameState() {
    return currentGameState;
  }

  public String getMessageType() {
    return messageType;
  }
}
