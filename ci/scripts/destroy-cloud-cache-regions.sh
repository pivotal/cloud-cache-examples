#!/usr/bin/env bash
export TERM=screen-256color

gfsh -e "${CONNECT_URL}" -e "destroy region --name=Hello --if-exists=true"
gfsh -e "${CONNECT_URL}" -e "destroy region --name=BikeIncidentsByZip --if-exists=true"
gfsh -e "${CONNECT_URL}" -e "destroy region --name=ClusteredSpringSessions --if-exists=true"

region_count=$(gfsh -e "${CONNECT_URL}" -e "list regions" | grep 'Hello\|BikeIncidentsByZip\|ClusteredSpringSessions' | wc -l)

if [[ ${region_count} -gt 0 ]] ; then
    echo "$(tput setaf 1)Regions not destroyed successfully :( $region_count$(tput sgr 0)"
    exit 1
else
    echo "$(tput setaf 2)Success! Specified regions destroyed!$(tput sgr 0)"
fi