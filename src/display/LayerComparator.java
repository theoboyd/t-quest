package display;

import java.util.Comparator;

public class LayerComparator implements Comparator<Layer> {
  public int compare(Layer l1, Layer l2) {
    if (l1.zIndex < l2.zIndex) {
      return -1;
    } else
      if (l1.zIndex > l2.zIndex) {
        return 1;
      } else {
        System.err.println("Z-indices cannot be the same for " + l1.name + " and " + l2.name + ".");
        return 0;
      }
  }
}
