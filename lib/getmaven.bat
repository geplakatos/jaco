set OUTPUT="_mvn"
set MAVENEXEC="c:\Program Files\NetBeans 8.2\java\maven\bin\mvn.bat"

%MAVENEXEC% dependency:copy-dependencies -DoutputDirectory=%OUTPUT%