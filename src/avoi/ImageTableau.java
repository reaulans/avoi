/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author faneva
 */
public class ImageTableau {
    public ImageTableau(){}
    public static double[] getRedTableau(Image image){
        BufferedImage img = (BufferedImage)image;
        double[] matrice = new double [img.getWidth()* img.getHeight()];
        int indice = 0;
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    Color pixelcolor = new Color(img.getRGB(i, j));
                    int r = pixelcolor.getRed();
                    matrice[indice] = r;
                    indice++;
                }
            }
           return matrice;
    }
    
    public static double[] getAlphaMatrice(Image image){
        BufferedImage img = (BufferedImage)image;
        double[] matrice = new double [img.getWidth()* img.getHeight()];
        int indice = 0;
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    Color pixelcolor = new Color(img.getRGB(i, j));
                    int r = pixelcolor.getRed();
                    matrice[indice] = r;
                    indice++;
                }
            }
           return matrice;
    }
    
    public static double[] getGreenMatrice(Image image){
        BufferedImage img = (BufferedImage)image;
        double[] matrice = new double [img.getWidth()* img.getHeight()];
        int indice = 0;
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    Color pixelcolor = new Color(img.getRGB(i, j));
                    int gb = pixelcolor.getGreen();
                    matrice[indice] = gb;
                    indice++;
                }
            }
           return matrice;
    }
    
    public static double[] getBlueMatrice(Image image){
        BufferedImage img = (BufferedImage)image;
        double[] matrice = new double [img.getWidth()* img.getHeight()];
        int indice = 0;
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    Color pixelcolor = new Color(img.getRGB(i, j));
                    int b = pixelcolor.getBlue();
                    matrice[indice] = b;
                    indice++;
                }
            }
           return matrice;
    }
}
