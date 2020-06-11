#!/usr/bin/env bash

TMP_DIR=$(pwd)
cd cloud-cache-prs/${APP_PATH}

./gradlew build \
    -PgemfireReleaseRepoUser=${GEMFIRE_RELEASE_REPO_USER} \
    -PgemfireReleaseRepoPassword=${GEMFIRE_RELEASE_REPO_PASSWORD} \
    -PspringBootVersion=${SPRING_BOOT_VERSION} \
    -PspringBootStarterVersion=${SPRING_BOOT_STARTER_VERSION} \
    -PjavaVersion=1.${JAVA_VERSION}

cp build/libs/${JAR_NAME}.jar ~/..${TMP_DIR}/${OUTPUT_DIR}