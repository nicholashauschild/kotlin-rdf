#!/bin/bash

version=$(cat 'version.txt')
echo "Read version [$version] from version.txt"

if ! [[ $version =~ ^.*SNAPSHOT$ ]];
then
    echo "Tagging commit with release tag for version $version"
    git tag -a "v$version" -m "Release $version"
fi