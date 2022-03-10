package org.mccandless

/**
 * Similar to HashIndex, but require that store is sorted by key
 *
 * SortedStringTable
 *
 * - our index can be sparse
 * - no longer need to have an offset entry for every key
 */
trait SSTable[K, V] extends HashIndex[K, V] {
  implicit val ordering: Ordering[K]

  // sparse index: we don't need to index entries for every key,
  // eg searching for key "handiwork"
  // if we have index entries for "handbag" and "handsome",
  // we can start scanning at "handbag" until we find "handiwork"

}