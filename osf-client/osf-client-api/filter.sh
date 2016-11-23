git filter-branch -f --tree-filter "find . -type f|egrep -v '(java/org/dataconservancy/cos/osf/client/service|pom.xml)'| sed -e '/^\.$/d' | xargs rm -df"
git filter-branch -f --tree-filter "find . -name '*Factory.java' | sed -e '/^\.$/d' | xargs rm -f"
