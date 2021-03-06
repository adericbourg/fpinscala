package fpinscala.datastructures

import scala.annotation.tailrec

sealed trait List[+A] // `List` data type, parameterized on a type, `A`
case object Nil extends List[Nothing] // A `List` data constructor representing the empty list
/* Another data constructor, representing nonempty lists. Note that `tail` is another `List[A]`,
which may be `Nil` or another `Cons`.
 */
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List { // `List` companion object. Contains functions for creating and working with lists.
  def sum(ints: List[Int]): Int = ints match { // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x,xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)
  }

  def apply[A](as: A*): List[A] = // Variadic function syntax
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  val x = List(1,2,3,4,5) match {
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + sum(t)
    case _ => 101
  }

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match {
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))
    }

  def foldRight[A,B](as: List[A], z: B)(f: (A, B) => B): B = // Utility functions
    as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }

  def sum2(ns: List[Int]) =
    foldRight(ns, 0)((x,y) => x + y)

  def product2(ns: List[Double]) =
    foldRight(ns, 1.0)(_ * _) // `_ * _` is more concise notation for `(x,y) => x * y`; see sidebar


  def tail[A](l: List[A]): List[A] = l match {
    case Nil => sys.error("empty list")
    case Cons(_, tail) => tail
  }

  def setHead[A](l: List[A], h: A): List[A] = l match {
    case Nil => sys.error("empty list")
    case Cons(_, tail) => Cons(h, tail)
  }

  @tailrec
  def drop[A](l: List[A], n: Int): List[A] = {
    if (n == 0) l
    else drop(tail(l), n - 1)
  }

  @tailrec
  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
    case Cons(head, tail) if f(head) => dropWhile(tail, f)
    case _ => l
  }

  def init[A](l: List[A]): List[A] = l match {
    case Cons(_, Nil) => Nil
    case Cons(head, tail) => Cons(head, init(tail))
    case _ => l
  }

  def length[A](l: List[A]): Int = foldRight(l, 0)((_, acc) => acc + 1)

  @tailrec
  def foldLeft[A,B](l: List[A], z: B)(f: (B, A) => B): B = l match {
    case Nil => z
    case Cons(head, tail) => foldLeft(tail, f(z, head))(f)
  }

  def sumLeft(l: List[Int]): Int = foldLeft(l, 0)((acc, item) => acc + item)

  def productLeft(l: List[Int]): Int = foldLeft(l, 1)((acc, item) => acc * item)

  def lengthLeft[A](l: List[A]): Int = foldLeft(l, 0)((acc, _) => acc + 1)

  def reverse[A](l: List[A]) = foldLeft(l, Nil: List[A])((acc, item) => Cons(item, acc))

  def foldLeftFromFoldRight[A,B](l: List[A], z: B)(f: (B, A) => B): B = {
    def identity[BB] = (bb: BB) => bb
    foldRight(l, identity[B])((a, g) => b => g(f(b, a)))(z)
  }

  def foldRightFromFoldLeft[A,B](as: List[A], z: B)(f: (A, B) => B): B = {
    def identity[BB] = (bb: BB) => bb
    foldLeft(as, identity[B])((g, a) => b => g(f(a, b)))(z)
  }

  def append[A](a1: List[A], a2: List[A]): List[A] = foldLeft(a1, a2)((acc, item) => Cons(item, acc))

  def concat[A](as: List[List[A]]): List[A] = foldLeft(as, Nil: List[A])(append)

  def addOne(ints: List[Int]): List[Int] = foldRight(ints, Nil: List[Int])((item, tail) => Cons(item + 1, tail))

  def doubleToString(doubles: List[Double]): List[String] = foldRight(doubles, Nil: List[String])((d, tail) => Cons(d.toString, tail))

  def map[A, B](l: List[A])(f: A => B): List[B] = foldRight(l, Nil: List[B])((head, tail) => Cons(f(head), tail))

  def filter[A](as: List[A], predicate: (A => Boolean)): List[A] = foldRight(as, Nil: List[A]) { (item, filtered) =>
    if (predicate(item)) filtered
    else Cons(item, filtered)
  }

  def keepEven(ints: List[Int]): List[Int] = filter(ints, (i: Int) => i % 2 == 0)

  def flatMap[A, B](l: List[A])(f: A => List[B]): List[B] = foldRight(l, Nil: List[B])((item, acc) => concat(map(l)(f)))
}
