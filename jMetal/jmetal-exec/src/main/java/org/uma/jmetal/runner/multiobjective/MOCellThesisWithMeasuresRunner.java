package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellMeasures;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.OXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationScrambleMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.problem.multiobjective.VehicleRouting2;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Class to configure and run the MOCell algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOCellThesisWithMeasuresRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.MOCellThesis problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException, IOException {
    JMetalRandom.getInstance().setSeed(100L);

    PermutationProblem<PermutationSolution<Integer>> problem;
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

    crossover = new OXCrossover(0.9) ;

    double mutationProbability = 0.3 ;
    mutation = new PermutationScrambleMutation<>(mutationProbability) ;

    selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

    algorithm = new MOCellBuilder<PermutationSolution<Integer>>(problem, crossover, mutation)
            .setSelectionOperator(selection)
            .setMaxEvaluations(25000)
            .setPopulationSize(225)
            .setVariant(MOCellBuilder.MOCellVariant.Measures)
            .setArchive(new CrowdingDistanceArchive<PermutationSolution<Integer>>(100))
            .build() ;


    ((MOCellMeasures<PermutationSolution<Integer>>)algorithm).setReferenceFront(new ArrayFront(referenceParetoFront));

    /* Measure management */
    MeasureManager measureManager = ((MOCellMeasures<PermutationSolution<Integer>>)algorithm).getMeasureManager() ;

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
          System.out.println("MOCELL") ;
        }
        System.out.println(counter+" : " + value) ;
      }
    }
  }
}
