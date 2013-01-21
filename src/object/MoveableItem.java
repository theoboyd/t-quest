package object;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;

import module.DrawProperties;

import display.Map;
import display.Region;

public abstract class MoveableItem {
  public String name;
  public Point  location;
  public String examineText;
  public Region displayRegion;
  public Color  displayColour;
  protected Map parent;
  private int   radius = 3;
  public int    zIndex;

  public MoveableItem(String name, DrawProperties draw) {
    this.name = name;
    this.location = draw.location;
    this.examineText = draw.examineText;
    this.parent = draw.parent;
    this.displayColour = draw.displayColour;
    this.zIndex = draw.zIndex;
    buildDisplayRegion();
  }

  public void buildDisplayRegion() {
    Polygon p = getCappedPosition(location.x, location.y);
    displayRegion = new Region(parent, p.xpoints, p.ypoints, displayColour, name, examineText, zIndex);
  }

  public void moveTo(int x, int y) {
    location.x = x;
    location.y = y;
    Polygon p = getCappedPosition(location.x, location.y);
    displayRegion = new Region(parent, p.xpoints, p.ypoints, displayColour, name, examineText, zIndex);
  }

  private Polygon getCappedPosition(int x, int y) {
    Point a = new Point();
    Point b = new Point();
    if (x <= 0) {
      a.x = 0;
      b.x = radius;
    } else
      if (x >= parent.width) {
        a.x = parent.width - radius;
        b.x = parent.width;
      } else {
        a.x = x - radius;
        b.x = x + radius;
      }

    if (y <= 0) {
      a.y = 0;
      b.y = radius;
    } else
      if (y >= parent.height) {
        a.y = parent.height - radius;
        b.y = parent.height;
      } else {
        a.y = y - radius;
        b.y = y + radius;
      }

    Polygon p = new Polygon(new int[] { a.x, a.x, b.x, b.x }, new int[] { a.y, b.y, b.y, a.y }, 4);
    return p;
  }
}
