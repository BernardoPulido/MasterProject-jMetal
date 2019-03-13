package org.uma.jmetal.runner.bernardo;

import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.JMetalException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Calcular {

    public void calcular(){
        PISAHypervolume e = new PISAHypervolume();
    }


    public static void main(String[] args) throws JMetalException, FileNotFoundException, IOException {
        Calcular c = new Calcular();
        c.calcular();
    }
}
