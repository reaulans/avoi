/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi;

import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author faneva
 */
public class Avoi {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        // TODO code application logic here
        int [][] matrice = {{5,4,6,5,5},{5,5,4,0,5},{5,0,4,3,5},{6,2,1,4,3},{6,2,1,4,3},{7,7,9,5,8}};
        afficherMatrice(matrice);
        List<Integer> reponse = niveauDeGrisDesire(matrice);
        afficher(reponse, "Niveau de gris desiré");
    }
    static public void afficherMatrice(int [][] matrice){
        System.out.println("Image \n");
        for (int i = 0; i < matrice.length; i++) {
            for (int j = 0; j < matrice[i].length; j++) {
                 System.out.print(" "+matrice[i][j]);
            }
            System.out.print("\n");
        }
    }
    static public void afficherList(List<Integer> vecteur, String titre){
       System.out.println("\n"+titre+"\n");
       for (int i = 0; i < vecteur.size(); i++) {
           System.out.print(" "+vecteur.get(i));
       }
   }
   static public void afficher(List<Integer> vecteur, String titre){
       System.out.println("\n"+titre+"\n");
       for (int i = 0; i < vecteur.size(); i++) {
           System.out.print(" "+vecteur.get(i));
       }
   }
    
   static public List<Integer>  niveauDeGrisDesire(int i[][]){
        List<Integer> result = new ArrayList();
        List<Integer> niveau =niveauDeGris(i);
        afficherList(niveau, "niveau de gris");
        List<Integer> histogramme = histogrammeMatrice(i);
        afficherList(histogramme, "Histogramme");
        List<Integer> histogrammecum = histogrammeCumule(histogramme);
        afficherList(histogrammecum, "Histogramme cumulé");
        List<Integer> histogrammeprim = egalisationHistogramme(i);
        afficherList(histogrammeprim, "Histogramme égalisation");
        List<Integer> histogrammecumprim = histogrammeCumule(histogrammeprim);
        afficherList(histogrammecumprim, "Histogramme cumulé desiré");
        int distance =0; int indice = 0;
        for (int j = 0; j < niveau.size(); j++) {
            for (int k = 0; k < histogrammecumprim.size(); k++) {
                if(k == 0){
                   distance = (int) sqrt((histogrammecum.get(j) - histogrammecumprim.get(k))*(histogrammecum.get(j) - histogrammecumprim.get(k))); 
                }else{
                   if (sqrt((histogrammecum.get(j) - histogrammecumprim.get(k))*(histogrammecum.get(j) - histogrammecumprim.get(k)))  < distance) {
                    distance = (int) sqrt((histogrammecum.get(j) - histogrammecumprim.get(k))*(histogrammecum.get(j) - histogrammecumprim.get(k)));
                    indice = k;
                   } 
                }
            }
            result.add(niveau.get(indice));
        }
        return result;
    }
    
   static public List<Integer> histogrammeCumule(List<Integer> i){
        List<Integer> result = new ArrayList();
        for (int j = 0; j < i.size(); j++) {
            if(j == 0){
                result.add( i.get(j));
            }else{
                result.add(result.get(j-1) + i.get(j));
            }
        }
        return result;
    }
    
   static public List<Integer> histogrammeMatrice(int i[][]){
        int kk = 0;
        List<Integer> result = new ArrayList();
        List<Integer> niveau = niveauDeGris(i);
        for (int j = 0; j < niveau.size(); j++) {
            for (int k = 0; k < i.length; k++) {
                for (int l = 0; l < i[0].length; l++) {
                   if(niveau.get(j) == i[k][l]){
                    kk ++;
                }   
                }
            }
            result.add(kk);
            kk=0;
        }
        return result;
    }
    
   static public int[] histogrammeligne(int i[]){
        int result[] = new int[i.length];
        int nbr = 1;
        int valeur= 0;
        int results[] = i;
        for (int j = 0; j < i.length; j++) {
            valeur = i[j]; 
            for (int k = 0; k < results.length; k++) {
                
                    if(valeur == results[k]){
                        nbr ++;
                    }else{
                    
                    }
            }
             result[j] = nbr;
             nbr = 1;
        }
        return result;
    }
    
   static public List<Integer> niveauDeGris(int i[][]){
        List<Integer> templign = new ArrayList();
        List<Integer> lign = new ArrayList();
        for (int j = 0; j < i.length; j++) {
            for (int k = 0; k < i[j].length; k++) {
                lign.add(i[j][k]);
            }
        }
        
        for (int j = 0; j < lign.size(); j++) {
        if(!templign.isEmpty()){
                  if(!templign.contains(lign.get(j))){
                       templign.add(lign.get(j));
                  };
                  
        }else{
            templign.add(lign.get(j));
        }   
       }
        return templign;
    }
    
   static public List<Integer> egalisationHistogramme(int i[][]){
        List<Integer> result = new ArrayList();
        List<Integer> histogramme = histogrammeMatrice(i);
        int sommeH = 0;
        int moyenne = 0;
        List<Integer> niveau = niveauDeGris(i);
        for (int j = 0; j < histogramme.size(); j++) {
            sommeH += histogramme.get(j);
        }
        
         moyenne = sommeH/niveau.size();
         for (int j = 0; j < niveau.size(); j++) {
            result.add(moyenne);
        }
        return result;
    }
    
    
   static public void sumetrieParDroite(){
    
    }
    
    
    public void homotetie(){
    
    }
    
    
    public void symetrie(){
            
    }
    
    public void cisaillement(){
    
    }
    
    
    public double[][] rotation(double deg,double[][] image){
         double[][] result = null;
         double[][] rotateM = {{Math.cos(deg),Math.sin(deg)},{-Math.sin(deg), Math.cos(deg)}};
           result = produitMatrice(image, rotateM);
         return result;
    }
    
    public int[][] inversion(int[][] image){
        int[][] result = null;
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                result[i][j] = 255 - image[i][j];
            }
        }
        return result;
    }
    
   static public int[][]  binarisation(int[][] image,int seuil ){
        int[][] result = null;
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if( image[i][j] < seuil){
                 result[i][j] = 0;
                }else{
                   result[i][j] = 255;
                }
            }
        }
        return result;
    }
    
   static public double [][] produitMatrice(double vect[][], double vetctd[][]){
        int l = 0;
        int c = 0;
        int calcul = 0;
        double[][] result = null;
        if(vect[0].length == vetctd.length ){
            
        }else{
            System.out.println("Erreur de dimension");
        }
        
        if(vect.length * vect[0].length < vetctd.length * vetctd[0].length){
            l = vetctd.length;
            c = vetctd[0].length;
        }else{
            l = vect.length;
            c = vect[0].length; 
        }
        
        result = new double[l][c];
        l = 0;
        for (int i = 0; i < vect.length; i++) {
            c = 0;
            for (int j = 0; j < vect[0].length; j++) {
                calcul = 0;
                for (int k = 0; k < vetctd[0].length; k++) {
                    calcul += vect[i][k] * vetctd[i][j];
                }
                result[l][c] = calcul;
                c++;
            }
            l++;
        }
        return result;
    }
}
