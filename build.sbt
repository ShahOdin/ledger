name := "ledger"

version := "0.1"

scalaVersion := "3.1.2"

libraryDependencies ++= Seq(
  "org.scalameta" %% "munit" % "0.7.29",
  "org.scalacheck" %% "scalacheck" % "1.16.0",
  "org.scalameta" %% "munit-scalacheck" % "0.7.29"
).map(_ % Test)
