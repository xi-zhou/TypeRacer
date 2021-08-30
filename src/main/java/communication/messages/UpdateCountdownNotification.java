package communication.messages;

/**
 * Message send to client with the updated countdown.
 */
public class UpdateCountdownNotification {
  private final String messageType = "UpdateCountdownNotification";
  private final long countdown;

  public UpdateCountdownNotification(long countdown) {
    this.countdown = countdown;
  }

  public long getCountdown() {
    return countdown;
  }

  public String getMessageType() {
    return messageType;
  }
}
