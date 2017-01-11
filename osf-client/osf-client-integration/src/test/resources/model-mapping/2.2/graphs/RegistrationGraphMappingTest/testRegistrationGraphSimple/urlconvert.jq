def tweak: rindex("?") as $r | if $r then .[0:$r] + (.[$r:] | @base64) else . end;
# Apply f to composite entities recursively, and to atoms
def walk(f):
  . as $in
  | if type == "object" then
      reduce keys[] as $key
        ( {}; . + { ($key):  ($in[$key] | walk(f)) } ) | f
  elif type == "array" then map( walk(f) ) | f
  else f
  end;
walk( if type == "object" and has("href") then .href |= tweak else . end )
