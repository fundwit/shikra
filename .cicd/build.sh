set -e
set -o pipefail
set -x

./gradlew build

mkdir -p docker-build/service
cp build/libs/*.jar docker-build/service/service.jar
chmod +x .cicd/docker/scripts/*
cp .cicd/docker/scripts/* docker-build/service/
cp .cicd/docker/Dockerfile docker-build/

export PROJECT_VERSION=`./gradlew properties | grep "^version:" | awk '{print $2}'`
export BUILD_TIMESTAMP=`date '+%Y%m%d%H%M%S'`
export BUILD_VERSION=${PROJECT_VERSION}.${BUILD_TIMESTAMP:2:6}.${TRAVIS_COMMIT:0:6}
export IMAGE_NAME=registry.cn-beijing.aliyuncs.com/fundwit/shikra:$BUILD_VERSION

echo "build.version=$BUILD_VERSION" > docker-build/service/build.properties

docker build -t $IMAGE_NAME docker-build