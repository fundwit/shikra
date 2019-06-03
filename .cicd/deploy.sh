set -e
set -o pipefail
set -x

docker login registry.cn-beijing.aliyuncs.com -u $DOCKER_ACCOUNT -p $DOCKER_PASSWORD
docker push $IMAGE_NAME

mkdir -p kube-build
envsubst < .cicd/kube/shikra.yaml > kube-build/shikra.yaml

ssh -p $DEPLOY_PORT -i ~/.ssh/deploy_key -o StrictHostKeyChecking=no $DEPLOY_USER@$DEPLOY_TARGET "mkdir -p /opt/cicd/shikra/$BUILD_VERSION"
scp -P $DEPLOY_PORT -i ~/.ssh/deploy_key kube-build/shikra.yaml $DEPLOY_USER@$DEPLOY_TARGET:/opt/cicd/shikra/$BUILD_VERSION/shikra.yaml
ssh -p $DEPLOY_PORT -i ~/.ssh/deploy_key $DEPLOY_USER@$DEPLOY_TARGET "kubectl apply -f /opt/cicd/shikra/$BUILD_VERSION/shikra.yaml"

# TODO: test this release is running