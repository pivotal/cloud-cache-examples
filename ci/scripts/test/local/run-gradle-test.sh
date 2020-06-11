#!/usr/bin/env bash
export TERM=screen-256color

cd cloud-cache-prs/${APP_PATH}

./gradlew clean test -PgemfireReleaseRepoUser=${GEMFIRE_RELEASE_REPO_USER} \
 -PgemfireReleaseRepoPassword=${GEMFIRE_RELEASE_REPO_PASSWORD} \
-PjavaVersion=1.${JAVA_VERSION}