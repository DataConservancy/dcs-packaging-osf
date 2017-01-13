#!/bin/bash
#set -vx
JQ_FILTER='if .data | type == "array" then .data[] else .data end | .relationships[].links.related.href'
ACCEPT_HEADER="Accept: application/vnd.api+json;version=2.2"

#
# Returns 0 (i.e. true) if the supplied URL has already been 'seen'.  Seen
# URLs are presumed to have already been harvested.  If a URL has been seen,
# then it does not need to be re-downloaded or processed.
#
function seen() {
  egrep "^$1$" seen 2>&1 > /dev/null
}

#
# Downloads the content at the supplied URL and stores it in the filesystem
# according to the hostname, port, and url path of the URL.
#
# If the URL has been 'seen', this function will do nothing.  If the URL has
# not been seen, its contents are downloaded and saved as described, and the
# URL is marked as being seen.
#
function download() {
  local url="$1"
  local filename="index.json"
  if [ ! -z $2 ] ;
  then
    filename="$2"
  fi

  seen $url

  if [ $? != 0 ] ;
  then
    echo $url >> seen
    wget -nc -q --header="$ACCEPT_HEADER" -w 1 --random-wait -r --default-page=$filename $url
  fi
}

#
# Removes files related to a harvest operation.  This includes the files that
# were originally downloaded as part of a harvest and any URLs that were seen.
# It will not remove files produced after processing harvested files.
#
if [ "$1" == "--clean" ] ;
then
  rm -rf seen api.osf.io
  exit 0;
fi

#
# Processes harvested files.  Processing is comprised of URL rewrite operations
# of the harvested content, and storing processed files in a new directory
# structure that their new URLs dictate.
#
if [ "$1" == "--process" ] ;
then
  for f in `find api.osf.io/v2 -type f` ;
  do
    i=`echo $f | cut -f 2- -d "/"`
    mkdir -p localhost/8000/`dirname $i`
    cp -n $f localhost/8000/`dirname $i`
  done

  for f in `find api.osf.io/v1 -type f` ;
  do
    i=`echo $f | cut -f 2- -d "/"`
    mkdir -p localhost/7777/`dirname $i`
    cp -n $f localhost/7777/`dirname $i`
  done

  for f in `find localhost -type f` ;
  do
    converted=`echo $f | cut -f 1 -d '?'`
    echo "$f $converted"
    if [ "$converted" != "$f" ] ;
    then
      mv $f $converted
      f="$converted"
    fi
    sed -f urlconvert.sed $f > $f.tmp
    jq -f urlconvert.jq $f.tmp > $f
    rm $f.tmp
  done

  exit $?;
fi

if [ "$1" == "--harvest" ] ;
then
  URL="$2"
  download $URL
  for rel in `curl -sH "$ACCEPT_HEADER" $URL | jq -r "$JQ_FILTER" | grep -v view_only_links` ;
  do
    filename=`echo $rel | jq -rR 'rindex("?") as $r | if $r then (.[$r:] | @base64) else "" end'`
    download $rel $filename
  done

  for source in `find api.osf.io -type f` ;
  do
    echo "Downloading relationships found in '$source' ..."

    if [ $? == 0 ] ;
    then
      for rel in `jq -r "$JQ_FILTER" < $source | grep -v view_only_links | grep -v localhost` ;
      do
        filename=`echo $rel | jq -rR 'rindex("?") as $r | if $r then (.[$r:] | @base64) else "" end'`
        download $rel $filename
      done
    fi

  done
  exit $?;
fi

echo "Usage $0 [ --process | --clean | --harvest  <url> ]"
exit 1
