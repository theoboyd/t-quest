package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import voxel.Voxel;
import display.Layer;

public class MapData extends BaseData {

  Map<String, Voxel> MapPixels = new HashMap<String, Voxel>();
  
  public MapData(String filePath, Data data) {
    super(filePath, data);
  }

  public void loadMapDataFromFile() throws TQDException {
    Layer l = new Layer("VoxelMap", enclosingData.parent.currZIndex, true);
    
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
      
      for (String s : currLine.split(" ")) {
        Voxel v = new Voxel(false, false, false, null, null, null, false, false, s);
        l.regions.add(v);
      }
    }

    l.isVoxelMap = true;
    enclosingData.layers.add(l);
    enclosingData.parent.currZIndex++;
  }
}