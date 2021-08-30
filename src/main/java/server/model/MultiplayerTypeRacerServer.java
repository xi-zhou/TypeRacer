package server.model;

/**
 * Represents a typeracer game.
 */
public class MultiplayerTypeRacerServer {

  private final int id;
  private final GameState state;
  private long startTime;

  /**
   * Create a new game with the given id.
   *
   * @param id The game id.
   */
  public MultiplayerTypeRacerServer(int id) {
    state = new GameState(new TextDatabase().getText());
    this.id = id;
  }

  /**
   * Updates the game progress of a specific player.
   *
   * @param username name of the player.
   * @param wpm the to be updated wpm progress.
   */
  public void updatePlayerState(String username, int wpm) {
    state.getPlayerStateMap().get(username).updateProgress(wpm);
  }

  /**
   * Calculates the current countdown value after the timer is started.
   *
   * @return the countdown value as a long in seconds.
   */
  public long getCountdownValue() {
    long secondsPassed = (System.currentTimeMillis() - startTime) / 1000;
    return state.getCountdownInSeconds() - secondsPassed;
  }


  /**
   * Adds a new player with the given name to the game.
   *
   * @param playername the name of the player.
   */
  public synchronized void addNewPlayer(final String playername) {
    state.getPlayerStateMap().put(playername, new PlayerState());
  }

  /**
   * Removes the player with the given name. The current player is updated if the given player was
   * the current one. The method does nothing if no player with the given name exists in the game.
   *
   * @param playername the name of the player.
   */
  public synchronized void removePlayer(final String playername) {
    if (state.getPlayerStateMap().containsKey(playername)) {
      state.getPlayerStateMap().remove(playername);
    } else {
      // Possibly the player has already been removed
      return;
    }
  }

  /**
   * Returns how many player are in the game.
   *
   * @return the size as int.
   */
  public synchronized int getNumPlayers() {
    return state.getPlayerStateMap().size();
  }

  /**
   * Returns the id of the game.
   *
   * @return the id as int.
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the current state of the game.
   *
   * @return the state as a Gamestate class.
   */
  public GameState getState() {
    return state;
  }

  /**
   * Sets the startTime for the Countdown.
   */
  public void prepareCountdown() {
    startTime = System.currentTimeMillis();
  }
}
