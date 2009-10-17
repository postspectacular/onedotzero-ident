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

import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB helper class to translate between the XML and internal representations
 * of {@link Path}s.
 */
public class SequenceAdapter extends XmlAdapter<String, int[]> {

	@Override
	public String marshal(int[] sequence) throws Exception {
		String s = "";
		for (int i = 0; i < sequence.length; i++) {
			s += i + (i < sequence.length - 1 ? "," : "");
		}
		return s;
	}

	@Override
	public int[] unmarshal(String sequenceDef) throws Exception {
		StringTokenizer st = new StringTokenizer(sequenceDef, ",");
		int[] sequence = new int[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			sequence[i++] = Integer.parseInt(st.nextToken());
		}
		return sequence;
	}

}
