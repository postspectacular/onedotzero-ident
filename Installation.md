# Download, installation & requirements #

The onedotzero identity generator has been developed in Java 6 and so will be able to run on Windows, OS X and Linux systems (although it so far has not been tested on Linux). You'll need a compatible version Java in order to run the software. Instructions are explained for each platform below.

The software has been developed on a 2009 MacBook with 4GB RAM, but we assume the following as...

## Minimum hardware requirements ##

  * 2GHz dual core CPU
  * 2GB RAM (more required for high resolution asset export)
  * Graphics card with 256MB VRAM (should have support for texture sizes of 4096+ pixels)

## Mac OS X ##

### Requirements ###

OS X comes with different versions of Java pre-installed, however Apple does NOT offer the latest version for all CPU architectures and versions of their OS. At the moment you can only run Java 6 software with these combinations:

| **CPU architecture** | **OSX version(s)** |
|:---------------------|:-------------------|
| 64bit Intel | 10.5.x, 10.6.x |
| 32bit Intel | 10.6.x |
| PPC | N/A |

Unless you're already running Snow Leopard, you should ensure you have actually updated to the latest version via the standard Software Update mechanism.

You can check which version is currently installed by opening Terminal and typing

```
java -version
```

The output should look something like this:

```
java version "1.6.0_15"
Java(TM) SE Runtime Environment (build 1.6.0_15-b03-226)
Java HotSpot(TM) 64-Bit Server VM (build 14.1-b02-92, mixed mode)
```

If the version number is less than 1.6.x, open `Java Preferences` (located in Applications > Utilities) and use drag & drop to arrange the order of Java Runtimes for Java applications as below (if you don't have a 64bit machine, you'll need to upgrade to Snow Leopard which does support Java 6 on older 32bit machines):

[![](http://farm4.static.flickr.com/3171/4022802206_e6489189f3.jpg)](http://flickr.com/photos/toxi/4022802206/)

### Installation ###

  1. Download the latest OSX binary distribution from the [downloads page](http://code.google.com/p/onedotzero-ident/downloads/list).
  1. Unzip the archive
  1. Double click ODZGen.app in the folder created

![http://farm3.static.flickr.com/2541/4024819767_5c13133d35.jpg](http://farm3.static.flickr.com/2541/4024819767_5c13133d35.jpg)

Once running, go on and read the UserGuide to learn how everything works and how the visuals can be tweaked.

## Windows ##

### Requirements ###

Windows does not come with Java pre-installed and unless you're confident that you have a recent version of Java installed, head over to [java.sun.com](http://java.sun.com/javase/downloads/index.jsp) and download & install the latest version (Java 6 Update 16 at the time of writing).

**Important: If you're planning to do any development work with this or any other Java project, download the JDK (Java Development Kit) version, else just get the JRE (Java Runtime Environment).**

You can check which version is currently installed like this:

  * Choose Start menu > Run...
  * Type in `cmd` to launch command line
  * Type in `java -version` and press Enter

The output should look something like this:

```
// TODO insert cmd output
```

### Installation ###

  1. Download the latest binary distribution from the [downloads page](http://code.google.com/p/onedotzero-ident/downloads/list).
  1. Unzip the archive
  1. Double click the file `run.bat` in the folder created

Once running, go on and read the UserGuide to learn how everything works and can be tweaked...