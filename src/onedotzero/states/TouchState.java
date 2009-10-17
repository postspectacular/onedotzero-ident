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

import onedotzero.CameraConfig;
import onedotzero.ODZApp;
import toxi.geom.Vec3D;
import toxi.util.datatypes.TypedProperties;

/**
 * State specific behaviour when the N900 user has chosen touch mode.
 */
public class TouchState extends AppState {

    @Override
    public void enter(ODZApp app, TypedProperties camConfig) {
        app.getScheduler().enableProcessQueue(true);
        CameraConfig camera = app.getCamera();
        camera.enableModulation(true);
        camera.targetPos.clear();
    }

    @Override
    public void update(ODZApp app) {
        app.updateRibbonTouch();
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.states.AppState#updateTilt(float, float, float)
     */
    @Override
    public void updateTilt(ODZApp app, float x, float y, float z) {
        Vec3D dir = new Vec3D(x, y, z);
        app.getCamera().targetTiltOrient.set(0, dir.normalize());
    }
}
