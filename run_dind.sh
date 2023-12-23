docker run --privileged -d --name dind -d -p 2375:2375 \
        --network test-network --network-alias dind \
        -e DOCKER_TLS_CERTDIR="" \
        docker:dind
