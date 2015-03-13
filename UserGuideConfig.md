# Default configuration #

The application loads all (95%) of its default settings from the `app.properties` in the `config` subfolder. This includes the default message shown, feed definitions, colour settings etc. The file already contains a few comments and the name of the parameters should in many cases be self-explanatory, but this page will outline the most important ones in some more detail...

Also, please be aware of the following disclaimer. Unfortunately we will not be able to provide much more hand holding than the descriptions below (and further comments in the source code of the app). Editing some settings in this file and achieving different effects is for advanced users only and you'll be a bit on your own.

Below are the contents of the `config/app.properties` file. **Lines starting with a `#` symbol are comments and not active.**

```
#######################################################################
# ODZGen application default settings
# 
# Please edit this file only if you know what you're doing!
# Consult the project wiki at: http://onedotzero-ident.googlecode.com
# for further information.
#
# We recommend backing up this original file before making any changes
#######################################################################
```

### General applications setup ###

The default window size is HD720 (1280 x 720). You should edit these settings to create assets in portrait format. The window size should have the same ratio as the required output aspect.

```
app.width=1280
app.height=720
```

By default the app is set to run at 60 fps... Enabling this line you can limit the framerate and better judge movements/speeds when recording assets for video

```
#app.framerate=25
```

As mentioned previously, the app can be run in "identity generator" or "installation" mode. The former is enabled by default and is meant for asset creation tasks and enables the GUI. Uncomment this line or set the value to `false` to enable installation mode (currently not recommended unless you have a [Nokia N900](http://maemo.nokia.com/n900/))

```
app.mode.identity=true
```

A black gradient mask for the top, left & right sides can be enabled optionally. Never used so far properly, but might be useful for single screen projections...

Also, the mouse cursor can be hidden by commenting out this line or setting value to `false`.

```
#app.mask.enabled=true
app.mouse.enabled=true
```

### Message configuration ###

Defines the following defaults for message rendering, read as:
  * 160 3D world units leading between lines
  * line width of 15 characters

```
message.leading=160
message.wordwrap=15
}}

For installation mode the lines below configure also the following behaviour:

  * Long messages are split in segments if longer than 5 lines
  * Messages are shown for 18 seconds (unless the user is interacting, then longer)
  * When entering menu screen on the N900, the current message is held for 6 seconds, after which the message queue is being processed again

{{
message.maxlines=5
message.num.recent=1
message.ttl=18000
message.menu.queue.delay=6000
```

Also for installation mode:

  * User submitted messages are cached and written out to a log file in chunks of 25 messages.
  * These messages are also used to create an in-memory data feed which results in user submitted messages appearing as text on ribbons. The color for this feed is defined in the usual hex notation. See next section.

```
message.log.count=25
```

### Colours ###

Define background & feed colours in the usual 6-digit hex notation. Each [data feed](#Data_feed_definitions.md) can have its unique colour. The last setting for `message.feed.col` is only used during installation mode & contains messages submitted from the N900.

The application is using OpenGL's additive blending feature to create a neon/blowout effect for overlapping ribbons. This effect is hardcoded in the app and so you should avoid setting the background colour to pale/bright colours (unless you like white on white! :) Bright, but strongly saturated background colours will create interesting effects.

```
background.col=000000
feed0.col=87d200
feed1.col=dc3600
feed2.col=dc007c
feed3.col=4d00dc
feed4.col=0075dc
feed5.col=00dc7e
message.feed.col=ff0044
```

### Texture width ###

Defines the texture width used for the ribbons. Most of the recent graphic cards should be able to handle 4096 pixels (modern ones even higher, e.g. 8192), but if you run into problems (see [Troubleshooting](Troubleshooting.md)), you might want to edit this setting to a lower value (**Note: the value must be a power of 2**).

```
texture.width=4096
# uncomment one of the lines below for smaller texture sizes
# on weaker machines with old graphic cards not supporting
# texture sizes of 4096 pixels
#texture.width=2048
#texture.width=1024
```

### Wordmark / default message ###

Define the content and layout of the wordmark / default message shown on application start (in identity mode this never changes)

```
defaults.message.line0=onedotzero
defaults.message.line1=adventures in
defaults.message.line2=motion
defaults.message.lines.count=3
```

If the message has more than a single line defined, then you can adjust the scale and offset for the other lines (called "subtitle") below. The TTL (time-to-live) setting is only used for installation mode...

```
defaults.message.subtitle.scale=0.83
defaults.message.subtitle.offset.x=0
defaults.message.subtitle.offset.z=160
defaults.message.ttl=13000
```

### Poles & ribbons ###

Enable or disable the inclusion of hardcoded poles read from the `config/custompoles.xml` file...

```
defaults.poles.custom.enabled=true
```

Set number of external poles (not including any "custom poles"), hit counts (see UserGuide) and the so called exclusion zone which, using the default values, translates into: external poles are positioned along the Y axis between 50%-100% of the world space size. That way they never come too close to the actual message/letters and cause visual havok...

```
defaults.poles.count=6
defaults.poles.exclusion.depth=0
defaults.poles.exclusion.height=0.5
defaults.poles.external.maxhitcount=120
defaults.poles.letters.maxhitcount=14
```

Ribbon parameters to define density, size, birth behaviour (35% chance of 1 new ribbon added per frame, unless all poles are saturated or the `totalmaxcount` of ribbons has been reached)

```
defaults.ribbon.width=15
defaults.ribbon.letter.scale=2.5
defaults.ribbon.maxscrollspeed=0.006
defaults.ribbon.add.perframe=1
defaults.ribbon.add.chance=0.35
defaults.ribbon.totalmaxcount=1000
defaults.ribbon.loopcount=3
defaults.ribbon.spawndelay=0
```

### World space dimensions ###

Sets up the size of the world space. When computing field lines for the ribbons, the path tracing is cancelled when it goes out of these bounds...

```
defaults.bounds.extent.x=1000
defaults.bounds.extent.y=400
defaults.bounds.extent.z=500
```

### Camera default settings ###

Setting up 3D position, rotation/orientation, zoom and smoothing settings (see UserGuide how these apply)

```
cam.pos.x=0
cam.pos.y=140
cam.pos.z=40
cam.rotation.x=-0.5
cam.rotation.y=0
cam.rotation.z=0
cam.zoom=1
cam.zoom.smooth=0.025
cam.rotation.smooth=0.05
cam.pan.smooth=0.02
```

### Data feed definitions ###

Defines data feeds to be used as content source for the text on ribbons. By default 6 feeds are defined.

```
feeds.count=6
```

For each feed you must tell the app the feed format used. Only these 2 values are valid:

  * `atom`: Atom 1.0 feed (e.g. [Twitter search](http://search.twitter.com))
  * `rss`: RSS2.0 feed

```
feed0.type=atom
feed1.type=atom
feed2.type=atom
feed3.type=atom
feed4.type=atom
feed5.type=atom
```

This public version of the app is by default only using a duplicated, locally cached offline version of a twitter search feed from 2009-09-11, see below... However, you can change the feed URLs to point to live data sources by editing the lines below. Feeds are only loaded once during application startup.

Important to remember, you can adjust the number of feeds, but need to configure for each:

  * Ensure only one URL is assigned per feed (either delete lines or comment them out)
  * Each feed needs to have a colour defined (see above). If none is found, white will be used by default.

```
#feed0.url=http://search.twitter.com/search.atom?q=onedotzero
#feed1.url=http://search.twitter.com/search.atom?q=sermad
#feed2.url=http://search.twitter.com/search.atom?q=toxi
#feed3.url=http://search.twitter.com/search.atom?q=nokia
#feed4.url=http://search.twitter.com/search.atom?q=n900
#feed5.url=http://search.twitter.com/search.atom?q=iphone

feed0.url=assets/feeds/search20090911.xml
feed1.url=assets/feeds/search20090911.xml
feed2.url=assets/feeds/search20090911.xml
feed3.url=assets/feeds/search20090911.xml
feed4.url=assets/feeds/search20090911.xml
feed5.url=assets/feeds/search20090911.xml
```

All feeds are enabled by default. You can disable them individually by uncommenting respective lines below. The feed will still be loaded & parsed by the application, only not used for sourcing ribbon texts (same as toggling feeds via the GUI).

```
#feed0.enabled=false
#feed1.enabled=false
#feed2.enabled=false
#feed3.enabled=false
#feed4.enabled=false
#feed5.enabled=false
```

#### Customizing ribbon texts ####

If you want your own text messages to appear on the ribbons, simply create/reference your own feed or edit the existing file `assets/feeds/search20090911.xml`. In the XML only the `<title>` value of each feed item is used.