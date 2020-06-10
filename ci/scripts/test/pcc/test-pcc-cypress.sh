#!/usr/bin/env bash
export TERM=screen-256color
cd private-cloud-cache-examples/${APP_PATH}/frontend
yarn add cypress --dev
CYPRESS_baseUrl=${PCC_ENDPOINT} yarn cypress run