package org.mccandless

import scala.collection.mutable

/**
 * Similar to the default storage engine in riak.
 *
 * well suited to situations where the value for each key is updated frequently.
 * For example, the key might be the URL of a cat video, and the value might be the number of times
 * it has been played (incremented every time someone hits the play button).
 * In this kind of workload, there are a lot of writes, but there are not too many distinct keys.
 * you have a large number of writes per key, but it’s feasible to keep all keys in memory.
 *
 * @tparam K
 * @tparam V
 */
trait HashIndex[K, V] extends SimpleDb[K, V] {

  override val db: mutable.IndexedBuffer[(K, V)] = mutable.IndexedBuffer.empty

  // simple indexing strategy: keep an in-memory hash map where every key is mapped to a byte offset in the file
  val index: mutable.Map[K, Int] = mutable.Map.empty

  override def db_set(key: K, value: V): Unit = {
    db.append((key, value))
    index.update(key, db.length - 1)
  }

  override def db_get(key: K): Option[V] = index.get(key).map(idx => db(idx)._2)


  // deletion:
  // use a "tombstone" record to signal that all previous values for this key should be discarded
  val tombstone: V
  def delete(key: K): Unit = db_set(key, tombstone)

  // crash recovery:
  // read each segment file, reconstructing the index
  // a real implementation would save "checkpoints" to speed up recovery
  def crash_recovery(): mutable.Map[K, Int] = {
    val newIndex: mutable.Map[K, Int] = mutable.Map.empty
    var offset: Int = 0
    db.foreach { case (key, _) =>
      newIndex.update(key, offset)
      offset += 1
    }
    newIndex
  }

  // compaction:
  // break the log into segments by closing a segment file when it reaches a certain size,
  // and making subsequent writes to a new segment file.
  // We can then perform compaction on these segments.
  // Compaction means throwing away duplicate keys in the log,
  // and keeping only the most recent update for each key.
  def compact(segments: Seq[mutable.IndexedBuffer[(K, V)]]): mutable.IndexedBuffer[(K, V)] = ???


  // drawbacks:
  // - range queries are not efficient, requires scanning over each key
}
