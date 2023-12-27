echo 'pull image'
docker pull $REGISTRY/$IMAGE_NAME:$TAG

echo 'stop running container'
docker stop wegather
docker rm wegather

echo 'set setting file directory'
export ENV_DIR=/home/ec2-user/app/wegather/env

echo 'run deployed container'
docker run -d --name wegather -v $ENV_DIR:/app/env -e ENV_DIR=/app/env -p 8080:8080 $REGISTRY/$IMAGE_NAME:$TAG
