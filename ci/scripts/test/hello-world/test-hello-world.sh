#!/usr/bin/env bash
export TERM=screen-256color

APP_URL=${PCC_ENDPOINT:-127.0.0.1:8080}
echo ${CONNECT_URL:=connect}

if [ -z ${PCC_ENDPOINT} ]; then
    update-alternatives --set java /usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64/jre/bin/java
    export JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64
    export GF_JAVA=$JAVA_HOME/bin/java

    cd cloud-cache-prs/hello-world/

    ./gradlew startCluster

    if [ $? -ne 0 ]; then
        exit 1
    fi

    wait $!
    ./gradlew bootRun -PgemfireReleaseRepoUser=${GEMFIRE_RELEASE_REPO_USER} \
    -PgemfireReleaseRepoPassword=${GEMFIRE_RELEASE_REPO_PASSWORD} \
     -PspringBootVersion=${SPRING_BOOT_VERSION} \
     -PspringBootStarterVersion=${SPRING_BOOT_STARTER_VERSION} \
     -PjavaVersion=1.${JAVA_VERSION} &

    if [ $? -ne 0 ]; then
        echo "$(tput setaf 1)Could not run app"
        exit 1
    fi

    while ! lsof -i tcp:8080; do
      sleep 1
    done
fi
region_row_count=$(gfsh -e "${CONNECT_URL}" -e "query --query='Select * from /Hello'" | grep Rows | cut -f2 -d":")

if [[ ${region_row_count} -gt 0 ]] ; then
    echo "$(tput setaf 1)Make sure to destroy or clear your region :( Row count $region_row_count$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! No rows!$(tput sgr 0)"
fi

status_code=$(curl --write-out %{http_code} --silent --output /dev/null ${APP_URL}/hello)

if [[ ${status_code} -ne 200 ]] ; then
    echo "$(tput setaf 1)Le fail :( Site status $status_code$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! Site status $status_code$(tput sgr 0)"
fi

region_row_count=$(gfsh -e "${CONNECT_URL}" -e "query --query='Select * from /Hello'" | grep Rows | cut -f2 -d":")

if [[ ${region_row_count} -lt 1 ]] ; then
    echo "$(tput setaf 1)Le fail :( Row count $region_row_count$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! Row count $region_row_count$(tput sgr 0)"
    exit 0
fi

if [ -z ${PCC_ENDPOINT} ]; then
    ./gradlew stopAndCleanUpCluster
fi