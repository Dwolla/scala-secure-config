package com.dwolla.config

import cats.effect.*
import fs2.compression.Compression
import fs2.io.file.Files
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import pureconfig.module.catseffect.loadF
import pureconfig.{ConfigReader, ConfigSource}
import smithy4s.aws.{AwsEnvironment, AwsRegion}

object ExampleApp extends ResourceApp.Simple {
  private def reader[F[_] : Async : Compression : Files : Network] =
    for {
      httpClient <- EmberClientBuilder.default.build
      awsEnv <- AwsEnvironment.default[F](httpClient, AwsRegion.US_WEST_2) // TODO get region from environment
      secureReader <- SecureReader[F](awsEnv)
    } yield secureReader

  // encrypt some text using `aws kms encrypt --key-id alias/my-key --plaintext foo | jq -r .CiphertextBlob` and replace the base64 text below
  val base64CipherText = "AQICAHh38+DAqADvcRLU4+t2AYhr82YbZuuFQdjdX95NTppHhwEQd+ovBiiMlelM0yL+97WRAAAAYTBfBgkqhkiG9w0BBwagUjBQAgEAMEsGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMlNQWVvaAt/VACynHAgEQgB4AzBpBA1ozpJFZTIhC91Q+Emlx40gbhTFmXyqBE+g="

  override def run: Resource[IO, Unit] =
    reader[IO].evalMap { implicit r: ConfigReader[IO[SecurableString]] =>
      loadF[IO, Foo[IO]](ConfigSource.string(s"""foo = "SECURE: $base64CipherText""""))
        .flatTap(IO.println(_))
        .flatMap(_.foo)
        .flatMap(IO.println(_))
    }
}

case class Foo[F[_]](foo: F[SecurableString])
object Foo {
  implicit def configReader[F[_]](implicit ev: ConfigReader[F[SecurableString]]): ConfigReader[Foo[F]] = ConfigReader.forProduct1("foo")(Foo.apply[F] _)
}
