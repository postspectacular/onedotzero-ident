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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import toxi.geom.Vec2D;

/**
 * Container class for all paths and properties defining an individual
 * letter/character in the {@link Alphabet}. This class is initialized
 * automatically by JAXB.
 */
public class Letter {

    @XmlAttribute
    public String id;

    @XmlAttribute
    protected float width;

    @XmlAttribute
    protected float kern, kernTall;

    @XmlAttribute
    protected boolean isTall;

    @XmlElement(name = "outline")
    public Path outer = new Path();

    @XmlElement(name = "inline")
    public Path inner = new Path();

    boolean containsPoint(Vec2D p) {
        if (outer.points.indexOf(p) != -1) {
            return true;
        }
        return inner.points.indexOf(p) != -1;
    }

    public float getKerning(Letter prevLetter) {
        if (prevLetter != null && prevLetter.isTall) {
            return kernTall;
        } else {
            return kern;
        }
    }

    public float getWidth() {
        return width;
    }

    @Override
    public String toString() {
        String s = id + ": out: " + outer + " in: " + inner;
        return s;
    }
}
