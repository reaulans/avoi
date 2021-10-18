/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi.local.Couleur;

import java.awt.image.RGBImageFilter;

/**
 *
 * @author faneva
 */
public class Luminosite extends RGBImageFilter {

    int luminosoteValue ;
    public Luminosite(int lumine){
     this.luminosoteValue = lumine;
    }
    @Override
    public int filterRGB(int x, int y, int rgb) {
        int r = (rgb>>16) & 0xff;
        int g = (rgb>>8) & 0xff;
        int b = (rgb>>1) & 0xff;
        r+= (luminosoteValue * r) / 100;
        g+= (luminosoteValue * g) / 100;
        b+= (luminosoteValue * b) / 100;
        r+= Math.min(Math.max(0,r), 255);
        g+= Math.min(Math.max(0,g), 255);
        b+= Math.min(Math.max(0,b), 255);
        return (rgb & 0xff000000)  | (r<<16) | (g<<8)| (b<<0);
    }
    
}
