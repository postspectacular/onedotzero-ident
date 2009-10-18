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

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import processing.core.PImage;
import toxi.geom.Vec2D;
import toxi.math.MathUtils;

import com.sun.opengl.util.BufferUtil;

/**
 * Simple OpenGL texture buffer wrapper. Supports both RGB & RGBA textures.
 * Hardcoded behaviour for mipmaps & disabled texture repeat.
 */
public class Texture {

    protected static final Logger logger =
            Logger.getLogger(Texture.class.getName());

    protected GL gl;
    protected GLU glu;

    protected final int id;
    protected final boolean hasAlpha;

    protected final int width, height;
    protected final Vec2D maxUV;

    /**
     * Creates a new OpenGL texture object from the given PImage.
     * 
     * @param gl
     *            JOGL GL instance
     * @param glu
     *            JOGL GLU instance
     * @param img
     *            source texture bitmap
     * @param hasAlpha
     *            true, if the texture should have an alpha channel
     */
    public Texture(GL gl, GLU glu, PImage img, boolean hasAlpha) {
        this.gl = gl;
        this.glu = glu;
        this.hasAlpha = hasAlpha;
        id = generateTextureID(gl);
        width = MathUtils.ceilPowerOf2(img.width);
        height = MathUtils.ceilPowerOf2(img.height);
        maxUV =
                new Vec2D((float) img.width / width, 1 - (float) img.height
                        / height);
        gl.glBindTexture(GL.GL_TEXTURE_2D, id);
        ByteBuffer buffer = getTextureByteBuffer(img);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, hasAlpha ? GL.GL_RGBA : GL.GL_RGB,
                width, height, 0, hasAlpha ? GL.GL_RGBA : GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                GL.GL_LINEAR);
        glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, 4, width, height, hasAlpha
                ? GL.GL_RGBA
                : GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buffer);
    }

    /**
     * Binds/activates the texture and sets texture filters to mag=linear,
     * min=linear_mipmap_linear, turns of texture repeat
     */
    public void bind() {
        gl.glBindTexture(GL.GL_TEXTURE_2D, id);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                GL.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
                GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
                GL.GL_CLAMP_TO_EDGE);
    }

    /**
     * Deletes the texture object from OpenGL.
     */
    public void delete() {
        final int[] tmp = new int[] { id };
        gl.glDeleteTextures(1, tmp, 0);
        logger.info("removed tex: " + id);
    }

    /**
     * Generates a new OpenGL texture ID.
     * 
     * @param gl
     * @return texture id
     */
    protected int generateTextureID(GL gl) {
        final int[] tmp = new int[1];
        gl.glGenTextures(1, tmp, 0);
        return tmp[0];
    }

    /**
     * Returns the max. UV coordinate actually occupied by the image. This is
     * only interesting when the original image wasn't of powers-of-two
     * dimensions.
     * 
     * @return the maxUV
     */
    public Vec2D getMaxUV() {
        return maxUV.copy();
    }

    /**
     * Converts the given image into an RGB or RGBA ordered {@link ByteBuffer}.
     * 
     * @param img
     *            texture image
     * @return byte buffer
     */
    protected ByteBuffer getTextureByteBuffer(PImage img) {
        int bytesPerPixel = hasAlpha ? 4 : 3;
        ByteBuffer rgbaBuffer =
                BufferUtil.newByteBuffer(width * height * bytesPerPixel);
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0, idx = y * img.width; x < width; x++) {
                int c =
                        (y < img.height && x < img.width)
                                ? img.pixels[idx++]
                                : 0;
                rgbaBuffer.put((byte) (c >> 16 & 0xff));
                rgbaBuffer.put((byte) (c >> 8 & 0xff));
                rgbaBuffer.put((byte) (c & 0xff));
                if (hasAlpha) {
                    rgbaBuffer.put((byte) (c >>> 24 & 0xff));
                }
            }
        }
        rgbaBuffer.flip();
        return rgbaBuffer;
    }
}