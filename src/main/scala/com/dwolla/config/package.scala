package com.dwolla

import cats.effect.*
import cats.syntax.all.*
import com.dwolla.config.smithy_shaded.com.amazonaws.kms.{CiphertextType, KMS}
import fs2.compression.Compression
import monix.newtypes.NewtypeWrapped
import mouse.all.*
import pureconfig.ConfigReader
import scodec.bits.ByteVector
import smithy4s.Blob
import smithy4s.aws.{AwsClient, AwsEnvironment}

import scala.util.control.NoStackTrace

package object config {
  private[this] val secureStringRegex = "^SECURE: (.+)".r

  def SecureReader[F[_] : Async : Compression](awsEnv: AwsEnvironment[F]): Resource[F, ConfigReader[F[SecurableString]]] =
    AwsClient(KMS, awsEnv).map { kms =>
      ConfigReader[String].map {
        case secureStringRegex(ciphertext) =>
          ByteVector.fromBase64(ciphertext)
            .map(_.toArray)
            .map(Blob(_))
            .liftTo[F](InvalidCiphertextException(ciphertext))
            .map(CiphertextType(_))
            .flatMap(kms.decrypt(_))
            .map(_.plaintext)
            .liftOptionT
            .getOrRaise(UnexpectedMissingPlaintextResponseException)
            .map(_.value.toUTF8String)
            .map(SecurableString(_))

        case s => SecurableString(s).pure[F]
      }
    }

  type SecurableString = SecurableString.Type
  object SecurableString extends NewtypeWrapped[String]
}

class InvalidCiphertextException(txt: String)
  extends RuntimeException(s"The provided ciphertext $txt is invalid, probably because it is not base64 encoded")
object InvalidCiphertextException {
  def apply(txt: String): Throwable = new InvalidCiphertextException(txt)
}

object UnexpectedMissingPlaintextResponseException
  extends RuntimeException("the KMS response was expected to contain a plaintext field, but it did not")
    with NoStackTrace
