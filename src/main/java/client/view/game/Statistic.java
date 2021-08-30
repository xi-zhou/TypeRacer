package client.view.game;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** when game over then pop up a window,that show the statistic of the player. */
public class Statistic extends JOptionPane {

  /**
   * Show a window when game is over.
   *
   * @param wpm The final wpm when game end
   * @param gameTime The whole time for this game
   * @param accuracy The final accuracy when game end
   * @param isWon If the player win or not
   */
  public Statistic(int wpm, long gameTime, double accuracy, boolean isWon) {
    String endMsg;
    ImageIcon icon = null;

    if (isWon) {
      endMsg = "You won!";
      try {
        icon = new ImageIcon(ImageIO.read(getClass().getResource("/winIcon.png")));
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      endMsg = "You lose!";
      try {
        icon = new ImageIcon(ImageIO.read(getClass().getResource("/loseIcon.png")));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.add(new JLabel("Your speed :" + wpm + " wpm"));
    panel.add(new JLabel("Time :" + gameTime + " seconds"));
    panel.add(new JLabel("Accuracy :" + accuracy + "%"));
    
    showMessageDialog(this, panel, endMsg, INFORMATION_MESSAGE, icon);

  }
}
