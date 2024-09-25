package bincompat

import munit.*

class ShadedGeneratedSmithySpec extends FunSuite {
  test("we don't have access to the generated KMS objects in their original package") {
    assertNoDiff(compileErrors("""com.amazonaws.kms.KMS.id == smithy4s.ShapeId("com.amazonaws.kms", "TrentService")"""),
      """error:
        |value amazonaws is not a member of com, but could be made available as an extension method.
        |
        |The following import might make progress towards fixing the problem:
        |
        |  import munit.Clue.generate
        |
        |com.amazonaws.kms.KMS.id == smithy4s.ShapeId("com.amazonaws.kms", "TrentService")
        |   ^
        |""".stripMargin)
  }

  test("we don't have access to the generated KMS objects in the shaded package") {
    assertNoDiff(compileErrors("""assertEquals(com.dwolla.config.smithy_shaded.com.amazonaws.kms.KMS.id, smithy4s.ShapeId("com.dwolla.config.smithy_shaded.com.amazonaws.kms", "TrentService"))"""),
      """error:
        |value KMS in package com.dwolla.config.smithy_shaded.com².amazonaws.kms cannot be accessed as a member of com.dwolla.config.smithy_shaded.com².amazonaws.kms.type from class ShadedGeneratedSmithySpec.
        |
        |where:    com  is a package
        |          com² is a package in package com.dwolla.config.smithy_shaded
        |assertEquals(com.dwolla.config.smithy_shaded.com.amazonaws.kms.KMS.id, smithy4s.ShapeId("com.dwolla.config.smithy_shaded.com.amazonaws.kms", "TrentService"))
        |                                                              ^
        |""".stripMargin)
  }
}
