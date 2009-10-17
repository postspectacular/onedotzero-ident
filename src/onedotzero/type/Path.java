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

package onedotzero.type;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import onedotzero.Ribbon;
import toxi.geom.Vec2D;

/**
 * Wrapper for a collection/sequence of 2D points forming part of a
 * {@link Letter}. For each point of the path {@link FlowHints} are defined to
 * indicate which way {@link Ribbon}s can flow through the letter/path.
 * 
 * This class is initialized automatically by JAXB.
 */
public class Path {

	@XmlElement(name = "vec2d")
	public ArrayList<Vec2D> points = new ArrayList<Vec2D>();

	@XmlAttribute(name = "seq")
	@XmlJavaTypeAdapter(SequenceAdapter.class)
	public int[] sequenceIndex;

	@XmlElement(name = "flow")
	public FlowHints flowHints;

	@Override
	public String toString() {
		String s = "";
		for (int i : sequenceIndex) {
			Vec2D p = points.get(i);
			s += p.toString() + " ";
		}
		return s;
	}
}
