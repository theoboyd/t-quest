package util;

public class TQDException extends Exception {
  private static final long serialVersionUID = -5167911106513600814L;

  public TQDException(String arg0, String fileName) {
    super("The tQuest Data file reader encountered an error: " + arg0 + " [" + fileName + "]");
  }
}
