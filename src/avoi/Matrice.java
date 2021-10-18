/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi;

/**
 *
 * @author faneva
 */
public class Matrice {

    /**
     *
     */
    public double[][] entree;
    
    public Matrice(){}
    public Matrice(double[][] matriceentree){
        this.entree = matriceentree;
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
