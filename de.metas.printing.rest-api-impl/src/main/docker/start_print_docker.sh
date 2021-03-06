﻿#!/bin/bash
set -e


# The variables have defaults that can be set from outside, e.g. via -e "DB_HOST=mydbms" or from the docker-compose.yml file.
# Also see https://docs.docker.com/engine/reference/builder/#environment-replacement
db_host=${DB_HOST:-db}
db_port=${DB_PORT:-5432}
db_name=${DB_NAME:-metasfresh}
db_user=${DB_USER:-metasfresh}
db_password=${DB_PASSWORD:-metasfresh}
app_host=${APP_HOST:-app}
skip_run_db_update=${SKIP_DB_UPDATE:-false}
debug_port=${DEBUG_PORT:-8791}
debug_suspend=${DEBUG_SUSPEND:-n}
admin_url=${METASFRESH_ADMIN_URL:-NONE}
java_max_heap=${JAVA_MAX_HEAP:-256M}
server_port=${SERVER_PORT:-8183}

echo_variable_values()
{
 echo "Note: all these variables can be set using the -e parameter."
 echo ""
 echo "DB_HOST=${db_host}"
 echo "DB_PORT=${db_port}"
 echo "DB_NAME=${db_name}"
 echo "DB_USER=${db_user}"
 echo "DB_PASSWORD=*******"
 echo "SKIP_DB_UPDATE=${skip_run_db_update}"
 echo "APP_HOST=${app_host}"
 echo "DEBUG_PORT=${debug_port}"
 echo "DEBUG_SUSPEND=${debug_suspend}"
 echo "METASFRESH_ADMIN_URL=${admin_url}"
 echo "JAVA_MAX_HEAP=${java_max_heap}"
 echo "SERVER_PORT=${server_port}"
}

set_properties()
{
 echo "set_properties BEGIN"
 local prop_file="$1"
 if [[ $(cat $prop_file | grep FOO | wc -l) -ge "1" ]]; then
	sed -Ei "s/FOO_DBMS_HOST/${db_host}/g" $prop_file
	sed -Ei "s/FOO_DBMS_PORT/${db_port}/g" $prop_file
	sed -Ei "s/FOO_DB_NAME/${db_name}/g" $prop_file
	sed -Ei "s/FOO_DB_USER/${db_user}/g" $prop_file
	sed -Ei "s/FOO_DB_PASSWORD/${db_password}/g" $prop_file
	sed -Ei "s/FOO_APP/${app_host}/g" $prop_file
 fi
 echo "set_properties END"
}
 
wait_dbms()
{
 until nc -z $db_host $db_port
 do
   sleep 1
 done
}

# Note: the Djava.security.egd param is supposed to let tomcat start quicker, see https://spring.io/guides/gs/spring-boot-docker/
run_metasfresh()
{
 if [ "$admin_url" != "NONE" ]; 
 then
	# see https://codecentric.github.io/spring-boot-admin/1.5.0/#spring-boot-admin-client
	# spring.boot.admin.client.prefer-ip=true because within docker, the hostname is no help
	metasfresh_admin_params="-Dspring.boot.admin.url=${admin_url} -Dmanagement.security.enabled=false -Dspring.boot.admin.client.prefer-ip=true"
 else
	metasfresh_admin_params=""
 fi

 cd /opt/metasfresh/metasfresh-print/ && java -Dsun.misc.URLClassPath.disableJarChecking=true \
 ${ext_lib_param}\
 -Xmx${java_max_heap}\
 -XX:+HeapDumpOnOutOfMemoryError ${metasfresh_admin_params}\
 -DPropertyFile=/opt/metasfresh/metasfresh-print/metasfresh.properties\
 -Djava.security.egd=file:/dev/./urandom\
 -Dserver.port=${server_port}
 -agentlib:jdwp=transport=dt_socket,server=y,suspend=${debug_suspend},address=${debug_port}\
 -jar metasfresh-print.jar
}

echo "*************************************************************"
echo "Display the variable values we run with"
echo "*************************************************************"
echo_variable_values
echo ""

set_properties /opt/metasfresh/metasfresh-print/metasfresh.properties


echo "*************************************************************"
echo "Wait for the database server to start on DB_HOST = '${db_host}'"
echo "*************************************************************"
wait_dbms
echo ">>>>>>>>>>>> Database Server has started"

echo "*************************************************************"
echo "Start metasfresh-print-endpoint";
echo "*************************************************************"
run_metasfresh

exit 0 
