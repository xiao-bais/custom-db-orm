chcp 65001

echo '开始在本地安装comm依赖包'
mvn clean install

echo '开始发布comm依赖'
mvn clean package deploy -DskipTests

pause

