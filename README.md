# JAVA-LOGISTICS-EG-CODE

## Introduction
Work In Progress - 95% there though. Mainly just this README needs to be updated fully.

Some work still needed to tidy up this repo. See LogisticsAppNotes.pdf until that is done - for overview of spec and detailed design discussions.
This example is intended to support the following article (Architectural Reference Model):

https://www.infoq.com/articles/arm-enterprise-applications/

## 'Layering Model' (as per ARM).

In terms of that article the packages are distrbuted as follows:

* Control - is outside the layering defined in the aforementioned article - as it is program startup and related control.
* Parsers - Interface Layer - contains code to translate from input file (via reasonably sophisicated parsing)  and to make appropriate calls into the Application layer (via the 'service API - which could be wrapped in a REST api - but isn't here).
* ProductLine - Application layer "API" exposed here. Calls down into the Domain layer.
* Products & Ingrediants - Domain Layer - used by ProductLine package in Application Layer.
* Utils - Infrastructure - generic utilities not bound to this application particularly.

## Other Notes (more TBD) - Installing Unit Test Framework (Junit).

* goto the 'src' directory.
* mkdir "EXTERNAL-LIBS" (if not there - in the git ROOT dir)
* go to https://github.com/junit-team/junit4/wiki/Download-and-Install
* download junit.jar and hamcrest-core.jar to the EXTERNAL-LIBS dir.
* to test - $ runTests.sh (make exectuable first, if not)

NB:
* CLASSPATH is set in: classpath.shi - which is 'included' (using '.') in other bash scripts (.sh).
* CLASSPATH uses the EXTERNAL-LIBS directory as specified above. If you want to move EXTERNAL-LIBS - change the CLASSPATH in classpath.shi (only need to change once).
* set CLASSPATH globally if you wish to run tests, etc. manually (not using bash scripts).

