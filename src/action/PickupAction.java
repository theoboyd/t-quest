package action;

import object.Item;
import display.Map;

public class PickupAction extends Action {
  public Item item;

  public PickupAction(Item i) {
    super("Pick up " + i.name.toLowerCase());
    this.item = i;
  }

  @Override
  public String run(Map parent) {
    return item.pickUp();
  }
}
