package org.uma.jmetal.runner.bernardo;

import org.uma.jmetal.util.JMetalException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ANOVARunner {

    private ArrayList<ArrayList<Integer>> combinaciones;

    /**
     * Constructor
     */
    ANOVARunner(){
        combinaciones = new ArrayList<ArrayList<Integer>>();
    }

    /**
     * Ejecutar análisis
     */
    public void run(){
        generarCombinaciones();
        for(int i=0; i<this.combinaciones.size();i++){
            
        }
    }

    /**
     * i = Tamaño de población (1=100, 2=150, 3=200)
     * j = Operadores de cruzamiento (1=PMX, 2=OX, 3=pOX)
     * k = Probabilidades de cruzamiento (1 = 0.5, 2 = 0.6, 3 = 0.7, 4 = 0.8, 5 = 0.9)
     * l = Operadores de mutación (1=Swap, 2=pSwap, 3=Insert, 4=Scramble, 5=Inverse, 6=pInverse)
     * m = Probabilidades de mutación (1 = 0.05, 2 = 0.1, 3 = 0.2, 4 = 0.3, 5 = 0.4)
     */
    private void generarCombinaciones() {
        for(int a=1; a<=3;a++){
            for(int b=1; b<=3;b++){
                for(int c=1; c<=5;c++){
                    for(int d=1;d<=6;d++){
                        for(int e=1; e<=5;e++){
                            ArrayList<Integer> temp = new ArrayList<Integer>();
                            temp.add(a);
                            temp.add(b);
                            temp.add(c);
                            temp.add(d);
                            temp.add(e);
                            combinaciones.add(temp);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws JMetalException, FileNotFoundException, IOException {
        ANOVARunner a = new ANOVARunner();
        a.run();
    }
}
