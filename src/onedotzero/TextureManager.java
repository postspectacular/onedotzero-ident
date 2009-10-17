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

import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;

/**
 * The texture manager produces {@link Texture} instances from given strings,
 * using a P3D offscreen renderer. All textures are generated lazily on demand
 * and kept in {@link HashMap} to avoid the creation of duplicates.
 */
public class TextureManager {

    public static final String ELLIPSE = "...";
    public static final int INDENT = 128;
    public static final int BASELINE = 10;

    public int texWidth = 4096;
    public int texHeight = 64;

    protected HashMap<String, Texture> textures =
            new HashMap<String, Texture>();

    protected ODZApp app;
    protected PFont font;
    protected GL gl;
    protected static GLU glu = new GLU();

    public TextureManager(ODZApp app, GL gl, PFont font, int texWidth) {
        this.app = app;
        this.gl = gl;
        this.font = font;
        this.texWidth = texWidth;
    }

    private Texture createTexture(String txt) {
        PGraphics g = app.createGraphics(texWidth, texHeight, PConstants.P3D);
        g.beginDraw();
        g.background(0);
        g.fill(255);
        g.textFont(font);
        boolean isTruncated = false;
        if (g.textWidth(txt) > texWidth - INDENT * 2) {
            while (g.textWidth(txt + ELLIPSE) > texWidth - INDENT * 2) {
                txt = txt.substring(0, txt.length() - 1);
                isTruncated = true;
            }
        }
        if (isTruncated) {
            txt += ELLIPSE;
        }
        g.text(txt, INDENT, texHeight - BASELINE);
        g.endDraw();
        g.loadPixels();
        Texture tex = new Texture(gl, glu, g, true);
        return tex;
    }

    public Texture getTextureFor(String txt) {
        Texture tex = textures.get(txt);
        if (tex == null) {
            tex = createTexture(txt);
            textures.put(txt, tex);
        }
        return tex;
    }
}
