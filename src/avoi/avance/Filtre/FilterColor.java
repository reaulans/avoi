/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi.avance.Filtre;

import java.awt.image.RGBImageFilter;

/**
 *
 * @author auflida     
 */
public class FilterColor extends RGBImageFilter {
    boolean red, green, blue;
    public FilterColor(boolean r, boolean g, boolean b){
        this.red = r;
        this.green = g;
        this.blue = b;
        canFilterIndexColorModel = true;
    }

    @Override
    public int filterRGB(int x, int y, int rgb) {
        int r = red? 0:(rgb>>16) & 0xff;
        int g = green? 0:(rgb>>8) & 0xff;
        int b = green? 0:(rgb>>0) & 0xff;
        return (rgb & 0xff000000)  | (r<<16) | (g<<8)| (b<<0);
    }
}
