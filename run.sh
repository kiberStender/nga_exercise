#!/bin/sh

if [[ -z $1 ]]
then
  # sbt clean test &&
  sbt "main/run 03-delivery/target/scala-2.13/test-classes/test_csv_3"
else
  sbt clean compile &&
  sbt "main/run $1"
fi