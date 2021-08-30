package server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represent the text which needs to be typed.
 */
public class TextToType implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String fullText;
  private final List<String> words;

  /**
   * Takes a text and formats it so it can be used in the game.
   *
   * @param text the text which will be used for the game.
   */
  public TextToType(String text) {
    fullText = text;
    words = new ArrayList<>();
    String[] textArray = text.split(" ");
    for (String word : textArray) {
      words.add(word);
    }

  }

  /**
   * Returns a word at a specific part of the text.
   *
   * @param index is location of the game.
   * @return the word as a String.
   */
  public String getWord(int index) {
    return words.get(index);
  }

  /**
   * Returns the whole text of the game.
   *
   * @return the text as a String.
   */
  public String getFullText() {
    return fullText;
  }

  /**
   * Return the length of the text.
   */
  public int textLength() {
    return words.size();
  }
}
