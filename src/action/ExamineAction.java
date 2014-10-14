package action;

import object.MoveableItem;
import display.Map;
import display.Region;

public class ExamineAction extends Action {
  public Region       region;
  public MoveableItem moveableItem;

  public ExamineAction(Region r) {
    super("Examine " + r.toString().toLowerCase());
    this.region = r;
  }

  public ExamineAction(MoveableItem mi) {
    super("Examine " + mi.name.toLowerCase());
    this.moveableItem = mi;
  }

  @Override
  public String run(Map parent) {
    if (moveableItem == null) {
      return region.examineText;
    } else {
      return moveableItem.displayRegion.examineText;
    }
  }
}
