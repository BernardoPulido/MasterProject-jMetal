
package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Measures;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.VehicleRouting2;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.*;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Class for configuring and running the SPEA2 algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class SPEA2ThesisMeasuresRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.SPEA2BinaryRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException, IOException {
    Problem<PermutationSolution<Integer>> problem;
    Algorithm<List<PermutationSolution<Integer>>> algorithm;
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;
    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

    String referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.VehicleRouting2";
      referenceParetoFront = "experiments/VehicleRoutingStudy/referenceFronts/VRP.pf" ;
    }

    problem = new VehicleRouting2("/experiments/ol_temp_tres.txt");

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new PMXCrossover(0.9) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;


    mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

    selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

    algorithm = new SPEA2Builder<>(problem, crossover, mutation)
        .setSelectionOperator(selection)
        .setMaxIterations(250)
        .setPopulationSize(100)
            .setVariant(SPEA2Builder.SPEA2Variant.Measures)
        .build() ;

    ((SPEA2Measures<PermutationSolution<Integer>>)algorithm).setReferenceFront(new ArrayFront(referenceParetoFront));

    /* Measure management */
    MeasureManager measureManager = ((SPEA2Measures<PermutationSolution<Integer>>)algorithm).getMeasureManager() ;

    DurationMeasure currentComputingTime =
            (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");

    BasicMeasure<Double> hypervolumeMeasure =
            (BasicMeasure<Double>) measureManager.<Double>getPushMeasure("hypervolume");

    hypervolumeMeasure.register(new Listener());
    /* End of measure management */

    Thread algorithmThread = new Thread(algorithm) ;
    algorithmThread.start();

    try {
      algorithmThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    List<PermutationSolution<Integer>> population = algorithm.getResult() ;
    long computingTime = currentComputingTime.get() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
  private static class Listener implements MeasureListener<Double> {

    private static int counter = 0 ;
    @Override synchronized public void measureGenerated(Double value) {
      if ((counter++ % 1 == 0)) {
        if(counter==1){
          System.out.println("SPEA") ;
        }
        System.out.println(value) ;
      }
    }
  }
}