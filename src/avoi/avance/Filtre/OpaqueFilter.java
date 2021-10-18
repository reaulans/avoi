/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi.avance.Filtre;

import java.awt.image.RGBImageFilter;

/**
 *
 * @author jaoharison
 */
public class OpaqueFilter extends RGBImageFilter {
    int opaqueValue;
    public OpaqueFilter(int opacity){
         opaqueValue = opacity;
         canFilterIndexColorModel = true;
    }

    @Override
    public int filterRGB(int x, int y, int rgb) {
        int opacity = (rgb>>24) & 0xff;
        opacity = (opacity * opaqueValue) / 255;
          return (rgb & 0xff000000)  | (opacity<<24);
    }
}
