package util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * An extension of a {@link LinkedList}. Adds the ability to obtain an element by its string representation which is
 * useful for objects whose names are unique.
 * 
 * @param <E> - the data type to store in the list.
 */
public class ExtList<E> extends LinkedList<E> {
  private static final long serialVersionUID = -7591087701545248811L;

  public ExtList() {
    super();
  }

  public ExtList(E a) {
    super();
    this.add(a);
  }

  public ExtList(Collection<? extends E> c) {
    super(c);
  }

  public E getByName(String name) {
    Iterator<E> i = iterator();
    while (i.hasNext()) {
      E curr = i.next();
      if (curr.toString() == name) {
        return curr;
      }
    }
    return null;
  }
}
