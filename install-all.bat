chcp 65001

echo '开始执行安装依赖..'

mvn clean install -DskipTests -pl custom-comm,custom-db-jdbc,custom-db-action,custom-db-proxy,custom-springboot-starter -am

echo '安装完成，可以准备发布了'

pause