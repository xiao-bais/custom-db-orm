chcp 65001

echo '开始执行发布starter..'

cd ..
cd .\custom-comm
echo '开始在本地安装公共依赖包'
mvn clean install

cd ..
cd .\custom-db-jdbc
echo '开始在本地安装jdbc依赖包'
mvn clean install

cd ..
cd .\custom-db-action
echo '开始在本地安装dao核心依赖包'
mvn clean install

cd ..
cd .\custom-db-proxy
echo '开始在本地安装proxy依赖包'
mvn clean install

cd ..
cd .\custom-springboot-starter
echo '开始在本地安装SpringBoot-starter依赖包'
mvn clean install

echo '开始发布'
mvn clean package deploy -DskipTests

pause

