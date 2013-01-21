package display;

import util.ExtList;
import util.MapDivision;

public class Layer {
  public ExtList<MapDivision> regions    = new ExtList<MapDivision>();
  public String          name       = "";
  public int             zIndex     = 0;
  public boolean         visible    = false;
  public boolean         isVoxelMap = false;

  public Layer(ExtList<MapDivision> regions, String name, int zIndex, boolean visible) {
    this.regions = regions;
    this.name = name;
    this.zIndex = zIndex;
    this.visible = visible;
  }

  public Layer(String name, int zIndex, boolean visible) {
    this.name = name;
    this.zIndex = zIndex;
    this.visible = visible;
  }

  public void removeRegionByName(String name) {
    for (int i = 0; i < regions.size(); i++) {
      if (((Region) regions.get(i)).toString() == name) {
        regions.remove(i);
        return;
      }
    }
  }
}
