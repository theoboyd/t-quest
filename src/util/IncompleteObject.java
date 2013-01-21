package util;

/**
 * Extends {@link PreSizedObject} to include an indicator of whether the object is complete. This measure is arbitrary,
 * but can be used to indicate that the object built has reached its predictedSize.
 * 
 * @param <T> - the object to encapsulate
 */
public class IncompleteObject<T> extends PreSizedObject<T> {
  public boolean complete = false;

  public IncompleteObject(int predictedSize, T object) {
    super(predictedSize, object);
  }
}
