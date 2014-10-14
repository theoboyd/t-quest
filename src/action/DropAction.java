package action;

import object.Item;
import display.Map;

public class DropAction extends Action {
  public Item item;

  public DropAction(Item i) {
    super("Drop " + i.name.toLowerCase());
    this.item = i;
  }

  @Override
  public String run(Map parent) {
    return item.drop();
  }
}
