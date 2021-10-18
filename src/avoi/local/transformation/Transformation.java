/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi.local.transformation;

import static avoi.ImageMatrice.getBlueMatrice;
import static avoi.ImageMatrice.getGreenMatrice;
import static avoi.ImageMatrice.getImageByRGB;
import static avoi.ImageMatrice.getRedMatrice;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author antsapm
 */
public class Transformation {
    
    public Transformation(){}
    
    public Image inversion(Image imagee){
       int[][] matriceRed = getRedMatrice(imagee);
       int[][] matriceBlue = getBlueMatrice(imagee);
       int[][] matriceGreen = getGreenMatrice(imagee);
       int[][] pixelRed = new int [matriceRed.length][matriceRed[0].length];
       int[][] pixelGreen = new int [matriceGreen.length][matriceGreen[0].length];
       int[][] pixelBlue = new int [matriceBlue.length][matriceBlue[0].length];
        for (int i = 0; i < matriceRed.length; i++) {
            for (int j = 0; j < matriceRed[i].length; j++) {
                pixelRed[i][j] = 255 - matriceRed[i][j];
                pixelGreen[i][j] = 255 - matriceGreen[i][j];
                pixelBlue[i][j] = 255 - matriceBlue[i][j];
            }
        }
        return getImageByRGB(imagee, pixelRed, pixelGreen, pixelBlue);
    }
    public Image resizeImage(Image originalImage, int width, int heigth){
           BufferedImage original = (BufferedImage)originalImage;
           BufferedImage resized = new BufferedImage(width, heigth, original.getType());
           Graphics2D g2 = resized.createGraphics();
           g2.drawImage(original, 0, 0, width, heigth, null);
           g2.dispose();
           return (Image)resized;
   }
    public Image homothetie(Image image, String var1, String var2){
        BufferedImage buffered = (BufferedImage)image;
        double variableUne = Double.parseDouble(var1);
        double variableDeux = Double.parseDouble(var2);
        int largeurImage = buffered.getWidth();
        int hauteurImage = buffered.getHeight();
        System.out.println("L "+String.valueOf(largeurImage));
        System.out.println("H "+String.valueOf(hauteurImage));
        int Hwidth = (int)Math.round(largeurImage * variableUne);
        int Hheigth = (int)Math.round(hauteurImage * variableDeux);
        return resizeImage(image, Hwidth, Hheigth);
        
    }
    
    public Image cisaillement(Image image, String var1, String var2){
       BufferedImage imageReturn = (BufferedImage)image;
       AffineTransform transformer = new AffineTransform();
       transformer.shear(Integer.parseInt(var1), Integer.parseInt(var2));
       AffineTransformOp op = new AffineTransformOp(transformer, AffineTransformOp.TYPE_BILINEAR);
       imageReturn = op.filter(imageReturn, null);
       return (Image) imageReturn;
    }
    
    public Image symetrieY(Image image){
        
         BufferedImage buffered = (BufferedImage) image;
        for (int i = 0; i < buffered.getHeight(); i++) {
            for (int j = 0; j < buffered.getWidth()/2; j++) {
                buffered.setRGB(j, i,buffered.getRGB( buffered.getWidth()-j-1,i));
                buffered.setRGB(buffered.getWidth()-j-1, i,buffered.getRGB(j,i));
            }
        }
        
        return (Image)buffered;
    }
    
    public Image symetrieX(Image image){
       BufferedImage imageReturn = (BufferedImage)image;
       AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
       tx.translate(-imageReturn.getWidth(null), 0);
       AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
       imageReturn = op.filter(imageReturn, null);
       return (Image)imageReturn;
    }
    
    public Image seuillage(Image image, int seuil){
       BufferedImage buffered = (BufferedImage) image;
        BufferedImage bufferedSeuil = new BufferedImage(buffered.getWidth(), buffered.getHeight(), BufferedImage.TYPE_INT_ARGB);
       
        for (int i = 0; i < buffered.getWidth(); i++) {
            for (int j = 0; j < buffered.getHeight(); j++) {
                int rgb = buffered.getRGB(i, j);
                Color color = new Color(rgb, true);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                if(r<seuil){
                    r = 0;
                }else{
                    r = 255;
                }
                if(g<seuil){
                    g = 0;
                }else{
                    g = 255;
                }
                if(b<seuil){
                    b = 0;
                }else{
                    b = 255;
                }
                color = new Color(r,g,b, color.getAlpha());
                bufferedSeuil.setRGB(i, j, color.getRGB());
            }
        }
        return (Image) bufferedSeuil;
    }
    
    public Image negatif(Image image){
        BufferedImage buffered = (BufferedImage) image;
        for (int i = 0; i < buffered.getWidth(); i++) {
            for (int j = 0; j < buffered.getHeight(); j++) {
                buffered.setRGB(i, j,255- buffered.getRGB(i, j));
            }
        }
        return buffered;
    }
}
