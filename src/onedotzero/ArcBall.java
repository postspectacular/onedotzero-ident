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
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.math.MathUtils;

/**
 * Arcball/trackball navigation controller to naturally rotate 3D object or
 * camera orientation using 2D mouse gestures. Based on code by Simon Greenwold
 * & Tom Carden, simplified and adapted to use toxiclibs classes by Karsten
 * Schmidt
 * 
 * @author Simon Greenwold
 * @author Tom Carden
 * @author Karsten Schmidt
 */
class ArcBall {

    protected PApplet app;
    protected Vec2D center;
    protected Vec3D downPos, dragPos;
    protected Quaternion currOrientation, downOrientation, dragOrientation;
    protected Vec3D[] axisSet;
    protected float radius;
    protected int constrainedAxisID;

    public ArcBall(PApplet app) {
        this(app, app.width / 2.0f, app.height / 2.0f, MathUtils.min(
                app.width / 2.0f, app.height / 2.0f));
    }

    public ArcBall(PApplet app, float cx, float cy, float radius) {
        this.app = app;

        this.center = new Vec2D(cx, cy);
        this.radius = radius;

        downPos = new Vec3D();
        dragPos = new Vec3D();

        currOrientation = new Quaternion();
        downOrientation = new Quaternion();
        dragOrientation = new Quaternion();

        axisSet = new Vec3D[] { Vec3D.X_AXIS, Vec3D.Y_AXIS, Vec3D.Z_AXIS };
        constrainedAxisID = -1;
    }

    public void apply() {
        currOrientation = dragOrientation.multiply(downOrientation);
        applyQuatToRotation(currOrientation);
    }

    public void applyQuatToRotation(Quaternion q) {
        float[] aa = q.toAxisAngle();
        app.rotate(aa[0], aa[1], aa[2], aa[3]);
    }

    public Vec3D constrainVector(Vec3D v, Vec3D axis) {
        Vec3D res = v.sub(axis.scale(axis.dot(v)));
        return res.normalize();
    }

    /**
     * @return the constrainedAxisID
     */
    public int getConstrainedAxisID() {
        return constrainedAxisID;
    }

    public Vec3D mapPointOnSphere(Vec2D pos) {
        Vec2D p = pos.sub(center).scaleSelf(1 / radius);
        Vec3D v = p.to3DXY();
        float mag = p.magSquared();
        if (mag > 1.0f) {
            v.normalize();
        } else {
            v.z = (float) Math.sqrt(1.0f - mag);
        }
        return (constrainedAxisID == -1) ? v : constrainVector(v,
                axisSet[constrainedAxisID]);
    }

    public void mouseDragged() {
        dragPos = mapPointOnSphere(new Vec2D(app.mouseX, app.mouseY));
        dragOrientation.set(downPos.dot(dragPos), downPos.cross(dragPos));
    }

    public void mousePressed() {
        downPos = mapPointOnSphere(new Vec2D(app.mouseX, app.mouseY));
        downOrientation.set(currOrientation);
        dragOrientation.identity();
    }

    /**
     * @param constrainedAxisID
     *            the constrainedAxisID to set
     */
    public void setConstrainedAxisID(int constrainedAxisID) {
        if (constrainedAxisID >= 0 && constrainedAxisID < axisSet.length) {
            this.constrainedAxisID = constrainedAxisID;
        }
    }
}
