# JAVA-LOGISTICS-EG-CODE

## Installing Unit Test Framework (Junit).
* goto the 'src' directory.
* mkdir "EXTERNAL-LIBS" (if not there - in the git ROOT dir)
* go to https://github.com/junit-team/junit4/wiki/Download-and-Install
* download junit.jar and hamcrest-core.jar to the EXTERNAL-LIBS dir.
* to test - $ runTests.sh (make exectuable first, if not)

NB:
* CLASSPATH is set in: classpath.shi - which is 'included' (using '.') in other bash scripts (.sh).
* CLASSPATH uses the EXTERNAL-LIBS directory as specified above. If you want to move EXTERNAL-LIBS - change the CLASSPATH in classpath.shi (only need to change once).
* set CLASSPATH globally if you wish to run tests, etc. manually (not using bash scripts).

