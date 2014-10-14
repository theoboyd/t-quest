package display;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import object.Item;

public class MapClick implements MouseListener {
  Region  r;
  boolean isButton;
  Item item = null;

  @Override
  public void mouseClicked(MouseEvent e) {
    Map m = ((Map) e.getSource());
    m.mouseX = e.getX();
    m.mouseY = e.getY();
    r = m.getRegionUnderPoint(m.mouseX, m.mouseY);
    isButton = (r.getClass() == Button.class);
    
    item = null;
    for (Item i : m.data.items) {
      if (i.displayRegion == r) {
        item = i;
        break;
      }
    }

    if (e.getButton() == 1) {
      // Left click
      // Is it a menu option from the right click menu?
      if (m.contextLayer.visible) {
        r = m.getRegionUnderPoint(m.contextX, m.contextY); // Get the region underneath the menu
        // m.contextRegion.xpoints[0], contextRegion.ypoints[0]
        r.secondaryClick(m.getCMIUnderMouse());
      } else {
        r.leftClick();
      }
      m.hideRightClick();
    } else
      if (e.getButton() == 3) {
        // Right click
        if (!isButton) {
          m.showRightClick(r.actions, m.mouseX, m.mouseY);
        }
      } else {
        // No middle click functionality used
      }
  }

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mousePressed(MouseEvent e) {}

  @Override
  public void mouseReleased(MouseEvent e) {}
}
