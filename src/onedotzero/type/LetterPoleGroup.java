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

package onedotzero.type;

import java.util.ArrayList;
import java.util.List;

import onedotzero.Ribbon;
import onedotzero.poles.ParticlePole3D;
import toxi.geom.Vec3D;

/**
 * This class constitutes a full description of a {@link Letter} parsed into
 * {@link ParticlePole3D}s positioned in 3D space. The class is used during the
 * creation of {@link Ribbon}s to map poles to letters, look up the correct
 * vertex within the letter and identify the correct/possible next flow
 * information for the ribbon. Furthermore, each instance of this class keeps
 * track of its usage statistics (hit counts for each pole). These stats are
 * then used as metric to identify the position of new ribbons.
 */
public class LetterPoleGroup {

    public Letter letter;
    public Vec3D offset;

    public float innerUsage = 0;
    public float outerUsage = 0;
    public float totalUsage = 0;

    public List<ParticlePole3D> outer = new ArrayList<ParticlePole3D>();
    public List<ParticlePole3D> inner = new ArrayList<ParticlePole3D>();

    public LetterPoleGroup(Letter l, Vec3D off) {
        letter = l;
        offset = off.copy();
    }

    private float computeInlineUsage(int maxHitCount) {
        if (inner.size() > 0) {
            int totalHitCount = 0;
            for (ParticlePole3D p : inner) {
                totalHitCount += p.hitCount;
            }
            innerUsage = (float) totalHitCount / (inner.size() * maxHitCount);
        } else {
            innerUsage = 0;
        }
        return innerUsage;
    }

    private float computeOutlineUsage(int maxHitCount) {
        int totalHitCount = 0;
        for (ParticlePole3D p : outer) {
            totalHitCount += p.hitCount;
        }
        outerUsage = (float) totalHitCount / (outer.size() * maxHitCount);
        return outerUsage;
    }

    public float computeTotalUsage(int maxHitCount) {
        totalUsage = computeOutlineUsage(maxHitCount);
        if (hasInline()) {
            totalUsage += computeInlineUsage(maxHitCount);
            totalUsage *= 0.5f;
        }
        return totalUsage;
    }

    public ParticlePole3D[] getFlowOptionsForPole(ParticlePole3D p) {
        ParticlePole3D[] options = null;
        int idx = outer.indexOf(p);
        if (idx != -1) {
            int[] optionIDs = letter.outer.flowHints.routes.get(idx);
            options = new ParticlePole3D[optionIDs.length];
            for (int i = 0; i < optionIDs.length; i++) {
                options[i] = outer.get(optionIDs[i]);
            }
        } else {
            idx = inner.indexOf(p);
            if (idx != -1) {
                int[] optionIDs = letter.inner.flowHints.routes.get(idx);
                options = new ParticlePole3D[optionIDs.length];
                for (int i = 0; i < optionIDs.length; i++) {
                    options[i] = inner.get(optionIDs[i]);
                }
            }
        }
        return options;
    }

    public boolean hasInline() {
        return inner.size() > 0;
    }

    @Override
    public String toString() {
        return letter.id + " inner: " + innerUsage + " outer: " + outerUsage
                + " total: " + totalUsage;
    }
}
