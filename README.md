# Secure Config

![Dwolla/scala-secure-config CI](https://github.com/Dwolla/scala-secure-config/actions/workflows/ci.yml/badge.svg)
[![license](https://img.shields.io/github/license/Dwolla/scala-secure-config.svg?style=flat-square)](https://github.com/Dwolla/scala-secure-config/blob/master/LICENSE)

 [PureConfig](https://pureconfig.github.io) [ConfigReader](https://github.com/pureconfig/pureconfig/blob/master/core/src/main/scala/pureconfig/ConfigReader.scala) for automatically decrypting encrypted config values using AWS KMS.

## Artifacts

#### Library

```scala
"com.dwolla" %% "secure-config" % "0.1-SNAPSHOT"
```

## Examples

```scala
case class JenkinsConfig[F[_]](baseUri: Uri, username: String, password: F[SecurableString])

val config: Stream[IO, JenkinsConfig[Id]] = 
  Stream.eval(loadConfigF[IO, JenkinsConfig[IO]]).through( _.evalMap { b ⇒
    val cryptoTexts = b.password :: HNil
  
    cryptoTexts.sequence.map {
      case password :: HNil ⇒
        JenkinsConfig[Id](b.baseUri, b.username, password)
    }
  })
```

(The hope is that the pipe can eventually be made generic so that knowledge of the specific encrypted fields doesn't need to be maintained in the pipe.)
