package util;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import action.Action;
import action.ExamineAction;
import action.MoveAction;

import object.MoveableItem;

import display.ColourPalette;
import display.Layer;
import display.Map;
import display.Region;

public class GameData extends BaseData {

  public GameData(String filePath, Data data) {
    super(filePath, data);
  }

  private enum GameToken {
    COMMENT, LAYER, REGION, ANY_ACTION, ACTION, EXAMINE_ACTION, MOVE_ACTION
  }

  public GameToken pullGameToken(String s) {
    // Pulls out the first token from the string
    String line = s.trim();
    return (line.startsWith("#") ? GameToken.COMMENT : (line.startsWith("REGION") ? GameToken.REGION : (line
        .startsWith("ACTION") ? GameToken.ACTION : (line.startsWith("EXACTION") ? GameToken.EXAMINE_ACTION : (line
        .startsWith("MVACTION") ? GameToken.MOVE_ACTION : GameToken.LAYER)))));
  }

  public void loadGameDataFromFile() throws TQDException {
    // Null, pre-initialised values
    IncompleteObject<Layer> currLayer = new IncompleteObject<Layer>(0, null);
    IncompleteObject<Region> currRegion = new IncompleteObject<Region>(0, null);
    Action a;
    BufferedReader br;

    try {
      br = new BufferedReader(new FileReader(filePath));
    } catch (FileNotFoundException e) {
      throw new TQDException(e.getMessage(), filePath);
    }

    String currLine = "";
    GameToken expToken = GameToken.LAYER;
    int expRegionCount = 0;
    int expActionCount = 0;

    while (currLine != null) {
      try {
        currLine = br.readLine();
      } catch (IOException e) {
        throw new TQDException(e.getMessage(), filePath);
      }

      if (currLine == null) {
        continue; // And then quit in next iteration
      }

      GameToken currToken = pullGameToken(currLine);
      if (currToken == GameToken.COMMENT) {
        continue;
      }

      // Are we expecting a certain token, but have not been given it?
      boolean unexpected = (expToken == GameToken.ANY_ACTION && currToken != GameToken.EXAMINE_ACTION
          && currToken != GameToken.MOVE_ACTION && currToken != GameToken.ACTION)
          || (expToken != GameToken.ANY_ACTION && currToken != expToken);
      if (unexpected) {
        throw new TQDException("Expected " + expToken + ", but got " + currToken, filePath);
      }

      switch (currToken) {
        case LAYER:
          currLayer = createLayerFromString(currLine);
          currRegion.complete = false;
          expRegionCount += currLayer.predictedSize;
          break;
        case REGION:
          expRegionCount--;
          currRegion = createRegionFromString(currLine);
          expActionCount += currRegion.predictedSize;
          if (expRegionCount == 0) {
            currLayer.complete = true;
          }
          break;
        case ACTION:
          expActionCount--;
          a = createActionFromString(currLine, currRegion.object);
          currRegion.object.actions.add(a);
          if (expActionCount == 0) {
            currRegion.complete = true;
          }
          break;
        case EXAMINE_ACTION:
          expActionCount--;
          a = createExamineAction(currRegion.object);
          currRegion.object.actions.add(a);
          if (expActionCount == 0) {
            currRegion.complete = true;
          }
          break;
        case MOVE_ACTION:
          expActionCount--;
          a = createMoveAction(currRegion.object);
          currRegion.object.actions.add(a);
          if (expActionCount == 0) {
            currRegion.complete = true;
          }
          break;
        default:
          break;
      }

      if (expActionCount > 0) {
        expToken = GameToken.ANY_ACTION;
      } else
        if (expActionCount == 0) {
          if (currRegion.complete) {
            currLayer.object.regions.add(currRegion.object);
          }
          if (expRegionCount > 0) {
            expToken = GameToken.REGION;
          } else
            if (expRegionCount == 0) {
              if (currLayer.complete) {
                enclosingData.layers.add(currLayer.object);
              }
              expToken = GameToken.LAYER;
            }
        }
    }
  }

  private Action createActionFromString(String predictedAction, Region region) {
    return new Action(MultilinePrint.getStringBetweenQuotes(predictedAction, "\"").replaceAll("\\$",
        "$" + region.toString().toLowerCase() + "$")) {
      public String run(Map parent) {
        // TODO Make generalised
        parent.getActiveRegion().setColour(ColourPalette.DEBUG_RED);
        parent.drawMap();
        return "";
      }
    };
  }

  private Action createExamineAction(Object regionOrMoveableItem) {
    if (regionOrMoveableItem.getClass() == Region.class) {
      return new ExamineAction((Region) regionOrMoveableItem);
    } else {
      return new ExamineAction((MoveableItem) regionOrMoveableItem);
    }
  }

  private Action createMoveAction(Region region) {
    Point c = region.approxCentre();
    return new MoveAction(region, c.x, c.y, enclosingData.parent);
  }

  private IncompleteObject<Region> createRegionFromString(String string) throws TQDException {
    String[] currValues = string.trim().split(" ");
    String newDisplayName = currValues[1]; // 0th element was the string "REGION"
    Polygon newCoords = createPolygonFromString(currValues[2]);
    int[] newXs = newCoords.xpoints;
    int[] newYs = newCoords.ypoints;
    Color newColour = ColourPalette.parseColour(currValues[3]);
    int newActionCount = Integer.parseInt(currValues[4]);
    Region newRegion = new Region(enclosingData.parent, newXs, newYs, newColour, newDisplayName,
        MultilinePrint.getStringBetweenQuotes(string, "\""), enclosingData.parent.currZIndex);
    enclosingData.parent.currZIndex++;
    return new IncompleteObject<Region>(newActionCount, newRegion);
  }

  private IncompleteObject<Layer> createLayerFromString(String string) {
    String[] currValues = string.trim().split(" ");
    String newName = currValues[1]; // 0th element was the string "LAYER"
    int newZIndex = Integer.parseInt(currValues[2]);
    boolean newVisibility = Boolean.parseBoolean(currValues[3]);
    int newRegionCount = Integer.parseInt(currValues[4]);
    Layer newLayer = new Layer(newName, newZIndex, newVisibility);
    return new IncompleteObject<Layer>(newRegionCount, newLayer);
  }

  private Polygon createPolygonFromString(String string) throws TQDException {
    // Expected format is: x1,y1;x2,y2;...
    String xyRaw = string.trim();
    xyRaw = xyRaw.replaceAll("\\^", "" + enclosingData.parent.height);
    xyRaw = xyRaw.replaceAll(">", "" + enclosingData.parent.width);
    String[] xyPairs = xyRaw.split(";");
    Polygon p = new Polygon();
    for (String xyPair : xyPairs) {
      String[] values = xyPair.trim().split(",");
      try {
        p.addPoint(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
      } catch (Exception e) {
        throw new TQDException("Invalid point coordinates provided (" + e.getMessage() + ")", filePath);
      }
    }
    return p;
  }
}