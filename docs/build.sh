#!/bin/bash
# This part is used to fetch data from backend and build it into slate compatible md
echo "Fetching data from backend " $WEB_SERVER_ADDRESS
curl --max-time 5 \
     --retry 5 \
     --retry-delay 10 \
     --retry-connrefused \
     -X GET $WEB_SERVER_ADDRESS/v3/api-docs.yaml > ./pre-build/api-docs.yaml
node /widdershins/widdershins --language-tabs 'javascript:Javascript' --yaml ./pre-build/api-docs.yaml ./source/index.html.md

# And this builds static html file and serves it via slate script
/srv/slate/slate.sh build && /srv/slate/slate.sh serve
