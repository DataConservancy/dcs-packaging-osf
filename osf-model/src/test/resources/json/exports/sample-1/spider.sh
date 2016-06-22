#!/bin/bash
# set -vx
COOKIES_FILE=~/Downloads/cookies.txt
ESC_BASE_OSF_V2_URL="http:\/\/192.168.99.100:8000\/v2\/"
ESC_BASE_WB_V1_URL="http:\/\/192.168.99.100:7777\/v1\/"

ROOT_URL=$1

if [ -z $ROOT_URL ] ; 
then
  echo "Usage: $0 <root url>"
  exit 1
fi


BASE_OUT_DIR=`pwd`

function strip_prefix {
  echo $1 | \
           sed -e "s/^$ESC_BASE_OSF_V2_URL\(.*\)/v2\/\1/" | \
           sed -e "s/^$ESC_BASE_WB_V1_URL\(.*\)/v1\/\1/"
}

function download_json {
    local RELATIVE_OUT_DIR=`strip_prefix $1`
    local RELATIVE_OUT_FILE=${RELATIVE_OUT_DIR}${2}
    local ABS_OUT_DIR=${BASE_OUT_DIR}/${RELATIVE_OUT_DIR}
    ABS_OUT_FILE=${ABS_OUT_DIR}${2}
    if [ ! -f ${ABS_OUT_FILE} ] ;
    then
      echo "Downloading $1 to ${RELATIVE_OUT_FILE}"
      mkdir -p ${ABS_OUT_DIR} 
      curl -S -s -b $COOKIES_FILE $1 | jq . > ${ABS_OUT_FILE}
      download_rels ${ABS_OUT_FILE}
    fi
}

function download_rels {
  # jq -r .data.relationships[].links[].href $1 | \
  # jq -r ".[] // .data | .relationships[].links[].href" $1 | \
  jq -r ".data[]?.relationships[]?.links[].href" $1 | \
  while read -r REL_URL ; 
  do
    download_json $REL_URL index-01.json
  done

  jq -r ".data?.relationships[]?.links[].href" $1 | \
  while read -r REL_URL ; 
  do
    download_json $REL_URL index-01.json
  done
}

download_json $ROOT_URL index-01.json
