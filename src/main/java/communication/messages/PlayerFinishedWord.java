package communication.messages;

import server.model.GameState;

/**
 * Message to be sent to the clients to notify them that some player finished a word.
 */
public class PlayerFinishedWord {

  private final GameState gameStateAfterFinishedWord;
  private final String messageType = "PlayerFinishedWord";


  /**
   * Creates a PlayerFinsihedWordNotification object.
   *
   * @param gameStateAfterFinishedWord Game state after typing the word
   */
  public PlayerFinishedWord(GameState gameStateAfterFinishedWord) {

    this.gameStateAfterFinishedWord = gameStateAfterFinishedWord;
  }


  public GameState getGameStateAfterFinishedWord() {
    return gameStateAfterFinishedWord;
  }

  public String getMessageType() {
    return messageType;
  }

}
