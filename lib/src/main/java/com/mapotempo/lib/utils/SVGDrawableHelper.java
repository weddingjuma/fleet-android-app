package com.mapotempo.lib.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.caverock.androidsvg.SVG;

public class SVGDrawableHelper {
    private static final int BITMAP_WIDTH = 64;

    private static final int BITMAP_HEIGHT = 64;

    private static final String viewBox = "0 0 128 128";

    @Nullable
    public static Drawable getDrawableFromSVGPath(String svg_path, String color, Drawable defaultDrawable) {
        if (color == null)
            color = "#FFFFFF";

        try {
            String test = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "<!-- Created with Inkscape (http://www.inkscape.org/) -->\n" +
                    "\n" +
                    "<svg\n" +
                    "   viewBox=\"" + viewBox + "\"\n" +
                    "   version=\"1.1\"\n>" +
                    "   <path d=\"" + svg_path + "\"\n " +
                    "       fill=\"" + color + "\" stroke-width=\"3\" />\n" +
                    "</svg>";

            SVG svg = SVG.getFromString(test);
            Bitmap newBM = Bitmap.createBitmap(BITMAP_WIDTH,
                    BITMAP_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBM);
            svg.renderToCanvas(canvas);

            return new BitmapDrawable(newBM);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultDrawable;
        }
    }
}
