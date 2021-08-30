package server.model;

import java.util.HashMap;
import java.util.Map;


/**
 * Saves all the important information of the game.
 */
public class GameState {

  private final Map<String, PlayerState> playerStateMap;
  private final TextToType textToType;
  private final int countdownInSeconds;

  /**
   * Creates a gameState with a chosen text.
   *
   * @param text a text which will be used in the game, needs to be a String.
   */
  public GameState(String text) {
    textToType = new TextToType(text);
    playerStateMap = new HashMap<>();
    countdownInSeconds = 10;
  }

  /**
   * Return the current map of player.
   *
   * @return the map of player.
   */
  public Map<String, PlayerState> getPlayerStateMap() {
    return playerStateMap;
  }

  /**
   * Returns a class with the current text.
   *
   * @return the current text.
   */
  public TextToType getTextToType() {

    return textToType;
  }

  public int getCountdownInSeconds() {
    return countdownInSeconds;
  }

}
