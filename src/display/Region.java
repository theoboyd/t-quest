package display;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;

import action.Action;

import util.ExtList;
import util.MapDivision;

public class Region extends Polygon implements MapDivision {
  private static final long serialVersionUID = 331665334935905582L;
  private Color             colour, defaultColour;
  private String            displayText      = "";
  public String             examineText      = "";
  public ExtList<Action>    actions          = new ExtList<Action>();
  private Map               parent;
  public int                zIndex           = 0;

  public Region(ExtList<Action> actions, Map parent, int[] xs, int[] ys, Color c, String displayText,
      String examineText, int zIndex) {
    this.actions = actions;
    this.parent = parent;
    this.xpoints = xs;
    this.ypoints = ys;
    this.npoints = Math.max(this.xpoints.length, this.ypoints.length);
    this.colour = c;
    this.defaultColour = c;
    if (displayText != "") {
      this.setDisplayText(displayText);
    } else {
      System.err.println("Region does not have display text.");
    }
    this.examineText = examineText;
    this.zIndex = zIndex;
  }

  public Region(Map parent, int[] xs, int[] ys, Color c, String displayText, String examineText, int zIndex) {
    this.parent = parent;
    this.xpoints = xs;
    this.ypoints = ys;
    this.npoints = Math.max(this.xpoints.length, this.ypoints.length);
    this.colour = c;
    this.defaultColour = c;
    if (displayText != "") {
      this.setDisplayText(displayText);
    } else {
      System.err.println("Region does not have display text.");
    }
    this.examineText = examineText;
    this.zIndex = zIndex;
  }

  public void removeActionByName(String name) {
    for (int i = 0; i < actions.size(); i++) {
      if (actions.get(i).name == name) {
        actions.remove(i);
        return;
      }
    }
  }

  public void setColour(Color c) {
    this.colour = c;
    parent.drawMap();
  }

  public Color getColour() {
    return colour;
  }

  public Color defaultColour() {
    return defaultColour;
  }

  public void leftClick() {
    secondaryClick(0);
  }

  public void secondaryClick(int id) {
    try {
      parent.printStatusLine(actions.get(id).run(parent));
    } catch (Exception e) {
      // Ignore (debug below:)
      // System.out.println("Region " + name + " does not have an action with ID " + id + ".");
    }
  }

  public double getSignedArea() {
    double area = 0;

    for (int i = 0; i < npoints; i++) {
      int j = (i + 1) % npoints;
      area += xpoints[i] * ypoints[j];
      area -= ypoints[i] * xpoints[j];
    }
    area /= 2.0;

    return (area);
    // return(area < 0 ? -area : area); for unsigned
  }

  public double[] getCentroid() {
    double cx = 0, cy = 0;
    double area = getSignedArea();
    int i, j;

    double factor = 0;
    for (i = 0; i < npoints; i++) {
      j = (i + 1) % npoints;
      factor = (xpoints[i] * ypoints[j] - xpoints[j] * ypoints[i]);
      cx += (xpoints[i] + xpoints[j]) * factor;
      cy += (ypoints[i] + ypoints[j]) * factor;
    }
    area *= 6.0;
    factor = 1.0 / area;
    cx *= factor;
    cy *= factor;
    return new double[] { cx, cy };
  }

  public Point approxCentre() {
    double[] xy = getCentroid();
    return new Point((int) Math.round(xy[0]), (int) Math.round(xy[1]));
  }

  public String toString() {
    return displayText;
  }

  public void setDisplayText(String displayText) {
    this.displayText = displayText;
  }
}
