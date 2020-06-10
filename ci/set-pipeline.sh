#!/bin/bash

generate_pipeline_file() {
 cat <<EOF
---
$(cat resources.yml)
$(python3 render_pipeline_jobs.py)
EOF
}

generate_secrets() {
  cat << EOF
---
$(lpass show "Shared-PCC-Experience/credentials.yml (hello-world)" --notes)
EOF
}

fly -t pccxp set-pipeline --pipeline cloud-cache-developer-guide-examples \
    --config <(generate_pipeline_file) \
    --load-vars-from <(generate_secrets)
