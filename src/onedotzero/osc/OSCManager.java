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

package onedotzero.osc;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import onedotzero.ODZApp;
import onedotzero.states.AppState;
import oscP5.OscMessage;
import oscP5.OscP5;

/**
 * The OSC manager provides a facade handling all communication with the N900.
 * The manager automatically parses incoming OSC messages & broadcasts
 * corresponding events to attached listeners as specified by the
 * {@link InteractionStateListener} interface.
 */
public class OSCManager {

    private static final Logger logger =
            Logger.getLogger(OSCManager.class.getName());

    OscP5 osc;

    ArrayList<InteractionStateListener> listeners =
            new ArrayList<InteractionStateListener>();

    public OSCManager(String ip, int port) {
        osc = new OscP5(this, ip, port);
        logger.info("starting OSCManager: " + ip + ":" + port);
    }

    public void addListener(InteractionStateListener l) {
        logger.info("adding OSC listener: " + l);
        listeners.add(l);
    }

    private void forwardMessage(OscMessage msg) {
        logger.info(msg.addrPattern() + " " + msg.typetag());
        for (InteractionStateListener l : listeners) {
            l.oscMessageReceived(msg);
        }
    }

    void oscEvent(OscMessage m) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("received from: " + m.netAddress());
            logger.fine("addrpattern: " + m.addrPattern() + " type: "
                    + m.typetag());
        }
        if (m.checkAddrPattern("/menufinished")
                || m.checkAddrPattern("/menuscreen")) {
            setAppState(ODZApp.MENU_STATE);
        } else if (m.checkAddrPattern("/menu")) {
            int mode = m.get(0).intValue();
            if (mode == 0) {
                setAppState(ODZApp.TILT_STATE);
            } else if (mode == 1) {
                setAppState(ODZApp.SHAKE_STATE);
            } else if (mode == 2) {
                setAppState(ODZApp.TOUCH_STATE);
            }
        } else if (m.checkAddrPattern("/acc")) {
            if (m.checkTypetag("fff")) {
                setTilt(m.get(0).floatValue(), m.get(1).floatValue(), m.get(2)
                        .floatValue());
            }
        } else if (m.checkAddrPattern("/rumble")) {
            if (m.checkTypetag("ffff")) {
                setShake(m.get(0).floatValue(), m.get(1).floatValue(), m.get(2)
                        .floatValue(), m.get(3).floatValue());
            }
        } else if (m.checkAddrPattern("/touch")) {
            if (m.checkTypetag("ff")) {
                setTouch(m.get(0).floatValue(), m.get(1).floatValue());
            }
        } else if (m.checkAddrPattern("/newmsg")) {
            if (m.checkTypetag("s")) {
                setMessage(m.get(0).stringValue());
            }
        } else if (m.checkAddrPattern("/closeapp")) {
            setAppState(ODZApp.IDLE_STATE);
        } else if (m.checkAddrPattern("/entermsg")) {
            setAppState(ODZApp.IDLE_STATE);
        } else if (m.checkAddrPattern("/zoom")) {
            setZoom(m.get(1).floatValue());
        } else if (m.checkAddrPattern("/playstatecmd")) {
            togglePlayButton();
        } else {
            forwardMessage(m);
        }
    }

    public void removeListener(InteractionStateListener l) {
        logger.info("removing OSC listener: " + l);
        listeners.remove(l);
    }

    private void setAppState(AppState state) {
        for (InteractionStateListener l : listeners) {
            l.setAppState(state);
        }
    }

    private void setMessage(String msg) {
        for (InteractionStateListener l : listeners) {
            l.setMessage(msg);
        }
    }

    private void setShake(float x, float y, float z, float w) {
        for (InteractionStateListener l : listeners) {
            l.setShake(x, y, z, w);
        }
    }

    private void setTilt(float x, float y, float z) {
        for (InteractionStateListener l : listeners) {
            l.setTilt(x, y, z);
        }
    }

    private void setTouch(float x, float y) {
        for (InteractionStateListener l : listeners) {
            l.setTouch(x, y);
        }
    }

    private void setZoom(float zoom) {
        for (InteractionStateListener l : listeners) {
            l.setZoom(zoom);
        }
    }

    public void shutdown() {
        osc.stop();
    }

    private void togglePlayButton() {
        for (InteractionStateListener l : listeners) {
            l.togglePlayButton();
        }
    }
}
