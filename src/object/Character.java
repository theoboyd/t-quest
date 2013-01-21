package object;

import module.CharacterProperties;
import module.DrawProperties;

public abstract class Character extends MoveableItem {
  CharacterProperties p;
  public Character(String name, DrawProperties draw, CharacterProperties p) {
    super(name, draw);
    this.p = p;
  }
  
  public CharacterProperties characterProperties() {
    return p;
  }
}