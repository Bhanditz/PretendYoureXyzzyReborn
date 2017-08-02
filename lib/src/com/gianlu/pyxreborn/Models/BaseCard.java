package com.gianlu.pyxreborn.Models;

public abstract class BaseCard {
    public final int id;
    public final String text;
    public final String watermark;

    public BaseCard(int id, String text, String watermark) {
        this.id = id;
        this.text = text;
        this.watermark = watermark;
    }
}
