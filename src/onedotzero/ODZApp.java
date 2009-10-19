/*
 * This file is part of onedotzero 2009 identity generator (ODZGen).
 * 
 * Copyright 2009 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * ODZGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ODZGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ODZGen. If not, see <http://www.gnu.org/licenses/>.
 */

package onedotzero;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import onedotzero.data.FeedPool;
import onedotzero.data.SMSProvider;
import onedotzero.data.UserMessageProvider;
import onedotzero.data.FeedPool.FeedConfiguration;
import onedotzero.export.FrameSequenceExporter;
import onedotzero.export.Tiler;
import onedotzero.message.MessageLine;
import onedotzero.message.MessageScheduleListener;
import onedotzero.message.MessageScheduler;
import onedotzero.message.UserMessage;
import onedotzero.osc.InteractionStateListener;
import onedotzero.osc.OSCManager;
import onedotzero.poles.ParticlePole3D;
import onedotzero.poles.PoleManager;
import onedotzero.poles.strategies.RandomXPolePositioning;
import onedotzero.states.AppState;
import onedotzero.states.IdentState;
import onedotzero.states.IdleState;
import onedotzero.states.MenuState;
import onedotzero.states.ShakeState;
import onedotzero.states.TiltState;
import onedotzero.states.TouchState;
import onedotzero.text.MessageFormatter;
import onedotzero.text.WordWrapFormatter;
import onedotzero.type.Alphabet;
import onedotzero.type.CustomPoles;
import onedotzero.type.LetterPoleGroup;
import oscP5.OscMessage;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;
import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;
import toxi.math.MathUtils;
import toxi.math.conversion.UnitTranslator;
import toxi.math.waves.SineWave;
import toxi.util.datatypes.TypedProperties;
import controlP5.Bang;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.Radio;
import controlP5.Slider;
import controlP5.Textlabel;
import controlP5.Toggle;

/**
 * <p>
 * Main class of the onedotzero 2009 identity generator & installation
 * application. Handles initialization and management of all data structures and
 * components, rendering, user interface, events and communication with the
 * Nokia N900 device used for the interactive installation version.
 * </p>
 * 
 * <p>
 * All code is commented as much as possible, please consult the available
 * javadocs in the docs folder as well as further documentation on the project
 * wiki: <a
 * href="http://onedotzero-ident.googlecode.com/">onedotzero-ident.googlecode
 * .com</a>
 * </p>
 * 
 * @author Karsten Schmidt <info at postspectacular dot com>
 * 
 */
public class ODZApp extends PApplet implements InteractionStateListener,
        MessageScheduleListener {

    private static final long serialVersionUID = -1L;

    private static final String APP_NAME = "ODZIdent";
    private static final String APP_VERSION = "20091019";

    public static final AppState IDLE_STATE = new IdleState();
    public static final AppState MENU_STATE = new MenuState();
    public static final AppState TILT_STATE = new TiltState();
    public static final AppState SHAKE_STATE = new ShakeState();
    public static final AppState TOUCH_STATE = new TouchState();
    public static final AppState IDENT_STATE = new IdentState();

    private static TypedProperties config;

    private static final Logger logger =
            Logger.getLogger(ODZApp.class.getName());

    private static int WIDTH, HEIGHT;
    private static final int UI_X = 20;
    private static final int UI_Y = 50;

    /**
     * Main entry point of the app. By default the default configuration is read
     * from the file <code>config/app.properties</code>, however this can be
     * overruled by specifying another location as command line parameter.
     * 
     * @param args
     */
    public static void main(String[] args) {
        config = new TypedProperties();
        String configFile = args.length > 0 ? args[0] : "config/app.properties";
        if (config.load(configFile)) {
            WIDTH = config.getInt("app.width", WIDTH);
            HEIGHT = config.getInt("app.height", HEIGHT);
            String mainClass = config.getProperty("app.mainclass");
            if (mainClass != null) {
                PApplet.main(new String[] {
                        // "--present",
                        "--bgcolor="
                                + config.getProperty("app.bgcolor", "000000"),
                        "--hide-stop", mainClass });
            } else {
                logger
                        .severe("no main class define in config file, exiting...");
            }
        } else {
            logger.severe("can't load main config file: " + configFile
                    + ", exiting...");
        }
    }

    private TColor bgColor;

    private Alphabet alphabet;
    private PoleManager poles;
    private CustomPoles customPoles;

    private FeedPool feedPool;
    private TextureManager textureManager;

    private PGraphicsOpenGL pgl;
    private GL gl;
    private PFont font;

    private ControlP5 ui;
    private CameraState camera;
    private ArcBall arcBall;
    private AABB worldBounds = new AABB(new Vec3D(), new Vec3D(1500, 400, 500));

    private FrameSequenceExporter exporter;
    private Tiler tiler;

    private List<Ribbon> ribbons = new ArrayList<Ribbon>();
    private List<Ribbon> oldRibbons = new ArrayList<Ribbon>();

    private MessageFormatter messageFormatter;
    private MessageScheduler messageScheduler;
    private UserMessageProvider userMessageProvider;
    private UserMessage newMessage;
    private String prevMessage;
    private long lastMessageTime;

    private SMSProvider smsProvider;
    private TypedProperties smsConfig;

    private OSCManager osc;

    private AppState appState;
    private long lastAppStateChange;

    private float ribbonWidth = 12;
    private float letterScale = 1.75f;
    private float maxScrollSpeed = 0.005f;
    private int numNewRibbons = 1;
    private float newRibbonChance = 0.5f;

    private boolean isDebug = false;
    private boolean isShiftDown = false;
    private boolean isTouching = false;
    private boolean isControlDown = false;
    private boolean doShowMask = false;
    private boolean doUseSMS = false;
    private boolean doUpdate = true;

    private Textlabel uiLabelNumTiles;

    private Vec3D centreExclusion;
    private Vec3D shakeDir = new Vec3D();
    private Vec3D targetTouchPos = new Vec3D();
    private Vec3D touchPos = new Vec3D();

    private int numExportTiles = 4;
    private int numPoles = 60;
    private int sequenceID;
    private int maxRibbonCount = 500;
    private int camPresetID;
    private int maxRibbonDelay;
    private float shakeEnergy;
    private float shakeMaxEnergy = 1200;
    private float shakeEnergyDecay = 0.95f;

    private PImage maskImg;

    private int ribbonLoopCount;

    /**
     * Initializes and adds a new single text ribbon in the space. The ribbon is
     * created so that it flows through the currently least used letter.
     */
    private void addRibbon() {
        List<ParticlePole3D> poleSet;
        LetterPoleGroup letter = poles.getLeastUsedLetter();
        if (letter.hasInline() && letter.innerUsage < letter.outerUsage) {
            poleSet = letter.inner;
        } else {
            poleSet = letter.outer;
        }
        FeedConfiguration fc = feedPool.getRandomActiveFeed();
        Texture tex = textureManager.getTextureFor(fc.feed.getMessage());
        Ribbon r = new Ribbon(poles, tex, fc, maxScrollSpeed, maxRibbonDelay);
        if (r.create(poleSet, Vec3D.Y_AXIS, sequenceID, ribbonLoopCount)) {
            ribbons.add(r);
        }
    }

    /**
     * Main update & rendering loop.
     * 
     * @see processing.core.PApplet#draw()
     */
    @Override
    public void draw() {
        if (!config.getBoolean("app.mouse.enabled", false)) {
            noCursor();
        }
        appState.pre(this);
        if (newMessage != null) {
            initPolesAndRibbonsForMessage(newMessage);
            newMessage = null;
        }
        pushMatrix();
        {
            sequenceID++;
            camera.perspective(this);
            background(bgColor.toARGB());
            translate(width * 0.5f, height * 0.5f, 0);
            arcBall.apply();
            if (!tiler.isTiling()) {
                camera.update(this);
            }
            camera.apply(this);
            tiler.pre();
            gl.glDepthMask(false);
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
            gl.glTexParameterf(GL.GL_TEXTURE_2D,
                    GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, 4f);
            if (!tiler.isTiling()) {
                shakeEnergy *= shakeEnergyDecay;
                appState.update(this);
                if (doUpdate) {
                    if (ribbons.size() < maxRibbonCount) {
                        for (int i = 0; i < numNewRibbons
                                && ribbons.size() < maxRibbonCount; i++) {
                            if (random(1f) < newRibbonChance) {
                                addRibbon();
                            }
                        }
                    }
                }
                float displaceDecay = (appState != SHAKE_STATE ? 0.9f : 0.99f);
                for (Iterator<Ribbon> i = oldRibbons.iterator(); i.hasNext();) {
                    Ribbon r = i.next();
                    if (!r.update(sequenceID, doUpdate, displaceDecay)) {
                        r.cleanup();
                        i.remove();
                    }
                }
                for (Iterator<Ribbon> i = ribbons.iterator(); i.hasNext();) {
                    Ribbon r = i.next();
                    if (!r.update(sequenceID, doUpdate, displaceDecay)) {
                        r.cleanup();
                        i.remove();
                    }
                }
            }
            pgl.beginGL();
            {
                gl.glEnable(GL.GL_TEXTURE_2D);
                for (Ribbon r : oldRibbons) {
                    r.draw(g, gl);
                }
                for (Ribbon r : ribbons) {
                    r.draw(g, gl);
                }
            }
            pgl.endGL();
            if (isDebug) {
                noStroke();
                textSize(32);
                for (ParticlePole3D p : poles.c12poles) {
                    drawDebugPole(p);
                }
                for (ParticlePole3D p : poles.c3poles) {
                    drawDebugPole(p);
                }
            }
            noTint();
            tiler.post();
            exporter.update(g);
        }
        popMatrix();
        if (doShowMask) {
            drawMask();
        }
        fill(255);
        if (exporter.isExporting()) {
            fill(255, 0, 0);
            rect(width - UI_X - 20, UI_Y, 20, 20);
            textAlign(RIGHT);
            text(exporter.getTimeCode(), width - UI_X - 30, UI_Y + 20);
            textAlign(LEFT);
        }
        if (isShiftDown) {
            drawArcBallCue();
        }
        // don't show GUI if in installation mode
        ui.setAutoDraw(appState == IDENT_STATE);
    }

    /**
     * Displays the boundary of the active arc ball navigation.
     */
    private void drawArcBallCue() {
        noFill();
        stroke(255);
        ellipse(width * 0.5f, height * 0.5f, arcBall.radius * 2,
                arcBall.radius * 2);
    }

    /**
     * Shows debug info for an individual pole (displays hit count, i.e. number
     * of ribbons currently attached to this pole)
     * 
     * @param p
     *            pole
     */
    private void drawDebugPole(ParticlePole3D p) {
        pushMatrix();
        translate(p.x, p.y, p.z);
        if (customPoles != null && customPoles.points.indexOf(p) != -1) {
            fill(255, 255, 0);
            box(5);
        } else {
            fill(p.charge < 0 ? 0xff0000ff : 0xffff0000);
            box(2);
        }
        text("" + p.hitCount, 0, 0);
        popMatrix();
    }

    /**
     * Draws optional gradient overlay to fade edges to black.
     */
    private void drawMask() {
        hint(DISABLE_DEPTH_TEST);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        imageMode(CENTER);
        image(maskImg, width * 0.5f, maskImg.height * 0.5f, width,
                maskImg.height);
        pushMatrix();
        {
            translate(maskImg.height * 0.5f, height * 0.5f);
            rotate(-HALF_PI);
            image(maskImg, 0, 0);
        }
        popMatrix();
        pushMatrix();
        {
            translate(width - maskImg.height * 0.5f, height * 0.5f);
            rotate(HALF_PI);
            image(maskImg, 0, 0);
        }
        popMatrix();
        hint(ENABLE_DEPTH_TEST);
    }

    /**
     * Returns the camera configuration.
     * 
     * @return cam config
     */
    public CameraState getCamera() {
        return camera;
    }

    /**
     * Returns the main app configuration.
     * 
     * @return app config
     */
    public TypedProperties getConfig() {
        return config;
    }

    /**
     * Returns a reference to the ControlP5 GUI instance.
     * 
     * @return main gui
     */
    public ControlP5 getGUI() {
        return ui;
    }

    /**
     * Returns time stamp of the last {@link AppState} change.
     * 
     * @return timestamp
     */
    public long getLastAppStateChange() {
        return lastAppStateChange;
    }

    /**
     * Returns time stamp of the last message scheduled.
     * 
     * @return timestamp
     */
    public long getLastMessageTime() {
        return lastMessageTime;
    }

    /**
     * Returns list of expired, but still visible ribbons.
     * 
     * @return list of ribbons
     */
    public List<Ribbon> getOldRibbons() {
        return oldRibbons;
    }

    /**
     * Returns list of active ribbons.
     * 
     * @return list of ribbons
     */
    public List<Ribbon> getRibbons() {
        return ribbons;
    }

    /**
     * Returns the {@link MessageScheduler} instance.
     * 
     * @return scheduler
     */
    public MessageScheduler getScheduler() {
        return messageScheduler;
    }

    /**
     * Returns the tiled exporter instance for high res images.
     * 
     * @return tiler
     */
    public Tiler getTiler() {
        return tiler;
    }

    /**
     * Initializes alphabet class structure from XML using JAXB.
     * 
     * @see Alphabet
     */
    private void initAlphabet() {
        try {
            JAXBContext context = JAXBContext.newInstance(Alphabet.class);
            File file = new File("assets/alphabet/alphabet.xml");
            alphabet = (Alphabet) context.createUnmarshaller().unmarshal(file);
            alphabet.init();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes camera configuration & arc ball navigation using default
     * settings from app.properties
     * 
     * @see CameraState
     */
    private void initCamera() {
        arcBall = new ArcBall(this);
        camera = new CameraState();
        camera.flipCamera(!config.getBoolean("app.mode.identity", false));
        camera.pos.set(config.getFloat("cam.pos.x", 0), config.getFloat(
                "cam.pos.y", 0), config.getFloat("cam.pos.z", 0));
        camera.rotation.set(config.getFloat("cam.rotation.x", 0), config
                .getFloat("cam.rotation.y", 0), config.getFloat(
                "cam.rotation.z", 0));
        camera.zoom = config.getFloat("cam.zoom", camera.zoom);
        camera.zoomSmooth =
                config.getFloat("cam.zoom.smooth", camera.zoomSmooth);
        camera.rotSmooth =
                config.getFloat("cam.rotation.smooth", camera.rotSmooth);
        camera.panSmooth = config.getFloat("cam.pan.smooth", camera.panSmooth);
    }

    /**
     * Initializes default settings for GUI, poles & ribbon behaviour from
     * app.properties
     */
    private void initDefaults() {
        bgColor = TColor.newHex(config.getProperty("background.col", "000000"));
        frameRate(config.getInt("app.framerate", 60));
        numPoles = config.getInt("defaults.poles.count", numPoles);
        centreExclusion =
                new Vec3D(0, config.getFloat("defaults.poles.exclusion.depth",
                        0.33f), config.getFloat(
                        "defaults.poles.exclusion.height", 0.5f));
        maxRibbonCount =
                config.getInt("defaults.ribbon.totalmaxcount", maxRibbonCount);
        maxRibbonDelay =
                config.getInt("defaults.ribbon.spawndelay", maxRibbonDelay);
        ribbonLoopCount = config.getInt("defaults.ribbon.loopcount", 2);
        ribbonWidth = config.getFloat("defaults.ribbon.width", ribbonWidth);
        letterScale =
                config.getFloat("defaults.ribbon.letter.scale", letterScale);
        maxScrollSpeed =
                config.getFloat("defaults.ribbon.maxscrollspeed", 0.005f);
        numNewRibbons = config.getInt("defaults.ribbon.add.perframe", 1);
        newRibbonChance = config.getFloat("defaults.ribbon.add.chance", 0.5f);
        worldBounds.setExtent(new Vec3D(config.getFloat(
                "defaults.bounds.extent.x", 1000), config.getFloat(
                "defaults.bounds.extent.y", 400), config.getFloat(
                "defaults.bounds.extent.z", 500)));

        numExportTiles =
                config.getInt("defaults.export.numtiles", numExportTiles);
    }

    /**
     * Initializes the graphical user interface with default settings. See <a
     * href="http://sojamo.de/">sojamo.de</a> for further information about this
     * GUI library.
     */
    private void initGUI() {
        String UI_EXPORT = "export";
        String UI_FEEDS = "feeds";
        String UI_CAMERA = "camera";

        ui = new ControlP5(this);
        ui.setColorActive(0xe00099cc);
        ui.setColorBackground(0x20ffffff);
        ui.setColorForeground(0xe0003355);
        ui.setColorLabel(0xffffffff);
        ui.setColorValue(0xffffffff);
        ui.setAutoInitialization(false);
        ui.getTab("default").setLabel("ribbons");
        ui.addTab(UI_CAMERA);
        ui.addTab(UI_FEEDS);
        ui.addTab(UI_EXPORT);

        ui.addSlider("numPoles", 1, 20, numPoles, UI_X, UI_Y, 100, 14)
                .setLabel("Number of poles");
        ui.addSlider("setPoleHitCount", 1, 300,
                poles.getMaxExternalPoleHitcount(), UI_X, UI_Y + 20, 100, 14)
                .setLabel("max pole hitcount");
        ui.addSlider("maxRibbonCount", 0, 1000, maxRibbonCount, UI_X,
                UI_Y + 40, 100, 14).setLabel("max ribbon count");
        ui.addSlider("setRibbonHitCount", 1, 150, poles.getMaxLetterHitCount(),
                UI_X, UI_Y + 60, 100, 14).setLabel("max letter hitcount");
        ui.addSlider("ribbonLoopCount", 0, 4, ribbonLoopCount, UI_X, UI_Y + 80,
                100, 14).setLabel("letter loop count");
        ui.addSlider("maxScrollSpeed", 0.001f, 0.02f, maxScrollSpeed, UI_X,
                UI_Y + 100, 100, 14).setLabel("text scroll speed");

        ui.addToggle("isDebug", isDebug, UI_X, height - UI_Y - 184, 28, 28)
                .setLabel("toggle debug mode");

        ui.addToggle("doUpdate", doUpdate, UI_X, height - UI_Y - 132, 28, 28)
                .setLabel("toggle ribbon animation");

        ui.addBang("triggerDefaultMessage", UI_X, height - UI_Y - 80, 28, 28)
                .setLabel("clear all");

        ui.addBang("initRibbons", UI_X, height - UI_Y - 28, 28, 28).setLabel(
                "clear ribbons");

        // ////////////////////////// camera

        Slider s =
                ui.addSlider("setCamTargetZoom", 0.5f, 7, camera.targetZoom,
                        UI_X, UI_Y, 200, 14);
        s.setLabel("Zoom");
        s.setTab(UI_CAMERA);

        s =
                ui.addSlider("setCamZoomSmooth", 0.005f, 0.25f,
                        camera.zoomSmooth, UI_X + 400, UI_Y, 200, 14);
        s.setLabel("smooth");
        s.setTab(UI_CAMERA);

        s =
                ui.addSlider("setCamRotSpeedX", -0.05f, 0.05f,
                        camera.targetRotSpeed.x, UI_X, UI_Y + 40, 200, 14);
        s.setLabel("X rotation speed");
        s.setTab(UI_CAMERA);
        s =
                ui.addSlider("setCamRotSpeedY", -0.05f, 0.05f,
                        camera.targetRotSpeed.y, UI_X, UI_Y + 80, 200, 14);
        s.setLabel("Y rotation speed");
        s.setTab(UI_CAMERA);

        s =
                ui.addSlider("setCamRotSmooth", 0.005f, 0.25f,
                        camera.rotSmooth, UI_X + 400, UI_Y + 40, 200, 14);
        s.setLabel("smooth");
        s.setTab(UI_CAMERA);

        Bang b = ui.addBang("resetCamSpeedX", UI_X + 300, UI_Y + 40, 14, 14);
        b.setLabel("clear X");
        b.setTab(UI_CAMERA);
        b = ui.addBang("resetCamSpeedY", UI_X + 300, UI_Y + 80, 14, 14);
        b.setLabel("clear Y");
        b.setTab(UI_CAMERA);

        s =
                ui.addSlider("setTargetPanX", -1000, 1000, camera.pos.x, UI_X,
                        UI_Y + 120, 200, 14);
        s.setLabel("cam offset X");
        s.setTab(UI_CAMERA);

        s =
                ui.addSlider("setTargetPanY", -worldBounds.getExtent().y,
                        worldBounds.getExtent().y, camera.pos.y, UI_X,
                        UI_Y + 140, 200, 14);
        s.setLabel("cam offset Y");
        s.setTab(UI_CAMERA);

        s =
                ui.addSlider("setTargetPanZ", -worldBounds.getExtent().z,
                        worldBounds.getExtent().z, camera.pos.z, UI_X,
                        UI_Y + 160, 200, 14);
        s.setLabel("cam offset Z");
        s.setTab(UI_CAMERA);

        s =
                ui.addSlider("setCamPanSmooth", 0.005f, 0.25f,
                        camera.panSmooth, UI_X + 400, UI_Y + 120, 200, 14);
        s.setLabel("smooth");
        s.setTab(UI_CAMERA);

        Radio r = ui.addRadio("camPresetID", UI_X, UI_Y + 200);
        for (int i = 1; i <= 9; i++) {
            r.addItem("preset #" + i, i);
        }
        r.setTab(UI_CAMERA);

        b = ui.addBang("loadCamPreset", UI_X + 100, UI_Y + 200, 28, 28);
        b.setLabel("load preset");
        b.setTab(UI_CAMERA);
        b = ui.addBang("saveCamPreset", UI_X + 100, UI_Y + 250, 28, 28);
        b.setLabel("save preset");
        b.setTab(UI_CAMERA);

        // ////////////////////////// feeds

        int y = UI_Y;
        int feedID = 0;
        for (FeedConfiguration f : feedPool) {
            Toggle t = ui.addToggle("toggleFeed", f.isEnabled, UI_X, y, 14, 14);
            t.setId(feedID);
            t.setLabel(f.id);
            t.addListener(new ControlListener() {

                @Override
                public void controlEvent(ControlEvent e) {
                    int num = feedPool.getActiveFeedCount();
                    int id = e.controller().id();
                    if (!feedPool.getFeedForID(id).isEnabled || num > 1) {
                        feedPool.toggleFeedStatus(id);
                    }
                }
            });
            t.setTab(UI_FEEDS);
            y += 40;
            feedID++;
        }

        // ////////////////////////// export

        r = ui.addRadio("setExportFormat", UI_X, UI_Y);
        r.setBroadcast(false);
        int i = 0;
        for (String f : FrameSequenceExporter.FORMATS) {
            r.addItem(f, i);
            i++;
        }
        r.activate(exporter.getFileFormat());
        println("exporter: " + r.value() + " " + exporter.getFileFormat());
        r.setBroadcast(true);
        r.setTab(UI_EXPORT);

        s =
                ui.addSlider("setNumExportTiles", 1, 10, numExportTiles,
                        UI_X + 100, UI_Y, 100, 14);
        s.setLabel("num tiles");
        s.setTab(UI_EXPORT);

        uiLabelNumTiles =
                ui.addTextlabel("uiLabelNumTiles", "", UI_X + 100, UI_Y + 20);
        uiLabelNumTiles.setTab(UI_EXPORT);

        b = ui.addBang("saveTiles", UI_X + 100, UI_Y + 40, 28, 28);
        b.setLabel("export tiles");
        b.setTab(UI_EXPORT);

        setNumExportTiles(numExportTiles);
    }

    /**
     * Loads & initializes all registered message feeds. Starts up the message
     * scheduler.
     * 
     * @param isIdentity
     */
    private void initMessages(boolean isIdentity) {
        feedPool = new FeedPool();
        int numFeeds = config.getInt("feeds.count", 1);
        for (int i = 0; i < numFeeds; i++) {
            String feedID = "feed" + i;
            String id = config.getProperty(feedID + ".id");
            String type = config.getProperty(feedID + ".type");
            String url = config.getProperty(feedID + ".url");
            String hexCol = config.getProperty(feedID + ".col");
            FeedConfiguration fc = null;
            if (id != null && type != null && url != null && hexCol != null) {
                TColor col = TColor.newHex(hexCol);
                if (url.indexOf("http://") == -1) {
                    File f = new File(sketchPath(url));
                    URI u = f.toURI();
                    url = u.toString();
                }
                if (type.equalsIgnoreCase("atom")) {
                    fc = feedPool.addAtomFeed(id, url, col);
                } else if (type.equalsIgnoreCase("rss")) {
                    fc = feedPool.addRSSFeed(id, url, col);
                }
            }
            if (fc != null) {
                fc.isEnabled = config.getBoolean(feedID + ".enabled", true);
            } else {
                logger.warning("feed #" + i
                        + " config invalid, ignoring this feed");
            }
        }
        userMessageProvider =
                new UserMessageProvider(config.getInt("message.log.count", 10));
        if (!isIdentity) {
            String userCol = config.getProperty("message.feed.col", "ffff00");
            feedPool.addFeed("user", TColor.newHex(userCol),
                    userMessageProvider);
        }
        messageFormatter =
                new WordWrapFormatter(config.getInt("message.wordwrap", 12));
        messageScheduler =
                new MessageScheduler(config.getInt("message.num.recent", 1));
        messageScheduler.addListener(this);
        messageScheduler.start();
    }

    /**
     * Initializes OSC communication & listener based on config settings.
     */
    private void initOSC() {
        TypedProperties conf = new TypedProperties();
        conf.load("config/osc.properties");
        String ip = conf.getProperty("osc.ip", "239.0.0.1");
        int port = conf.getInt("osc.port", 7777);
        osc = new OSCManager(ip, port);
        osc.addListener(this);
    }

    private void initPoleManager() {
        poles = new PoleManager(alphabet, worldBounds);
        poles.setPositionStrategy(new RandomXPolePositioning());
        poles.setMaxLetterHitcount(config.getInt(
                "defaults.poles.letters.maxhitcount", 30));
        poles.setMaxExternalPoleHitcount(config.getInt(
                "defaults.poles.external.maxhitcount", 60));
    }

    /**
     * (Re)Initializes the pole manager and loads optional, hardcoded custom
     * poles.
     */
    private void initPoles() {
        if (config.getBoolean("defaults.poles.custom.enabled", false)) {
            try {
                JAXBContext context =
                        JAXBContext.newInstance(CustomPoles.class);
                File file = new File("config/custompoles.xml");
                customPoles =
                        (CustomPoles) context.createUnmarshaller().unmarshal(
                                file);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        poles.clear();
        poles.setCentreExclusion(centreExclusion);
        initRibbons();
    }

    /**
     * Parses the given message and creates a new constellation of poles
     * matching the letters of the message. Currently active ribbons are expired
     * and new ones triggered.
     * 
     * @param msg
     *            message
     */
    private void initPolesAndRibbonsForMessage(UserMessage msg) {
        logger.info("reset poles for new message");
        poles.setCentreExclusionZ(centreExclusion.z);
        poles.init(numPoles);
        if (customPoles != null) {
            poles.addExternalPoles(customPoles.points);
        }
        List<MessageLine> lines = msg.getContent();
        for (MessageLine line : lines) {
            poles.addMessageAt(line.text, line.offset, line.scale);
        }
        poles.processGroups();
        sequenceID = 0;
        ArrayList<Ribbon> or = new ArrayList<Ribbon>();
        synchronized (ribbons) {
            for (Ribbon r : ribbons) {
                r.retire();
                or.add(r);
            }
            initRibbons();
        }
        synchronized (oldRibbons) {
            or.addAll(oldRibbons);
        }
        oldRibbons = or;
    }

    /**
     * Clears all existing ribbons.
     */
    private void initRibbons() {
        Ribbon.configureWidth(ribbonWidth, letterScale);
        ribbons.clear();
        if (poles != null) {
            poles.clearHitCounts();
        }
    }

    /**
     * Initializes shake mode for all ribbons in the given list.
     * 
     * @param ribbons
     *            list of ribbons
     */
    public void initRibbonShake(List<Ribbon> ribbons) {
        if (ribbons != null) {
            synchronized (ribbons) {
                for (Ribbon r : ribbons) {
                    r.initShake();
                }
            }
        }
    }

    /**
     * Loads sms.properties config file and sets up default configuration if SMS
     * messaging is enabled.
     */
    private void initSMS() {
        smsConfig = new TypedProperties();
        if (smsConfig.load("config/sms.properties")) {
            doUseSMS = smsConfig.getBoolean("sms.enabled", false);
            if (doUseSMS) {
                float minZoom = smsConfig.getFloat("cam.zoom.min", 0.5f);
                float maxZoom = smsConfig.getFloat("cam.zoom.max", 5);
                float zoomSpeed = smsConfig.getFloat("cam.zoom.speed", 0.01f);
                camera.setZoomMod(new SineWave(0, zoomSpeed,
                        (maxZoom - minZoom) / 2, (maxZoom + minZoom) / 2));
                smsProvider = new SMSProvider(this, smsConfig);
                smsProvider.start();
            }
        }
    }

    /**
     * Initializes the texture manager with default settings from
     * app.properties.
     */
    private void initTextures() {
        textureManager =
                new TextureManager(this, gl, font, config.getInt(
                        "texture.width", 4096));
    }

    /**
     * Handles keyboard events / shortcuts and updates settings/states.
     * 
     * @see processing.core.PApplet#keyPressed()
     */
    @Override
    public void keyPressed() {
        if (keyCode == SHIFT) {
            isShiftDown = true;
        }
        if (keyCode == CONTROL) {
            isControlDown = true;
        }
        if (key == 32) {
            if (exporter.isExporting()) {
                exporter.stop();
            } else {
                if (exporter.newSession()) {
                    exporter.start();
                } else {
                    logger
                            .severe("could not create folder for new export session");
                }
            }
        } else if (key == 'c' || key == 'C') {
            triggerDefaultMessage();
        } else if (key == 'r' || key == 'R') {
            initRibbons();
        } else if (key == 'p' || key == 'P') {
            initPoles();
            triggerDefaultMessage();
        } else if (key == 'u' || key == 'U') {
            doUpdate = !doUpdate;
        } else if (key == 't' || key == 'T') {
            saveTiles();
        } else if (key >= '1' && key <= '9') {
            if (isControlDown) {
                saveCamera(key - '0');
            } else {
                loadCameraPreset(key - '0');
            }
        } else if (key == 'd' || key == 'D') {
            isDebug = !isDebug;
        }
    }

    @Override
    public void keyReleased() {
        if (keyCode == SHIFT) {
            isShiftDown = false;
        }
        if (keyCode == CONTROL) {
            isControlDown = false;
        }
    }

    /**
     * Attempts to load & initialize the given camera preset from a config file.
     * Updates arc ball orientation, camera position, zoom & rotation.
     * 
     * @param id
     */
    public void loadCameraPreset(int id) {
        TypedProperties conf = new TypedProperties();
        if (conf.load("config/cam" + id + ".properties")) {
            StringTokenizer st =
                    new StringTokenizer(conf.getProperty("arcball.q_down",
                            "1,0,0,0"), ",");
            arcBall.downOrientation.set(parseFloat(st.nextToken()),
                    parseFloat(st.nextToken()), parseFloat(st.nextToken()),
                    parseFloat(st.nextToken()));
            st =
                    new StringTokenizer(conf.getProperty("arcball.q_drag",
                            "1,0,0,0"), ",");
            arcBall.dragOrientation.set(parseFloat(st.nextToken()),
                    parseFloat(st.nextToken()), parseFloat(st.nextToken()),
                    parseFloat(st.nextToken()));
            st =
                    new StringTokenizer(conf.getProperty("cam.pan", "0,0,0,0"),
                            ",");
            camera.targetPos.set(parseFloat(st.nextToken()), parseFloat(st
                    .nextToken()), parseFloat(st.nextToken()));
            camera.pos.set(camera.targetPos);
            st =
                    new StringTokenizer(conf.getProperty("cam.rotation",
                            "0,0,0,0"), ",");
            camera.rotation.set(parseFloat(st.nextToken()), parseFloat(st
                    .nextToken()), parseFloat(st.nextToken()));
            camera.zoom =
                    camera.targetZoom = conf.getFloat("cam.zoom", camera.zoom);
            ui.controller("setCamTargetZoom").setValue(camera.zoom);
            ui.controller("setTargetPanX").setValue(camera.targetPos.x);
            ui.controller("setTargetPanY").setValue(camera.targetPos.y);
            ui.controller("setTargetPanZ").setValue(camera.targetPos.z);
            ui.controller("camPresetID").setValue(id);
        }
    }

    /**
     * Loads the currently chosen (via GUI) camera preset.
     */
    public void loadCamPreset() {
        loadCameraPreset(camPresetID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.message.MessageScheduleListener#messageQueueProcessed()
     */
    @Override
    public void messageQueueProcessed() {
        logger.info("message queue processed...");
        triggerDefaultMessage();
        messageScheduler.replayRecent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * onedotzero.message.MessageScheduleListener#messageScheduled(onedotzero
     * .message.UserMessage)
     */
    @Override
    public void messageScheduled(UserMessage msg) {
        logger.info("setting new message");
        newMessage = msg;
    }

    @Override
    public void mouseDragged() {
        if (isShiftDown) {
            arcBall.mouseDragged();
        }
    }

    @Override
    public void mousePressed() {
        if (isShiftDown) {
            arcBall.mousePressed();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * onedotzero.osc.InteractionStateListener#oscMessageReceived(oscP5.OscMessage
     * )
     */
    @Override
    public void oscMessageReceived(OscMessage msg) {
        logger.info("generic osc msg callback");
        if (msg.checkAddrPattern("/touchoff")) {
            isTouching = false;
        }
    }

    public void resetCamSpeedX() {
        camera.targetRotSpeed.x = 0;
    }

    public void resetCamSpeedY() {
        camera.targetRotSpeed.y = 0;
    }

    public void saveCamera(int id) {
        TypedProperties conf = new TypedProperties();
        Quaternion q = arcBall.downOrientation;
        conf.setProperty("arcball.q_down", q.w + "," + q.x + "," + q.y + ","
                + q.z);
        q = arcBall.dragOrientation;
        conf.setProperty("arcball.q_drag", q.w + "," + q.x + "," + q.y + ","
                + q.z);
        conf.setProperty("cam.zoom", "" + camera.zoom);
        conf.setProperty("cam.pan", "" + camera.pos.x + "," + camera.pos.y
                + "," + camera.pos.z);
        conf.setProperty("cam.rotation", "" + camera.rotation.x + ","
                + camera.rotation.y + "," + camera.rotation.z);
        try {
            conf.store(new FileOutputStream("config/cam" + id + ".properties"),
                    null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCamPreset() {
        saveCamera(camPresetID);
    }

    public void saveTiles() {
        tiler.initTiles(camera.fov, camera.near, camera.far);
        tiler.save(sketchPath("export"), "odz-xl-"
                + (System.currentTimeMillis() / 1000), "tga");
    }

    public void scheduleMessage(String message) {
        userMessageProvider.addMessage(message);
        int maxLines = config.getInt("message.maxlines", 5);
        float leading = config.getFloat("message.leading", 160);
        List<String> lines = messageFormatter.format(message);
        int numLines = min(maxLines, lines.size());
        float totalHeight = numLines * leading;
        // split wordwrapped message in chunks of x lines
        while (numLines > 0) {
            ArrayList<MessageLine> msgPart =
                    new ArrayList<MessageLine>(maxLines);
            Vec3D offset = new Vec3D(0, 0, -totalHeight / 2);
            for (int i = 0; i < numLines; i++) {
                String line = lines.remove(0);
                offset.x = -alphabet.getWidthForString(line) / 2;
                msgPart.add(new MessageLine(line, offset.copy(), 1));
                offset.z += leading;
            }
            numLines = lines.size();
            messageScheduler.addMessage(new UserMessage(msgPart, config.getInt(
                    "message.ttl", 10000), 1));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * onedotzero.osc.InteractionStateListener#setAppState(onedotzero.states
     * .AppState)
     */
    @Override
    public void setAppState(AppState state) {
        logger.info("new app state: " + state);
        appState = state;
        lastAppStateChange = System.currentTimeMillis();
        isTouching = false;
        TypedProperties camConfig = (doUseSMS ? smsConfig : config);
        appState.enter(this, camConfig);
    }

    public void setCamPanSmooth(float s) {
        camera.panSmooth = s;
    }

    public void setCamRotSmooth(float s) {
        camera.rotSmooth = s;
    }

    public void setCamRotSpeedX(float s) {
        camera.targetRotSpeed.x = s;
    }

    public void setCamRotSpeedY(float s) {
        camera.targetRotSpeed.y = s;
    }

    public void setCamTargetZoom(float z) {
        camera.targetZoom = z;
    }

    public void setCamZoomSmooth(float s) {
        camera.zoomSmooth = s;
    }

    public void setExportFormat(int id) {
        exporter.setFileFormat(FrameSequenceExporter.FORMATS[id]);
        System.out.println("new export format: " + exporter.getFileFormat());
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.osc.InteractionStateListener#setMessage(java.lang.String)
     */
    @Override
    public void setMessage(String message) {
        if (!message.equalsIgnoreCase(prevMessage)) {
            scheduleMessage(message);
            prevMessage = message;
            lastMessageTime = System.currentTimeMillis();
        }
    }

    public void setNumExportTiles(int num) {
        numExportTiles = num;
        tiler = new Tiler(pgl, numExportTiles);
        int totalWidth = num * width;
        int totalHeight = num * height;
        int px = (int) UnitTranslator.pixelsToMillis(totalWidth, 300);
        int py = (int) UnitTranslator.pixelsToMillis(totalHeight, 300);
        uiLabelNumTiles.setValue(totalWidth + " x " + totalHeight + " (" + px
                + " x " + py + " mm @ 300 dpi)");
    }

    public void setPoleHitCount(int count) {
        poles.setMaxExternalPoleHitcount(count);
    }

    public void setRibbonColorSaturation(float amount) {
        feedPool.adjustFeedColors(amount);
    }

    public void setRibbonHitCount(int count) {
        poles.setMaxLetterHitcount(count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.osc.InteractionStateListener#setShake(float, float,
     * float, float)
     */
    @Override
    public void setShake(float x, float y, float z, float strength) {
        strength = MathUtils.clip(strength / shakeMaxEnergy, 0, 1f);
        if (strength > shakeEnergy) {
            shakeEnergy = strength;
            shakeDir.interpolateToSelf(new Vec3D(x, z, y).normalize(), 0.25f);
        } else {
            shakeDir.interpolateToSelf(Vec3D.Y_AXIS, 0.05f);
        }
        for (Ribbon r : oldRibbons) {
            r.applyShake(shakeDir, shakeEnergy);
        }
        for (Ribbon r : ribbons) {
            r.applyShake(shakeDir, shakeEnergy);
        }
    }

    public void setTargetPanX(float x) {
        camera.targetPos.x = x;
    }

    public void setTargetPanY(float y) {
        camera.targetPos.y = y;
    }

    public void setTargetPanZ(float z) {
        camera.targetPos.z = z;
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.osc.InteractionStateListener#setTilt(float, float, float)
     */
    @Override
    public void setTilt(float x, float y, float z) {
        appState.updateTilt(this, x, y, z);
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.osc.InteractionStateListener#setTouch(float, float)
     */
    @Override
    public void setTouch(float x, float y) {
        x = map(x, -100, 500, -1f, 1f);
        y = map(y, -100, 420, -1f, 1f);
        targetTouchPos.set(new Vec3D(x, 0, y)
                .scaleSelf(worldBounds.getExtent()));
        isTouching = true;
    }

    /**
     * Main setup/initialization method, registering & initializing all needed
     * resources & components.
     * 
     * @see processing.core.PApplet#setup()
     */
    @Override
    public void setup() {
        size(WIDTH, HEIGHT, OPENGL);
        frame.setTitle(APP_NAME + APP_VERSION);
        pgl = (PGraphicsOpenGL) g;
        gl = pgl.gl;
        font = loadFont(sketchPath("assets/fonts/odzroman-64.vlw"));
        textFont(font, 18);
        boolean isIdentity = config.getBoolean("app.mode.identity", false);
        initCamera();
        initDefaults();
        initMessages(isIdentity);
        initAlphabet();
        initPoleManager();
        initTextures();
        initPoles();
        exporter =
                new FrameSequenceExporter(sketchPath("export"), APP_NAME,
                        config.getProperty("defaults.export.format", "tga"));
        tiler = new Tiler(pgl, numExportTiles);
        doShowMask = config.getBoolean("app.mask.enabled", false);
        if (doShowMask) {
            maskImg = loadImage("assets/textures/mask_1280x128.png");
        }
        initSMS();
        initGUI();
        setAppState(isIdentity ? IDENT_STATE : IDLE_STATE);
        if (appState != IDENT_STATE) {
            initOSC();
        }
        triggerDefaultMessage();
    }

    /**
     * Sets the main animation update flag. If false, ribbons do not animate and
     * no new ribbons are added.
     * 
     * @param state
     */
    public void setUpdate(boolean state) {
        doUpdate = state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.osc.InteractionStateListener#setZoom(float)
     */
    @Override
    public void setZoom(float zoom) {
        camera.targetZoom = map(zoom, 0, 1, 0.5f, 5);
    }

    public void stop__() {
        logger.warning("app shutdown triggered...");
        messageScheduler.shutdown();
        while (messageScheduler.isAlive()) {
        }
        osc.shutdown();
        super.stop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.osc.InteractionStateListener#togglePlayButton()
     */
    @Override
    public void togglePlayButton() {
        doUpdate = !doUpdate;
    }

    /**
     * Triggers the display of the default message set in app.properties. This
     * message has more properties than normal user submitted messages in order
     * to gain more freedom for layouts when running the app in identity
     * generator mode/asset creation. See app.properties and wiki for more
     * information about the available message config settings.
     */
    public void triggerDefaultMessage() {
        ArrayList<MessageLine> lines = new ArrayList<MessageLine>();
        String logoText =
                config.getProperty("defaults.message.line0", "onedotzero");
        float subtitleScale =
                config.getFloat("defaults.message.subtitle.scale", 0.5f);
        float subtitleLeading = config.getFloat("message.leading", 160);
        int numSubtitles = config.getInt("defaults.message.lines.count", 1);
        boolean hasSubtitles = numSubtitles > 1;
        float totalHeight;
        if (hasSubtitles) {
            totalHeight =
                    subtitleLeading + numSubtitles * subtitleLeading
                            * subtitleScale;
        } else {
            totalHeight = alphabet.baseHeight;
        }
        Vec3D offset =
                new Vec3D(-alphabet.getWidthForString(logoText) / 2, 0,
                        -totalHeight / 2);
        lines.add(new MessageLine(logoText, offset.copy(), 1));
        if (hasSubtitles) {
            offset.addSelf(config.getInt("defaults.message.subtitle.offset.x",
                    0), 0, config.getInt("defaults.message.subtitle.offset.z",
                    160));
            for (int i = 1; i < numSubtitles; i++) {
                String line =
                        config.getProperty("defaults.message.line" + i, "");
                lines.add(new MessageLine(line, offset.copy(), subtitleScale));
                offset.z += subtitleLeading * subtitleScale;
            }
        }
        // in ident state don't expire message (practically)
        int ttl =
                appState == IDENT_STATE ? (int) 1e+9 : config.getInt(
                        "defaults.message.ttl", 10000);
        messageScheduler.setDefaultMessage(new UserMessage(lines, ttl, 0));
    }

    /**
     * Interpolates the touch focal point to the currently set target position
     * (updated from the N900) and applies distortion effect to all visible
     * ribbons.
     */
    public void updateRibbonTouch() {
        touchPos.interpolateToSelf(targetTouchPos, 0.05f);
        if (isTouching) {
            float radius =
                    config.getFloat("defaults.ixd.touch.displace.radius", 200);
            float radiusSq = radius * radius;
            for (Ribbon r : oldRibbons) {
                r.applyTouch(touchPos, radius, radiusSq);
            }
            for (Ribbon r : ribbons) {
                r.applyTouch(touchPos, radius, radiusSq);
            }
        }
    }
}
