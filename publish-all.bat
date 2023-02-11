
chcp 65001

echo '开始执行发布依赖..'

mvn clean deploy -DskipTests -pl custom-comm,custom-db-jdbc,custom-db-action,custom-db-proxy,custom-springboot-starter -am

echo '发布完成，可以去看看是否发布成功'

pause

