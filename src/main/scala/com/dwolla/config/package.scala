package com.dwolla

import java.nio.charset.Charset

import cats.effect._
import cats.implicits._
import com.dwolla.fs2aws.kms._
import pureconfig.ConfigReader
import shapeless.tag
import shapeless.tag.@@

package object config {
  private[this] val secureStringRegex = "^SECURE: (.+)".r
  private[this] val utf8 = Charset.forName("UTF-8")

  implicit def SecureReader[F[_] : Effect](implicit decryptionClient: KmsDecrypter[F]): ConfigReader[F[SecurableString]] = {
    ConfigReader[String].map {
      case secureStringRegex(cryptotext) ⇒
        for {
          bytes ← decryptionClient.decrypt(base64DecodingTransform, cryptotext)
        } yield tagSecurableString(new String(bytes, utf8))
      case s ⇒ Sync[F].delay(tagSecurableString(s))
    }
  }

  type SecurableString = String @@ SecurableStringTag
  val tagSecurableString: String ⇒ SecurableString = tag[SecurableStringTag][String](_)

}

package config {
  trait SecurableStringTag
}
