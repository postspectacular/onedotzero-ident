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

import processing.core.PApplet;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;
import toxi.math.MathUtils;
import toxi.math.waves.AbstractWave;
import toxi.math.waves.SineWave;

/**
 * Camera configuration wrapper & updater. Handles the setting up &
 * manipulation/modulation and interpolation of parameters of the perspective
 * view. Tied to {@link ODZApp} and GUI.
 */
public class CameraConfig {

    public static final Quaternion CAM_ORIENTATION_IDLE =
            new Quaternion(0, -0.1242f, -0.3897f, -0.9126f).normalize();

    public final Vec3D pos = new Vec3D();
    public final Vec3D rotation = new Vec3D();
    public final Vec3D rotSpeed = new Vec3D();
    public final Vec3D targetRotSpeed = new Vec3D();
    public final Vec3D targetPos = new Vec3D();

    public float rotSmooth = 0.05f;
    public float zoomSmooth = 0.025f;
    public float panSmooth = 0.25f;
    public float targetZoom = 1f;
    public float zoom = 1;

    public float fov = MathUtils.DEG2RAD * 60;
    public float near = 1;
    public float far = 5000;

    public Quaternion tiltOrientation = new Quaternion();
    public Quaternion targetTiltOrient = new Quaternion();

    private boolean isCamModEnabled;
    private boolean isFlipped;

    private AbstractWave camModX, camModY;
    private AbstractWave zoomMod;
    public float maxModAmpX = 0.1f;
    public float maxModAmpY = 0.1f;

    public CameraConfig() {
        camModX = new SineWave(0, 0.01f, 0.1f, 0);
        camModY = new SineWave(0, 0.0137f, 0.1f, 0);
    }

    public void enableModulation(boolean state) {
        isCamModEnabled = state;
    }

    public void flipCamera(boolean state) {
        isFlipped = state;
    }

    public void perspective(PApplet app) {
        app.perspective(fov, (float) app.width / app.height, near, far);
    }

    public void setZoomMod(AbstractWave wave) {
        zoomMod = wave;
    }

    public void update(PApplet app) {
        tiltOrientation.interpolateToSelf(targetTiltOrient, 0.05f);
        if (Float.isInfinite(tiltOrientation.x)
                || Float.isNaN(tiltOrientation.x)) {
            tiltOrientation = targetTiltOrient.copy();
        }
        pos.interpolateToSelf(targetPos, panSmooth);
        rotSpeed.interpolateToSelf(targetRotSpeed, rotSmooth);
        rotation.addSelf(rotSpeed);
        if (zoomMod != null) {
            targetZoom = zoomMod.update();
        }
        zoom += (targetZoom - zoom) * zoomSmooth;
        float[] axis = tiltOrientation.toAxisAngle();
        app.rotate(axis[0], axis[1], axis[2], axis[3]);
        if (isFlipped) {
            app.rotateZ(MathUtils.PI);
        }
        if (isCamModEnabled) {
            camModX.amp += (maxModAmpX - camModX.amp) * 0.25;
            camModY.amp += (maxModAmpY - camModY.amp) * 0.25;
        } else {
            camModX.amp *= 0.9;
            camModY.amp *= 0.9;
        }
        app.rotateY(camModY.update());
        app.rotateX(camModX.update());
        app.scale(zoom);
        app.translate(pos.x, pos.y, pos.z);
    }
}
