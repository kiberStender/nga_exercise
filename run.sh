#!/bin/sh

# In order to properly run the program, run sbt test first to have the below file placed in the correctly place, or pass another url
sbt "main/run 03-delivery/target/scala-2.13/test-classes/test_csv"