#!/usr/bin/env bash
export TERM=screen-256color

APP_URL=${PCC_ENDPOINT:-127.0.0.1:8080}

if [ -z ${PCC_ENDPOINT} ]; then

    cd cloud-cache-prs/${APP_PATH}

    apt-get update
    apt-get install -y lsof

    if [[ $(basename `pwd`) == "redis" ]]; then
        echo exit 0 > /usr/sbin/policy-rc.d
        apt-get install -y redis-server
    fi

    ./gradlew clean bootRun \
        -PgemfireReleaseRepoUser=${GEMFIRE_RELEASE_REPO_USER} \
        -PgemfireReleaseRepoPassword=${GEMFIRE_RELEASE_REPO_PASSWORD} \
        -PjavaVersion=1.${JAVA_VERSION} &

    if [ $? -ne 0 ]; then
        echo "$(tput setaf 1)Could not run app"
        exit 1
    fi

    while ! lsof -i tcp:8080; do
        sleep 1
    done
fi

CSRF_TOKEN=$(curl -k -c cookies.txt -b cookies.txt -L ${APP_URL} | grep csrf | grep -v post | cut -d" " -f4 | cut -d'"' -f 2)

echo first token: ${CSRF_TOKEN}

curl -k -c cookies.txt -b cookies.txt --write-out %{http_code} -D - -d "username=user&password=password&_csrf=${CSRF_TOKEN}" -L ${APP_URL}/login > temp_page_output.txt

CSRF_TOKEN=$(cat temp_page_output.txt | grep csrf | grep setValue | cut -d" " -f8 | cut -d'"' -f2)
echo new token: ${CSRF_TOKEN}

CORRECT_PAGE_FOUND=$(cat temp_page_output.txt | grep -l "Home</title>" | wc -l)

if [[ ${CORRECT_PAGE_FOUND} -ne 1 ]] ; then
    echo "$(tput setaf 1)Le fail :( Could not login to secured page!$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! Logged into secured page.$(tput sgr 0)"
fi

curl -k -c cookies.txt -b cookies.txt --write-out %{http_code} -D - -d "key=EGG&value=EASTER&_csrf=${CSRF_TOKEN}" -L ${APP_URL}/setValue > temp_page_output.txt

FOUND_NEW_ATTRIBUTE=$(cat temp_page_output.txt | grep -l "EASTER" | wc -l)

if [[ ${FOUND_NEW_ATTRIBUTE} -ne 1 ]] ; then
    echo "$(tput setaf 1)Le fail :( Submitted attribute not found!$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! Correctly set new attribute.$(tput sgr 0)"
    exit 0
fi