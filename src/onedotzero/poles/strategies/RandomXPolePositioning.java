/*
 * This file is part of onedotzero 2009 identity generator (ODZGen).
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
 * Positions {@link ParticlePole3D}s randomly along the X axis. Y and Z are
 * based on a central exclusion vector.
 */
public class RandomXPolePositioning implements PolePositionStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see onedotzero.poles.strategies.PolePositionStrategy#createPosition(int,
	 * int, toxi.geom.Vec3D)
	 */
	@Override
	public Vec3D createPosition(int id, int total, Vec3D exclusion) {
		float x = MathUtils.random(-1f, 1f) * 0.66f;
		float y = MathUtils.random(exclusion.y, 1f);
		float z = MathUtils.random(exclusion.z, 1f);
		y = MathUtils.random(1f) < 0.5 ? y : -y;
		z = (id % 2 == 0) ? z : -z;
		return new Vec3D(x, y, z);
	}

}
