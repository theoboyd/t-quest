package module;

public class CharacterProperty {
  private String name;
  private int current;
  private int max;
  private static int min = 0;
  private int boost;

  public CharacterProperty(String name, int current, int max, int boost) {
    this.name = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase(); // Sentence case format
    this.current = current;
    this.max = max;
    this.boost = boost;
  }
  
  public String getName() {
    return name;
  }
  
  public int getNet() {
    return current + boost;
  }
  
  public int getBoost() {
    return boost;
  }
  
  public void setBoost(int boost) {
    this.boost = boost;
  }

  public void setCurrent(int new_) {
    // This ignores any boost
    if (new_ <= max && new_ >= min) {
      this.current = new_;
    } else if (new_ > max) {
      this.current = max;
    } else {
      this.current = 0;
    }
  }

  public void decrementCurrent(int delta) {
    setCurrent(this.current - delta);
  }

  public void incrementCurrent(int delta) {
    setCurrent(this.current + delta);
  }

  public void fillCurrent(int andBoostBy) {
    setCurrent(max);
    if (andBoostBy > 0) setBoost(andBoostBy);
  }
  
  public void zeroCurrent(boolean clearBoost) {
    setCurrent(0);
    if (clearBoost) setBoost(0);
  }
  
  public int getMax() {
    return max;
  }
  
  public void setMaxAndFill(int max) {
    this.max =  max;
    // Retain any health boosts
    setCurrent(max);
  }
}