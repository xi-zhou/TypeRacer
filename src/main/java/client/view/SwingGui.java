package client.view;

import client.controller.Controller;
import client.model.MultiplayerTypeRacerClient;
import client.view.game.GameScreen;
import client.view.start.StartScreen;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/** takes care of the different windows that are displayed. */
public class SwingGui extends JFrame {
  private final Controller controller;
  private MultiplayerTypeRacerClient model;

  private static final int MINIMUM_FRAME_WIDTH = 800;
  private static final int MINIMUM_FRAME_HEIGHT = 400;

  /**
   * Creates a new SwingGui window for TypeRacer, with the given {@link Controller}. The window
   * provides two views: A start screen and a game-screen. By default, the welcome screen is shown.
   *
   *<p>The window is only made visible once {@link #makeVisible()} is called.
   *
   * @see #setStartScreen()
   * @see #setNewMultiPlayerGameScreen(MultiplayerTypeRacerClient)
   */
  public SwingGui(final Controller controller) {
    super("Type Racer");
    FlatDarculaLaf.install();
    setMinimumSize(new Dimension(MINIMUM_FRAME_WIDTH, MINIMUM_FRAME_HEIGHT));
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.controller = controller;

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        controller.closeWindow(model);
      }
    });
    // default screen when starting
    setStartScreen();
  }

  /**
   * Sets the start screen. This screen shows the TypeRacer start screen and allows to change to
   * creating a new game.
   */

  public void setStartScreen() {
    clearAllContent();
    add(StartScreen.create(controller));
    pack();
  }

  /** Sets the new-game screen. This screen provides utilities to create a new TypeRacer game. */
  public void setNewMultiPlayerGameScreen(MultiplayerTypeRacerClient game) {
    model = game;
    clearAllContent();
    add(GameScreen.create(controller, game));
    pack();
  }

  private void clearAllContent() {
    getContentPane().removeAll();
  }

  /** Shows the GUI. If the GUI is already visible, this method has no effect. */
  public void makeVisible() {
    if (isVisible()) {
      return;
    }
    pack();
    setVisible(true);
  }

  public void showDuplicatePlayerNameMessage() {
    showError("duplicatePlayername");
  }

  /** Show the given error message to the user. */
  public void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error!", JOptionPane.ERROR_MESSAGE);
  }

  public void showUnknownGameIdMessage() {
    showError("game id unkown");
  }

  /**
   * Show a user guide for the player.
   */
  public void showInstruction() {      
    ImageIcon icon = null;
    try {
      icon = new ImageIcon(ImageIO.read(getClass().getResource("/instruction.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("1: Give a username and put 127.0.0.1 as server adresse"));
    panel.add(new JLabel(
        "2: Click on *start new game* button to start or give a GameID to *join* your friends"));
    panel.add(new JLabel("3: Whenever you are ready just click on *start coundown* to play!"));
    JOptionPane.showMessageDialog(this, panel, "Instruction", JOptionPane.INFORMATION_MESSAGE,
        icon);
  }
}
