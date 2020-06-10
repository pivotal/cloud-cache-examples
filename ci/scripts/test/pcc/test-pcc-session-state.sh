#!/usr/bin/env bash
export TERM=screen-256color

row_count=$(gfsh -e "${CONNECT_URL}" -e 'query --query="SELECT session.id, attribute.key, attribute.value FROM /ClusteredSpringSessions session, session.attributes attribute"' | grep Rows | cut -f2 -d ":")

if [[ ${row_count} -ne 0 ]] ; then
    echo "$(tput setaf 1)Make sure to destroy or clear your region :( Row count $row_count$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! No rows!$(tput sgr 0)"
fi

curl -k -c cookies.txt -b cookies.txt -H "Content-Type: text/plain" -d "GARGOYLES" -L ${PCC_ENDPOINT}/addSessionNote

row_count=$(gfsh -e "${CONNECT_URL}" -e 'query --query="SELECT session.id, attribute.key, attribute.value FROM /ClusteredSpringSessions session, session.attributes attribute"' | grep Rows | cut -f2 -d ":")

if [[ ${row_count} -ne 1 ]] ; then
    echo "$(tput setaf 1)Le fail :( Row count $row_count$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! Row count $row_count$(tput sgr 0)"
    exit 0
fi

