package action;

import java.awt.Point;

import display.Map;
import display.Region;

public class MoveAction extends Action {
  Region region;
  int    x;
  int    y;
  Map    map;

  public MoveAction(Region r, int x, int y, Map m) {
    super("Move here");
    this.region = r;
    this.x = x;
    this.y = y;
    this.map = m;
  }

  @Override
  public String run(Map parent) {
    map.movePlayer(new Point(x, y));
    return "";
  }
}