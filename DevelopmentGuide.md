# Development guide #

The software has been developed in [Java 6](http://java.sun.com) using [Eclipse 3.5](http://eclipse.org). Several open source libraries are forming core parts of the application, most notably [Processing](http://processing.org) & [toxiclibs](http://toxiclibs.org). A [full list of libraries](#Libraries.md) used is further down in this document.

Even though Processing has been used for this project, it played a rather small part and you'll also not be able to use the source code in the Processing IDE. The main reason for this is that the application has been developed using the "new" (since 2004) syntax features extensively (annotations, generics, extended for-loops etc.). We still hope though that at least some of you coming from Processing or similar backgrounds will find this project interesting and maybe use it as jumping board to give Eclipse a serious spin and start using some of the actually really nice (contrary to popular prejudice) language features plain Java has to offer (for example XML parsing with [JAXB](https://jaxb.dev.java.net/), the Collections API etc.)

## Technology overview ##

The following map is meant to provide a brief overview of the different parts & technologies used by the application:

[![](http://farm3.static.flickr.com/2749/4021802897_31ec4c97c3.jpg)](http://www.flickr.com/photos/toxi/4021802897/)

## Source code requirements ##

  * Java 6 SDK (or newer)
  * [Eclipse](http://eclipse.org) (optional, but highly recommended)
  * All required libraries are bundled in binary form in the project's `lib` folder (web links are provided in the Readme file included)
  * Ant (already incl. in Eclipse)
  * [FatJar](http://fjep.sourceforge.net/) for exporting application

## Getting the source ##

You can either [download the source code](http://code.google.com/p/onedotzero-ident/downloads/list) or clone the [Mercurial](http://mercurial.selenic.com/wiki/) repository. Instructions for checking out from the repository are over [here](http://code.google.com/p/onedotzero-ident/source/checkout). In brief, when using the command line:

```
hg clone https://onedotzero-ident.googlecode.com/hg/ onedotzero-ident
```

Alternatively, you can use the [Mercurial plugin](http://bitbucket.org/mercurialeclipse/main/wiki/Home) for Eclipse to checkout the project. See next section...

## Eclipse project setup ##

The downloadable source version is an exported [Eclipse](http://eclipse.org) Java project, so if you're using Eclipse you should be able to just import the zip archive as project into your workspace. Some notes for other IDE's are further below...

### Using the downloaded source distribution ###

[![](http://farm3.static.flickr.com/2559/4030735327_855e8d3a12_o.png)](http://www.flickr.com/photos/toxi/4030735327/)

[![](http://farm3.static.flickr.com/2580/4031489760_cf477d0327_o.png)](http://www.flickr.com/photos/toxi/4031489760/)

[![](http://farm3.static.flickr.com/2782/4030735437_5c0fb31a4e_o.png)](http://www.flickr.com/photos/toxi/4030735437/)

Done.

### Using MercurialEclipse ###

[![](http://farm3.static.flickr.com/2559/4030735327_855e8d3a12_o.png)](http://www.flickr.com/photos/toxi/4030735327/)

[![](http://farm4.static.flickr.com/3487/4033306262_79756729ae_o.png)](http://www.flickr.com/photos/toxi/4033306262/)

[![](http://farm3.static.flickr.com/2519/4032553031_5d1ee81aa2_o.png)](http://www.flickr.com/photos/toxi/4032553031/)

Done.

## Package structure ##

[![](http://farm3.static.flickr.com/2658/4031489852_d55a2bbb5e.jpg)](http://www.flickr.com/photos/toxi/4031489852/)

Code is split into two source folders:

### src (main code folder) ###

  * **onedotzero**: main application components
    * **onedotzero.data**: data feed handling
    * **onedotzero.export**: high res & image sequence exporters
    * **onedotzero.message**: message handling & scheduling
    * **onedotzero.osc**: Wrapper for [OscP5](http://sojamo.de/libraries/oscP5/) backend used to communicate with the N900
    * **onedotzero.poles**: Pole management & field line computation
      * **onedotzero.poles.strategies**: Strategies for pole positioning
    * **onedotzero.states**: Application states
    * **onedotzero.text**: Text formatting
    * **onedotzero.type**: Flow based typography parsing & handling, pole-to-letter lookups

### src.tools ###

  * **onedotzero.tools**: Letter flow editor & bitmap parsers to create alphabet


## Getting INTO the code ##

Next we'll take a brief look at the **ODZApp class** in the main `onedotzero` package, the main class & entry point of the application. This class acts as a hub for all other parts and initializes most components during start up. It subclasses Processing's `PApplet` class and so has the usual `setup` & `draw` methods. However, the class can't be run as Applet and instead needs to be [launched as Java application](#Running_the_app.md) using the standard `public static void main(String[] args)` method. This method attempts to load the main configuration file and then calls `PApplet.main()`, after which execution continues in `setup()`...

## Running the app ##

Right click on `src/onedotzero/ODZApp.java` in the Package Explorer and then choose **Run As > Java Application** from the context menu.

You can also create/edit a Run Configuration and use the settings below...

[![](http://farm3.static.flickr.com/2468/4031535206_5fd8d29f00_o.png)](http://www.flickr.com/photos/toxi/4031535206/)

These VM parameters will set memory requirements & logging preferences

```
-Xms256m -Xmx640m -Djava.util.logging.config.file=config/logging.properties
```

## Building the standalone application ##

### Using Ant... ###

You can use the included Ant build file to build & bundle the cross-platform binaries. Each revision should have its own time stamp, so please first edit the following line in the `build.xml` file in the project root:

```
<property name="release.version" value="${app.name}-20091020" />
```

Save changes and then right click on the filename in the Package Explorer and choose **Run As... > Ant Build**.

[![](http://farm3.static.flickr.com/2682/4034121173_177605f7a1_o.png)](http://www.flickr.com/photos/toxi/4034121173/)

The default build target (called `release`) will create a new zip file called `odzgen-YYYYMMDD-bin.zip` in the `dist` folder (will be created upon 1st run). This archive contains the bundled application JAR (using [FatJar](http://fjep.sourceforge.net/)), native library files for OpenGL, assets, configuration & readme files.

### Building an OSX application ###

Eclipse can generate a native OSX application stub/launcher for our application. AFAIK this process can't be fully automated as with Ant, so do the following steps:

Right click on the project name in the Package Explorer and choose **Export...** and from the following dialog **Other > Mac OS X application bundle**... Press **Next**.

[![](http://farm3.static.flickr.com/2537/4034138775_381542da79_o.png)](http://www.flickr.com/photos/toxi/4034138775/)

Next up we will set the launch configuration & application icon:

  * Choose a suitable **Run configuration** to use for the app (see [above](#Running_the_app.md))
  * Set the destination folder to the project root using the **Browse** button
  * Set JVM version to be **1.6 compatible**
  * Set the application icon, using (for example) the supplied one here: `assets/icon/odz128x128.icns`
  * Choose **Finish**

[![](http://farm4.static.flickr.com/3498/4034138887_de98057afc_o.png)](http://www.flickr.com/photos/toxi/4034138887/)

Back in the Package explorer, we'll now choose all the files required for distribution:

  * Right click on the project folder & choose **Refresh** (or press **F5**)
  * Right click again and choose **Export...**
  * In the dialog choose **General > Archive file**, then press **Next**

[![](http://farm3.static.flickr.com/2439/4034993848_c83444c925_o.png)](http://www.flickr.com/photos/toxi/4034993848/)

  * Choose the following files and sub folders for export

[![](http://farm4.static.flickr.com/3496/4034247543_ab413afd09_o.png)](http://www.flickr.com/photos/toxi/4034247543/)

## Libraries ##

The software bundles the following libraries in binary form. All JARs are located in the `lib` folder.

  * http://processing.org (LGPL: core.jar & opengl.jar, v103)
  * http://toxiclibs.org (LGPL: toxiclibscore-0015, datautils-0001, colorutils-0003)
  * http://sojamo.de/libraries/oscP5/ (LGPL)
  * http://sojamo.de/libraries/controlP5/ (LGPL)
  * https://jogl.dev.java.net (BSD)

## Javadocs ##

Available for all classes & located in the `docs` folder. Javadocs can be rebuild using the `javadoc` target in the Ant build file. Also see further below how to use Ant.