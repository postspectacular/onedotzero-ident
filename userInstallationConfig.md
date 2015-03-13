# Installation configuration #

The onedotzero application can controlled by a Nokia N900 handset as an interactive installation. The instructions below outline installing the software on the handset and configuring the application.

The handset talks to the application via OSC over a wireless network.

For ease of testing it is recommended the wireless network is left open and then protect the network once the installation is running correctly.

## Application configuration ##

The OSC i.p. and port are define in the 'config/osc.properties' file. By default this file will not need amending but as a reference to know where the configuration is stored.

#######################################################################

# OSC settings

#######################################################################

osc.ip=224.0.0.1

osc.port=7770

Below are the contents of the `config/app.properties` file that need amending.

As mentioned previously, the app can be run in "identity generator" or "installation" mode. The former is enabled by default and is meant for asset creation tasks and enables the GUI. Set the value to `false` to enable installation mode.

```
app.mode.identity=false
```

For installation mode the lines below configure you can also change the following behaviour:

  * Long messages are split in segments if longer than 5 lines
  * Messages are shown for 18 seconds (unless the user is interacting, then longer)
  * When entering menu screen on the N900, the current message is held for 6 seconds, after which the message queue is being processed again

```
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

## N900 configuration ##

**Step 1:** Launch the application manager.

![http://farm5.static.flickr.com/4031/4275631789_2d5c820556.jpg](http://farm5.static.flickr.com/4031/4275631789_2d5c820556.jpg)

**Step 2:** Select download.

![http://farm5.static.flickr.com/4036/4276376292_2bacf6d6ae.jpg](http://farm5.static.flickr.com/4036/4276376292_2bacf6d6ae.jpg)

**Step 3:** Select the 'Application Manager' title at the top of the screen.

![http://farm3.static.flickr.com/2778/4275631717_46d70fe452.jpg](http://farm3.static.flickr.com/2778/4275631717_46d70fe452.jpg)

**Step 4:** Select the 'Application catalogues' button.

![http://farm5.static.flickr.com/4020/4276376456_b8404a414b.jpg](http://farm5.static.flickr.com/4020/4276376456_b8404a414b.jpg)

**Step 5:** Select the 'new' button.

![http://farm5.static.flickr.com/4030/4329113850_40d18c54d2.jpg](http://farm5.static.flickr.com/4030/4329113850_40d18c54d2.jpg)

**Step 6:** Enter the following ->

  * Enter a catalog name of : Maemo extras-testing
  * Enter a web address of : http://repository.maemo.org/extras-testing
  * Enter a distribution of : fremantle
  * Enter components of : free non-free
  * Select ‘Save’

[to insert image](Need.md)


**Step 7:** The catalogues should update. Once complete, select the 'search' button.

![http://farm5.static.flickr.com/4020/4276376456_b8404a414b.jpg](http://farm5.static.flickr.com/4020/4276376456_b8404a414b.jpg)

**Step 8:** Enter 'onedotzero' in the search words input field and press 'search'.

![http://farm3.static.flickr.com/2750/4276376322_e554db472e.jpg](http://farm3.static.flickr.com/2750/4276376322_e554db472e.jpg)

**Step 9:** The onedotzero application should be found and then select the application to install.

![http://farm5.static.flickr.com/4011/4276376554_0d775189c1.jpg](http://farm5.static.flickr.com/4011/4276376554_0d775189c1.jpg)

**Step 10:** Select the 'I understand and agree' checkbox and then select 'continue'.

![http://farm5.static.flickr.com/4071/4275631941_628f23829b.jpg](http://farm5.static.flickr.com/4071/4275631941_628f23829b.jpg)

**Step 11:** Once the application has installed, launch it from the application manager.

![http://farm5.static.flickr.com/4064/4275631577_0c89c09818.jpg](http://farm5.static.flickr.com/4064/4275631577_0c89c09818.jpg)

**Step 12:** Launch the identity application on the computer and the N900 should control the identity.