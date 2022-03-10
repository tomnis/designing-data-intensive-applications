package org.mccandless

import scala.collection.mutable

trait SimpleDb[K, V] {

  // a "file", each element is a "new line" in the "file"
  val db: mutable.Buffer[(K, V)] = mutable.Buffer.empty


  // echo "$1,$2" >> database
  // pretty good performance, as we are only appending
  def db_set(key: K, value: V): Unit = db.append((key, value))

  // grep "^$1," database | sed -e "s/^$1,//" | tail -n 1
  // horrible O(n) performance
  def db_get(key: K): Option[V] = db.findLast(_._1 == key).map(_._2)
}
