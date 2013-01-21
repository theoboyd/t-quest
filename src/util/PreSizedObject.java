package util;

/**
 * Encapsulates any object with an additional predictedSize parameter that allows for storing an estimate of its size.
 * This is most useful for objects that contain lists which have not yet been filled but must reach a certain size.
 * 
 * @param <T> - the object to encapsulate
 */
public class PreSizedObject<T> {
  public int     predictedSize;
  public final T object;

  public PreSizedObject(int predictedSize, T object) {
    this.predictedSize = predictedSize;
    this.object = object;
  }
}
