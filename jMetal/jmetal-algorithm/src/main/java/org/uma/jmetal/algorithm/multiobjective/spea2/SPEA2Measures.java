package org.uma.jmetal.algorithm.multiobjective.spea2;

import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.measure.Measurable;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SPEA2Measures<S extends Solution<?>> extends SPEA2<S> implements Measurable {
  protected CountingMeasure evaluations ;
  protected DurationMeasure durationMeasure ;
  protected SimpleMeasureManager measureManager ;

  protected BasicMeasure<List<S>> solutionListMeasure ;
  protected BasicMeasure<Integer> numberOfNonDominatedSolutionsInPopulation ;
  protected BasicMeasure<Double> hypervolumeValue ;

  protected Front referenceFront ;

  /**
   * Constructor
   */
  public SPEA2Measures(Problem<S> problem, int maxIterations, int populationSize,
                       CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                       SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator) ;

    referenceFront = new ArrayFront() ;

    initMeasures() ;
  }

  @Override protected void initProgress() {
    evaluations.reset(getMaxPopulationSize());
  }

  @Override protected void updateProgress() {
    evaluations.increment(getMaxPopulationSize());

    solutionListMeasure.push(getPopulation());

    if (referenceFront.getNumberOfPoints() > 0) {
      FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront) ;

      Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront) ;
      Front normalizedFront = frontNormalizer.normalize(new ArrayFront(getPopulation())) ;
      List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(normalizedFront) ;

      hypervolumeValue.push(new PISAHypervolume<S>(normalizedReferenceFront).evaluate((List<S>)normalizedPopulation));
      /*
      Para SPREAD
       */
      //hypervolumeValue.push(new Spread<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation));
      //hypervolumeValue.push(new Spread<S>(referenceFront).evaluate(getPopulation()));

    }
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations.get() >= 25000;
  }

  @Override
  public void run() {
    durationMeasure.reset();
    durationMeasure.start();
    super.run();
    durationMeasure.stop();
  }

  /* Measures code */
  private void initMeasures() {
    durationMeasure = new DurationMeasure() ;
    evaluations = new CountingMeasure(0) ;
    numberOfNonDominatedSolutionsInPopulation = new BasicMeasure<>() ;
    solutionListMeasure = new BasicMeasure<>() ;
    hypervolumeValue = new BasicMeasure<>() ;

    measureManager = new SimpleMeasureManager() ;
    measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
    measureManager.setPullMeasure("currentEvaluation", evaluations);
    measureManager.setPullMeasure("numberOfNonDominatedSolutionsInPopulation",
        numberOfNonDominatedSolutionsInPopulation);

    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
    measureManager.setPushMeasure("currentEvaluation", evaluations);
    measureManager.setPushMeasure("hypervolume", hypervolumeValue);
  }

  @Override
  public MeasureManager getMeasureManager() {
    return measureManager ;
  }

  @Override protected List<S> replacement(List<S> population,
      List<S> offspringPopulation) {
    List<S> pop = super.replacement(population, offspringPopulation) ;

    Ranking<S> ranking = new DominanceRanking<S>(dominanceComparator);
    ranking.computeRanking(population);

    numberOfNonDominatedSolutionsInPopulation.set(ranking.getSubfront(0).size());

    return pop;
  }

  public CountingMeasure getEvaluations() {
    return evaluations;
  }

  @Override public String getName() {
    return "MOCEllM" ;
  }

  @Override public String getDescription() {
    return "MOCell Version using measures" ;
  }

  public void setReferenceFront(Front referenceFront) {
    this.referenceFront = referenceFront ;
  }
}
