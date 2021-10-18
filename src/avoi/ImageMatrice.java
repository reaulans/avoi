/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author faneva
 */
public class ImageMatrice {
    public ImageMatrice(){}
    public static int[][] getRedMatrice(Image image){
        BufferedImage img = (BufferedImage)image;
        int[][] matrice = new int [img.getWidth()][img.getHeight()];
            int x,y,g;
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    Color pixelcolor = new Color(img.getRGB(i, j));
                    int r = pixelcolor.getRed();
                    matrice[i][j] = r;
                }
            }
           return matrice;
    }
    
    public static int[][] getAlphaMatrice(Image image){
        BufferedImage img = (BufferedImage)image;
        int[][] matrice = new int [img.getWidth()][img.getHeight()];
            int x,y,g;
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    Color pixelcolor = new Color(img.getRGB(i, j));
                    int r = pixelcolor.getRed();
                    matrice[i][j] = r;
                }
            }
           return matrice;
    }
    
    public static int[][] getGreenMatrice(Image image){
        BufferedImage img = (BufferedImage)image;
        int[][] matrice = new int [img.getWidth()][img.getHeight()];
            int x,y,g;
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    Color pixelcolor = new Color(img.getRGB(i, j));
                    int gb = pixelcolor.getGreen();
                    matrice[i][j] = gb;
                    int b = pixelcolor.getBlue();
                }
            }
           return matrice;
    }
    
    public static int[][] getBlueMatrice(Image image){
        BufferedImage img = (BufferedImage)image;
        int[][] matrice = new int [img.getWidth()][img.getHeight()];
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    Color pixelcolor = new Color(img.getRGB(i, j));
                    int b = pixelcolor.getBlue();
                    matrice[i][j] = b;
                }
            }
           return matrice;
    }
    
    public static Image getImageByRGB(Image image,int[][] pixelRed,int[][] pixelGreen,int[][] pixelBlue){
        BufferedImage img = (BufferedImage)image;
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    int rgbC = img.getRGB(i, j);
                    Color color = new Color(rgbC, true);
                    int hy = (pixelRed[i][j] + pixelGreen[i][j] + pixelBlue[i][j]) / 3;
                    int rgb = new Color(pixelRed[i][j],pixelGreen[i][j],pixelBlue[i][j],color.getAlpha()).getRGB();
                    img.setRGB(i, j, rgb);
                    
                }
            }
            
            return (Image)img;
    }
    public static Image getImageByRGBL(Image image,List<Integer> pixelRed,List<Integer> pixelGreen,List<Integer> pixelBlue){
        BufferedImage img = (BufferedImage)image;
            int indice =0;
            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    int hy = (pixelRed.get(indice) + pixelGreen.get(indice) + pixelBlue.get(indice)) / 3;
                    int rgb = new Color(hy,hy,hy).getRGB();
                    img.setRGB(i, j, rgb);
                    indice++;
                }
            }
            
            return (Image)img;
    }
    
    public int[][] getMatriceByList(List<Integer> list,int width, int height){
        int[][] reponse  = new int[height][width];
        int indice = 0;
        if(list.size() ==(width * height)){
            for (int i = 0; i < height; i++) {
                 for (int j = 0; j < width; j++) {
                    reponse[i][j] = list.get(indice);
                     indice += 1;
                }
            }
            return reponse;
        }else{
            System.out.println("List incompatible");
            return null;
        }
        
    }
}
