package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.problem.multiobjective.VehicleRouting2;
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
import java.util.List;

/**
 * Class to configure and run the MOCell algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOCellThesis2 extends AbstractAlgorithmRunner {
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

    problem = new VehicleRouting2("/experiments/vrp_temp.txt");

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    //crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
    crossover = new PMXCrossover(0.9) ;

    //double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    double mutationProbability = 0.2 ;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

    selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

    algorithm = new MOCellBuilder<PermutationSolution<Integer>>(problem, crossover, mutation)
            .setSelectionOperator(selection)
            .setMaxEvaluations(500000)
            .setPopulationSize(100)
            .setArchive(new CrowdingDistanceArchive<PermutationSolution<Integer>>(100))
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    List<PermutationSolution<Integer>> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionListOutput(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("results/VAR_MOCell.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("results/FUN_MOCell.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN_MOCell.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR_MOCell.tsv");

    //printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
