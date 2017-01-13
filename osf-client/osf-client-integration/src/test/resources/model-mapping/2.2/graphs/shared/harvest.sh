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
# If the file to be downloaded already exists on the filesystem, it will not
# be overwritten.
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
# This will:
# - re-write all URLs in JSON documents to use localhost:8000 instead of 
#   api.osf.io
# - re-write all URLs in JSON documents that carry query parameters by
#   base64 encoding the query parameters
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
    # Unfortunately, wget appends query parameters to
    # saved filenames, even when we supply one.  So this
    # hack checks the filename to see if it contains
    # query parameters, and truncates them. 
    converted=`echo $f | cut -f 1 -d '?'`
    echo "$f $converted"
    if [ "$converted" != "$f" ] ;
    then
      mv $f $converted
      f="$converted"
    fi

    # re-write URLs from api.osf.io to localhost:8000
    sed -f urlconvert.sed $f > $f.tmp

    # re-write URLs containing query parameters by base64
    # the parameters.
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
    # If the relationship URL has query parameters, base64 encode them, and use the base64
    # query parameters as the name of the file to be downloaded.  Wget still appends query
    # parameters to the provided filename, which is unfortunate.  This is dealt with in the
    # process phase.
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
        # If the relationship URL has query parameters, base64 encode them, and use the base64
        # query parameters as the name of the file to be downloaded.
        filename=`echo $rel | jq -rR 'rindex("?") as $r | if $r then (.[$r:] | @base64) else "" end'`
        download $rel $filename
      done
    fi

  done
  exit $?;
fi

echo "Usage $0 [ --process | --clean | --harvest  <url> ]"
exit 1
