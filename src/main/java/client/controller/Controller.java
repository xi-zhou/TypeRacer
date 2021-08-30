package client.controller;

import client.model.MultiplayerTypeRacerClient;
import client.view.SwingGui;
import java.io.IOException;
import java.util.Objects;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/** Controller class for the Type Racer game, following the MVC pattern. */
public class Controller {
  // private MultiplayerTypeRacerClient model;
  private SwingGui view;

  /** Initializes and starts the user interface. */
  public void start() {
    view = new SwingGui(this);
    view.makeVisible();
  }


  /** Performs the actions when a player want to quit the game and click on "X" on the Window. */
  public void closeWindow(MultiplayerTypeRacerClient model) {

    final SwingWorker<Void, Void> doClose = new SwingWorker<>() {
      @Override
      protected Void doInBackground() throws Exception {
        try {
          model.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    };
    doClose.execute();

  }


  /**
   * Performs the actions when a player request start a new game.
   *
   * @param userName The desired user name
   * @param serverAddress The address of the game server
   */
  public void requestStartGame(String userName, String serverAddress) {
    final SwingWorker<Void, Void> connect = new SwingWorker<>() {
      MultiplayerTypeRacerClient multiplayerModel;
      private boolean isError;

      @Override
      protected Void doInBackground() throws Exception {
        try {
          multiplayerModel = MultiplayerTypeRacerClient.create();

          multiplayerModel.newGame(userName, serverAddress);
          registerModelAtView(multiplayerModel);
        } catch (IOException e) {
          e.printStackTrace();
          view.showError("Could not establish a connection.");
          isError = true;
        } catch (MultiplayerTypeRacerClient.DuplicatePlayerNameException e) {
          view.showDuplicatePlayerNameMessage();
          isError = true;
        }
        return null;
      }

      @Override
      protected void done() {
        if (isError) {
          view.setStartScreen();
        }
        view.setNewMultiPlayerGameScreen(multiplayerModel);
      }
    };
    connect.execute();
  }


  /**
   * Register TypeRacer model at GUI.
   *
   * @param model Type Racer game
   *
   */
  protected void registerModelAtView(MultiplayerTypeRacerClient model) {
    Objects.requireNonNull(model, "Trying to register with model == null");
    view.setNewMultiPlayerGameScreen(model);
  }


  /**
   * Performs the actions when player want to quit the game during the play.
   */
  public void requestQuit(MultiplayerTypeRacerClient multi) {

    SwingWorker<Void, Void> doForfeit = new SwingWorker<>() {
      private MultiplayerTypeRacerClient model = multi;

      @Override
      protected Void doInBackground() {

        try {
          model.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      protected void done() {
        model = null;
        view.dispose();
      }

    };
    doForfeit.execute();
  }


  /**
   * Performs the actions when a player request join a game with a given id.
   *
   * @param userName The desired user name
   * @param serverAddress The address of the game server
   * @param gameId The id of the game
   */
  public void joinMultiplayerGame(String userName, String serverAddress, String gameId) {
    final SwingWorker<Void, Void> connect = new SwingWorker<>() {
      private MultiplayerTypeRacerClient multiplayerModel;
      private boolean isError;

      @Override
      protected Void doInBackground() throws Exception {
        int gameIdInt = -1;
        try {
          gameIdInt = Integer.parseInt(gameId);
        } catch (NumberFormatException e) {
          SwingUtilities.invokeLater(() -> view.showError("Invalid game id. Number is required!"));
          return null;
        }
        try {
          multiplayerModel = MultiplayerTypeRacerClient.create();
          multiplayerModel.joinGame(userName, serverAddress, gameIdInt);
          registerModelAtView(multiplayerModel);

        } catch (IOException e) {
          e.printStackTrace();
          view.showError("Could not establish a connection.");
          isError = true;
        } catch (MultiplayerTypeRacerClient.UnknownGameIdException e) {
          view.showUnknownGameIdMessage();
          isError = true;
        } catch (MultiplayerTypeRacerClient.DuplicatePlayerNameException e) {
          view.showDuplicatePlayerNameMessage();
          isError = true;
        }
        return null;
      }

      @Override
      protected void done() {
        if (!isError) {
          view.setNewMultiPlayerGameScreen(multiplayerModel);
        } else {
          view.setStartScreen();
        }
      }

    };
    connect.execute();
  }

  /**
   * Performs the actions when player want to start a NEW game DURING the play.
   */
  public void requestNewGame(MultiplayerTypeRacerClient multi) {
    new SwingWorker<Void, Void>() {
      private MultiplayerTypeRacerClient model = multi;

      @Override
      protected Void doInBackground() {
        try {
          model.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      protected void done() {
        model = null;
        view.setStartScreen();
      }
    }.execute();
  }

  /**
   * Perform the action when user types a word.
   *
   * @param userInput The word typed by user
   *
   */
  public void checkType(MultiplayerTypeRacerClient model, String userInput) {

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() {
        model.handleUserTyped(userInput);
        return null;
      }
      
      @Override
      protected void done() {
        view.setNewMultiPlayerGameScreen(model);
      }
    }.execute();
  }

  /**
   * Perform the action when user want to begin the game and start coundown.
   *
   * @param model The multiplayer typeRacer client
   */
  public void startCoundown(MultiplayerTypeRacerClient model) {

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() {
        try {
          model.startCountdown();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      protected void done() {
        view.setNewMultiPlayerGameScreen(model);
      }
    }.execute();
  }


  /**
   * Perform the action when user click on Instruction buttion on start screen.
   */
  public void showInstruction() {
    view.showInstruction();
  }

}
