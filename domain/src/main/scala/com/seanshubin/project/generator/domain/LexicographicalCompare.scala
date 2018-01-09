package com.seanshubin.project.generator.domain

object LexicographicalCompare {
  private def isDigit(x: Char): Boolean = !(x < '0' || x > '9')

  private def buildParts(soFar: Seq[String], remain: String, currentWord: String, isDigits: Boolean): Seq[String] = {
    if (remain.isEmpty) {
      soFar :+ currentWord
    } else {
      val ch = remain.head
      if (isDigit(ch)) {
        if (isDigits) {
          buildParts(soFar, remain.tail, currentWord :+ ch, isDigits = true)
        } else {
          buildParts(soFar :+ currentWord, remain.tail, ch.toString, isDigits = true)
        }
      } else {
        if (isDigits) {
          buildParts(soFar :+ currentWord, remain.tail, ch.toString, isDigits = false)
        } else {
          buildParts(soFar, remain.tail, currentWord :+ ch, isDigits = false)
        }
      }
    }
  }

  private def parts(x: String): Seq[String] = {
    val result = if (x.isEmpty) {
      Seq()
    } else {
      val ch = x.head
      if (isDigit(ch)) {
        buildParts(Seq(), x.tail, ch.toString, isDigits = true)
      } else {
        buildParts(Seq(), x.tail, ch.toString, isDigits = false)
      }
    }
    result
  }

  private def padLeft(x: String, size: Int, padChar: Char): String = {
    val quantity = size - x.length
    val padding = padChar.toString * quantity
    padding + x
  }

  private def numberLessThan(left: String, right: String): Boolean = {
    val maxLength = Math.max(left.length, right.length)
    val paddedLeft = padLeft(left, maxLength, '0')
    val paddedRight = padLeft(right, maxLength, '0')
    if (paddedLeft < paddedRight) {
      true
    } else if (paddedRight < paddedLeft) {
      false
    } else {
      left.length < right.length
    }
  }

  private def wordLessThan(left: String, right: String): Boolean = {
    left < right
  }

  private def partLessThan(left: String, right: String): Boolean = {
    (isDigit(left.head), isDigit(right.head)) match {
      case (true, true) =>
        numberLessThan(left, right)
      case (true, false) =>
        true
      case (false, true) =>
        false
      case (false, false) =>
        wordLessThan(left, right)
    }
  }

  private def listLessThan(leftList: List[String], rightList: List[String]): Boolean = {
    (leftList, rightList) match {
      case (left :: leftTail, right :: rightTail) =>
        if (partLessThan(left, right)) {
          true
        } else if (partLessThan(right, left)) {
          false
        } else {
          listLessThan(leftTail, rightTail)
        }
      case (_ :: _, Nil) =>
        false
      case (Nil, _ :: _) =>
        true
      case (Nil, Nil) =>
        false
    }
  }

  def lessThan(left: String, right: String): Boolean = {
    listLessThan(parts(left).toList, parts(right).toList)
  }
}
