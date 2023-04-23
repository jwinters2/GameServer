VERSION=""

if [ -n $1 ]; then 
  VERSION="-$1"
fi

cp ../../target/gameserver$VERSION.war wars/gameserver.war
docker build -t tomcat-prod:latest .
docker tag tomcat-prod:latest registry.jameswinters.net/tomcat-prod:latest
docker push registry.jameswinters.net/tomcat-prod:latest
