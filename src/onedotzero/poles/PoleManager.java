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
import java.util.HashMap;
import java.util.List;

import onedotzero.poles.strategies.PolePositionStrategy;
import onedotzero.type.Alphabet;
import onedotzero.type.Letter;
import onedotzero.type.LetterPoleGroup;
import toxi.geom.AABB;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

/**
 * The pole manager acts as a facade for all management tasks of
 * {@link ParticlePole3D} instances and pole groups. It's responsible for
 * translating text messages into constellations of poles in 3D space, reverse
 * look ups to identify associated letters/characters for poles, apply set
 * operations etc.
 */
public class PoleManager {

    public static enum PoleClass {
        C1, C2, C3, UNKNOWN
    }

    public static final float MAX_CHARGE = 20;
    public static int MAX_HITCOUNT = 30;
    public static int C1_MAX_HITCOUNT = 60;

    /**
     * Returns a new filtered list of poles with a hitcount less than the given
     * limit.
     * 
     * @param poles
     *            pole list to filter
     * @param maxHitCount
     * @return filtered list
     */
    public static List<ParticlePole3D> getFiltered(List<ParticlePole3D> poles,
            int maxHitCount) {
        ArrayList<ParticlePole3D> filtered =
                new ArrayList<ParticlePole3D>(poles.size() / 2);
        for (ParticlePole3D p : poles) {
            if (p.hitCount < maxHitCount) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    /**
     * Returns the least used pole, i.e. the one which currently has the least
     * number of ribbons assigned to it.
     * 
     * @param poles
     *            list of poles
     * @return pole
     */
    public static ParticlePole3D getLeastUsedPole(List<ParticlePole3D> poles) {
        ParticlePole3D pole = null;
        int usage = Integer.MAX_VALUE;
        for (ParticlePole3D p : poles) {
            if (p.hitCount < usage) {
                pole = p;
                usage = p.hitCount;
            }
        }
        return pole;
    }

    /**
     * Returns the pole nearest to the given point.
     * 
     * @param src
     *            reference point
     * @param poles
     *            list of poles
     * @return pole, or null, if list was empty.
     */
    public static ParticlePole3D getNearestPole(Vec3D src,
            List<ParticlePole3D> poles) {
        ParticlePole3D pole = null;
        float minDist = Float.MAX_VALUE;
        for (ParticlePole3D p : poles) {
            float dist = src.distanceToSquared(p);
            if (dist < minDist) {
                pole = p;
                minDist = dist;
            }
        }
        return pole;
    }

    /**
     * Returns the oldest pole, based on the timestamp of the last ribbon
     * flowing through this pole.
     * 
     * @param poles
     *            list of poles
     * @return pole
     */
    public static ParticlePole3D getOldestPole(List<ParticlePole3D> poles) {
        ParticlePole3D oldest = null;
        long oldestTime = System.nanoTime();
        for (ParticlePole3D p : poles) {
            if (p.lastHit < oldestTime) {
                oldest = p;
                oldestTime = p.lastHit;
            }
        }
        return oldest;
    }

    /**
     * Class 1: External poles
     */
    public final List<ParticlePole3D> c1poles = new ArrayList<ParticlePole3D>();

    /**
     * Class 1+2: External + outline
     */
    public final List<ParticlePole3D> c12poles =
            new ArrayList<ParticlePole3D>();

    /**
     * Class 1+3: External + inline
     */
    public final List<ParticlePole3D> c13poles =
            new ArrayList<ParticlePole3D>();

    /**
     * Class 2: Outline poles
     */
    public final List<ParticlePole3D> c2poles = new ArrayList<ParticlePole3D>();

    /**
     * Class 3: Inline poles
     */
    public final List<ParticlePole3D> c3poles = new ArrayList<ParticlePole3D>();

    /**
     * World space bounding box
     */
    public final AABB bounds;

    protected final HashMap<ParticlePole3D, LetterPoleGroup> messagePoles =
            new HashMap<ParticlePole3D, LetterPoleGroup>();
    protected final Alphabet alphabet;

    protected final Vec3D centreExclusion;
    private PolePositionStrategy positionStrategy;

    public PoleManager(Alphabet alphabet, AABB bounds) {
        this.alphabet = alphabet;
        this.bounds = bounds;
        centreExclusion = new Vec3D();
    }

    /**
     * Adds all poles of the provided list to the existing set of Class 1 poles.
     * 
     * @param poles
     *            list of poles
     */
    public void addExternalPoles(List<Vec3D> poles) {
        if (poles != null) {
            for (Vec3D p : poles) {
                c1poles.add(new ParticlePole3D(p, -MAX_CHARGE * p.magnitude()
                        / 200));
            }
        }
    }

    /**
     * Parses the given message and creates corresponding poles at requested
     * position and scale. If the message contains characters for which there's
     * no XML definition, then they'll simple be skipped.
     * 
     * @param message
     * @param offset
     *            3D position
     * @param scale
     *            scale factor
     */
    public void addMessageAt(String message, Vec3D offset, float scale) {
        String lowercase = message.toLowerCase();
        int len = message.length();
        Letter prevLetter = null;
        for (int i = 0; i < len; i++) {
            Letter letter = alphabet.getForName(message.charAt(i));
            if (letter == null) {
                letter = alphabet.getForName(lowercase.charAt(i));
            }
            if (letter != null) {
                offset.x += letter.getKerning(prevLetter);
                initLetterPoles(letter, offset, scale);
                offset.x += letter.getWidth() * scale;
                prevLetter = letter;
            }
        }
    }

    /**
     * Classifies the given pole by identifying which set of poles it belongs
     * to:
     * <ul>
     * <li>C1: external pole</li>
     * <li>C2: letter outline pole</li>
     * <li>C3: letter inline pole</li>
     * <li>UNKNOWN: not part of any current set</li>
     * </ul>
     * 
     * @param currPole
     * @return
     */
    public PoleClass classifyPole(ParticlePole3D currPole) {
        PoleClass type = PoleClass.UNKNOWN;
        if (currPole != null) {
            if (c2poles.indexOf(currPole) != -1) {
                type = PoleClass.C2;
            } else if (c3poles.indexOf(currPole) != -1) {
                type = PoleClass.C3;
            } else if (c1poles.indexOf(currPole) != -1) {
                type = PoleClass.C1;
            }
        }
        return type;
    }

    /**
     * Clears all pole sets.
     */
    private void clear() {
        c1poles.clear();
        c12poles.clear();
        c13poles.clear();
        c2poles.clear();
        c3poles.clear();
        messagePoles.clear();
    }

    /**
     * Resets the hitcount of all poles.
     */
    public void clearHitCounts() {
        for (ParticlePole3D p : c12poles) {
            p.hitCount = 0;
            p.lastHit = 0;
        }
        for (ParticlePole3D p : c3poles) {
            p.hitCount = 0;
            p.lastHit = 0;
        }
    }

    /**
     * Retrieves the {@link LetterPoleGroup} of the letter currently least used
     * by ribbons.
     * 
     * @return
     */
    public LetterPoleGroup getLeastUsedLetter() {
        LetterPoleGroup lpg = null;
        float minUsage = Float.MAX_VALUE;
        for (LetterPoleGroup g : messagePoles.values()) {
            float usage = g.computeTotalUsage(MAX_HITCOUNT);
            if (usage < minUsage) {
                lpg = g;
                if (usage == 0) {
                    break;
                }
                minUsage = usage;
            }
        }
        return lpg;
    }

    /**
     * Returns the associated {@link LetterPoleGroup} for the given pole.
     * 
     * @param pole
     * @return null, if pole is not part of any letter.
     */
    public LetterPoleGroup getLetterForPole(ParticlePole3D pole) {
        return messagePoles.get(pole);
    }

    /**
     * Clears all existing poles and creates the specified number of new
     * external poles.
     * 
     * @param numPoles
     */
    public void init(int numPoles) {
        clear();
        for (int i = 0; i < numPoles; i++) {
            Vec3D pos =
                    positionStrategy.createPosition(i, numPoles,
                            centreExclusion);
            pos.scaleSelf(bounds.getExtent());
            ParticlePole3D p =
                    new ParticlePole3D(pos, -MAX_CHARGE * pos.magnitude() / 200);
            c1poles.add(p);
        }
    }

    /**
     * Creates the required poles (inline & outline) for the given
     * {@link Letter} definition.
     * 
     * @param letter
     *            letter definition
     * @param offset
     *            3D position
     * @param scale
     *            scale factor
     */
    private void initLetterPoles(Letter letter, Vec3D offset, float scale) {
        LetterPoleGroup lpg = new LetterPoleGroup(letter, offset);
        for (Vec2D v : letter.outer.points) {
            ParticlePole3D p =
                    new ParticlePole3D(v.to3DXZ().scaleSelf(scale).addSelf(
                            offset), MAX_CHARGE);
            c2poles.add(p);
            lpg.outer.add(p);
            messagePoles.put(p, lpg);
        }
        for (Vec2D v : letter.inner.points) {
            ParticlePole3D p =
                    new ParticlePole3D(v.to3DXZ().scaleSelf(scale).addSelf(
                            offset), MAX_CHARGE);
            c3poles.add(p);
            lpg.inner.add(p);
            messagePoles.put(p, lpg);
        }
    }

    /**
     * Last initialization step: arrange the created poles into different sets
     * to help with later lookups.
     */
    public void processGroups() {
        c12poles.clear();
        c13poles.clear();
        c12poles.addAll(c1poles);
        c12poles.addAll(c2poles);
        c13poles.addAll(c1poles);
        c13poles.addAll(c3poles);
    }

    /**
     * Sets the centre exclusion zone for external poles. This is required so
     * that external poles are not positioned in the area used by letters and so
     * potentially create a visual conflict.
     * 
     * Only the Y and Z components of the exclusion vector are used. The vector
     * is considered to be normalized and will later be scaled to the size of
     * the world bounding box to compute absolute coordinates.
     * 
     * @param excl
     */
    public void setCentreExclusion(Vec3D excl) {
        centreExclusion.set(excl);
    }

    /**
     * @see #setCentreExclusion(Vec3D)
     * @param y
     */
    public void setCentreExclusionY(float y) {
        centreExclusion.y = y;
    }

    /**
     * @see #setCentreExclusion(Vec3D)
     * @param y
     */
    public void setCentreExclusionZ(float z) {
        centreExclusion.z = z;
    }

    /**
     * Sets the new positioning strategy used for C1 (external) poles.
     * 
     * @param positionStrategy
     *            the positionStrategy to set
     */
    public void setPositionStrategy(PolePositionStrategy positionStrategy) {
        this.positionStrategy = positionStrategy;
    }

    /**
     * Debug only. Displays current usage values for each
     * {@link LetterPoleGroup}.
     */
    public void showLetterUsage() {
        for (LetterPoleGroup g : messagePoles.values()) {
            g.computeTotalUsage(MAX_HITCOUNT);
            System.out.println(g);
        }
    }
}
