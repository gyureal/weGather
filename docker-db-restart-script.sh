docker rm mysql-dev
docker run --name mysql-dev -e MYSQL_ROOT_PASSWORD=12 -e MYSQL_DATABASE=weGather -d -p 3316:3306 mysql
docker rm mysql-test
docker run --name mysql-test -e MYSQL_ROOT_PASSWORD=12 -e MYSQL_DATABASE=weGather -d -p 3326:3306 mysq
