#!/usr/bin/env bash
export TERM=screen-256color

if [ -z ${PCF_API_ENDPOINT} ]; then

    cd cloud-cache-prs/${APP_PATH}

   if [[ $(basename `pwd`) == "redis" ]]; then
        echo exit 0 > /usr/sbin/policy-rc.d
        apt-get update
        apt-get install -y redis-server
    fi

    ./gradlew clean bootRun \
        -PgemfireReleaseRepoUser=${GEMFIRE_RELEASE_REPO_USER} \
        -PgemfireReleaseRepoPassword=${GEMFIRE_RELEASE_REPO_PASSWORD} \
        -PjavaVersion=1.${JAVA_VERSION} > log-output.txt
else
    apt-get update
    apt-get install -y cf-cli

    cf login --skip-ssl-validation -a ${PCF_API_ENDPOINT} -u admin -p ${PCC_PASSWORD} -o system -s pipeline
    cf logs ${APP_NAME} --recent > log_output.txt
fi

grep "Subscriber received" log-output.txt

if [[ $? -eq 1 ]] ; then
    echo "$(tput setaf 1)Le fail :( No 'Subscriber received' message found!$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! Subscriber received message.$(tput sgr 0)"
    exit 0
fi