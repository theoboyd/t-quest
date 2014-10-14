package display;

import java.awt.Color;

import action.Action;

import util.ExtList;

/**
 * A consistently-styled UI element with a click action.
 */
public class Button extends Region {
  private static final long serialVersionUID = 3760093907741478761L;
  private Color             hoverColour;
  private Color             downColour;

  public Button(Map parent, int[] xs, int[] ys, Action onClick, int zIndex) {
    super(new ExtList<Action>(onClick), parent, xs, ys, ColourPalette.UI_BUTTON_DEFAULT, onClick.name, "", zIndex);
    this.hoverColour = ColourPalette.UI_BUTTON_HIGHLIGHT;
    this.downColour = ColourPalette.UI_BUTTON_SHADOW;
  }

  public Color hoverColour() {
    return hoverColour;
  }

  public Color downColour() {
    return downColour;
  }
}
