echo 'pull image'
docker pull $REGISTRY/$IMAGE_NAME:$TAG

echo 'stop running container'
docker stop wegather
docker rm wegather

echo 'run deployed container'
docker run -d --name wegather -p 8080:8080 $REGISTRY/$IMAGE_NAME:$TAG
