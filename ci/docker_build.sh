#!/bin/bash

set -o nounset
set -o errexit

## common build args for TravisCI builds
TRAVIS_BUILDARGS=(--build-arg "TRAVIS=${TRAVIS}" --build-arg "TRAVIS_JOB_ID=${TRAVIS_JOB_ID}")

echo "[travis_docker_build] running base docker build"
docker build "${TRAVIS_BUILDARGS[@]}" --target build .

docker_tag="${TRAVIS_TAG:-latest}"
echo "[travis_docker_build] packaging docker images for tag [${docker_tag}]"
docker build "${TRAVIS_BUILDARGS[@]}" \
             --tag "${TRAVIS_REPO_SLUG}:${docker_tag}" \
             --tag "quay.io/${TRAVIS_REPO_SLUG}:${docker_tag}" \
             --target package .
