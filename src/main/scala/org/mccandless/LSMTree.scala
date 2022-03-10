package org.mccandless

import scala.collection.mutable

/**
 * Log-Structured Merge-Tree
 *
 * Maintain a cascade of sorted string tables in the background
 *
 * - typically faster than [[BTree]] for writes, but slower for reads
 *
 * disadvantages:
 *   - compaction can interfere with ongoing read/write requests
 *   - if not configured carefully, compaction will be unable to keep up with writes, run out of disk space
 *   - may have multiple copies of key in different segments
 *
 */
trait LSMTree[K, V] extends SSTable[K, V] {

  // red-black tree
  val memtable: mutable.TreeMap[K, V] = mutable.TreeMap.empty

  // maintained in reverse chronological order, newer tables at the front
  val segments: mutable.Buffer[SSTable[K, V]] = mutable.Buffer.empty

  /**
   * Merging segments is simple and efficient, similar to mergesort
   * - look at segments side by side, copy the lowest key to the output segment
   * - when multiple segments contain the same key, keep the value from the most recent segment and discard the rest
   */
  def merge(oldSegments: mutable.Buffer[SSTable[K, V]]): SSTable[K, V] = ???


  /**
   * - add to an in-memory balanced tree (memtable)
   * - when memtable reaches a certain size, write it out to disk as an SSTable
   */
  override def db_set(key: K, value: V): Unit = {
    memtable.update(key, value)
    if (memtable.size >= 4096) {
      writeSSTable()
      memtable.clear()
    }
  }

  // write memtable to disk as an SSTable
  private def writeSSTable(): Unit = ???


  /**
   * - check memtable
   * - then check SSTables in reverse chronological order
   * - reading a non-existing key is slow (need to check all SSTables)
   * - TODO use a bloom filter to approximate the set of all keys
   */
  override def db_get(key: K): Option[V] = {
    if (memtable.contains(key)) {
      memtable.get(key)
    }
    else {
      segments.find(_.db_get(key).nonEmpty).flatMap(_.db_get(key))
    }
  }
}
