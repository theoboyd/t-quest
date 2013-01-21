package util;

import org.apache.tools.ant.DirectoryScanner;

import action.Action;
import object.Item;
import object.NonPlayerCharacter;
import object.PlayerCharacter;
import display.Layer;
import display.Map;

/**
 * A parser for tQuest Data (.tq*) files. Reads in the specified files, parses and then constructs the relevant objects.
 * Note: items are layered and built up by z-index order in the order they are listed in the file.
 */
public class Data {
  private static final boolean       SERVER_MODE_ENABLED  = false;
  public ExtList<Layer>              layers               = new ExtList<Layer>();
  public ExtList<PlayerCharacter>    players              = new ExtList<PlayerCharacter>();
  public ExtList<NonPlayerCharacter> npcs                 = new ExtList<NonPlayerCharacter>();
  public ExtList<Item>               items                = new ExtList<Item>();
  public ExtList<Action>             actions              = new ExtList<Action>();
  public Map                         parent;

  // private static final String GAME_FILE_PATH = "./data/game.tqd";
  /**
   * GAME_FILE_PATH syntax:
   */
  // # Comment
  // LAYER name z_index visible_by_default region_count //Immediately followed by its regions
  // REGION name coord_list r,g,b action_count "examine_text" //Immediately followed by its actions
  // ACTION "pretty_name" function_name
  // MVACTION
  // EXACTION
  //
  // The dollar symbol ($) when used in an action name will be replaced with the parent region name
  // The right angle brace (>) and caret symbol (^) when used instead of a coordinate will be replaced with the canvas
  // width or height, respectively
  // A coord_list is of the format x1,y1;x2,y2;...
  // Parameters surrounded by quotes may contain spaces
  //

  /**
   * WORLD_FILE_PATH syntax:
   */
  // First line is configuration data (width, height, etc)
  // Subsequent lines are grid representations of the contents of that cell, space separated

  private static final String        CHARACTERS_FILE_PATH = "./data/characters.tqd";
  /**
   * CHARACTERS_FILE_PATH syntax:
   */
  // # Comment
  // PLAYER name x,y stats_list
  // NPC name x,y stats_list "examine_text"
  //
  // A stats_list is of the format stat1,current_value,max_value,boost;stat2,current_value,max_value,boost;...

  private static final String        ITEMS_FILE_PATH      = "./data/items.tqd";

  /**
   * ITEMS_FILE_PATH syntax:
   */
  // # Comment
  // ITEM name x,y
  // TODO Finish full item syntax

  public Data(Map parent) {
    // Look for .tqm maps
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setIncludes(new String[] { "*.tqm" });
    scanner.setBasedir("./data/");
    scanner.setCaseSensitive(false);
    scanner.scan();
    String[] files = scanner.getIncludedFiles();

    // Reference to the parent map, so actions can modify it.
    this.parent = parent;
    // GameData gd = new GameData(GAME_FILE_PATH, this);
    MapData md = new MapData(files[0], this);
    CharacterData cd = new CharacterData(CHARACTERS_FILE_PATH, this);
    ItemData id = new ItemData(ITEMS_FILE_PATH, this);

    // Load from server or file
    if (SERVER_MODE_ENABLED) {
      loadDataFromServer();
    } else {
      try {
        md.loadMapDataFromFile();
        cd.loadCharacterDataFromFile();
        id.loadItemDataFromFile();
      } catch (TQDException e) {
        System.err.println(e.getMessage());
        System.exit(1); // Critical error
      }
    }
  }

  public void loadDataFromServer() {
    // Server stub
  }
}