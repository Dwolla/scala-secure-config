package com.dwolla.scalafix.smithy4s

import com.dwolla.scalafix.smithy4s.PrivatizeGeneratedCode.{basePackage, packageScope}
import scalafix.v1
import scalafix.v1.*

import scala.annotation.nowarn
import scala.meta.*
import scala.meta.Mod.Private
import scala.meta.internal.semanticdb.Scala.*

@nowarn // due to reflectiveCalls being unused on Scala 2 and deprecated but needed on Scala 3
class PrivatizeGeneratedCode extends SemanticRule("com.dwolla.scalafix.smithy4s.PrivatizeGeneratedCode") {
  import scala.language.reflectiveCalls

  override def description: String = s"Adds private[$packageScope] to members in the $basePackage package"

  private type HasMods = {def mods: List[Mod]}

  private def isMemberOfCorrectPackage(t: Defn)(implicit doc: SemanticDocument): Boolean =
    t.symbol.owner.value.startsWith(basePackage)

  private def isAlreadyPrivate[T <: Defn & HasMods](t: T): Boolean =
    t.mods.exists(_.is[Private])

  private def isDefinedAtPackageLevel(t: Defn)(implicit doc: SemanticDocument): Boolean =
    t.symbol.owner.value.isPackage || t.symbol.owner.value.endsWith("package.")

  private def shouldBePrivate[T <: Defn & HasMods](t: T)
                                                  (implicit doc: SemanticDocument): Boolean =
    isMemberOfCorrectPackage(t) && !isAlreadyPrivate(t) && isDefinedAtPackageLevel(t)

  override def fix(implicit doc: SemanticDocument): v1.Patch = {
    doc
      .tree
      .collect {
        case t: Defn.Type if shouldBePrivate(t) =>
          Patch.addLeft(t, s"private[$packageScope] ")
        case t: Defn.Val if shouldBePrivate(t) =>
          Patch.addLeft(t, s"private[$packageScope] ")
        case t: Defn.Def if shouldBePrivate(t) =>
          Patch.addLeft(t, s"private[$packageScope] ")
        case t: Defn.Object if shouldBePrivate(t) =>
          Patch.addLeft(t, s"private[$packageScope] ")
        case t: Defn.Class if shouldBePrivate(t) =>
          Patch.addLeft(t, s"private[$packageScope] ")
        case t: Defn.Trait if shouldBePrivate(t) =>
          Patch.addLeft(t, s"private[$packageScope] ")
      }
      .asPatch
  }
}

object PrivatizeGeneratedCode {
  val packageScope: String = "config"
  val basePackage = (s"com/dwolla/$packageScope/smithy_shaded")
}
