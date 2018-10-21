package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.VehicleRouting;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the VehicleRouting problem.
 * The used algorithms are NSGA-II and MOCell.
 *
 * This experiment assumes that the reference Pareto front is of problem VehicleRouting is not known, so a
 * reference front must be obtained.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are:
 *    1. Configure the experiment
 *    2. Execute the algorithms
 *    3. Generate the reference Pareto sets and Pareto fronts
 *    4. Compute the quality indicators
 *    5. Generate Latex tables reporting means and medians
 *    6. Generate Latex tables with the result of applying the Wilcoxon Rank Sum Test
 *    7. Generate Latex tables with the ranking obtained by applying the Friedman test
 *    8. Generate R scripts to obtain boxplots
 *
 * @author Luis Bernardo Pulido <lpulido@cicese.edu.mx>
 */
public class VehicleRoutingStudy {

  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {

    String experimentBaseDirectory = "experiments";

    List<ExperimentProblem<PermutationSolution<Integer>>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new VehicleRouting("/tspInstances/vrp.txt"), "VRP"));

    List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<PermutationSolution<Integer>, List<PermutationSolution<Integer>>> experiment =
        new ExperimentBuilder<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>("VehicleRoutingStudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory("/pareto_fronts")
            .setReferenceFrontDirectory(
                experimentBaseDirectory + "/VehicleRoutingStudy/referenceFronts")
            .setIndicatorList(Arrays.asList(
                new Epsilon<PermutationSolution<Integer>>(),
                new Spread<PermutationSolution<Integer>>(),
                new GenerationalDistance<PermutationSolution<Integer>>(),
                new PISAHypervolume<PermutationSolution<Integer>>(),
                new InvertedGenerationalDistance<PermutationSolution<Integer>>(),
                new InvertedGenerationalDistancePlus<PermutationSolution<Integer>>()))
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(8)
            .build();

    new ExecuteAlgorithms<>(experiment).run();
    new GenerateReferenceParetoFront(experiment).run();
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link
   * ExperimentAlgorithm} has an optional tag component, that can be set as it is shown in this
   * example, where four variants of a same algorithm are defined.
   */
  static List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> configureAlgorithmList(
      List<ExperimentProblem<PermutationSolution<Integer>>> problemList) {
    List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> algorithms = new ArrayList<>();
    for (int run = 0; run < INDEPENDENT_RUNS; run++) {

      for (int i = 0; i < problemList.size(); i++) {

        PermutationProblem<PermutationSolution<Integer>> problem = (PermutationProblem<PermutationSolution<Integer>>) problemList.get(i).getProblem();
        Algorithm<List<PermutationSolution<Integer>>> algorithm;
        CrossoverOperator<PermutationSolution<Integer>> crossover;
        MutationOperator<PermutationSolution<Integer>> mutation;
        SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

        crossover = new PMXCrossover(0.9) ;

        double mutationProbability = 0.2 ; //NSGAII
        //double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

        selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

        algorithm = new NSGAIIBuilder<PermutationSolution<Integer>>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                //.setMaxEvaluations(10000)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build() ;

        algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i), run));
      }

      for (int i = 0; i < problemList.size(); i++) {

        PermutationProblem<PermutationSolution<Integer>> problem = (PermutationProblem<PermutationSolution<Integer>>) problemList.get(i).getProblem();
        Algorithm<List<PermutationSolution<Integer>>> algorithm;
        CrossoverOperator<PermutationSolution<Integer>> crossover;
        MutationOperator<PermutationSolution<Integer>> mutation;
        SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

        double crossoverProbability = 0.9 ;
        double crossoverDistributionIndex = 20.0 ;
        crossover = new PMXCrossover(0.9) ;

        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;
        //double mutationProbability = 0.2 ; NSGAII
        mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

        selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

        algorithm = new MOCellBuilder<PermutationSolution<Integer>>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .setArchive(new CrowdingDistanceArchive<PermutationSolution<Integer>>(100))
                .build() ;

        algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i), run));
      }
    }
    return algorithms;
  }
}