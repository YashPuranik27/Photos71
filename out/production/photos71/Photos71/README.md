# Group 71 Photos Project Submission
### Yash Puranik (yap13), Joseph Arrigo (jma396)

## Note to Graders

We've included below a configuration guide for the JavaFX Library. It contains a guide for the methods we used to develop using the JavaFX SDK (i.e. configuring the project to use JavaFX Library and setting up a run configuration).

The file `src/photos/Photos.java` is the main class and it contains the main method. Run this file to start the App.
Compiled code is found in `out/production/photos71/Photos71`

We used Java 20 for this project.

## Library Configuration Guide

For this project, the JavaFX sdk found in folder `lib` was used. It is attached to this repository for your convenience, however you can use your own JavaFX library.

To configure the JavaFX library files, ensure that your project and IDE include the entire `lib/javafx-sdk-21.0.1/lib` folder as a set of libraries. In IntelliJ, this can be done by:

`File > Project Structure > Libraries > New Project Library > Java`

In addition, you must either create a run configuration for this project, or append some extra commands to your `java` command. To create a run configuration for this in IntelliJ, do the following:

`Run > Edit Configurations > Add New Configuration > Application > `

There, you must set your java version to Java 20, the main class to Photos, and you must add the following VM Options (this may be hidden behind "Modify Options"):

`--module-path "lib\javafx-sdk-21.0.1\lib" --add-modules javafx.controls,javafx.fxml`

where `lib\javafx-sdk-21.0.1\lib` can be replaced with any valid path to a JavaFX SDK `lib` folder.
