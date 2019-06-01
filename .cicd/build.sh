set -e
set -o pipefail
set -x

./gradlew build

mkdir -p docker-build/service
cp build/libs/*.jar docker-build/service/service.jar
cp .cicd/docker/Dockerfile docker-build/

export PROJECT_VERSION=`./gradlew properties | grep "^version:" | awk '{print $2}'`
export BUILD_TIMESTAMP=`date '+%Y%m%d%H%M%S'`
export BUILD_VERSION=${PROJECT_VERSION}.${BUILD_TIMESTAMP:2:6}.${TRAVIS_COMMIT:0:6}
export IMAGE_NAME=registry.cn-beijing.aliyuncs.com/fundwit/owlet:$BUILD_VERSION

docker login registry.cn-beijing.aliyuncs.com -u $DOCKER_ACCOUNT -p $DOCKER_PASSWORD

docker build -t $IMAGE_NAME docker-build
docker push $IMAGE_NAME

mkdir -p kube-build
envsubst < .cicd/kube/shikra.yaml > kube-build/shikra.yaml

ssh -p $DEPLOY_PORT -i ~/.ssh/deploy_key -o StrictHostKeyChecking=no $DEPLOY_USER@$DEPLOY_TARGET "mkdir -p /opt/cicd/shikra/$BUILD_VERSION"
scp -P $DEPLOY_PORT -i ~/.ssh/deploy_key kube-build/shikra.yaml $DEPLOY_USER@$DEPLOY_TARGET:/opt/cicd/shikra/$BUILD_VERSION/shikra.yaml
ssh -p $DEPLOY_PORT -i ~/.ssh/deploy_key $DEPLOY_USER@$DEPLOY_TARGET "kubectl apply -f /opt/cicd/shikra/$BUILD_VERSION/shikra.yaml"