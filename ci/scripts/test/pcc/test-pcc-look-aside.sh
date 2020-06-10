#!/usr/bin/env bash
export TERM=screen-256color

region_row_count=$(gfsh -e "${CONNECT_URL}" -e 'describe region --name="BikeIncidentsByZip"' | grep size | cut -f3 -d"|")

if [[ ${region_row_count} -gt 0 ]] ; then
    echo "$(tput setaf 1)Make sure to destroy or clear your region :( Row count $region_row_count$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! No rows!$(tput sgr 0)"
fi

status_code=$(curl -d zipCode="97007" --write-out %{http_code} --silent --output /dev/null ${PCC_ENDPOINT})

if [[ ${status_code} -ne 200 ]] ; then
    echo "$(tput setaf 1)Le fail :( Site status $status_code$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! Site status $status_code$(tput sgr 0)"
fi

region_row_count=$(gfsh -e "${CONNECT_URL}" -e 'describe region --name="BikeIncidentsByZip"' | grep size | cut -f3 -d "|")

if [[ ${region_row_count} -lt 1 ]] ; then
    echo "$(tput setaf 1)Le fail :( Row count $region_row_count$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! Row count $region_row_count$(tput sgr 0)"
    exit 0
fi