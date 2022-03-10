package org.mccandless

import scala.collection.mutable


/**
 *
 * - rather than breaking storage down into variable-sized segments,
 *   b-trees break down into fixed-sized pages or blocks
 * - typically faster than [[LSMTree]] for reads, but slower for writes
 *
 * disadvantages:
 *   - must write all data twice; to WAL, and to tree
 *   - leaves some disk space unused due to fragmentation and fixed page size
 */
trait BTree[K, V] {

  // each page contains several keys and references to child pages
  case class Page[K, V](keys: Seq[K], children: Seq[Page[K, V]])

  val root: Page[K, V]

  // reads:
  // start at the root and follow pointers until we get to a leaf


  // writes:
  // find the page whose range encompasses the new key and add it to that page.
  // If there isn’t enough free space in the page to accommodate the new key, it is split into two half-full pages,
  // and the parent page is updated to account for the new subdivision of keys
  // (ensures that the tree remains balanced)


  // updates:
  // to update the value for an existing key in a B-tree, you search for the leaf page containing that key,
  // change the value in that page, and write the page back to disk (any references to that page remain valid).


  // an additional data structure on disk: a write-ahead log (WAL, also known as a redo log).
  // This is an append-only file to which every B-tree modification must be written before it can be applied to the pages of the tree itself.
  // When the database comes back up after a crash, this log is used to restore the B-tree back to a consistent state
  val writeAheadLog: mutable.Buffer[(K, V)]
}
