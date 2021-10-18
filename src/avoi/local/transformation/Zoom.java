/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi.local.transformation;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author faneva
 */
public class Zoom {
    public void Zoom(){}
    
    public Image setZoom(Image image,double zoomlevel){
        BufferedImage buffered   = (BufferedImage) image;
        int nouveauHeight =(int)(buffered.getHeight()* zoomlevel);
        int nouveauWidth =(int)(buffered.getWidth() * zoomlevel);
        BufferedImage imageModifier = new BufferedImage(nouveauWidth, nouveauHeight, buffered.getType());
        Graphics2D g = imageModifier.createGraphics();
        g.drawImage(image, 0, 0, nouveauWidth, nouveauHeight,null);
        g.dispose();
        return (Image)imageModifier;
    }
}
