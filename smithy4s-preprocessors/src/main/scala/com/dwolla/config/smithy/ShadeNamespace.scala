package com.dwolla.config.smithy

import software.amazon.smithy.build.transforms.FlattenNamespaces
import software.amazon.smithy.build.{ProjectionTransformer, TransformContext}
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.node.ObjectNode

class ShadeNamespace extends ProjectionTransformer {
  override def getName: String = "com.dwolla.config.smithy.ShadeNamespace"

  val sourceService: String = "TrentService"
  val sourceNamespace: String = "com.amazonaws.kms"
  val targetNamespacePrefix: String = "com.dwolla.config.smithy_shaded"

  override def transform(context: TransformContext): Model =
    new FlattenNamespaces().transform {
      context
        .toBuilder
        .projectionName(getName)
        .settings {
          ObjectNode.builder()
            .withMember("namespace", s"$targetNamespacePrefix.$sourceNamespace")
            .withMember("service", s"$sourceNamespace#$sourceService")
            .build()
        }
        .build()
    }
}
