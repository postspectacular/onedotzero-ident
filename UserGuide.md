# User Guide #

## Overview & concept ##

The aggregation and attraction of textual content from various social networks formed the basic concept behind the `onedotzero` 2009 identity visuals. `onedotzero` as festival & media entity becomes a content magnet of its wider community. We're using text in a fractal fashion to form new text on bigger scale. For that matter, the `onedotzero` wordmark (or any other message chosen/submitted by users of the interactive installation version) is constructed of a constellation of simulated electro-magnetic point poles in 3D space. In addition to the poles making up the outline of the wordmark, several other poles are positioned in the perimeter outside, representing nodes of the online community from which the text ribbons emanate from.

In order to visualize the attraction of social content harvested from online sources, the EMF field between the different poles is simulated by systematically tracing the field lines between poles. These lines often result in beautiful curves and are used as guides for ribbons of text to flow from pole to pole. Through scrolling text along the otherwise invisible field lines, the structure of the space as well as of the wordmark are temporarily highlighted before disappearing again in outer space. Multiplying this basic behaviour to a sufficiently larger number of ribbons allows us then to orchestrate the appearance of the wordmark/message. Placing the entire process into a 3 dimensional space adds another expressive layer on top which was also used to support the narrative for the festival trailer produced by Wieden + Kennedy.

## Features ##

The underlying identity model has many parameters influencing aspects of the overall behaviour and look & feel of the visual outcomes. The most important ones can be controlled via the minimal GUI and are described below. Since this is an identity project, the GUI only gives you access to a reduced set of options in order to keep the possible outcomes "on brand". If you want to achieve a totally different look, there's a greater set of parameters which can be tweaked via the [configuration file](UserGuideConfig.md) and by hacking the source code.

### Interactive controls ###
The 3D camera view and some other parameters can be controlled using the mouse and keyboard. Press and hold shift and then click and drag to change the camera view. For a complete list see the Readme file included in the downloads or [this wiki page](UserGuideKeyboard.md).

### Ribbons & Poles ###
[![](http://farm3.static.flickr.com/2456/4028887950_cf32478193.jpg)](http://www.flickr.com/photos/toxi/4028887950/)

The applications GUI is split into several tabs, grouped into thematically related parameters. The default tab open on application start allows you to control the number of poles and ribbons in the space. All of the parameters are closely interrelated and mainly control the overall density of elements in the space. So it's best to change one at the time. In top down order the parameters are:

#### Number of poles ####

Defines the number of external poles from which ribbons are emitted, outside the wordmark/message.

#### Maximum pole hitcount ####

Each pole in the space keeps track of the number of ribbons flowing through it at current. For external poles as source of ribbons, this means how many ribbons can be emitted at the same time before the pole is considered saturated and won't emit any further ones until the number of its ribbons has reduced again.

#### Maximum number of ribbons ####

Sets the limit of possible ribbons in space. Depending on the number of external poles, loop count and hitcount settings this limit never might be reached.

#### Maximum letter hitcount ####

Similar to "Maximum pole hitcount" above, with this parameter we can adjust the overall ribbons density/saturation (and hence legibility) of the wordmark. E.g. a letter hitcount of 5 means, that each point on the letter outlines can only accomodate upto 5 ribbons. If this number is reached no further ribbons will flow through this point.

#### Letter loop count ####

When a ribbon enters a letter we can define how many times it loops around its outline/inline. Because looping takes time, this setting also has an impact on the overall legibility of the main message/wordmark.

#### Text scroll speed ####

Defines the overall scroll speed of text on ribbons.

The 4 buttons in the bottom left hand corner are:

#### Toggle debug mode on/off (keyboard shortcut: D) ####

In debug mode you can see the number of ribbons currently associated with each pole.

#### Toggle ribbon updates/animation on/off (keyboard shortcut: U) ####

Stopping the ribbon animation allows us to create freeze time effects. The text animation might have stopped, however you can still change the 3D view parameters independently. Another side effect is that you can better read the actual texts on the ribbons.

If the app is running in installation mode this setting can also be controlled via the controller software running on the Nokia N900.

#### Clear all (keyboard shortcut: C) ####

In the default identity generator mode, this will simply rerender the wordmark, but randomly re-positions all external poles and so create a new constallation.

In installation mode, this will kill the currently shown message and retriggers the default message ("Panic mode").

#### Clear ribbons (keyboard shortcut: R) ####

Keeps the current pole constellation, but re-triggers all ribbons.

### Camera ###
[![](http://farm3.static.flickr.com/2768/4028887706_715a028b24.jpg)](http://www.flickr.com/photos/toxi/4028887706/)

The 3D camera view can be controlled via a virtual arc ball controller. Press **Shift and then click and drag the mouse** to rotate the view in the direction of movement. The arcball navigation is using a circular area around the center of screen (turn on debug mode and press Shift to see it). If you click outside that area it will be unresponsive or behave weirdly...

#### Zoom ####

Zoom factor for the camera.

#### X/Y rotation speed ####

Automatic camera rotation (was used for recording short image sequences of camera sweeps, else fairly useless :) You can define individual speeds for both X and Y axis.

#### XYZ offset ####

Spatial camera offset position to create different crops (or animations).

#### Smooth settings ####

Each of the above the camera parameters has an attached "smoothing" slider to adjust the speed of value changes done. By setting a "smooth" slider to really small values, we can achieve relatively long & slow pans/rotations which can then be recorded as image sequences.

#### Camera presets ####

The application supports the loading & saving of camera configurations (excluding the smoothing slider settings).

##### Loading presets #####

Presets can be loaded by pressing a number key from 1-9 or using the radio button preset selector and then pressing the "Load Preset" button. The app comes with 3 default presets (1-3).

##### Loading presets #####

You can save the current configuration by pressing Control and a number key 1-9 or again by using the radio button preset selector and then pressing the "Save Preset" button. There's no user feedback for this action and existing presets will be overwritten without confirmation.

### Feeds ###
[![](http://farm3.static.flickr.com/2571/4028134871_13f397749d.jpg)](http://www.flickr.com/photos/toxi/4028134871/)

This interface tab displays a list of all configured data feeds acting as content providers for the text ribbons. Each feed has its own colour and can be turned on/off individually. If turned off, all ribbons showing messages from that feed become invisible. Not all feeds can be disabled at once, there's always at least one of them active...

### Export ###
[![](http://farm3.static.flickr.com/2444/4028135073_2cea4a985f.jpg)](http://www.flickr.com/photos/toxi/4028135073/)

Here you can choose export formats for image sequences and configure the size for the super highres image export.

#### High res image export ####

In order to keep down hardware (RAM) requirements for this public release, the maximum export size for highres images has been clipped to 10x the window size. For the default window size of 1280x720 pixels that still is equivalent to 108 x 60 cm (43 x 24 inch) at 300 dpi.

The actual export can be triggered via the "Export tiles" button or via **keyboard shortcut T**. The export works by rendering the current frame in tiles/slices which are automatically stitched together behind the scenes before saving out the complete image. For large number of tiles (e.g. 10 = 100 tiles) this process can take a few seconds. The export is finished when the app returns back into normal mode. Highres images are always saved as [Targa format](http://en.wikipedia.org/wiki/Truevision_TGA), which is lossless and fast to save, but creates large filesizes (approx. 40-120MB, depending on contents).

#### Exporting image sequences ####

Anything currently shown on screen (without the GUI) can be exported at any moment as image sequence. Depending on the fileformat chosen, this background export will slightly slow down the application. Whilst export is active a timecode of the currently exported frame in the active export session is displayed in the top right hand corner. The timecode format is currently hardcoded to 25fps, but this is purely for display purposes and does not have any impact on the actual movement speed of the animation (this actually controlled via the ribbon & camera settings above).

Exporting of image sequences can be started & stopped by pressing **SPACE**.

#### Export location ####

All exported assets are stored in the `export` folder inside the main application folder (will be automatically created if missing). Image sequences are furthermore placed within another subfolder with a unique timestamp.

**Note:** If you're exporting lots of long sequences, keep an eye on the file size of this `export` folder. Especially with TGA format, you'll quickly gather GB's of data...

## More parameters ##

If you want to get more under the hood and tweak parameters not currently exposed through the user interface, take a look at the description of the [default configuration](UserGuideConfig.md) file.