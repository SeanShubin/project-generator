package com.seanshubin.project.generator.domain

import org.scalatest.FunSuite

class LexicographicalCompareTest extends FunSuite {
  test("compare numbers") {
    assert(LexicographicalCompare.lessThan("1", "2") === true)
    assert(LexicographicalCompare.lessThan("2", "2") === false)
    assert(LexicographicalCompare.lessThan("3", "2") === false)
    assert(LexicographicalCompare.lessThan("2", "10") === true)
    assert(LexicographicalCompare.lessThan("1", "01") === true)
    assert(LexicographicalCompare.lessThan("01", "1") === false)
  }
  test("compare empty") {
    assert(LexicographicalCompare.lessThan("", "1") === true)
    assert(LexicographicalCompare.lessThan("", "a") === true)
    assert(LexicographicalCompare.lessThan("1", "") === false)
    assert(LexicographicalCompare.lessThan("a", "") === false)
  }
  test("compare text") {
    assert(LexicographicalCompare.lessThan("a", "b") === true)
    assert(LexicographicalCompare.lessThan("b", "b") === false)
    assert(LexicographicalCompare.lessThan("c", "b") === false)
  }
  test("numbers less than text") {
    assert(LexicographicalCompare.lessThan("1", "a") === true)
    assert(LexicographicalCompare.lessThan("a", "1") === false)
  }
  test("complex comparisons") {
    assert(LexicographicalCompare.lessThan("abc123", "abc123") === false)
    assert(LexicographicalCompare.lessThan("abc123", "abc") === false)
    assert(LexicographicalCompare.lessThan("abc", "abc123") === true)
    assert(LexicographicalCompare.lessThan("123abc", "123abc") === false)
    assert(LexicographicalCompare.lessThan("123", "123abc") === true)
    assert(LexicographicalCompare.lessThan("123abc", "123") === false)
  }
}
