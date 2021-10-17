
call mvn clean package  -D maven.test.skip=true

call mvn source:jar

echo --------------------------package success----------------------------

call mvn install:install-file "-DgroupId=com.custom" "-DartifactId=custom-jdbc" "-Dversion=1.1-SNAPSHOT" "-Dpackaging=jar" "-Dfile=D:\Program Files\Idea-work\custom-jdbc\target\custom-jdbc-1.1-SNAPSHOT.jar" "-DgeneratePom=true"

echo --------------------------install success----------------------------

pause