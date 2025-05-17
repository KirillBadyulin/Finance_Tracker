<<<<<<< HEAD
# Finance_Tracker
=======
Finance tracker app which helps you understand you monthly/yearly spending habits and adjust them.
Will contain major bug solutions in this file.

1. "Error: JavaFX runtime components are missing, and are required to run this application
TWO SOLUTIONS:

INTELLIJ SOLUTION. Easy one: Need to modify the VM Options.First, download JavaFX SDK, and put it where you need it. Main folder of the app will contain a copy of that as well. 
In VM options choose the path (SPOILER: you might need to point/list ALL the libraries inside JavaFX SDK, with a coma).
Should work now.
MAVEN SOLUTION. Easy and not. In pom file, you need to list separate plugin to run program thru 1) BASH or 2)Maven plugin in IntelliJ.
So adding into pom.xml : 1. JavaFX dependancies 2.!PluginRepositories! 3. Plugin "JavaFX-Maven-Plugin"-itself.
MAKE SURE Java version on your PC is the same as Maven and JavaFX. Restarting PC AND IntelliJ of course to make any serious changes active.
Now you can run your program in BASH (from you project folder) using "mvn clean javvafx:run" or "mvn  javafx:run", OR in intelliJ, but from Maven tab-project_name> plugins-> javafx:run
   







>>>>>>> 242e723d65d12845afb196c558b8c2f3f41a8004
