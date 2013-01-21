package util;

public class BaseData {
  public String filePath;
  public Data   enclosingData;

  public BaseData(String filePath, Data data) {
    this.filePath = filePath;
    this.enclosingData = data;
  }
}
