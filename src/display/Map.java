package display;

import java.applet.Applet;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collections;

import action.Action;

import module.CharacterProperty;
import object.Item;
import object.NonPlayerCharacter;
import object.PlayerCharacter;
import security.User;
import util.Data;
import util.ExtList;
import util.MultilinePrint;

/**
 * The main tQuest game class. Represents the entire game world (or map). Stores a list of {@link Layer} objects, each
 * with its own {@link Region}.
 */
public class Map extends Applet {
  private static final long    serialVersionUID       = -6996684955881174321L;
  public final boolean         DEBUG_MODE             = false;
  private static final User    DEBUG_USER             = new User("debug_user", "123456");

  private static final int     MAX_ZINDEX             = Integer.MAX_VALUE;
  private static final int     MESSAGE_ZINDEX         = MAX_ZINDEX - 1;
  private static final int     CONTEXT_ZINDEX         = MAX_ZINDEX - 2;
  private static final int     STATUS_ZINDEX          = MAX_ZINDEX - 3;
  private static final int     STATS_ZINDEX           = MAX_ZINDEX - 4;
  private static final int     INPUT_ZINDEX           = MAX_ZINDEX - 5;
  private static final int     MOVEABLE_ITEM_ZINDEX   = MAX_ZINDEX - 6;
  private static final int     BASE_ZINDEX            = 0;
  public int                   currZIndex             = 1;
  // WARNING Update settings, for these two dimensions:
  private static final int     MAP_WIDTH              = 1400;
  private static final int     MAP_HEIGHT             = 830;
  private static final int     INFO_BOX_LINES_VISIBLE = 2;
  private static final boolean UP                     = true;
  private static final boolean DOWN                   = false;
  public Point                 OFF_MAP_INVENTORY      = new Point(MAP_WIDTH - 100, MAP_HEIGHT + 100);   // TODO fix
  public int                   INVENTORY_OFFSET       = 10;
  public static final Point    OFF_MAP                = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
  private int                  INFO_BOX_POINTER       = 0;
  private ExtList<String>      infoBoxText;
  private Graphics2D           g;
  private BufferedImage        b;
  protected Data               data;
  private LayerComparator      lComp;
  private RegionComparator     rComp;
  // Special layers and regions:
  Layer                        statusLayer, contextLayer, baseLayer, moveableItemLayer, inputLayer, messageLayer,
      statsLayer;
  Region                       statusRegion_InfoBox, statusRegion_InfoBoxUpBtn, statusRegion_InfoBoxDnBtn,
      statusRegion_CloseBox, statusRegion_StatsButton, statsRegion, statusRegion_HUD, contextRegion, baseRegion,
      inputRegion, inputButton1, inputButton2, messageRegion;
  int                          mouseX                 = 0;
  int                          mouseY                 = 0;
  int                          contextX               = 0;
  int                          contextY               = 0;
  public int                   width                  = 0;
  public int                   height                 = 0;
  int                          statusDimension        = 16;
  public User                  loggedInUser;

  public Map() {
    // Prepare applet and add listeners
    infoBoxText = new ExtList<String>();
    MapClick mc = new MapClick();
    MapHover mh = new MapHover();
    addMouseListener(mc);
    addMouseMotionListener(mh);
    setSize(MAP_WIDTH, MAP_HEIGHT);
    width = getWidth();
    height = getHeight();
    resize(width, height);

    // Construct objects
    lComp = new LayerComparator();
    rComp = new RegionComparator();
    data = new Data(this);
    b = new BufferedImage(width, height, 1);
    g = b.createGraphics();
    g.setBackground(ColourPalette.UI_APPLET);
    g.setColor(ColourPalette.GENERAL_BLACK);

    // Wait for log in
    // TODO Finish log in mechanics
    loggedInUser = DEBUG_USER;
    loggedInUser.playerCharacter = data.players.get(0);
    movePlayer(loggedInUser.playerCharacter.location);

    finaliseMap();

    printStatusLine(" ", true);
    printStatusLine("=== Welcome to tQuest ===", true); // Must be easily noticeable when doing a looped scroll-through
    // of the info box
    drawMap();
  }

  private void setupContextLayer(int ZINDEX, boolean show, int[] regionXs, int[] regionYs, Color regionFill,
      String display, String examine) {
    // Adds one of the predefined layer-region combos to the map
    String name = ((display != "") ? display : "#" + ZINDEX); // Optional name
    contextLayer = new Layer(name + " layer", ZINDEX, show);
    contextRegion = new Region(this, regionXs, regionYs, regionFill, name + " region", examine, 0);
    contextLayer.regions.add(contextRegion);
    data.layers.add(contextLayer);
  }

  private void setupBaseLayer(int ZINDEX, boolean show, int[] regionXs, int[] regionYs, Color regionFill,
      String display, String examine) {
    // Adds one of the predefined layer-region combos to the map
    String name = ((display != "") ? display : "#" + ZINDEX); // Optional name
    baseLayer = new Layer(name + " layer", ZINDEX, show);
    baseRegion = new Region(this, regionXs, regionYs, regionFill, name + " region", examine, 0);
    baseLayer.regions.add(baseRegion);
    data.layers.add(baseLayer);
  }

  private void setupStatusLayer() {
    statusLayer = new Layer("Status layer", STATUS_ZINDEX, true);
    int infoBoxHeight = INFO_BOX_LINES_VISIBLE * statusDimension;
    statusRegion_InfoBox = new Region(this, new int[] { 0, 0, width - statusDimension, width - statusDimension },
        new int[] { height - (infoBoxHeight + statusDimension), height - statusDimension, height - statusDimension,
            height - (infoBoxHeight + statusDimension) }, ColourPalette.UI_TRANSLUCENT_MID, "Status region", "", 0);
    statusLayer.regions.add(statusRegion_InfoBox);

    statusRegion_InfoBoxUpBtn = new Region(this, new int[] { width - statusDimension, width - statusDimension, width,
        width }, new int[] { height - (3 * statusDimension), height - (2 * statusDimension),
        height - (2 * statusDimension), height - (3 * statusDimension) }, ColourPalette.UI_TRANSLUCENT_LOW, " ^", "", 1);
    statusRegion_InfoBoxUpBtn.actions.add(new Action("Scroll up") {
      public String run(Map parent) {
        scrollInfoBoxText(UP);
        drawMap();
        return "";
      }
    });
    statusLayer.regions.add(statusRegion_InfoBoxUpBtn);

    statusRegion_InfoBoxDnBtn = new Region(this, new int[] { width - statusDimension, width - statusDimension, width,
        width }, new int[] { height - (2 * statusDimension), height - statusDimension, height - statusDimension,
        height - (2 * statusDimension) }, ColourPalette.UI_TRANSLUCENT_LOW, " v", "", 2);
    statusRegion_InfoBoxDnBtn.actions.add(new Action("Scroll down") {
      public String run(Map parent) {
        scrollInfoBoxText(DOWN);
        drawMap();
        return "";
      }
    });
    statusLayer.regions.add(statusRegion_InfoBoxDnBtn);

    statusRegion_HUD = new Region(this, new int[] { 0, 0, width, width }, new int[] { 0, statusDimension,
        statusDimension, 0 }, ColourPalette.GENERAL_TRANSPARENT, "HUD region", "", 3);
    statusLayer.regions.add(statusRegion_HUD);

    statusRegion_CloseBox = new Button(this,
        new int[] { width - statusDimension, width - statusDimension, width, width }, new int[] { 0, statusDimension,
            statusDimension, 0 }, new Action("X") {
          public String run(Map parent) {
            System.exit(0);
            return "Quitting...";
          }
        }, 4);
    statusLayer.regions.add(statusRegion_CloseBox);

    int statsButtonWidth = 40;
    int statsButtonHeight = statusDimension;
    int topBarPadding = 3;
    statusRegion_StatsButton = new Button(this, new int[] { width - statsButtonWidth - statusDimension - topBarPadding,
        width - statsButtonWidth - statusDimension - topBarPadding, width - statusDimension - topBarPadding,
        width - statusDimension - topBarPadding }, new int[] { 0, statsButtonHeight, statsButtonHeight, 0 },
        new Action("Stats") {
          public String run(Map parent) {
            parent.toggleStatsPane();
            return "";
          }
        }, 5);
    statusLayer.regions.add(statusRegion_StatsButton);

    data.layers.add(statusLayer);
  }

  private void setupStatsLayer() {
    statsLayer = new Layer("Stats layer", STATS_ZINDEX, false);

    int statsRegionWidth = 200;
    int statsRegionHeight = 400;

    statsRegion = new Region(this, new int[] { width - statsRegionWidth, width - statsRegionWidth, width, width },
        new int[] { statusDimension, statusDimension + statusDimension + statsRegionHeight,
            statusDimension + statusDimension + statsRegionHeight, statusDimension }, ColourPalette.UI_TRANSLUCENT_MID,
        "", "", 0);
    updateStats();
    statsLayer.regions.add(statsRegion);

    data.layers.add(statsLayer);
  }

  private void updateStats() {
    String displayText = "";
    for (CharacterProperty p : loggedInUser.playerCharacter.characterProperties().propertyList) {
      displayText += p.getName() + "\t" + p.getNet() + "/" + p.getMax() + "\n";
    }
    displayText += "\n\nInventory\n";
    for (Item i : loggedInUser.playerCharacter.characterProperties().inventory) {
      displayText += i.name + "\n";
    }
    statsRegion.setDisplayText(displayText);
  }

  private void toggleStatsPane() {
    statsLayer.visible = !statsLayer.visible;
    drawMap();
  }

  private void finaliseMap() {
    // Create base map overlay
    setupBaseLayer(BASE_ZINDEX, true, new int[] { 0, 0, width, width }, new int[] { 0, height, height, 0 },
        ColourPalette.UI_APPLET, "Base", "");

    // Create status information overlays
    setupStatusLayer();

    // Create statistics overlays
    setupStatsLayer();

    // Create context menu overlay
    setupContextLayer(CONTEXT_ZINDEX, false, new int[] { 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0 },
        ColourPalette.UI_TRANSLUCENT_MID, "Context", "");

    // Create input and buttons UI
    int buttonDimension = statusDimension;
    int buttonCount = 2;
    inputLayer = new Layer("Input layer", INPUT_ZINDEX, true);
    inputRegion = new Region(this, new int[] { 0, 0, width - (buttonCount * buttonDimension),
        width - (buttonCount * buttonDimension) }, new int[] { height - statusDimension, height, height,
        height - statusDimension }, ColourPalette.DEBUG_RED, "Input region", "", 0);
    inputLayer.regions.add(inputRegion);

    int wideButtonDimension = 2 * buttonDimension;
    inputButton1 = new Button(this, new int[] { width - (2 * wideButtonDimension), width - (2 * wideButtonDimension),
        width - wideButtonDimension, width - wideButtonDimension }, new int[] { height - statusDimension, height,
        height, height - statusDimension }, new Action("Yes") {
      public String run(Map parent) {
        return "You responded yes.";
      }
    }, 1);
    inputLayer.regions.add(inputButton1);

    inputButton2 = new Button(this,
        new int[] { width - wideButtonDimension, width - wideButtonDimension, width, width }, new int[] {
            height - statusDimension, height, height, height - statusDimension }, new Action("No") {
          public String run(Map parent) {
            return "You responded no.";
          }
        }, 2);
    inputLayer.regions.add(inputButton2);
    data.layers.add(inputLayer);

    // Create message box
    int messageWidthDiv2 = width / 6;
    int messageHeightDiv2 = height / 6;
    messageLayer = new Layer("Message layer", MESSAGE_ZINDEX, true);
    Region clickBlockRegion = new Region(this, new int[] { 0, 0, width, width }, new int[] { 0, height, height, 0 },
        ColourPalette.UI_TRANSLUCENT_HIGH, "ClickBlock region", "", 0);
    messageRegion = new Region(this, new int[] { (width / 2) - messageWidthDiv2, (width / 2) - messageWidthDiv2,
        (width / 2) + messageWidthDiv2, (width / 2) + messageWidthDiv2 }, new int[] { (height / 2) - messageHeightDiv2,
        (height / 2) + messageHeightDiv2, (height / 2) + messageHeightDiv2, (height / 2) - messageHeightDiv2 },
        ColourPalette.UI_TRANSLUCENT_MID,
        "Welcome!\nThis is a work in progress, be warned that you cannot save progress yet.\n\nUsername: "
            + loggedInUser.getUsername() + "\nPassword:  " + loggedInUser.getDisplayPassword(), "", 1);
    int buttonWidthDiv2 = 40;
    int buttonHeight = 40;
    int padding = 20;
    Button okButton = new Button(this, new int[] { (width / 2) - buttonWidthDiv2, (width / 2) - buttonWidthDiv2,
        (width / 2) + buttonWidthDiv2, (width / 2) + buttonWidthDiv2 }, new int[] {
        (height / 2) + messageHeightDiv2 - buttonHeight - padding, (height / 2) + messageHeightDiv2 - padding,
        (height / 2) + messageHeightDiv2 - padding, (height / 2) + messageHeightDiv2 - buttonHeight - padding },
        new Action("Log in") {
          public String run(Map parent) {
            messageLayer.visible = false;
            return "";
          }
        }, 2);
    messageLayer.regions.add(okButton);
    messageLayer.regions.add(messageRegion);
    messageLayer.regions.add(clickBlockRegion);
    data.layers.add(messageLayer);

    // Create a layer to hold the moveable items (players, objects, etc)
    moveableItemLayer = new Layer("Moveable item layer", MOVEABLE_ITEM_ZINDEX, true);
    for (Item i : data.items) {
      moveableItemLayer.regions.add(i.displayRegion);
    }
    for (NonPlayerCharacter npc : data.npcs) {
      moveableItemLayer.regions.add(npc.displayRegion);
    }
    for (PlayerCharacter pc : data.players) {
      moveableItemLayer.regions.add(pc.displayRegion);
    }
    data.layers.add(moveableItemLayer);
  }

  private void scrollInfoBoxText(boolean direction) {
    if (direction == UP) {
      if (INFO_BOX_POINTER > 0) {
        INFO_BOX_POINTER--; // No wrapping
      }
    } else {
      if (INFO_BOX_POINTER + INFO_BOX_LINES_VISIBLE < infoBoxText.size()) {
        INFO_BOX_POINTER++; // No wrapping
      }
    }
  }

  public void drawMap() {
    refreshItemMoved();
    updateStats();
    g.clearRect(0, 0, width, height);
    if (data.layers.isEmpty()) {
      g.drawString("No map found.", 10, 20);
    } else {
      Collections.sort(data.layers, lComp);
      for (int i = 0; i < data.layers.size(); i++) {
        drawLayer(i);
      }
    }
    update();
  }

  public void drawLayer(int id) {
    Layer l = data.layers.get(id);
    if (!l.visible) {
      return;
    }
    if (l.isVoxelMap) {

    } else {
      Collections.sort(l.regions, rComp);
      for (Region r : l.regions) {
        g.setColor(r.getColour());
        g.fillPolygon(r.xpoints, r.ypoints, r.npoints); // Colour it
        g.setColor(ColourPalette.GENERAL_BLACK);

        String displayText = "";

        if (r.equals(contextRegion) || r.equals(statusRegion_HUD) || r.equals(statusRegion_InfoBoxUpBtn)
            || r.equals(statusRegion_InfoBoxDnBtn) || r.equals(messageRegion) || r.equals(statsRegion)
            || (r.getClass() == Button.class)) {
          // These regions have text that needs to be shown
          displayText = r.toString();
          displayText.replace("$", "");

          // Add a shadow to the text for these translucent and transparent areas
          MultilinePrint.drawString(g, displayText, r.xpoints[0] + 1, r.ypoints[0] + 1);

          // Add a colour hue to the object if it has a special colour
          String colourText = r.toString();
          String toColourise = MultilinePrint.getStringBetweenQuotes(colourText, "$");
          String padded = "";
          int pos = 0;
          while (MultilinePrint.stringWidth(g, padded) < MultilinePrint.stringWidth(g, colourText)) {
            if (colourText.charAt(pos) == toColourise.charAt(pos)) {
              padded += toColourise.charAt(pos);
              pos++;
            } else {
              padded += " ";
            }
          }
          g.setColor(r.getColour());
          MultilinePrint.drawString(g, colourText, r.xpoints[0], r.ypoints[0]);
        } else if (r.equals(statusRegion_InfoBox)) {
          // Display the first INFO_BOX_LINES_VISIBLE lines
          displayText = "";
          String newLine = "";
          for (int i = INFO_BOX_POINTER; i < INFO_BOX_POINTER + INFO_BOX_LINES_VISIBLE; i++) {
            if (i != INFO_BOX_POINTER + INFO_BOX_LINES_VISIBLE - 1) {
              newLine = "\n";
            } else {
              newLine = "";
            }
            displayText += infoBoxText.get(i) + newLine;
          }
          // Add a shadow to the text for these translucent and transparent areas
          MultilinePrint.drawString(g, displayText, r.xpoints[0] + 1, r.ypoints[0] + 1);
        } else if (r.equals(baseRegion)) {
          //
        } else {
          if (DEBUG_MODE) {
            g.setColor(ColourPalette.DEBUG_RED);
            g.drawPolygon(r.xpoints, r.ypoints, r.npoints); // Draw a border around it
          }
        }
        g.setColor(ColourPalette.GENERAL_WHITE);
        MultilinePrint.drawString(g, displayText, r.xpoints[0], r.ypoints[0]); // Show message
        if (DEBUG_MODE) System.out.println("Filled polygon " + r.toString() + " [(" + r.xpoints[0] + ","
            + r.ypoints[0] + "), (" + r.xpoints[1] + "," + r.ypoints[1] + "), ...]");

      }
    }
  }

  public void update() {
    paint(g);
    repaint();
  }

  public void paint(Graphics g) {
    super.paint(g);
    g.drawImage(b, 0, 0, null);
  }

  public void showRightClick(ExtList<Action> actions, int x, int y) {
    FontMetrics fm = g.getFontMetrics();
    int predictedHeight = actions.size() * fm.getHeight();
    int predictedWidth = MultilinePrint.maxActionName(g, actions);

    // Tries to place the right click menu at (x, y).
    // Places it near that point if it overflows.
    int adjustedX = 0;
    if ((x + predictedWidth) > MAP_WIDTH) {
      adjustedX = MAP_WIDTH - predictedWidth;
    } else {
      adjustedX = x;
    }
    int adjustedY = 0;
    if ((y + predictedHeight) > MAP_HEIGHT) {
      adjustedY = MAP_HEIGHT - predictedHeight;
    } else {
      adjustedY = y;
    }

    contextRegion.xpoints = new int[] { adjustedX, adjustedX, adjustedX + predictedWidth, adjustedX + predictedWidth };
    contextRegion.ypoints = new int[] { adjustedY, adjustedY + predictedHeight, adjustedY + predictedHeight, adjustedY };
    contextX = x;
    contextY = y;
    contextRegion.setDisplayText("");
    contextRegion.invalidate();

    for (Action a : actions) {
      contextRegion.setDisplayText(contextRegion.toString() + (a.name + "\n"));
    }

    contextLayer.visible = true;
    drawMap();
  }

  public void hideRightClick() {
    contextLayer.visible = false;
    drawMap();
  }

  public void printStatusLine(String s, Boolean staticInit) {
    if (s != "") {
      infoBoxText.add(s);
      if (!staticInit) {
        scrollInfoBoxText(DOWN);
      }
    }
    if (!staticInit) drawMap();
  }

  public void printStatusLine(String s) {
    if (s != "") {
      infoBoxText.add(s);
      scrollInfoBoxText(DOWN);
    }
    drawMap();
  }

  public void printHUDLine(String s) {
    statusRegion_HUD.setDisplayText(s);
    drawMap();
  }

  public void removeLayer(int zIndex) {
    for (int i = 0; i < data.layers.size(); i++) {
      if (data.layers.get(i).zIndex == zIndex) {
        data.layers.remove(i);
        return;
      }
    }
  }

  public Region getRegionUnderPoint(int x, int y) {
    // WARNING Ignores the context menu
    // Look from topmost layer downwards until we find a region that is available under the mouse
    // Collections.sort(data.layers, lComp);
    for (int i = data.layers.size() - 1; i >= 0; i--) {
      Layer l = data.layers.get(i);
      if (l.visible) {
        // Collections.sort(l.regions, rComp); // Don't need to sort again
        for (int j = l.regions.size() - 1; j >= 0; j--) {
          Region r = (Region) l.regions.get(j);
          if (r.contains(x, y) && !r.equals(contextRegion)) {
            return r; // Found it; stop looking
          }
        }
      }
    }
    return null;
  }

  public int getCMIUnderMouse() {
    // CMI: context menu index
    int relativeY = mouseY - contextY; // contextRegion.ypoints[0];
    FontMetrics fm = g.getFontMetrics();
    return (int) Math.floor(relativeY / fm.getHeight());
  }

  public Region getActiveRegion() {
    int x = (contextLayer.visible ? contextX : mouseX); // contextRegion.xpoints[0]
    int y = (contextLayer.visible ? contextY : mouseY); // contextRegion.ypoints[0]
    return getRegionUnderPoint(x, y);
  }

  public Layer getTopmostLayer() {
    return (data.layers.isEmpty() ? null : data.layers.get(data.layers.size() - 1));
  }

  public void movePlayer(Point p) {
    if (getRegionUnderPoint(p.x, p.y) == getRegionUnderPoint(mouseX, mouseY)) {
      loggedInUser.playerCharacter.moveTo(mouseX, mouseY); // Go to where clicked if it matches the active region
    } else {
      loggedInUser.playerCharacter.moveTo(p.x, p.y); // Otherwise approximate by going to the centre of the region
    }
  }

  public void refreshItemMoved() {
    moveableItemLayer.regions.clear();
    for (Item i : data.items) {
      moveableItemLayer.regions.add(i.displayRegion);
    }
    for (NonPlayerCharacter npc : data.npcs) {
      moveableItemLayer.regions.add(npc.displayRegion);
    }
    for (PlayerCharacter pc : data.players) {
      moveableItemLayer.regions.add(pc.displayRegion);
    }
  }
}