#!/bin/bash

set -o nounset
set -o errexit

## setup different build args if the build is for a snapshot or a tag
echo "[travis_deploy] uploading maven artifact snapshot"
docker build --build-arg "TRAVIS=${TRAVIS}" --build-arg "TRAVIS_JOB_ID=${TRAVIS_JOB_ID}" --target package .

docker_tag="latest"

echo "[travis_deploy] dockerhub push for tag [${docker_tag}]"
echo "${DOCKERHUB_PASS}" | docker login -u="${DOCKERHUB_USER}" --password-stdin
docker push "${TRAVIS_REPO_SLUG}:${docker_tag}"

echo "[travis_deploy] quay.io push for tag [${docker_tag}]"
echo "${QUAYIO_PASS}" | docker login -u="${QUAYIO_USER}" --password-stdin quay.io
docker push "quay.io/${TRAVIS_REPO_SLUG}:${docker_tag}"
