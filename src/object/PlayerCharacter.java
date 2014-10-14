package object;

import module.CharacterProperties;
import module.DrawProperties;
import display.ColourPalette;

public class PlayerCharacter extends Character {
  public PlayerCharacter(String name, DrawProperties draw, CharacterProperties p) {
    super(name, draw, p);
    displayColour = ColourPalette.GAME_PLAYER_WHITE;
    buildDisplayRegion();
  }
}
