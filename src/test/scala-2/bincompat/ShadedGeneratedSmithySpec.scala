package bincompat

import cats.syntax.all.*
import munit.*

import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.*
import scala.tools.reflect.ToolBox

class ShadedGeneratedSmithySpec extends FunSuite {
  private val toolbox = currentMirror.mkToolBox()

  private def typecheck(input: String): Either[Throwable, Tree] =
    for {
      parsed <- Either.catchNonFatal(toolbox.parse(input))
      typechecked <- Either.catchNonFatal(toolbox.typecheck(parsed))
    } yield typechecked

  test("we don't have access to the generated KMS objects in their original package") {
    val typedchecked = typecheck("""com.amazonaws.kms.KMS.id""")

    assertEquals(typedchecked.leftMap(_.getMessage), Left("reflective typecheck has failed: object id is not a member of package com.amazonaws.kms.KMS"))
  }

  test("we don't have access to the generated KMS objects in the shaded package") {
    val typedchecked = typecheck("""com.dwolla.config.smithy_shaded.com.amazonaws.kms.KMS.id""")

    assertEquals(typedchecked.leftMap(_.getMessage), Left("reflective typecheck has failed: value KMS in package kms cannot be accessed as a member of object com.dwolla.config.smithy_shaded.com.amazonaws.kms.package from package <root>"))
  }
}
