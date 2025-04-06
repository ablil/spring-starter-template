#!/usr/bin/env bash

set -e

REPOSITORY=$1
AUTHOR=$2

group="com.github.$AUTHOR"
directory=$(echo $package_name | tr "." "/")


sed -i -e "s/com.example/$group/g" -e "s/spring-starter-template/$REPOSITORY/g" -e "s/ghcr.io\/ablil/ghcr.io\/$AUTHOR/g" build.gradle
sed -i "s/spring-starter-template/$REPOSITORY/g" settings.gradle
sed -i "s/ablil/$AUTHOR/g" .github/CODEOWNERS LICENSE

# rename packages
mkdir -p src/main/kotlin/$directory
mkdir -p src/test/kotlin/$directory

find src/main src/test -type f -exec sed -i s/com.example/$group/g {} +


mv src/main/kotlin/com/example/* src/main/kotlin/$directory
mv src/test/kotlin/com/example/* src/test/kotlin/$directory


rm .github/workflows/cleanup.yaml
rm cleanup.sh

# format code again
./gradlew spotlessApply

git reset
git add src/main src/test build.gradle settings.gradle .github/workflows/cleanup.yaml .github/CODEOWNERS cleanup.sh
git commit -m "Change package name from com.example to $group"

