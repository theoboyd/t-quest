package voxel;

import util.MapDivision;

public class Voxel implements MapDivision {

  boolean   backgroundSkyFill;
  boolean   backgroundWallFill;
  boolean   foregroundFill;
  VoxelType backgroundSkyType;
  VoxelType backgroundWallType;
  VoxelType foregroundType;
  boolean   spritesInteract;
  boolean   specialSpritesInteract;
  String    displayText;

  public Voxel(boolean sky, boolean wall, boolean fg, VoxelType skyT, VoxelType wallT, VoxelType fgT,
      boolean spritesInteract, boolean specialSpritesInteract, String name) {

    backgroundSkyFill = sky;
    backgroundWallFill = wall;
    foregroundFill = fg;
    backgroundSkyType = skyT;
    backgroundWallType = wallT;
    foregroundType = fgT;
    this.spritesInteract = spritesInteract;
    this.specialSpritesInteract = specialSpritesInteract;
    displayText = name;
  }

  public String toString() {
    return displayText;
  }
}