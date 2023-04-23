FROM tomcat:latest

RUN mkdir /usr/local/tomcat/conf/gameserver
RUN mkdir /keys
COPY wars/*.war /usr/local/tomcat/webapps/
COPY wars/server.xml /usr/local/tomcat/conf/
COPY config/* /usr/local/tomcat/conf/gameserver/
COPY wars/gameserver.xml /usr/local/tomcat/conf/Catalina/localhost/
#COPY jars/* /usr/local/tomcat/bin
