package util;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import module.CharacterProperties;
import module.CharacterProperty;
import module.DrawProperties;
import object.Item;
import object.NonPlayerCharacter;
import object.PlayerCharacter;

public class CharacterData extends BaseData {

  public CharacterData(String filePath, Data data) {
    super(filePath, data);
  }

  private enum CharacterToken {
    COMMENT, ANY_CHARACTER, PLAYER, NPC
  }

  public CharacterToken pullGameToken(String s) {
    // Pulls out the first token from the string
    String line = s.trim();
    return (line.startsWith("#") ? CharacterToken.COMMENT : (line.startsWith("PLAYER") ? CharacterToken.PLAYER : (line
        .startsWith("NPC") ? CharacterToken.NPC : CharacterToken.COMMENT)));
  }

  public void loadCharacterDataFromFile() throws TQDException {
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

      CharacterToken currToken = pullGameToken(currLine);
      if (currToken == CharacterToken.COMMENT) {
        continue;
      }

      switch (currToken) {
        case PLAYER:
          enclosingData.players.add(createPlayerFromString(currLine));
          break;
        case NPC:
          enclosingData.npcs.add(createNPCFromString(currLine));
          break;
        default:
          break;
      }
    }
  }

  public ExtList<CharacterProperty> propertiesFromStringTriplets(String input) throws TQDException {
    // Parse name,current_value,max_value,boost; triplets into property values
    String listOfTriplets[] = input.split(";");
    ExtList<CharacterProperty> output = new ExtList<CharacterProperty>();

    try {
      for (String statsData : listOfTriplets) {
        String statsDatum[] = statsData.split(",");
        String name = statsDatum[0];
        int curr = Integer.parseInt(statsDatum[1]);
        int max = Integer.parseInt(statsDatum[2]);
        int boost = Integer.parseInt(statsDatum[3]);

        output.add(new CharacterProperty(name, curr, max, boost));
      }
    } catch (Exception e) {
      throw new TQDException("Property string was incorrectly formatted.\n\n" + 
                             "Expected: stat1,current_value,max_value,boost;stat2,current_value,max_value,boost;...\n" +
                             "Got: " + input, filePath);
    }

    return output;
  }

  private PlayerCharacter createPlayerFromString(String string) throws TQDException {
    String[] currValues = string.trim().split(" ");
    String newName = currValues[1]; // 0th element was the string "PLAYER"
    String newXY[] = currValues[2].split(",");

    DrawProperties draw = new DrawProperties(new Point(Integer.parseInt(newXY[0]), Integer.parseInt(newXY[1])), "",
        enclosingData.parent, null, enclosingData.parent.currZIndex);

    CharacterProperties p = new CharacterProperties(new ExtList<Item>(), propertiesFromStringTriplets(currValues[3]));

    PlayerCharacter pc = new PlayerCharacter(newName, draw, p);
    enclosingData.parent.currZIndex++;
    return pc;
  }

  private NonPlayerCharacter createNPCFromString(String string) throws TQDException {
    String[] currValues = string.trim().split(" ");
    String newName = currValues[1]; // 0th element was the string "NPC"
    String newXY[] = currValues[2].split(",");
    String newExamineText = MultilinePrint.getStringBetweenQuotes(string, "\"");

    DrawProperties draw = new DrawProperties(new Point(Integer.parseInt(newXY[0]), Integer.parseInt(newXY[1])),
        newExamineText, enclosingData.parent, null, enclosingData.parent.currZIndex);

    CharacterProperties p = new CharacterProperties(new ExtList<Item>(), propertiesFromStringTriplets(currValues[3]));

    NonPlayerCharacter npc = new NonPlayerCharacter(newName, draw, p);
    enclosingData.parent.currZIndex++;
    return npc;
  }
}