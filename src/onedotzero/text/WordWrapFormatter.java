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

package onedotzero.text;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import toxi.math.MathUtils;

/**
 * Implementation of the {@link MessageFormatter} interface, formats a user
 * submitted message by word-wrapping it based on a given line width.
 */
public class WordWrapFormatter implements MessageFormatter {

    private Pattern wrapPattern;
    private int maxLen;

    public WordWrapFormatter(int lineWidth) {
        this.maxLen = lineWidth;
        // pattern taken from a comment by Jeremy Stein on:
        // http://joust.kano.net/weblog/archives/000060.html
        wrapPattern =
                Pattern.compile("(\\S\\S{" + lineWidth + ",}|.{1," + lineWidth
                        + "})(\\s+|$)");
    }

    @Override
    public List<String> format(String message) {
        List<String> lines = new LinkedList<String>();
        Matcher m = wrapPattern.matcher(message);
        while (m.find()) {
            String l = m.group().trim();
            if (l.length() <= maxLen) {
                lines.add(l);
            } else {
                while (l != null) {
                    lines
                            .add(l.substring(0, MathUtils.min(maxLen, l
                                    .length())));
                    if (l.length() >= maxLen) {
                        l = l.substring(maxLen);
                    } else {
                        l = null;
                    }
                }
            }
        }
        return lines;
    }
}
