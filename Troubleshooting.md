# Troubleshooting / FAQ #

## App doesn't start ##

Most likely cause is the lack of a suitable Java 6 runtime environment on your machine. Did you read the [requirements & installation instructions](Installation.md)? Another cause might be insufficient RAM available to launch. The app tries to reserve at least 256MB of RAM and usage can grow up to 640MB (by default, can be edited via `run.bat`/`run.sh` files or editing the `info.plist` file for the OSX version)

## App launches but I don't see any ribbons ##

Possibly your graphics card does not support the configured texture size. You'll have to edit the [configuration file](UserGuideConfig.md). Then try again with the new settings.

## App doesn't fill the screen ##

The application is by default set to a 1280x720 screen resolution and to change the size you'll have to edit the [configuration file](UserGuideConfig.md). Then try again with the new settings.

## How to change the words ##

The application is by default set to display 'onedotzero adventures in motion' and to change the words you'll have to edit the [configuration file](UserGuideConfig.md). Then try again with the new settings.

## App is running, but very sluggish (< 5fps) ##

This is most likely caused by insufficient RAM causing your system to repeatedly swap virtual memory from disk: http://en.wikipedia.org/wiki/Paging. Try editing the RAM settings (see above).

## App freezes when trying to export high res assets ##

Again, this is probably a RAM issue because the app can't reserve enough memory for the high res image. Make sure your machine satisfies the [minimum hardware requirements](Installation#Minimum_hardware_requirements.md) or try closing other applications before restarting ODZGen. In the default configuration (1280x720 and export tiles = 10) the app will require approx. 560MB of RAM when exporting.

## I edited the config file, but app is not working anymore ##

You'd better kept a backup of the original file. If not, simply download the app again.

## The App doesn't run on Windows 7 ##

Something we are looking into as Windows 7 launched after the app.