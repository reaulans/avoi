/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author jaoharison
 */
public class ImagePanel extends Canvas{
    Image image;
    final int HEIGHT = 330;
    final int WIDTH = 297;
    
    public ImagePanel(Image img){
        image = img;
        this.setSize(WIDTH, HEIGHT);
    }
    
    @Override
    public void paint(Graphics g){
        g.drawImage(image, 0, 0,WIDTH, HEIGHT, this);
    }
    
}
