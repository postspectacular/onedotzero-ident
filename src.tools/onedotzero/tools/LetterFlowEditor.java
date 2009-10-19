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

package onedotzero.tools;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import toxi.color.TColor;
import toxi.geom.Vec2D;

public class LetterFlowEditor extends PApplet {

    class Pole extends Vec2D {

        ArrayList<Pole> poles;
        int id;
        TColor col;

        int[] flow = new int[] { -1, -1 };

        Pole(ArrayList<Pole> poles, int id, int x, int y, TColor col) {
            super(x, y);
            this.poles = poles;
            this.id = id;
            this.col = col;
        }

        void draw() {
            TColor c = id == selectedID ? TColor.YELLOW : col;
            stroke(c.toARGB());
            beginShape(LINES);
            if (flow[0] != -1 && (id == selectedID || flow[0] > id)) {
                Pole a = poles.get(flow[0]);
                vertex(x, y);
                vertex(a.x, a.y);
                if (id == selectedID) {
                    drawArrow(a);
                }
            }
            if (flow[1] != -1 && (id == selectedID || flow[1] > id)) {
                Pole b = poles.get(flow[1]);
                vertex(x, y);
                vertex(b.x, b.y);
                if (id == selectedID) {
                    drawArrow(b);
                }
            }
            endShape();
            noStroke();
            fill(c.toARGB());
            ellipse(x, y, 5, 5);
            text("" + id, x, y - scale);
        }

        private void drawArrow(Pole p) {
            Vec2D dir = this.sub(p).normalize();
            vertex(p.x, p.y);
            Vec2D q = p.add(dir.copy().rotate(0.3f).scaleSelf(10));
            vertex(q.x, q.y);
            vertex(p.x, p.y);
            q = p.add(dir.rotate(-0.3f).scaleSelf(10));
            vertex(q.x, q.y);
        }
    }

    private static final long serialVersionUID = -1;

    private static final int SELECTED_COL = 0xffffff00;
    private static final int INLINE_COL = 0xff00ffff;
    private static final int OUTLINE_COL = 0xffff00ff;

    public static void main(String[] args) {
        PApplet.main(new String[] { "onedotzero.tools.LetterFlowEditor" });
    }

    ArrayList<Pole> outline = new ArrayList<Pole>();
    ArrayList<Pole> inline = new ArrayList<Pole>();
    ArrayList<Pole> currPath = outline;

    boolean isOutline = true;
    int scale = 3;
    int offset = 20;

    int selectedID = 0;

    private void chooseLetter() {
        FileDialog fd = new FileDialog(frame, "Choose a letter image");
        fd.setDirectory("assets/alphabet/png/");
        fd.setFilenameFilter(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.indexOf("png") != -1;
            }
        });
        fd.setVisible(true);
        String fileID = fd.getDirectory() + fd.getFile();
        if (fileID != null) {
            outline.clear();
            inline.clear();
            isOutline = true;
            currPath = outline;
            selectedID = 0;
            PImage img = loadImage(fileID);
            int countOut = 0;
            int countIn = 0;
            TColor colOut = TColor.newARGB(OUTLINE_COL);
            TColor colIn = TColor.newARGB(INLINE_COL);
            for (int y = 0, idx = 0; y < img.height; y++) {
                for (int x = 0; x < img.width; x++) {
                    int c = img.pixels[idx++] | 0xff000000;
                    if (c == OUTLINE_COL) {
                        outline.add(new Pole(outline, countOut, x * scale, y
                                * scale, colOut));
                        countOut++;
                    } else if (c == INLINE_COL) {
                        inline.add(new Pole(inline, countIn, x * scale, y
                                * scale, colIn));
                        countIn++;
                    }
                }
            }
        } else {
            System.exit(1);
        }
    }

    private void displayFlowInfo() {
        int y = 0;
        for (Pole p : currPath) {
            fill(p.id == selectedID ? SELECTED_COL : p.col.toARGB());
            text(p.id + ": " + p.flow[0] + "," + p.flow[1], 400, y);
            y += 14;
        }
    }

    private void displayUsage() {
        fill(255);
        int x = 400;
        int y = height - 120;
        for (int i = 0; i < 7; i++) {
            text(Messages.getString("LetterFlowEditor." + i), x, y);
            y += 14;
        }
    }

    @Override
    public void draw() {
        background(51);
        noStroke();
        translate(offset, offset);
        for (Pole p : currPath) {
            if (p.id != selectedID) {
                p.draw();
            }
        }
        currPath.get(selectedID).draw();
        displayFlowInfo();
        displayUsage();
    }

    private void exportFlowInfo() {
        StringBuilder flow = new StringBuilder(256);
        flow.append("<!-- " + (isOutline ? "out" : "in") + "line -->\n");
        for (Pole p : currPath) {
            flow.append("<node>").append(p.flow[0]).append(",").append(
                    p.flow[1]).append("</node>\n");
        }
        println(flow.toString());
    }

    @Override
    public void keyPressed() {
        if (keyCode == UP || keyCode == RIGHT) {
            selectedID = (selectedID + 1) % currPath.size();
        }
        if (keyCode == DOWN || keyCode == LEFT) {
            selectedID--;
            if (selectedID < 0) {
                selectedID += currPath.size();
            }
        }
        if (key == 's') {
            exportFlowInfo();
        }
        if (key == 'l') {
            chooseLetter();
        }
        if (key == ' ') {
            if (inline.size() > 0) {
                isOutline = !isOutline;
                currPath = isOutline ? outline : inline;
                selectedID = 0;
            }
        }
    }

    @Override
    public void mousePressed() {
        Pole clicked = null;
        for (Pole p : currPath) {
            float dx = abs(p.x - mouseX + offset);
            float dy = abs(p.y - mouseY + offset);
            if (dx < 20 && dy < 20) {
                clicked = p;
                break;
            }
        }
        if (clicked != null) {
            Pole selected = currPath.get(selectedID);
            int flowID = mouseButton == LEFT ? 0 : 1;
            selected.flow[flowID] = clicked.id;
            int partnerID = clicked.id > selectedID ? 1 : 0;
            if (clicked.flow[partnerID] != -1) {
                if (clicked.flow[(partnerID + 1) % 2] == -1) {
                    clicked.flow[(partnerID + 1) % 2] = selectedID;
                }
            } else {
                clicked.flow[partnerID] = selectedID;
            }
        }
    }

    @Override
    public void setup() {
        size(640, 480);
        smooth();
        textFont(createFont("SansSerif", 10));
        chooseLetter();
    }
}
