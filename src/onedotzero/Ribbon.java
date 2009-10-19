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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL;

import onedotzero.data.FeedPool.FeedConfiguration;
import onedotzero.poles.ParticlePole3D;
import onedotzero.poles.PoleManager;
import onedotzero.type.LetterPoleGroup;
import processing.core.PGraphics;
import toxi.color.TColor;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.math.MathUtils;
import toxi.math.waves.SineWave;

/**
 * Implements behaviour, creates path, geometry and texture animation for a
 * single text ribbon.
 */
public class Ribbon {

    protected static float RIBBON_STEP_LENGTH = 10;
    protected static float TEXTURE_WIDTH = 4096;
    protected static float RIBBON_WIDTH;
    protected static float LETTER_WIDTH;
    protected static float LETTER_UV_SCALE;
    protected static float SCALED_WIDTH;
    protected static float SCALED_LETTER_WIDTH;

    public static void configureWidth(float ribbonWidth, float letterScale) {
        RIBBON_WIDTH = ribbonWidth;
        LETTER_WIDTH = letterScale;
        LETTER_UV_SCALE = 8 * RIBBON_STEP_LENGTH / RIBBON_WIDTH;
        SCALED_WIDTH = LETTER_UV_SCALE / TEXTURE_WIDTH;
        SCALED_LETTER_WIDTH = SCALED_WIDTH / LETTER_WIDTH;
    }

    protected HashMap<ParticlePole3D, ParticlePole3D> poles =
            new HashMap<ParticlePole3D, ParticlePole3D>();

    protected ArrayList<Vec3D> vertices = new ArrayList<Vec3D>();
    protected float[] distances;

    protected final PoleManager poleManager;

    protected double currU = -1;
    protected double uSpeed;
    protected int letterStartID, letterEndID;

    protected Texture tex;

    protected int totalLength;
    protected FeedConfiguration feed;

    protected int startFrame, delay;
    protected SineWave displaceMod;
    protected Vec3D shakeDir = new Vec3D();
    protected Vec3D displaceOffset = new Vec3D();
    protected Vec3D tmp = new Vec3D();
    protected double uTargetSpeed;

    protected Vec3D origPos;

    public Ribbon(PoleManager poles, Texture tex, FeedConfiguration feed,
            float maxScroll, int maxDelay) {
        this.poleManager = poles;
        this.tex = tex;
        this.feed = feed;
        uSpeed = MathUtils.random(0.25f, 1f) * maxScroll;
        uTargetSpeed = uSpeed;
        delay = MathUtils.random(maxDelay);
        displaceMod = new SineWave(MathUtils.random(MathUtils.TWO_PI), 0);
    }

    public void applyShake(Vec3D shakeDir, float energy) {
        float amp = energy * 500;
        if (amp > displaceMod.amp) {
            displaceMod.amp += (amp - displaceMod.amp) * 0.15f;
            displaceMod.frequency +=
                    (energy * MathUtils.PI * 0.03f - displaceMod.frequency) * 0.15f;
        }
        this.shakeDir.set(shakeDir);
    }

    public void applyTouch(Vec3D touchPos, float radius, float radiusSquared) {
        float dist = origPos.distanceToSquared(touchPos);
        if (dist < radiusSquared) {
            shakeDir
                    .interpolateToSelf(origPos.sub(touchPos).normalize(), 0.15f);
            displaceMod.amp += (radius - displaceMod.amp) * 0.15f;
            displaceMod.phase = MathUtils.HALF_PI;
            displaceMod.frequency = 0;
        }
    }

    public void cleanup() {
        for (ParticlePole3D p : poles.values()) {
            if (p.hitCount > 0) {
                p.hitCount--;
            }
        }
        vertices = null;
        poles = null;
    }

    public boolean create(List<ParticlePole3D> poleSet, Vec3D dir,
            int startFrame, int loopCount) {
        this.startFrame = startFrame;
        List<ParticlePole3D> availablePoles =
                PoleManager.getFiltered(poleSet, poleManager
                        .getMaxLetterHitCount());
        ParticlePole3D currPole = PoleManager.getOldestPole(availablePoles);
        ParticlePole3D currPoleAlt =
                PoleManager.getLeastUsedPole(availablePoles);
        if (currPole != null || currPoleAlt != null) {
            if (currPole != null && currPoleAlt != null) {
                if (currPoleAlt.hitCount < currPole.hitCount) {
                    currPole = currPoleAlt;
                }
            } else {
                if (currPoleAlt != null) {
                    currPole = currPoleAlt;
                }
            }
            poles.put(currPole, currPole);
            ParticlePole3D startPole = currPole;
            origPos = startPole;
            List<ParticlePole3D> c1filtered =
                    PoleManager.getFiltered(poleManager.c1poles, poleManager
                            .getMaxExternalPoleHitcount());
            ParticlePole3D extPole =
                    currPole.computeFieldLine(c1filtered, dir,
                            RIBBON_STEP_LENGTH, poleManager.bounds, poleManager
                                    .getMaxExternalPoleHitcount());
            if (extPole != null) {
                extPole.updateHitCount();
                poles.put(extPole, extPole);
                int numV = currPole.vertices.size();
                for (int i = numV - 1; i >= 0; i--) {
                    vertices.add(currPole.vertices.get(i));
                }
                if (numV > 1) {
                    dir = vertices.get(numV - 1).sub(vertices.get(numV - 2));
                } else {
                    dir = new Vec3D(0, -1, 0);
                }
                LetterPoleGroup lpg = poleManager.getLetterForPole(startPole);
                if (lpg != null) {
                    letterStartID = vertices.size();
                    ParticlePole3D nextPole = currPole;
                    boolean isLoopComplete = false;
                    for (int i = 0; !isLoopComplete; i++) {
                        currPole.updateHitCount();
                        ParticlePole3D[] options =
                                lpg.getFlowOptionsForPole(currPole);
                        float minTheta = MathUtils.TWO_PI;
                        Vec2D curr2D = currPole.pos2D;
                        for (ParticlePole3D p : options) {
                            if (poles.get(p) == null) {
                                float theta =
                                        MathUtils.abs(curr2D
                                                .angleBetween(p.pos2D));
                                if (theta < minTheta) {
                                    minTheta = theta;
                                    nextPole = p;
                                }
                            } else if (i > 1 && p == startPole) {
                                isLoopComplete = true;
                                nextPole = p;
                                break;
                            }
                        }
                        if (poles.get(nextPole) == null || isLoopComplete) {
                            Vec3D.splitIntoSegments(currPole, nextPole,
                                    RIBBON_STEP_LENGTH, vertices, false);
                            dir.set(nextPole.sub(currPole));
                            currPole = nextPole;
                            poles.put(currPole, currPole);
                        } else {
                            break;
                        }
                    }
                    letterEndID = vertices.size();
                    for (int k = 0; k < loopCount; k++) {
                        for (int i = letterStartID; i < letterEndID; i++) {
                            vertices.add(vertices.get(i));
                        }
                    }
                    letterEndID = vertices.size();
                    dir.normalize().y = -1;
                    dir.normalize();
                    ParticlePole3D endPole =
                            currPole.computeFieldLine(c1filtered, dir,
                                    RIBBON_STEP_LENGTH, poleManager.bounds,
                                    poleManager.getMaxExternalPoleHitcount());
                    if (endPole != null) {
                        endPole.updateHitCount();
                        vertices.addAll(currPole.vertices);
                        poles.put(endPole, endPole);
                    }
                }
                Collections.reverse(vertices);
                numV = vertices.size();
                letterStartID = numV - letterStartID;
                letterEndID = numV - letterEndID;
                distances = new float[numV];
                totalLength = 0;
                for (int i = 1; i < numV; i++) {
                    float len = vertices.get(i).distanceTo(vertices.get(i - 1));
                    distances[i] = len;
                    totalLength += len;
                }
                return true;
            }
        }
        return false;
    }

    public void draw(PGraphics g, GL gl) {
        if (feed.isEnabled) {
            double u = currU - totalLength * SCALED_LETTER_WIDTH * 2;
            if (u < 1) {
                tex.bind();
                gl.glBegin(GL.GL_TRIANGLE_STRIP);
                float w2 = RIBBON_WIDTH * 0.5f;
                Vec3D prev = null;
                int i = 0;
                TColor c = feed.color.copy();
                c.alpha = 0;
                float cr = c.red();
                float cg = c.green();
                float cb = c.blue();
                int numV = vertices.size();
                for (Vec3D v : vertices) {
                    boolean isInLetter =
                            (i >= letterStartID && i < letterEndID);
                    if (prev != null) {
                        u +=
                                distances[i]
                                        * (isInLetter
                                                ? SCALED_LETTER_WIDTH
                                                : SCALED_WIDTH);
                    }
                    if (u >= 0 && u <= 1.0) {
                        float uu = (float) u;
                        gl.glColor4f(cr, cg, cb, c.alpha);
                        gl.glTexCoord2f(uu, 0.015625f);
                        tmp.set(v.x + displaceOffset.x, v.y + displaceOffset.y,
                                v.z + displaceOffset.z);
                        gl.glVertex3f(tmp.x, tmp.y + w2, tmp.z);
                        gl.glTexCoord2f(uu, 0.984375f);
                        gl.glVertex3f(tmp.x, tmp.y
                                - (isInLetter ? w2 * LETTER_WIDTH : w2), tmp.z);
                    }
                    prev = v;
                    i++;
                    if (i < numV - 10) {
                        if (c.alpha < 1) {
                            c.alpha += 0.1f;
                        }
                    } else {
                        if (c.alpha > 0) {
                            c.alpha -= 0.1;
                        }
                    }
                }
                gl.glEnd();
            }
        }
    }

    public void initShake() {
        displaceMod.phase = MathUtils.random(MathUtils.TWO_PI);
    }

    public void retire() {
        uTargetSpeed *= 5;
        startFrame = 0;
        delay = 0;
    }

    public boolean update(int currFrame, boolean doUpdate, float decay) {
        displaceMod.amp *= decay;
        displaceOffset.set(shakeDir).scaleSelf(displaceMod.update());
        if (doUpdate && currFrame > startFrame + delay) {
            uSpeed += (uTargetSpeed - uSpeed) * 0.02f;
            currU += uSpeed;
            double u = currU - totalLength * SCALED_LETTER_WIDTH * 2;
            return (u < 1);
        }
        return true;
    }
}