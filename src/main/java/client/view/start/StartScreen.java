package client.view.start;

import client.controller.Controller;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/** Start Screen for TypeRacer.It shows the title and a button that allows to start the game. */
public class StartScreen extends JPanel {

  private static final int TEXT_FIELD_WIDTH = 20;
  final JTextComponent fieldUser;
  final JTextComponent joinGame;
  private final JTextComponent fieldAddress;
  GridBagConstraints gbc = new GridBagConstraints();

  BufferedImage img;

  {
    try {
      img = ImageIO.read(getClass().getResource("/car1.jpg"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
  }

  /**
   * displays the start screen with welcome message and start game button.
   *
   * @param controller controller passes on object
   */
  public StartScreen(Controller controller) {

    setLayout(new GridBagLayout());

    // spread buttons away from each other
    gbc.insets = new Insets(2, 2, 2, 2);
    gbc.weighty = 1;

    // Welcome Message
    JLabel welcome = new JLabel("Welcome to TypeRacer");
    gbc.gridy = 0;
    gbc.gridx = 3;
    welcome.setForeground(Color.YELLOW);
    welcome.setFont(new Font("Papyrus", Font.BOLD, 45));
    add(welcome, gbc);

    // start game Button
    JButton newGame = new JButton("      Start  New  Game     ");
    gbc.gridy = 1;
    gbc.gridx = 2;
    newGame.setFont(new Font("Serif", Font.BOLD, 15));
    newGame.addActionListener(e -> controller.requestStartGame(getUserName(), getServerAddress()));
    add(newGame, gbc);

    // creates instruction Button
    final Component instructionButton = createInstructionButton(controller);
    gbc.gridy = 1;
    gbc.gridx = 3;
    instructionButton.setFont(new Font("Serif", Font.BOLD, 15));
    add(instructionButton, gbc);

    // creates join Button
    final Component joinButton = createJoinButton(controller);
    gbc.gridy = 1;
    gbc.gridx = 4;
    joinButton.setFont(new Font("Serif", Font.BOLD, 15));
    add(joinButton, gbc);

    // creates Textfield for username label
    fieldUser = new JTextField(TEXT_FIELD_WIDTH);
    gbc.gridy = 3;
    gbc.gridx = 2;
    gbc.weighty = 0.1;
    add(fieldUser, gbc);

    // creates username Label
    final JLabel userButton = new JLabel("     Enter  Username    ");
    userButton.setForeground(Color.MAGENTA);
    gbc.gridy = 2;
    gbc.gridx = 2;
    gbc.weighty = 0.1;
    userButton.setFont(new Font("Serif", Font.BOLD, 20));
    add(userButton, gbc);

    // creates Textfield for gameID Label
    joinGame = new JTextField(TEXT_FIELD_WIDTH);
    gbc.gridy = 3;
    gbc.gridx = 3;
    gbc.weighty = 0.1;
    add(joinGame, gbc);

    // creates gameID Label
    final JLabel gameId = new JLabel("     Enter  GameID    ");
    gameId.setForeground(Color.MAGENTA);
    gbc.gridy = 2;
    gbc.gridx = 3;
    gbc.weighty = 0.1;
    gameId.setFont(new Font("Serif", Font.BOLD, 20));
    add(gameId, gbc);

    // creates Textfield for fieladdress
    fieldAddress = new JTextField(TEXT_FIELD_WIDTH);
    gbc.gridy = 3;
    gbc.gridx = 4;
    gbc.weighty = 0.1;
    add(fieldAddress, gbc);

    // creates serveraddress label
    final JLabel serverButton = new JLabel("  Enter Serveraddress ");
    serverButton.setForeground(Color.MAGENTA);
    gbc.gridy = 2;
    gbc.gridx = 4;
    gbc.weighty = 0.1;
    serverButton.setFont(new Font("Serif", Font.BOLD, 20));
    add(serverButton, gbc);
  }

  /** returns a new start screen object. */
  public static StartScreen create(final Controller controller) {
    return new StartScreen(controller);
  }

  /** creates a button to read game instructions. */
  private Component createInstructionButton(final Controller controller) {
    JButton joinButton = new JButton("         Instructions        ");
    joinButton.addActionListener(e -> controller.showInstruction());
    return joinButton;
  }

  /** creates a button where the user can join an existing game. */
  private Component createJoinButton(final Controller controller) {
    JButton joinButton = new JButton("         Join  Game         ");
    joinButton.addActionListener(
        e -> controller.joinMultiplayerGame(getUserName(), getServerAddress(), getGameId()));
    return joinButton;
  }

  String getUserName() {
    return fieldUser.getText();
  }

  String getGameId() {
    return joinGame.getText();
  }

  String getServerAddress() {
    return fieldAddress.getText();
  }
}
