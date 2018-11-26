package com.nkorasa.cmp;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import com.nkorasa.cmp.exceptions.KeyCollisionException;

/**
 * Utils class used to partition collection based on key extractor
 */
public final class CollectionCmpPartitioner
{
  private CollectionCmpPartitioner() { }

  /**
   * Checks if collection can be partitioned using key extracted using provided keyExtractor
   * @param collection collection to partition
   * @param keyExtractor key extractor used to extract keys from items in collection
   * @param <O> objects generic type
   * @return true if collection can be partitioned
   */
  public static <O> boolean canPartition(final Collection<O> collection, final Function<O, Serializable> keyExtractor)
  {
    try
    {
      buildPartition(collection, keyExtractor);
    }
    catch (final KeyCollisionException e)
    {
      return false;
    }

    return true;
  }

  /**
   * Builds partition of collection using keyExtractor
   *
   * Use {@link #canPartition(Collection, Function)} to check if collection can be partitioned
   *
   * @throws KeyCollisionException if collection cannot be partitioned.
   * @param collection collection to partition
   * @param keyExtractor key extractor used to extract keys from items in collection
   * @param <O> objects generic type
   * @return map with collection items as values and it's keys as keys
   */
  public static <O> Map<Serializable, O> buildPartition(final Collection<O> collection, final Function<O, Serializable> keyExtractor)
  {
    final AtomicBoolean collision = new AtomicBoolean(false);
    final AtomicReference<Serializable> collisionKey = new AtomicReference<>(null);

    final Map<Serializable, O> partition = new HashMap<>();
    collection.forEach(item -> partition.compute(keyExtractor.apply(item), (k, v) -> {
      //noinspection VariableNotUsedInsideIf
      if (v != null)
      {
        collision.set(true);
        collisionKey.set(k);
      }
      return item;
    }));

    if (collision.get())
    {
      throw new KeyCollisionException("2 or more objects have the same key value: " + collisionKey + ", collection: " + collection);
    }

    return partition;
  }
}
