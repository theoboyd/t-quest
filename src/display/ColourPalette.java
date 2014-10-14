package display;

import java.awt.Color;

public class ColourPalette {
  public static final Color DEBUG_RED            = new Color(255, 0, 0);

  public static final Color UI_APPLET            = new Color(128, 128, 128);
  public static final Color UI_TRANSLUCENT_HIGH  = new Color(0, 0, 0, 64);
  public static final Color UI_TRANSLUCENT_MID   = new Color(0, 0, 0, 128);
  public static final Color UI_TRANSLUCENT_LOW   = new Color(0, 0, 0, 196);
  public static final Color UI_DEFAULT           = new Color(40, 64, 180);
  public static final Color UI_BUTTON_DEFAULT    = new Color(12, 48, 128);
  public static final Color UI_BUTTON_HIGHLIGHT  = new Color(24, 60, 164);
  public static final Color UI_BUTTON_SHADOW     = new Color(0, 30, 100);

  public static final Color GENERAL_BLACK        = new Color(0, 0, 0);
  public static final Color GENERAL_WHITE        = new Color(255, 255, 255);
  public static final Color GENERAL_TRANSPARENT  = new Color(0, 0, 0, 0);

  public static final Color TEXTURE_FOREST_GREEN = new Color(64, 255, 100);

  public static final Color GAME_ITEM_RED        = new Color(200, 20, 25);
  public static final Color GAME_NPC_YELLOW      = new Color(110, 110, 25);
  public static final Color GAME_PLAYER_WHITE    = new Color(220, 220, 220);

  public static Color parseColour(String string) {
    String[] rgb = string.split(",");
    int red = Integer.parseInt(rgb[0]);
    int green = Integer.parseInt(rgb[1]);
    int blue = Integer.parseInt(rgb[2]);
    return new Color(red, green, blue);
  }
}