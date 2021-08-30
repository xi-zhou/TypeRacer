package communication.messages;

import server.model.GameState;


/**
 * Message to be sent to the client as a response to {@link JoinGameRequest} and
 * {@link NewGameRequest}.
 */
public class JoinGameResponse {
  private final int gameId;
  private final String playerName;
  private final GameState currentGameState;
  private final String messageType = "JoinGameResponse";

  /**
   * Creates a JoinGameResponse object.
   *
   * @param gameId The game id.
   * @param playerName Name of the player who wants to join.
   * @param currentGameState The current game state.
   */
  public JoinGameResponse(int gameId, String playerName, GameState currentGameState) {
    this.gameId = gameId;
    this.playerName = playerName;
    this.currentGameState = currentGameState;
  }

  public int getGameId() {
    return gameId;
  }

  public String getPlayerName() {
    return playerName;
  }

  public GameState getCurrentGameState() {
    return currentGameState;
  }

  public String getMessageType() {
    return messageType;
  }
}
