# Letter flow tool #

In order for the ribbons to form letters/characters we needed to create our own flow based font handling. For each point of an outline/inline path store extra metadata defining the 2 other points a ribbon could flow to, letter width, kerning. Character definitions are stored in the file `assets/alphabet/alphabet.xml` which is loaded during application start (see [initAlphabet() method in ODZApp](http://code.google.com/p/onedotzero-ident/source/browse/src/onedotzero/ODZApp.java#496)). Because the creation of these custom definitions is manual & cumbersome, I've developed a little tool to help with the creation/annotation.

[![](http://farm3.static.flickr.com/2526/4028888400_84a6a56564_o.png)](http://www.flickr.com/photos/toxi/4028888400/)

**Note: The tool is only available via the [source version](DevelopmentGuide#Getting_the_source.md), not as standalone download**

## Using the tool ##

This little tool parses individual letters exported as PNG files and then allows us to semi-automatically annotate each point to define flow options. You can use the tool to define a new alphabet using a different typeface (Note: there's **no support for splines/curves**) or to extend the available alphabet with new characters (however, the onedotzero typeface is not public). At current only these characters are defined:

  * lower case letters: a-z
  * UPPER case letters: S,C,R,E,N
  * numbers: 0-9
  * punctuation: &@():,-.!?/'";

The mainclass of the tool is located in `src.tools/onedotzero/tools/LetterFlowEditor.java` - just right click on this file in the package explorer and choose **Run As > Java Application**

The app will first attempt to load a new letter PNG file. It will scan all pixels of the chosen image and classify points based on a pixels color:

  * #ff00ff - outline point
  * #00ffff - inline point

Use left & right mouse keys to choose
flow options for each point.

### Key commands ###

  * **cursor up/down:** switch points.
  * **space:** toggle inline/outline path
  * **s:** dump flow info for current path
  * **l:** choose another character to process

## Character definitions ##

The snipped below is a single XML character definition. The `<node>` elements constitute the flow information printed out by the tool and need to be copy & pasted into the `alphabet.xml` master file manually.

```
<letter id="o" width="90">
    <outline seq="0,1,3,5,7,6,4,2">
        <vec2d x="16" y="43" />
        <vec2d x="60" y="43" />
        <vec2d x="0" y="59" />
        <vec2d x="76" y="59" />
        <vec2d x="0" y="119" />
        <vec2d x="76" y="119" />
        <vec2d x="16" y="135" />
        <vec2d x="60" y="135" />
        <flow>
            <node>1,2</node>
            <node>0,3</node>
            <node>0,4</node>
            <node>1,5</node>
            <node>2,6</node>
            <node>3,7</node>
            <node>4,7</node>
            <node>5,6</node>
        </flow>
    </outline>
    <inline seq="0,1,3,5,7,6,4,2">
        <vec2d x="20" y="52" />
        <vec2d x="56" y="52" />
        <vec2d x="9" y="63" />
        <vec2d x="67" y="63" />
        <vec2d x="9" y="115" />
        <vec2d x="67" y="115" />
        <vec2d x="20" y="126" />
        <vec2d x="56" y="126" />
        <flow>
            <node>1,2</node>
            <node>0,3</node>
            <node>0,4</node>
            <node>1,5</node>
            <node>2,6</node>
            <node>3,7</node>
            <node>4,7</node>
            <node>5,6</node>
        </flow>
    </inline>
</letter>
```