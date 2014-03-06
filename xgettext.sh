find src -iname "*.java" | xargs xgettext -k -ktrc:1c,2 -ktrnc:1c,2,3 -ktr -kmarktr -ktrn:1,2 -o po/keys.pot
