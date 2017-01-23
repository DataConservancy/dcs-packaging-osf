#!/bin/bash
#set -vx
JQ_FILTER='if .data | type == "array" then .data[] else .data end | .relationships[].links.related.href'
ACCEPT_HEADER="Accept: application/vnd.api+json;version=2.2"


# Returns 0 (i.e. true) if the supplied URL has already been 'seen'.  Seen
# URLs are presumed to have already been harvested.  If a URL has been seen,
# then it does not need to be re-downloaded or processed.
function seen() {
  egrep "^$1$" seen 2>&1 > /dev/null
}

# Downloads the content at the supplied URL and stores it in the filesystem
# according to the hostname, port, and url path of the URL.
#
# If the URL has been 'seen', this function will do nothing.  If the URL has
# not been seen, its contents are downloaded and saved as described, and the
# URL is marked as being seen.
#
# If the file to be downloaded already exists on the filesystem, it will not
# be overwritten.
function download() {
  local url="$1"
  if [ ! -z $2 ] ;
  then
    filename="$2"
  else
    echo "$url" | egrep '.*\?page=[0-9]$' 2>&1 >/dev/null
    if [ $? == 0 ] ;
    then
      filename="index-0`echo $url | cut -f 2 -d '='`"
    else
      filename="index.json"
    fi
  fi

  seen $url

  if [ $? != 0 ] ;
  then
    echo $url >> seen
    wget -nc --header="$ACCEPT_HEADER" -w 1 --random-wait -r --default-page=$filename $url
    local rc=$?
    if [ $rc != 0 ] ;
    then
      echo "Error downloading $url: $rc"
    fi
  else
    echo "Ignoring seen URL $url"
  fi
}

# Downloads the content at the supplied relationship url.  If the relationship
# URL contains query parameters (like ?filter=...), the URL query parameters are
# used to generate a filename that is cross-platform compatible.
function downloadrel() {
  local rel="$1"
  # If the relationship URL has query parameters, base64 encode them, and use the base64
  # query parameters as the name of the file to be downloaded.  Wget still appends query
  # parameters to the provided filename, which is unfortunate.  This is dealt with in the
  # process phase.
  filename=`echo $rel | jq -rR 'rindex("?") as $r | if $r then (.[$r:] | @base64) else "" end'`
  sleep 1
  download $rel $filename
}

# Extracts the JSONAPI relationships from the supplied file and downloads the
# content for each extracted relationship.  Does _not_ process relationships
# that point to localhost, or relationships named 'view_only_links'.
function processrelsinfile() {
  local file=$1
  for rel in `jq -r "$JQ_FILTER" < $file | grep -v view_only_links | grep -v localhost` ;
  do
    download $rel
  done
}

# Extracts the JSONAPI relationships from the document at the supplied URL,
# and downloads the content for each extracted relationship.  Does _not_ process
# relationships that point to localhost, or relationships named
# 'view_only_links'.
function downloadandprocessrelsinurl() {
  local URL="$1"
  download $URL
  for rel in `curl -sH "$ACCEPT_HEADER" $URL | jq -r "$JQ_FILTER" | grep -v view_only_links` ;
  do
    downloadrel $rel
  done
}

# Re-bases the supplied file to a new base directory.  For example, if
# the supplied file is "api.osf.io/v2/path/to/file" and the supplied destination
# base directory is "localhost/8000", "api.osf.io/v2/path/to/file" will be
# copied to "localhost/8000/v2/path/to/file".  This function makes assumptions
# about the source base directory, specifically that it is equal to the
# first two path segments separated by '/' (in this example, the source base
# directory would be "api.osf.io/v2")
function copytodestbase() {
  # relative file path, not prefixed with '/', including base directory like
  # "api.osf.io/v2/path/to/file"
  local file="$1"
  # destination base directory which will contain processed file, e.g.
  # "localhost/8000/" (ending with trailing slash)
  local destbasedir="$2"

  local overwrite="$3"

  i=`echo $file | cut -f 2- -d "/"`
  mkdir -p ${destbasedir}`dirname $i`
  if [ $overwrite == "true" ] ;
  then
      cp -n $f ${destbasedir}`dirname $i`
  else
      cp $f ${destbasedir}`dirname $i`
  fi
}

# Manipulates the file name and URLs found in the file.  If the supplied
# filename contains a '?' character, the filename is truncated to the characters
# that appear before the '?' character.  The '?' and any characters thereafter
# are truncated from the filename.
#
# The file is examined for any URLs.  Urls in the form of 'https://api.osf.io'
# are replaced with 'http://localhost/8000'.  Relationship URLs that contain
# query parameters are rewritten to encode the query parameters as part of the
# url path.
function processurls() {
  local file="$1"

  # Unfortunately, wget appends query parameters to
  # saved filenames, even when we supply one.  So this
  # hack checks the filename to see if it contains
  # query parameters, and truncates them.
  converted=`echo $file | cut -f 1 -d '?'`
  echo "$file $converted"
  if [ "$converted" != "$file" ] ;
  then
    mv $file $converted
    file="$converted"
  fi

  # re-write URLs from api.osf.io to localhost:8000
  sed -f urlconvert.sed $file > $file.tmp

  # re-write URLs containing query parameters by base64
  # the parameters.
  jq -f urlconvert.jq $file.tmp > $file
  rm $file.tmp
}

# Removes files related to a harvest operation.  This includes the files that
# were originally downloaded as part of a harvest and any URLs that were seen.
# It will not remove files produced after processing harvested files.
if [ "$1" == "--clean" ] ;
then
  rm -rf seen api.osf.io
  exit 0;
fi

# Processes harvested files.  Processing is comprised of URL rewrite operations
# of the harvested content, and storing processed files in a new directory
# structure that their new URLs dictate.
#
# This will:
# - re-write all URLs in JSON documents to use localhost:8000 instead of
#   api.osf.io
# - re-write all URLs in JSON documents that carry query parameters by
#   base64 encoding the query parameters
if [ "$1" == "--process" ] ;
then
  overwrite="false"

  shift;
  while [ $# -gt 0 ] ;
  do
    if [ "$1" == "--overwrite" ] ;
    then
        overwrite="true"
    fi
    shift;
  done

  for f in `find api.osf.io/v2 -type f` ;
  do
    copytodestbase $f localhost/8000/ $overwrite
  done

  for f in `find api.osf.io/v1 -type f` ;
  do
    copytodestbase $f localhost/7777/ $overwrite
  done

  for f in `find localhost -type f` ;
  do
    processurls $f
  done

  exit $?;
fi

if [ "$1" == "--harvest" ] ;
then
  if [ -z "$2" ] ;
  then
    echo "Expecting a URL or a file!"
    exit 1
  fi

  echo $2 | egrep ^http 2>&1 > /dev/null
  if [ $? == 0 ] ;
  then
    downloadandprocessrelsinurl $2
    if [ $? == 0 ] ;
    then
      for source in `find api.osf.io -type f` ;
      do
        echo "Downloading relationships found in URL '$source' ..."
        processrelsinfile $source
      done
    fi
    exit $?;
  else
    echo "Downloading relationships found in file '$2' ..."
    processrelsinfile $2
    exit $?;
  fi
fi

echo "Usage $0 [ --process [ --overwrite ] | --clean | --harvest  <url or file> ]"
exit 1
