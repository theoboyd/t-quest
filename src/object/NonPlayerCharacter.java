package object;

import display.ColourPalette;
import module.CharacterProperties;
import module.DrawProperties;

public class NonPlayerCharacter extends Character {
  public NonPlayerCharacter(String name, DrawProperties draw, CharacterProperties p) {
    super(name, draw, p);
    displayColour = ColourPalette.GAME_NPC_YELLOW;
    buildDisplayRegion();
  }
}
