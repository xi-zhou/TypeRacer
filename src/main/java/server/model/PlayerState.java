package server.model;

import java.io.Serializable;

/**
 * Represent one player in the game.
 */
public class PlayerState implements Serializable {
  private static final long serialVersionUID = 1L;

  private int wpm;
  private int wordProgress;

  public PlayerState() {
    wordProgress = -1;
  }

  /**
   * Returns at which word the player current.
   *
   * @return thw wordprogress as an int.
   */
  public int getWordProgress() {
    return wordProgress;
  }

  /**
   * Returns the Wpm of the player.
   *
   * @return the wpm in int
   */
  public int getWpm() {
    return wpm;
  }

  /**
   * Updates the progress of the player.
   *
   * @param newWpmEntry is the wpm
   */
  public void updateProgress(int newWpmEntry) {
    wordProgress++;
    wpm = newWpmEntry;
  }

}
