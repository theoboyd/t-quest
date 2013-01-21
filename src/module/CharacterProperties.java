package module;

import object.Item;
import util.ExtList;

public class CharacterProperties {
  public ExtList<Item> inventory;
  public ExtList<CharacterProperty> propertyList;
  
  public CharacterProperties(ExtList<Item> inventory, ExtList<CharacterProperty> propertyList) {
    this.inventory = inventory;
    this.propertyList = propertyList;
  }
}
