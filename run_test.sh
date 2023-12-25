docker run --rm -v $PWD:$PWD -w $PWD -v /var/run/docker.sock:/var/run/docker.sock gradle:latest gradle test
