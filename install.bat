
call mvn clean package  -D maven.test.skip=true

echo --------------------------package success----------------------------

call mvn install:install-file "-DgroupId=com.custom" "-DartifactId=custom-jdbc" "-Dversion=1.0-SNAPSHOT" "-Dpackaging=jar" "-Dfile=D:\Program Files\Idea-work\custom-jdbc\target\custom-jdbc-1.0-SNAPSHOT.jar" "-DgeneratePom=true"


echo --------------------------install success----------------------------

pause