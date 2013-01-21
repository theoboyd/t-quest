package util;

import java.awt.FontMetrics;
import java.awt.Graphics;

import action.Action;

public class MultilinePrint {
  public static void drawString(Graphics g, String text, int x, int y) {
    y -= 4;
    for (String line : text.split("\n")) {
      g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
  }

  public static int maxActionName(Graphics g, ExtList<Action> actions) {
    int max = 0;
    for (Action a : actions) {
      int aWidth = stringWidth(g, a.toString());
      if (aWidth > max) {
        max = aWidth;
      }
    }
    return max;
  }
  
  public static String getStringBetweenQuotes(String s, String quoteSymbol) {
    return s.split("" + quoteSymbol + "", 3)[1];
  }
  
  public static int stringWidth(Graphics g, String s) {
    FontMetrics fm = g.getFontMetrics();
    return fm.stringWidth(s);
  }
}
