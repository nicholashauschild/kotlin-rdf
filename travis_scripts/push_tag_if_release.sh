#!/bin/bash

version=$(cat 'version.txt')
echo "Read version [$version] from version.txt"

if ! [[ $version =~ ^.*SNAPSHOT$ ]];
then
    git config --global user.email "builds@travis-ci.com"
    git config --global user.name "Travis CI"

    echo "Pushing tag to remote v$version"
    git push -q https://$githubToken@github.com/$TRAVIS_REPO_SLUG v$version
fi