package module;

import java.awt.Color;
import java.awt.Point;

import display.Map;

public class DrawProperties {
  public Point location;
  public String examineText;
  public Map parent;
  public Color displayColour;
  public int zIndex;

  public DrawProperties(Point location, String examineText, Map parent, Color displayColour, int zIndex) {
    this.location = location;
    this.examineText = examineText;
    this.parent = parent;
    this.displayColour = displayColour;
    this.zIndex = zIndex;
  }
}
