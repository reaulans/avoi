/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi.avance.Filtre;

import avoi.local.Couleur.Luminosite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;

/**
 *
 * @author auflida
 */
public class ColorFilter {
    
    public ColorFilter(){
    
    }
    public Image opacity(Image image){
        BufferedImage buffered = (BufferedImage)image;
        int width = buffered.getWidth();
        int height = buffered.getHeight();
        BufferedImage bufferedOut = new BufferedImage(width, height,buffered.getType());
         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = buffered.getRGB(x, y);
                OpaqueFilter opaqueFilter = new OpaqueFilter(5);
                bufferedOut.setRGB(x, y, opaqueFilter.filterRGB(x, y, p));
            }
        }
        
        return (Image)bufferedOut;
    }
    
    
    public Image contraste(Image image, float contrastePourcentage){
        BufferedImage buffered = (BufferedImage)image;
        RescaleOp rescaleOp = new RescaleOp(contrastePourcentage/100f, 0, null);
        rescaleOp.filter(buffered, buffered);
        return buffered;
    }
    
    public  Image nouveauLuminosite( Image image, float luminositePourcentage ) {
        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();

        if (bi.getColorModel().hasAlpha()) { 
            System.out.println("Image has got an alpha channel");
        }

        bg.drawImage(image, 0, 0, null);
        bg.dispose();

        RescaleOp rescaleOp = new RescaleOp(luminositePourcentage/100f, 0, null);
        rescaleOp.filter(bi, bi);

        image = bi;

        return bi;

    }
    
    public Image luminosite(Image image, int pourcent, String choix){
        BufferedImage buffered = (BufferedImage)image;
        int width = buffered.getWidth();
        int height = buffered.getHeight();
        BufferedImage bufferedOut = new BufferedImage(width, height,buffered.getType());
        
		// Cette fonction est chargée de changer soit la luminosité soit la saturation.
		int pix=0,h=0,w=0,x=0,y=0;
		double lumi=0,sat=0;
		if ((pourcent == 100) || (pourcent <=0)) {
			// Si la valeur est 100%, cela ne sert à rien d'appliquer la fonction.
			System.out.println("Erreur : le pourcentage est soit négatif soit egal a 0.");
			return buffered;
		}
		else {
			lumi = 1;
			sat = 1;
			if(choix.equals("luminosite")) {
				lumi =  (double)pourcent/100;
			}
			else {
				if(choix.equals("saturation")) {
					sat =  (double)pourcent/100;
				}
			}
		}
		
		
		w = buffered.getWidth();
		h = buffered.getHeight();
		for(y=0;y<h;y++) {
			for(x=0;x<w;x++) {
				int rgb = buffered.getRGB(x, y);
				Color clr = new Color(rgb,true);
				int alpha = clr.getAlpha();
				float[] hsb = Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), null);
				hsb[1] = (float) (hsb[1]*sat);
				hsb[2] = (float) (hsb[2]*lumi);
				rgb = Color.HSBtoRGB(hsb[0],hsb[1],hsb[2]);
				clr = new Color(rgb);
				clr = new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), alpha);
				bufferedOut.setRGB(x, y, clr.getRGB());
			}
		}
		
        return (Image)bufferedOut;
    }
    
    public Image filterGreen(Image image){
        BufferedImage buffered = (BufferedImage)image;
        int width = buffered.getWidth();
        int height = buffered.getHeight();
        BufferedImage bufferedOut = new BufferedImage(width, height,buffered.getType());
         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = buffered.getRGB(x, y);
                FilterColor filterColor = new FilterColor(false, true,false);
                bufferedOut.setRGB(x, y, filterColor.filterRGB(x, y, p));
            }
        }
        
        return (Image)bufferedOut;
    }
    
    public Image filterRed(Image image){
        BufferedImage buffered = (BufferedImage)image;
        int width = buffered.getWidth();
        int height = buffered.getHeight();
        BufferedImage bufferedOut = new BufferedImage(width, height,buffered.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = buffered.getRGB(x, y);
                FilterColor filterColor = new FilterColor(true, false,false);
                bufferedOut.setRGB(x, y, filterColor.filterRGB(x, y, p));
            }
        }
        
        return (Image)bufferedOut;
    }
    
    public Image filterBlue(Image image){
        BufferedImage buffered = (BufferedImage)image;
        int width = buffered.getWidth();
        int height = buffered.getHeight();
        BufferedImage bufferedOut = new BufferedImage(width, height,buffered.getType());
         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = buffered.getRGB(x, y);
                int a  = (rgb>>24) & 0xff;
                int b = rgb &  0xff;
                 rgb= (a<<24) | (0<<16)| (0<<8)| b;
                bufferedOut.setRGB(x, y, rgb);
            }
        }
        
        return (Image)bufferedOut;
    }
    
    public Image specifiedColorFileter(Image image, Color color){
        BufferedImage buffered = (BufferedImage)image;
        int width = buffered.getWidth();
        int height = buffered.getHeight();
        BufferedImage bufferedOut = new BufferedImage(width, height,buffered.getType());
        for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            Color pixelColor = new Color(buffered.getRGB(x, y), true);
            int r = (pixelColor.getRed() + color.getRed()) / 2;
            int g = (pixelColor.getGreen() + color.getGreen()) / 2;
            int b = (pixelColor.getBlue() + color.getBlue()) / 2;
            int a = pixelColor.getAlpha();
            int rgba = (a << 24) | (r << 16) | (g << 8) | b;
            bufferedOut.setRGB(x, y, rgba);
        }
    }
        return (Image)bufferedOut;
    }
    
    public Image filterSepia(Image image,int sepiaIntensity){
        BufferedImage img = (BufferedImage) image;
        BufferedImage sepia = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int sepiaDepth = 20;
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w*h*3];
        img.getRaster().getPixels(0,0,w, h, pixels);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
            int rgb = img.getRGB(x, y);
            Color color = new Color(rgb, true);
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            int gry = (r * g* b)/3;
            r =g =b =gry;
            r = r + (sepiaDepth *2);
            g = g + sepiaDepth;
            if(r > 255){
                r = 255;
            }
            if(g > 255){
                g = 255;
            }
            if(b > 255){
                b = 255;
            }
            
            b -= sepiaIntensity;
            if(b < 255){
                b = 255;
            }
            if(b < 0){
                b = 0;
            }
            color = new Color(r,g,b, color.getAlpha());
            sepia.setRGB(x, y, color.getRGB());
            }
        }
       
        return (Image) sepia;
    }
}
