package display;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MapHover implements MouseMotionListener {
  Button  hoveredButton;
  boolean isButton;

  @Override
  public void mouseDragged(MouseEvent e) {}

  @Override
  public void mouseMoved(MouseEvent e) {
    Map m = ((Map) e.getSource());
    m.mouseX = e.getX();
    m.mouseY = e.getY();
    Region r = ((Map) e.getSource()).getRegionUnderPoint(m.mouseX, m.mouseY);
    isButton = (r.getClass() == Button.class);
    if (isButton) hoveredButton = (Button) r;
    String hudLine = "";

    if (r.actions.size() >= 1) {
      hudLine = r.actions.get(0).name;
    }
    if (r.actions.size() > 1) {
      hudLine += " (" + (r.actions.size() - 1) + " more)";
    }
    m.printHUDLine(hudLine);

    if (m.DEBUG_MODE) {
      if (!r.equals(m.statusRegion_InfoBox)) {
        m.printStatusLine("Mouse over (" + m.mouseX + ", " + m.mouseY + "), in region " + r.toString());
      }
    }
    if (m.DEBUG_MODE) {
      if (r.equals(m.contextRegion)) {
        m.printStatusLine("Context menu item " + m.getCMIUnderMouse());
      }
    }

    if (!isButton && hoveredButton != null) {
      hoveredButton.setColour(hoveredButton.defaultColour());
    } else
      if (isButton) {
        hoveredButton.setColour(hoveredButton.hoverColour());
      }
  }
}