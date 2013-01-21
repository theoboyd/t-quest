package object;

import module.DrawProperties;
import action.DropAction;
import action.PickupAction;
import display.ColourPalette;

public class Item extends MoveableItem {

  public Item(String name, DrawProperties draw) {
    super(name, draw);
    displayColour = ColourPalette.GAME_ITEM_RED;
    buildDisplayRegion();
  }

  public String pickUp() {
    // Try conditions
    PlayerCharacter pc = parent.loggedInUser.playerCharacter;
    if (rule.RuleManager.characterCanPickUp(pc, this)) {
      parent.loggedInUser.playerCharacter.p.inventory.add(this);
      parent.OFF_MAP_INVENTORY.x += parent.INVENTORY_OFFSET;
      moveTo(parent.OFF_MAP_INVENTORY.x, parent.OFF_MAP_INVENTORY.y);
      displayRegion.actions.removeFirst();
      displayRegion.actions.addFirst(new DropAction(this));
      return "Picked up " + name.toLowerCase();
    } else {
      return "Could not pick up item";
    }
  }

  public String drop() {
    // Try conditions
    PlayerCharacter pc = parent.loggedInUser.playerCharacter;
    if (rule.RuleManager.characterCanDrop(pc, this)) {
      parent.loggedInUser.playerCharacter.p.inventory.remove(this);
      parent.OFF_MAP_INVENTORY.x -= parent.INVENTORY_OFFSET;
      moveTo(location.x, location.y);
      displayRegion.actions.removeFirst();
      displayRegion.actions.addFirst(new PickupAction(this));
      return "Dropped " + name.toLowerCase();
    } else {
      return "Could not drop item";
    }
  }
}
