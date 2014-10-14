package action;

import display.Map;

public abstract class Action {
  public String name    = "";
  public String message = "";

  public Action(String name) {
    this.name = name;
  }

  public abstract String run(Map parent); // Should ordinarily set the message string

  public String toString() {
    // As needed by ExtList.
    return name;
  }
}
