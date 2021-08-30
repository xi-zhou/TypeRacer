package server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a database for the game.
 */
public class TextDatabase {
  private static List<String> list;


  public TextDatabase() {
    bufferReaderToArrayList();
  }

  private void bufferReaderToArrayList() {

    InputStream input = getClass().getResourceAsStream("/story.txt");
    list = new ArrayList<String>();
    try {
      final BufferedReader in =
          new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
      String line;
      while ((line = in.readLine()) != null) {
        list.add(line);
      }
      in.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Returns a random Text out of the database.
   */
  public String getText() {
    int guessedIndex = new Random().nextInt(list.size());
    String text = list.get(guessedIndex);
    return text;
  }
}
