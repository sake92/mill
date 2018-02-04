#!/usr/bin/env bash

set -eux

# Starting from scratch...
git clean -xdf

# First build using SBT
sbt bin/test:assembly

# Build Mill using SBT
target/bin/mill --all _.publishLocal releaseAssembly

mv releaseAssembly ~/mill

git clean -xdf

# Second build & run tests using Mill

~/mill --all {core,scalalib,scalajslib}.test devAssembly
~/mill integration.test mill.integration.AmmoniteTests
~/mill integration.test "mill.integration.{AcyclicTests,BetterFilesTests,JawnTests}"
~/mill devAssembly
