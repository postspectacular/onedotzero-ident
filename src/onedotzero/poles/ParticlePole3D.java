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

package onedotzero.poles;

import java.util.ArrayList;
import java.util.List;

import toxi.geom.AABB;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

/**
 * This class implements the actual electro-magnetic pole simulation and
 * calculation of field lines between poles. Each pole has a positive or
 * negative charge influencing the path of field lines (which are used as guide
 * for the text ribbons).
 */
public class ParticlePole3D extends Vec3D {

    public static final int MAX_ITERATIONS = 200;
    public static final float RADIUS = 10;
    public static final float EVENT_HORIZON = (RADIUS * 0.9f) * (RADIUS * 0.9f);

    public final List<Vec3D> vertices = new ArrayList<Vec3D>();

    public final Vec2D pos2D;

    public final float charge;
    public int hitCount;
    public long lastHit;

    private float sign;

    /**
     * Constructs a new pole at the given position & charge
     * 
     * @param pos
     * @param charge
     */
    public ParticlePole3D(Vec3D pos, float charge) {
        super(pos);
        this.pos2D = to2DXZ().normalize();
        this.charge = charge;
        this.sign = Math.signum(charge);
    }

    /**
     * Computes the path of a field line from this pole to another one in the
     * given list of poles. The field line is initially emitted in the requested
     * direction, but will be manipulated by the configuration of the other
     * poles present. Of the poles supplied in the list only the ones with a hit
     * count less than the defined maximum will be considered and have an impact
     * on the field line. If the path traced is leaving the world bounds, the
     * search is terminated and the method returns null. In all other cases, the
     * method returns the target pole found and stores all intermediate vertices
     * of the field line in the public {@link #vertices} list.
     * 
     * @param poles
     *            list of poles in the EMF
     * @param dir
     *            initial field line direction
     * @param step
     *            step length (defines field line resolution)
     * @param bounds
     *            world space bounds
     * @param maxHitCount
     *            max. hit count limit for poles to be considered
     * @return target pole or null
     */
    public ParticlePole3D computeFieldLine(final List<ParticlePole3D> poles,
            final Vec3D dir, float step, AABB bounds, final int maxHitCount) {
        Vec3D pos = add(dir.scale(RADIUS));
        int iter = 0;
        boolean isTracing = true;
        vertices.clear();
        vertices.add(copy());
        step *= sign;
        ParticlePole3D target = null;
        while (isTracing && ++iter < MAX_ITERATIONS) {
            Vec3D dd = pos.sub(this);
            dir.set(dd.scaleSelf(charge / dd.magSquared()));
            for (ParticlePole3D p : poles) {
                if (p.hitCount < maxHitCount && p != this) {
                    Vec3D d = pos.sub(p);
                    float mag = d.magSquared();
                    if (mag < EVENT_HORIZON) {
                        isTracing = false;
                        target = p;
                        break;
                    }
                    dir.addSelf(d.scaleSelf(p.charge / mag));
                }
            }
            if (isTracing) {
                vertices.add(pos.copy());
                pos.addSelf(dir.normalizeTo(step));
                isTracing = pos.isInAABB(bounds);
            } else if (target != null && target != this) {
                vertices.add(target.copy());
            }
        }
        return target;
    }

    /**
     * Just overriding this for good style. Does the same as...
     * 
     * @see toxi.geom.Vec3D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object arg0) {
        return super.equals(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see toxi.geom.Vec3D#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() * 37 + (int) charge;
    }

    /**
     * Increases this pole's hit count and updates time stamp of last hit.
     */
    public void updateHitCount() {
        hitCount++;
        lastHit = System.nanoTime();
    }
}
