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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares poles based on their {@link ParticlePole3D#lastHit} time stamp.
 */
public class RecentHitPoleComparator implements Comparator<ParticlePole3D>,
        Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(ParticlePole3D a, ParticlePole3D b) {
        if (a.lastHit < b.lastHit) {
            return -1;
        } else if (a.lastHit > b.lastHit) {
            return 1;
        } else {
            return 0;
        }
    }
}
