package org.mccandless

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SimpleDbSpec extends AnyFlatSpec with Matchers with SimpleDb[String, String] {

  "SimpleDb" should "set and get" in {
    db_set("jean-luc", "picard")
    db.length should be (1)
    db_get("jean-luc") should be (Some("picard"))


    db_set("jean-luc", "riker")
    db.length should be (2)
    db_get("jean-luc") should be (Some("riker"))
  }
}
