package com.gianlu.pyxreborn.Models;

public class BlackCard extends BaseCard {
    public final int numDraw;
    public final int numPick;

    public BlackCard(int id, String text, String watermark, int numDraw, int numPick) {
        super(id, text, watermark);
        this.numDraw = numDraw;
        this.numPick = numPick;
    }
}
