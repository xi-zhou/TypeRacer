package client.view.game;

import client.controller.Controller;
import client.model.MultiplayerTypeRacerClient;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import server.model.PlayerState;

/** Provides all necessary interactions and displays for playing TypeRacer. */
public class GameScreen extends JPanel {
  GridBagConstraints gbc = new GridBagConstraints();
  Map<Object, Object> storeBar = new HashMap<>();
  int key = 0;
  JTextPane text = new JTextPane();

  /**
   * displays the screen when playing the game with its features.
   *
   * @param controller controller to pass on object
   * @param model model provides access to multiplayertyperacer
   */
  public GameScreen(Controller controller, MultiplayerTypeRacerClient model) {

    setLayout(new GridBagLayout());

    // spread buttons away from each other
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.weightx = 1;

    new CountdownBox(model);
    new ProgressBar(model);
    new Type(model);
    new TypeText(controller, model);

    startCountdown(controller, model);
    buttonNewGame(controller, model);
    buttonQuit(controller, model);
  }

  /** Box to display Countdown till game starts at the top. */
  class CountdownBox implements PropertyChangeListener {
    JLabel countdown = new JLabel("...LOS!");
    JLabel gameId;

    CountdownBox(MultiplayerTypeRacerClient multiplayer) {
      Border border = BorderFactory.createLineBorder(Color.BLACK);
      countdown.setBorder(border);
      countdown.setFont(new Font("Serif", Font.BOLD, 20));
      gbc.weighty = 0.5;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.gridheight = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      add(countdown, gbc);

      gbc.gridx = 2;
      gbc.gridy = 0;
      gameId = new JLabel("GameID: " + multiplayer.getGameId());
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      add(gameId, gbc);

      adjustTo(multiplayer);
      multiplayer.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
      if (propertyChangeEvent.getPropertyName().equals("countdown")) {
        adjustTo((MultiplayerTypeRacerClient) propertyChangeEvent.getNewValue());
      }
    }

    private void adjustTo(final MultiplayerTypeRacerClient player) {
      if (player.getCountdown() == -1) {
        countdown.setText("Welcome to TypeRacer");
      } else {
        countdown.setText((int) player.getCountdown() + "");
      }
    }
  }

  /** creates the ProgressBar on the GameScreen to show progress of the players. */
  public class ProgressBar implements PropertyChangeListener {
    JProgressBar bar = new JProgressBar();
    JPanel panel;
    JLabel label;
    JLabel label2;
    GridBagConstraints gb = new GridBagConstraints();

    ProgressBar(MultiplayerTypeRacerClient multiplayer) {
      panel = new JPanel();
      panel.setLayout(new GridBagLayout());
      gb.insets = new Insets(5, 5, 5, 5);
      gb.weighty = 0.5;
      gb.weightx = 1;
      gb.gridheight = 1;
      gb.fill = GridBagConstraints.HORIZONTAL;
      gb.gridwidth = GridBagConstraints.REMAINDER;

      Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
      panel.setBorder(border);
      add(panel);
      adjustTo(multiplayer);
      multiplayer.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
      if (propertyChangeEvent.getPropertyName().equals("GameState")) {
        panel.removeAll();
        adjustTo((MultiplayerTypeRacerClient) propertyChangeEvent.getNewValue());
      }
    }

    private void adjustTo(final MultiplayerTypeRacerClient player) {
      Map<String, PlayerState> map = player.getCurrentGameState().getPlayerStateMap();
      for (Map.Entry<String, PlayerState> entry : map.entrySet()) {
        bar = new JProgressBar();
        bar.setPreferredSize(new Dimension(1000, 30));
        String playername = entry.getKey();
        label2 = new JLabel(playername);
        gb.anchor = GridBagConstraints.FIRST_LINE_START;
        panel.add(label2, gb);
        int wpm = entry.getValue().getWpm();
        label = new JLabel(wpm + " wpm");
        gb.anchor = GridBagConstraints.FIRST_LINE_END;
        panel.add(label, gb);

        int value = entry.getValue().getWordProgress() + 1;
        String fullText = player.getCurrentGameState().getTextToType().getFullText();
        int totalWord = fullText.split(" ").length;

        int percentage = (value * 100 / totalWord);
        bar.setValue(percentage);
        bar.setStringPainted(true);
        panel.add(bar, gb);
        storeBar.put(key, bar);
        key++;
      }
    }
  }

  /** provides Text Box to display text that needs to be typed. */
  class Type implements PropertyChangeListener {

    JLabel textToType = new JLabel("here appears the text that needs to be typed.");

    Type(MultiplayerTypeRacerClient game) {
      gbc.weighty = 1;
      gbc.gridx = 0;
      gbc.gridy = 2;
      gbc.gridheight = 1;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      textToType.setOpaque(true);
      textToType.setBackground(Color.LIGHT_GRAY);
      Border border = BorderFactory.createLineBorder(Color.BLACK);
      textToType.setBorder(border);
      add(textToType, gbc);
      adjustTo(game);
      game.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("GameState")) {
        adjustTo((MultiplayerTypeRacerClient) evt.getNewValue());
      }
    }

    private void adjustTo(final MultiplayerTypeRacerClient game) {
      String text = game.getCurrentGameState().getTextToType().getFullText();
      String[] words = text.split("\\s+");
      int arrayLength = words.length;
      int count =
          game.getCurrentGameState().getPlayerStateMap().get(game.getMyName()).getWordProgress()
              + 1;
      String[] subset = Arrays.copyOfRange(words, count, arrayLength);
      String subsetToString = String.join(" ", subset);
      String[] correctWords = Arrays.copyOfRange(words, 0, count);
      String correctWordsString = String.join(" ", correctWords);
      textToType.setText("<html><p style=\"width:700px\">" + text + "</p></html>");

      textToType.setText(
          "<html><p style=\"width:700px\"><font color = green>"
              + correctWordsString
              + " "
              + "</font><font color = black>"
              + subsetToString
              + "</p></font></html>");
    }
  }

  /**
   * provides TextField for user to type the above text including display of wrongly typed word and
   * focus listener.
   */
  class TypeText implements PropertyChangeListener {

    String userInput;

    TypeText(Controller controller, MultiplayerTypeRacerClient multiplayer) {
      gbc.weighty = 0;
      gbc.gridheight = 2;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.gridx = 0;
      gbc.gridy = 3;
      text.setBackground(Color.WHITE);
      text.setEnabled(false);
      add(text, gbc);

      text.addFocusListener(
          new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
              text.setForeground(Color.BLACK);
              text.setText("");
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
              text.setForeground(Color.BLACK);
              requestFocus();
            }
          });
      adjustTo(multiplayer);
      adjustEnable(multiplayer);
      multiplayer.addPropertyChangeListener(this);
      initControl(controller, multiplayer);
    }

    /** key bindings for the space bar enables check if typed word is correct. */
    public void initControl(Controller controller, MultiplayerTypeRacerClient multiplayer) {
      InputMap im = text.getInputMap();
      ActionMap am = text.getActionMap();
      im.put(KeyStroke.getKeyStroke("SPACE"), "space");

      am.put(
          "space",
          new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
              userInput = text.getText();
              controller.checkType(multiplayer, userInput);
            }
          });
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
      if (propertyChangeEvent.getPropertyName().equals("GameState")) {
        adjustEnable((MultiplayerTypeRacerClient) propertyChangeEvent.getNewValue());
      } else if (propertyChangeEvent.getPropertyName().equals("countdown")) {
        adjustEnable((MultiplayerTypeRacerClient) propertyChangeEvent.getNewValue());
      }
    }

    private void adjustTo(final MultiplayerTypeRacerClient player) {

      boolean checkInput = player.isTypedCorrect();
      text.setForeground(Color.BLACK);

      if (checkInput) {
        text.setText(" ");

        addAncestorListener(
            new AncestorListener() {
              @Override
              public void ancestorAdded(AncestorEvent ancestorEvent) {
                text.requestFocus();
              }

              @Override
              public void ancestorRemoved(AncestorEvent ancestorEvent) {}

              @Override
              public void ancestorMoved(AncestorEvent ancestorEvent) {}
            });
      } else {
        text.setText(player.getUserInput());
        text.setForeground(Color.RED);
      }
    }

    private void adjustEnable(MultiplayerTypeRacerClient player) {
      boolean isEnable = (player.getCountdown() == 0);
      text.setEnabled(isEnable);
    }
  }

  /**
   * displays button to start countdown.
   *
   * @param controller controller passes on objects
   * @param model model provides access to multiplayertyperacer
   */
  public void startCountdown(Controller controller, MultiplayerTypeRacerClient model) {
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.gridwidth = GridBagConstraints.RELATIVE;
    JButton countdown = new JButton("Start Countdown");
    countdown.addActionListener(e -> controller.startCoundown(model));
    add(countdown, gbc);
  }

  /**
   * displays button for a new game.
   *
   * @param controller controller passes on objects
   * @param model model provides access to multiplayertyperacer
   */
  public void buttonNewGame(Controller controller, MultiplayerTypeRacerClient model) {
    gbc.gridx = 1;
    gbc.gridy = 7;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.gridwidth = GridBagConstraints.RELATIVE;
    JButton newGame2 = new JButton("New Game");
    newGame2.addActionListener(e -> controller.requestNewGame(model));
    add(newGame2, gbc);
  }

  /**
   * displays button to quit.
   *
   * @param controller controller passes on objects
   * @param model model provides access to multiplayertyperacer
   */
  public void buttonQuit(Controller controller, MultiplayerTypeRacerClient model) {
    gbc.gridx = 3;
    gbc.gridy = 7;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.gridwidth = GridBagConstraints.RELATIVE;
    JButton quit = new JButton("Quit Game");
    quit.addActionListener(e -> controller.requestQuit(model));
    add(quit, gbc);
  }

  /** returns a new game screen object. */
  public static GameScreen create(final Controller controller, MultiplayerTypeRacerClient model) {
    return new GameScreen(controller, model);
  }
}
