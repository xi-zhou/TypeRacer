package client.model;

import client.view.game.Statistic;
import com.google.gson.Gson;
import communication.Connection;
import communication.messages.FinishedWordRequest;
import communication.messages.GameFinishedNotification;
import communication.messages.JoinGameRequest;
import communication.messages.JoinGameResponse;
import communication.messages.NewGameRequest;
import communication.messages.PlayerFinishedWord;
import communication.messages.PlayerJoinedNotification;
import communication.messages.PlayerLeftNotification;
import communication.messages.StartGameRequest;
import communication.messages.UpdateCountdownNotification;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import server.model.GameState;

/**
 * Represents the game logic for a multiplayer game on the client side. This includes communication
 * with the server.
 */
public class MultiplayerTypeRacerClient implements Closeable {
  private final ExecutorService executorService;
  private Connection connection;
  private static final int PORT = 4441;

  private String gameId;
  private String myName;

  private GameState currentGameState;

  private String fullText;
  private String userInput;
  private int totalWord;
  private boolean isTypedCorrect;
  private int wrongCounter;
  private int typeCounter;
  private int countdown;

  private Instant startTime;
  private Instant stopTime;
  private int wpm;

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public long getCountdown() {
    return countdown;
  }

  public String getGameId() {
    return gameId;
  }

  public String getUserInput() {
    return userInput;
  }

  public boolean isTypedCorrect() {
    return isTypedCorrect;
  }

  public String getMyName() {
    return myName;
  }

  public GameState getCurrentGameState() {
    return currentGameState;
  }

  public MultiplayerTypeRacerClient() {
    executorService = Executors.newSingleThreadExecutor();
  }

  /**
   * Return an instance of class MultiplayerTypeRacerClient.
   *
   * @return multiplayerTypeRacerClient
   */
  public static MultiplayerTypeRacerClient create() {
    return new MultiplayerTypeRacerClient();
  }

  /**
   * Send a message to the server to start a new game. Upon successful creation and joining,
   * listeners are notified.
   *
   * @param userName User name for the player
   * @param serverAddress Address of the game server
   * @throws IOException If there is a communication problem
   * @throws DuplicatePlayerNameException If the player name already exists for the game id
   * @throws UnknownGameIdException If the gameid is not recognized by server
   */
  public void newGame(String userName, String serverAddress)
      throws IOException, DuplicatePlayerNameException, UnknownGameIdException {
    myName = userName;
    countdown = -1;
    typeCounter = 0;
    wrongCounter = 0;
    connection = establishConnection(serverAddress, PORT);

    String json = new Gson().toJson(new NewGameRequest(userName));
    connection.writeJson(json);

    finishConnectionSetup();

  }

  /** throw a exception when the user name is already existed. */
  public static class DuplicatePlayerNameException extends Exception {}


  private static Connection establishConnection(final String serverAddress, final int port)
      throws IOException {
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress(serverAddress, port));
    return new Connection(socket);
  }

  /**
   * Add a Property change listener to this class.
   *
   * @param changeListener The change listner
   */
  public void addPropertyChangeListener(PropertyChangeListener changeListener) {
    support.addPropertyChangeListener(changeListener);
  }

  private void finishConnectionSetup()
      throws IOException, DuplicatePlayerNameException, UnknownGameIdException {

    String json = connection.readJson();
    Gson gson = new Gson();
    if (json.contains("PlayerNameAlreadyExistsResponse")) {
      throw new DuplicatePlayerNameException();
    } else if (json.contains("GameDoesNotExistsResponse")) {
      throw new UnknownGameIdException();
    } else if (json.contains("JoinGameResponse")) {
      isTypedCorrect = true;
      JoinGameResponse joinGameResponse = gson.fromJson(json, JoinGameResponse.class);
      currentGameState = joinGameResponse.getCurrentGameState();
      gameId = joinGameResponse.getGameId() + "";
      fullText = currentGameState.getTextToType().getFullText();
      totalWord = fullText.split(" ").length;
      if (myName.equals("")) {
        myName = "unnamed";
      }
      executorService.execute(() -> {
        while (true) {
          try {
            receiveServerMessage();
          } catch (IOException e) {
            break;
          }
        }
      });
    }

  }

  private void receiveServerMessage() throws IOException {
    String json = connection.readJson();
    Gson gson = new Gson();
    if (json.contains("UpdateCountdownNotification")) {
      handleUpdateCountdownNotification(gson.fromJson(json, UpdateCountdownNotification.class));
    } else if (json.contains("PlayerJoinedNotification")) {
      handlePlayerJoinedMessage(gson.fromJson(json, PlayerJoinedNotification.class));
    } else if (json.contains("PlayerLeftNotification")) {
      handlePlayerLeftMessage(gson.fromJson(json, PlayerLeftNotification.class));
    } else if (json.contains("PlayerFinishedWord")) {
      handlePlayerFinishedMessage(gson.fromJson(json, PlayerFinishedWord.class));
    } else if (json.contains("GameFinishedNotification")) {
      handleGameFinished(gson.fromJson(json, GameFinishedNotification.class));
    } else {
      throw new AssertionError("Unknown communication");
    }
  }

  private void handleGameFinished(GameFinishedNotification message) {
    currentGameState = message.getFinishedGameState();

    long gameTime = Duration.between(startTime, stopTime).toSeconds();
    double accuracy = 100 * (double) (totalWord - wrongCounter) / (double) totalWord;
    accuracy = Math.round(accuracy * 100);
    accuracy = accuracy / 100;
    new Statistic(wpm, gameTime, accuracy, typeCounter == totalWord);

    try {
      close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleUpdateCountdownNotification(UpdateCountdownNotification message) {
    countdown = (int) message.getCountdown();
    if (countdown == 0) {
      startTime = Instant.now();
      isTypedCorrect = true;
    }
    support.firePropertyChange("countdown", null, this);
  }

  private void handlePlayerFinishedMessage(PlayerFinishedWord message) {
    currentGameState = message.getGameStateAfterFinishedWord();
    isTypedCorrect = true;
    support.firePropertyChange("GameState", null, this);
  }

  private void handlePlayerLeftMessage(PlayerLeftNotification message) {
    currentGameState = message.getCurrentGameState();
    support.firePropertyChange("GameState", null, this);
  }

  private void handlePlayerJoinedMessage(PlayerJoinedNotification message) {
    isTypedCorrect = true;
    currentGameState = message.getCurrentGameState();
    support.firePropertyChange("GameState", null, this);
  }



  /**
   * Check if the word is conform to the Text. if correct also calculate wpm
   *
   * @param userInput The word typed by user.
   */
  public void handleUserTyped(String userInput) {
    this.userInput = userInput;
    String currentWord = currentGameState.getTextToType().getWord(typeCounter);



    if (!userInput.equals(currentWord)) {
      isTypedCorrect = false;
      wrongCounter++;
    } else {
      isTypedCorrect = true;
      wpm = calculateWpm();
      typeCounter++;
      String json = new Gson().toJson(new FinishedWordRequest(wpm));
      try {
        connection.writeJson(json);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  /** Calculate word per minute, start time is when countdown became zero. */
  public int calculateWpm() {

    stopTime = Instant.now();
    if (typeCounter == 0) {
      return wpm = 12;
    } else {
      long seconds = Duration.between(startTime, stopTime).toSeconds();
      int wordProgress = getCurrentGameState().getPlayerStateMap().get(myName).getWordProgress();
      int wpm = (int) (wordProgress * 60 / seconds);
      return wpm;
    }
  }

  /**
   * Close the socket connection.
   */
  @Override
  public void close() throws IOException {
    executorService.shutdownNow();
    connection.close();
  }

  /**
   * Join type racer game with a given id.
   *
   * @param userName User name for the player
   * @param serverAddress Address of the game server
   * @throws IOException If there is a communication problem
   * @throws DuplicatePlayerNameException If the player name already exists for the game id
   */
  public void joinGame(final String userName, final String serverAddress, final int gameId)
      throws IOException, UnknownGameIdException, DuplicatePlayerNameException {
    myName = userName;
    connection = establishConnection(serverAddress, PORT);
    JoinGameRequest joinGameRequest = new JoinGameRequest(gameId, userName);

    Gson gson = new Gson();
    String json = gson.toJson(joinGameRequest);
    connection.writeJson(json);
    finishConnectionSetup();
    startTime = Instant.now();
    typeCounter = 0;
    isTypedCorrect = true;
  }

  /**
   * send server a Notification,that now can start a countdown.
   *
   * @throws IOException If there is a communication problem
   */
  public void startCountdown() throws IOException {
    StartGameRequest startGameRequest = new StartGameRequest();
    Gson gson = new Gson();
    String json = gson.toJson(startGameRequest);
    connection.writeJson(json);
  }

  /** When a given gameId is not known then throw out exception. */
  public static class UnknownGameIdException extends Exception {}
  
}
