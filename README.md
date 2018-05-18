# Secure Config

[![Travis](https://img.shields.io/travis/Dwolla/scala-secure-config.svg?style=flat-square)](https://travis-ci.org/Dwolla/scala-secure-config)
[![Bintray](https://img.shields.io/bintray/v/dwolla/maven/secure-config.svg?style=flat-square)](https://bintray.com/dwolla/maven/secure-config/view)
[![license](https://img.shields.io/github/license/Dwolla/scala-secure-config.svg?style=flat-square)](https://github.com/Dwolla/scala-secure-config/blob/master/LICENSE)

Tagged type and [PureConfig](https://pureconfig.github.io) [ConfigReader](https://github.com/pureconfig/pureconfig/blob/master/core/src/main/scala/pureconfig/ConfigReader.scala) for automatically decrypting encrypted config values in TypeSafe Config files.

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
