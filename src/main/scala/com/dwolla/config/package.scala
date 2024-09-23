package com.dwolla

import cats.effect.*
import cats.syntax.all.*
import com.dwolla.smithy_shaded.com.amazonaws.kms.{CiphertextType, KMS}
import fs2.compression.Compression
import monix.newtypes.NewtypeWrapped
import mouse.all.*
import pureconfig.ConfigReader
import smithy4s.Blob
import smithy4s.aws.{AwsClient, AwsEnvironment}

package object config {
  private[this] val secureStringRegex = "^SECURE: (.+)".r

  def SecureReader[F[_] : Async : Compression](awsEnv: AwsEnvironment[F]): Resource[F, ConfigReader[F[SecurableString]]] =
    AwsClient(KMS, awsEnv).map { kms =>
      ConfigReader[String].map {
        case secureStringRegex(cryptotext) =>
          kms.decrypt(CiphertextType(Blob(cryptotext.getBytes())))
            .map(_.plaintext) // TODO does this need to be base64-decoded?
            .liftOptionT
            .getOrRaise(new RuntimeException("boom")) // TODO convert to a better exception
            .map(_.value.toUTF8String)
            .map(SecurableString(_))

        case s => SecurableString(s).pure[F]
      }
    }

  type SecurableString = SecurableString.Type
  object SecurableString extends NewtypeWrapped[String]
}
