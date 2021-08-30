package communication.messages;

/**
 * Message to be sent to the server if a client finished a word.
 */
public class FinishedWordRequest {


  private final int newWpmEntry;
  private final String messageType = "FinishedWordRequest";

  public FinishedWordRequest(int newWpmEntry) {
    this.newWpmEntry = newWpmEntry;
  }

  public int getNewWpmEntry() {
    return newWpmEntry;
  }

  public String getMessageType() {
    return messageType;
  }
}
