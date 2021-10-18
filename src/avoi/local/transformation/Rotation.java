/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi.local.transformation;

import avoi.Matrice;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author antsapm
 */
public class Rotation {
    private double angle;
    public double[][] image;
    public Rotation(double angle,double[][] image)
    {
        this.angle = angle;
        this.image = image;
    }
    public Rotation(){}
    public double[][] getRotationR(double deg,double[][] image){
         
         double[][] rotateM = {{Math.cos(deg),Math.sin(deg)},{-Math.sin(deg), Math.cos(deg)}};
         double[][] result;
         result = Matrice.produitMatrice(image, rotateM);
         return result;
    }
    
    public Image getRotation(Image image, int degree){
       
            
            BufferedImage image1 = (BufferedImage)image;
            
            AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(degree),image1.getWidth() / 2,image1.getHeight() / 2);
            AffineTransformOp op = new AffineTransformOp(transform,AffineTransformOp.TYPE_BILINEAR);
            
            BufferedImage filteredImage = new BufferedImage(image1.getWidth(),image1.getHeight(),image1.getType());
	    op.filter(image1, filteredImage);
	    image1 = filteredImage;
            return (Image)image1;
        
   }
}
