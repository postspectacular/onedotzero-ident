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

package onedotzero.poles.strategies;

import onedotzero.poles.ParticlePole3D;
import toxi.geom.Vec3D;
import toxi.math.MathUtils;

/**
 * Positions {@link ParticlePole3D}s in a regular grid along the X axis. Y and Z
 * are based on a central exclusion vector. and are slightly randomized.
 */
public class GridXInterleavePolePositioning implements PolePositionStrategy {

    @Override
    public Vec3D createPosition(int id, int total, Vec3D exclusion) {
        float x = -1 + 2.0f * id / total;
        float y = MathUtils.random(exclusion.y, 1f);
        float z = MathUtils.random(exclusion.z, 1f);
        z = MathUtils.random(1f) < 0.5 ? z : -z;
        y = MathUtils.random(1f) < 0.5 ? y : -y;
        return new Vec3D(x, y, z);
    }

}
