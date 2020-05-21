#!/bin/sh
# This script will retrieve the labels.js file from map.wynncraft.com, parse
# the locations and coordinates that are present in it, and convert it to a
# json file.
MYDIR=$(cd $(dirname "$0") && pwd)
TARGET="$MYDIR/../src/main/resources/assets/wynntils/labels.json"

command -v curl > /dev/null 2>&1
if test $? -ne 0; then
  echo curl is required
  exit 1
fi

command -v gawk > /dev/null 2>&1
if test $? -ne 0; then
  echo gawk is required
  exit 1
fi

command -v jq > /dev/null 2>&1
if test $? -ne 0; then
  echo jq is required
  exit 1
fi

curl -s https://map.wynncraft.com/js/labels.js | gawk '
function jsonval(v)
{
  return "\""v"\": " SYMTAB[v]
}

function jsonvalquote(v)
{
  return "\""v"\": \"" SYMTAB[v] "\""
}

match($0, /fromWorldToLatLng\( *([0-9-]*), *[0-9-]*, *([0-9-]*), *ovconf/, a) {
  x = a[1];
  z = a[2];
}
match($0, /labelHtml\('"'"'(.*) *<div class="level">\[Lv. ([0-9+ -]*)\]<\/div>'"'"', '"'"'([0-9]*)'"'"'/, b) {
  name = b[1];
  gsub(/[ \t]+$/, "", name);
  gsub("\\\\", "", name);

  level = b[2]
  gsub(" ", "", level);

  switch (b[3]) {
case 25:
    layer=1; break
case 16:
    layer=2; break
case 14:
    layer=3; break
  }
  print "{ " jsonval("x") ", " jsonval("z") ", " jsonvalquote("name") ", " jsonval("layer") ", " jsonvalquote("level") " }"
}

match($0, /labelHtml\('"'"'([^[]*)'"'"', '"'"'([0-9]*)'"'"'/, b) {
  name = b[1];
  gsub(/[ \t]+$/, "", name);
  gsub("\\\\", "", name);

  switch (b[2]) {
case 25:
    layer=1; break
case 16:
    layer=2; break
case 14:
    layer=3; break
  }
  print "{ " jsonval("x") ", " jsonval("z") ", " jsonvalquote("name") ", " jsonval("layer") " }"
}
' | jq -s '{labels: .}' > "$TARGET"

echo Finished updating "$TARGET"
