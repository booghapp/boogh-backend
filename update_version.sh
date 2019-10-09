#!/bin/bash

echo "Will replace $1 with $2 ...."

echo "Replacing in pom.xml"
sed -i -e "s|<version>$1</version>|<version>$2</version>|g" pom.xml

echo "Replacing in package.json"
sed -i -e "s|\"version\": \"$1\"|\"version\": \"$2\"|g" package.json
