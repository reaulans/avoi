/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi.avance.Filtre;

import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author faneva
 */
public class EgalisationNiveauGris {
   static  List<Integer> niveau;
    public EgalisationNiveauGris(){
        
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
   static public int[][] matriceDesire(int i[][]){
       List<Integer>  niveauDeGrisDesire = niveauDeGrisDesire(i);
       int[][] out = new int[i.length][i[0].length]; 
       for (int j = 0; j < niveau.size(); j++) {
           for (int k = 0; k < i.length; k++) {
               for (int l = 0; l < i[k].length; l++) {
                   if(i[k][l] == niveau.get(j)){
                       out[k][l] = niveauDeGrisDesire.get(j);
                   }
               }
           }
       }
       
       return out;
   }
   static public List<Integer>  niveauDeGrisDesire(int i[][]){
        List<Integer> result = new ArrayList();
        niveau =niveauDeGris(i);
        List<Integer> histogramme = histogrammeMatrice(i);
        List<Integer> histogrammecum = histogrammeCumule(histogramme);
        List<Integer> histogrammeprim = egalisationHistogramme(i);
        List<Integer> histogrammecumprim = histogrammeCumule(histogrammeprim);
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
    
    
}
