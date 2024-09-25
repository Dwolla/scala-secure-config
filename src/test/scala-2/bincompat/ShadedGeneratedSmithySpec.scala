package bincompat

import munit.*

class ShadedGeneratedSmithySpec extends FunSuite {
  test("we don't have access to the generated KMS objects in their original package") {
    assertNoDiff(compileErrors("""com.amazonaws.kms.KMS.id == smithy4s.ShapeId("com.amazonaws.kms", "TrentService")"""),
      """error: object amazonaws is not a member of package com
        |com.amazonaws.kms.KMS.id == smithy4s.ShapeId("com.amazonaws.kms", "TrentService")
        |    ^
        |""".stripMargin)
  }

  test("we don't have access to the generated KMS objects in the shaded package") {
    assertNoDiff(compileErrors("""assertEquals(com.dwolla.config.smithy_shaded.com.amazonaws.kms.KMS.id, smithy4s.ShapeId("com.dwolla.config.smithy_shaded.com.amazonaws.kms", "TrentService"))"""),
      """error: value KMS in package kms cannot be accessed as a member of object com.dwolla.config.smithy_shaded.com.amazonaws.kms.package from class ShadedGeneratedSmithySpec in package bincompat
        |assertEquals(com.dwolla.config.smithy_shaded.com.amazonaws.kms.KMS.id, smithy4s.ShapeId("com.dwolla.config.smithy_shaded.com.amazonaws.kms", "TrentService"))
        |                                                               ^
        |""".stripMargin)
  }
}
