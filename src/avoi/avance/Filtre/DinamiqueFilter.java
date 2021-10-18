/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi.avance.Filtre;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 *
 * @author jaoharison
 */
public class DinamiqueFilter {
    public DinamiqueFilter(){
    
    }
    
    public BufferedImage getImageByFilter(Image activeImage){
         BufferedImage buffered = (BufferedImage)activeImage;
        BufferedImage imageFlou = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());
        float[] masqueFlou = 
        {
            1/25f, 1/25f, 1/25f,1/25f,1/25f,
            1/25f, 1/25f, 1/25f,1/25f,1/25f,
            1/25f, 1/25f, 1/25f,1/25f,1/25f,
            1/25f, 1/25f, 1/25f,1/25f,1/25f,
            1/25f, 1/25f, 1/25f,1/25f,1/25f,
        };
        float[] masqueGradientX = 
        {
            -1f, 0f, 1f,
            -2f, 0f, 2f,
            -1f, 0f, 1f,
        };
        
        float[] masqueGradientY = 
        {
            1f, 2f, 1f,
            0f, 0f, 0f,
            -1f, 2f, -1f,
        };
        
        Kernel masque = new Kernel(5,5,masqueFlou);
        ConvolveOp operation = new ConvolveOp(masque);
        operation.filter(buffered, imageFlou);
        return imageFlou;
    }
}
