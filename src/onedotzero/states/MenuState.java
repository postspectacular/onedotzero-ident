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

package onedotzero.states;

import onedotzero.CameraState;
import onedotzero.ODZApp;
import toxi.util.datatypes.TypedProperties;

/**
 * State specific behaviour when the N900 app is showing the menu screen waiting
 * for the user to choose an interaction mode (tilt, shake or touch).
 */
public class MenuState extends AppState {

    @Override
    public void enter(ODZApp app, TypedProperties camConfig) {
        app.getScheduler().enableProcessQueue(true);
        CameraState camera = app.getCamera();
        camera.enableModulation(true);
        camera.targetPos.clear();
        camera.targetZoom =
                camConfig.getFloat("defaults.state.menu.zoom", 0.75f);
        camera.targetTiltOrient.set(CameraState.CAM_ORIENTATION_IDLE);
        app.setUpdate(true);
    }

    @Override
    public void pre(ODZApp app) {
        long now = System.currentTimeMillis();
        boolean enabled =
                now - app.getLastAppStateChange() > app.getConfig().getInt(
                        "message.menu.queue.delay", 10000)
                        || now - app.getLastMessageTime() < app.getConfig()
                                .getInt("message.new.lockperiod", 2000);
        app.getScheduler().enableProcessQueue(enabled);
    }
}
