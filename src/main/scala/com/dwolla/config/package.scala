package com.dwolla

import cats.*
import cats.syntax.all.*
import com.dwolla.fs2aws.kms.*
import monix.newtypes.NewtypeWrapped
import pureconfig.ConfigReader

package object config {
  private[this] val secureStringRegex = "^SECURE: (.+)".r

  def SecureReader[F[_] : Monad](decryptionClient: KmsAlg[F]): ConfigReader[F[SecurableString]] =
    ConfigReader[String].map {
      case secureStringRegex(cryptotext) =>
        for {
          bytes <- decryptionClient.decrypt(cryptotext)
        } yield SecurableString(bytes)
      case s => SecurableString(s).pure[F]
    }

  type SecurableString = SecurableString.Type
  object SecurableString extends NewtypeWrapped[String]
}
