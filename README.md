# twasloglistener

1. Install WAS dependencies from `${WAS}/dev/was_public.jar` and `${WAS}/dev/JavaEE/6.0/j2ee.jar` into the local repository:
    1. `mvn install:install-file -Dfile=was_public.jar -DgroupId=com.ibm.websphere -DartifactId=twasapis -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true`
    1. `mvn install:install-file -Dfile=j2ee.jar -DgroupId=com.ibm.websphere -DartifactId=jeeapis -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true`
1. Compile: `mvn clean install`
1. Install the application in WAS:
    1. Set the context root to something other than `/`
1. If you need this to listen to other applications' startup messages, edit each other application under Startup Behavior and set Startup order to a value greater than the Startup order of twasloglistener.war
1. Restart the server and you should see:
   ```
   SystemOut     O twasloglistener.LogListener@5a438a87 handleNotification received message: WSVR0001I: Server server1 open for e-business
   ```
