package org.uma.jmetal.runner.bernardo;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.NullCrossover;
import org.uma.jmetal.operator.impl.crossover.OXCrossover;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.crossover.pOXCrossover;
import org.uma.jmetal.operator.impl.mutation.*;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.problem.multiobjective.VehicleRouting3;
import org.uma.jmetal.problem.singleobjective.VehicleRouting;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ANOVARunner extends AbstractAlgorithmRunner {

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
    public void run() throws JMetalException, FileNotFoundException, IOException {
        generarCombinaciones();
        for (ArrayList<Integer> combinacion : this.combinaciones)
        {
            System.out.print(combinacion.get(0)+", "+combinacion.get(1)+", "+combinacion.get(2)+", "+combinacion.get(3)+", "+combinacion.get(4)+", ");

            JMetalRandom.getInstance().setSeed(100L);

            PermutationProblem<PermutationSolution<Integer>> problem;
            Algorithm<List<PermutationSolution<Integer>>> algorithm;
            CrossoverOperator<PermutationSolution<Integer>> crossover;
            MutationOperator<PermutationSolution<Integer>> mutation;
            SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

            String referenceParetoFront = "experiments/VehicleRoutingStudy/referenceFronts/VRP.pf" ;

            problem = new VehicleRouting("/experiments/ol_temp_tres.txt");

            //Tamaño de población
            int populationSize = 0;
            switch (combinacion.get(0)){
                case 1: populationSize = 100;
                break;
                case 2: populationSize = 150;
                break;
                case 3: populationSize = 200;
                break;
            }

            //Probabilidad de cruzamiento
            double crossoverProbability = 0 ;
            switch (combinacion.get(2)){
                case 1: crossoverProbability = 0.5 ;
                break;
                case 2: crossoverProbability = 0.6 ;
                    break;
                case 3: crossoverProbability = 0.7 ;
                    break;
                case 4: crossoverProbability = 0.8 ;
                    break;
                case 5: crossoverProbability = 0.9 ;
                    break;
            }

            //Cruzamiento
            crossover = new NullCrossover<>();
            switch (combinacion.get(1)){
                case 1: crossover = new PMXCrossover(crossoverProbability) ;
                break;
                case 2: crossover = new OXCrossover(crossoverProbability);
                break;
                case 3: crossover = new pOXCrossover(crossoverProbability);
                break;
            }

            //Probabilidad de mutación
            double mutationProbability = 0 ;
            switch (combinacion.get(4)){
                case 1: mutationProbability = 0.05 ;
                break;
                case 2: mutationProbability = 0.1;
                break;
                case 3: mutationProbability = 0.2;
                break;
                case 4: mutationProbability = 0.3;
                break;
                case 5: mutationProbability = 0.4;
                break;
            }

            //Mutación
            mutation = new NullMutation<>();
            switch (combinacion.get(3)){
                case 1: mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;
                break;
                case 2: mutation = new PermutationPSwapMutation<Integer>(mutationProbability);
                break;
                case 3: mutation = new PermutationInsertMutation<Integer>(mutationProbability);
                break;
                case 4: mutation = new PermutationScrambleMutation<Integer>(mutationProbability);
                break;
                case 5: mutation = new PermutationInverseMutation<Integer>(mutationProbability);
                break;
                case 6: mutation = new PermutationPInverseMutation<Integer>(mutationProbability);
                break;
            }

            selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());
            algorithm = new MOCellBuilder<PermutationSolution<Integer>>(problem, crossover, mutation)
                    .setSelectionOperator(selection)
                    .setMaxEvaluations(100000)
                    .setPopulationSize(populationSize)
                    .setArchive(new CrowdingDistanceArchive<PermutationSolution<Integer>>(100))
                    .build() ;

            AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                    .execute() ;

            List<PermutationSolution<Integer>> population = algorithm.getResult() ;
            System.out.println(population.get(0).getObjective(0));
            /*long computingTime = algorithmRunner.getComputingTime() ;

            new SolutionListOutput(population)
                    .setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext("results/VAR_MOCell.tsv"))
                    .setFunFileOutputContext(new DefaultFileOutputContext("results/FUN_MOCell.tsv"))
                    .print();

            JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
            JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
            JMetalLogger.logger.info("Objectives values have been written to file FUN_MOCell.tsv");
            JMetalLogger.logger.info("Variables values have been written to file VAR_MOCell.tsv");*/

            //printFinalSolutionSet(population);
            /*if (!referenceParetoFront.equals("")) {
                printQualityIndicators(population, referenceParetoFront) ;
            }*/
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
