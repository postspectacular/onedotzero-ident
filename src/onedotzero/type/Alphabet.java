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
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import onedotzero.ODZApp;

/**
 * Container class for the custom flow-based vector typeface used as guides for
 * the text ribbons to flow through and hence form visible characters. An
 * instance of this class is initialized by loading an XML document using JAXB.
 * 
 * The XML schema for this class is stored in the assets/xsd folder. The actual
 * XML alphabet is stored in the assets/alphabet folder.
 * 
 * @see ODZApp#initAlphabet()
 */
@XmlRootElement
public class Alphabet {

    @XmlAttribute(name = "baseHeight")
    public int baseHeight;

    @XmlElement(name = "letter")
    public List<Letter> letters = new ArrayList<Letter>();

    public HashMap<String, Letter> nameLookup = new HashMap<String, Letter>();

    public Letter getForName(char name) {
        return nameLookup.get("" + name);
    }

    public Letter getForName(String name) {
        return nameLookup.get(name);
    }

    public float getWidthForString(String txt) {
        float width = 0;
        int len = txt.length();
        Letter prevLetter = null;
        for (int i = 0; i < len; i++) {
            Letter letter = getForName(txt.charAt(i));
            if (letter != null) {
                width += letter.getWidth() + letter.getKerning(prevLetter);
                prevLetter = letter;
            }
        }
        return width;
    }

    public void init() {
        for (Letter l : letters) {
            nameLookup.put(l.id, l);
        }
    }
}
