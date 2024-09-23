package com.dwolla.smithy

import cats.syntax.all._
import software.amazon.smithy.build.{ProjectionTransformer, TransformContext}
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.Shape

import java.util.stream.Collectors
import scala.collection.JavaConverters._

class ShadeNamespace extends ProjectionTransformer {
  override def getName: String = "com.dwolla.smithy.ShadeNamespace"

  private val namespacesToRename: Set[String] = Set("com.amazonaws.kms")

  override def transform(context: TransformContext): Model = {
    val renames =
      context.getModel
        .shapes().collect(Collectors.toList[Shape]).asScala.toList
        .map(_.getId)
        .filter(id => namespacesToRename.contains(id.getNamespace))
        .fproduct(id => id.withNamespace(s"com.dwolla.smithy_shaded.${id.getNamespace}"))
        .toMap.asJava

    context.getTransformer.renameShapes(context.getModel, renames)
  }
}
