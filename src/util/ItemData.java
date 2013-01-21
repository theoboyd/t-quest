package util;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import module.DrawProperties;
import object.Item;
import action.PickupAction;

public class ItemData extends BaseData {
  public ItemData(String filePath, Data data) {
    super(filePath, data);
  }

  private enum ItemToken {
    COMMENT, ITEM
  }

  public ItemToken pullGameToken(String s) {
    // Pulls out the first token from the string
    String line = s.trim();
    return (line.startsWith("#") ? ItemToken.COMMENT : ItemToken.ITEM);
  }

  public void loadItemDataFromFile() throws TQDException {
    BufferedReader br;

    try {
      br = new BufferedReader(new FileReader(filePath));
    } catch (FileNotFoundException e) {
      throw new TQDException(e.getMessage(), filePath);
    }

    String currLine = "";

    while (currLine != null) {
      try {
        currLine = br.readLine();
      } catch (IOException e) {
        throw new TQDException(e.getMessage(), filePath);
      }

      if (currLine == null) {
        continue; // And then quit in next iteration
      }

      ItemToken currToken = pullGameToken(currLine);
      if (currToken == ItemToken.COMMENT) {
        continue;
      }

      switch (currToken) {
        case ITEM:
          enclosingData.items.add(createItemFromString(currLine));
          break;
        default:
          break;
      }
    }
  }

  private Item createItemFromString(String string) throws TQDException {
    String[] currValues = string.trim().split(" ");
    String newName = currValues[1]; // 0th element was the string "ITEM"
    String newXY[] = currValues[2].split(",");
    String newExamineText = MultilinePrint.getStringBetweenQuotes(string, "\"");

    DrawProperties draw = new DrawProperties(new Point(Integer.parseInt(newXY[0]), Integer.parseInt(newXY[1])),
        newExamineText, enclosingData.parent, null, enclosingData.parent.currZIndex);

    Item item = new Item(newName, draw);
    item.displayRegion.actions.addFirst(new PickupAction(item));

    enclosingData.parent.currZIndex++;
    return item;
  }
}
