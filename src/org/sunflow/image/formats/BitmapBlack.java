package org.sunflow.image.formats;

import org.sunflow.image.Bitmap;
import org.sunflow.image.Color;

public class BitmapBlack extends Bitmap {
    public int getWidth() {
        return 1;
    }

    public int getHeight() {
        return 1;
    }

    public Color readColor(int x, int y) {
        return Color.BLACK;
    }

    public float readAlpha(int x, int y) {
        return 0;
    }
}