package display;

import java.util.Comparator;

public class RegionComparator implements Comparator<Region> {
  public int compare(Region r1, Region r2) {
    if (r1.zIndex < r2.zIndex) {
      return -1;
    } else
      if (r1.zIndex > r2.zIndex) {
        return 1;
      } else {
        System.err.println("Z-indices cannot be the same for " + r1.toString() + " and " + r2.toString() + ".");
        return 0;
      }
  }
}
