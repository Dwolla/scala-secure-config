package com.dwolla

import cats._
import cats.syntax.all._
import com.dwolla.fs2aws.kms._
import pureconfig.ConfigReader
import shapeless.tag
import shapeless.tag.@@

package object config {
  private[this] val secureStringRegex = "^SECURE: (.+)".r

  def SecureReader[F[_] : Monad](decryptionClient: KmsAlg[F]): ConfigReader[F[SecurableString]] =
    ConfigReader[String].map {
      case secureStringRegex(cryptotext) =>
        for {
          bytes <- decryptionClient.decrypt(cryptotext)
        } yield tagSecurableString(bytes)
      case s => tagSecurableString(s).pure[F]
    }

  type SecurableString = String @@ SecurableStringTag
  val tagSecurableString: String => SecurableString = tag[SecurableStringTag][String](_)
}

package config {
  trait SecurableStringTag
}
