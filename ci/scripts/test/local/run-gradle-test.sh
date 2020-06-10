#!/usr/bin/env bash
export TERM=screen-256color

cd private-cloud-cache-examples/${APP_PATH}

./gradlew clean test -PgemfireReleaseRepoUser=${GEMFIRE_RELEASE_REPO_USER} \
 -PgemfireReleaseRepoPassword=${GEMFIRE_RELEASE_REPO_PASSWORD} \
-PjavaVersion=1.${JAVA_VERSION}