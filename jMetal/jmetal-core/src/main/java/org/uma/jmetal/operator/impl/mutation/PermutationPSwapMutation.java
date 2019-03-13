package org.uma.jmetal.operator.impl.mutation;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a swap mutation. The solution type of the solution
 * must be Permutation.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class PermutationPSwapMutation<T> implements MutationOperator<PermutationSolution<T>> {
  private double mutationProbability ;
  private RandomGenerator<Double> mutationRandomGenerator ;
  private BoundedRandomGenerator<Integer> positionRandomGenerator ;

  /**
   * Constructor
   */
  public PermutationPSwapMutation(double mutationProbability) {
	  this(mutationProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a,  b));
  }

  /**
   * Constructor
   */
  public PermutationPSwapMutation(double mutationProbability, RandomGenerator<Double> randomGenerator) {
	  this(mutationProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
  }

  /**
   * Constructor
   */
  public PermutationPSwapMutation(double mutationProbability, RandomGenerator<Double> mutationRandomGenerator, BoundedRandomGenerator<Integer> positionRandomGenerator) {
    if ((mutationProbability < 0) || (mutationProbability > 1)) {
      throw new JMetalException("Mutation probability value invalid: " + mutationProbability) ;
    }
    this.mutationProbability = mutationProbability;
    this.mutationRandomGenerator = mutationRandomGenerator ;
    this.positionRandomGenerator = positionRandomGenerator ;
  }

  /* Getters */
  public double getMutationProbability() {
    return mutationProbability;
  }

  /* Setters */
  public void setMutationProbability(double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

  /* Execute() method */
  @Override
  public PermutationSolution<T> execute(PermutationSolution<T> solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(solution);
    return solution;
  }

  /**
   * Performs the operation
   */
  public void doMutation(PermutationSolution<T> solution) {
    int permutationLength ;
    permutationLength = solution.getNumberOfVariables() ;

    if ((permutationLength != 0) && (permutationLength != 1)) {
      if (mutationRandomGenerator.getRandomValue() < mutationProbability) {

        int pos1 = positionRandomGenerator.getRandomValue(0, permutationLength - 1);

        T temp = solution.getVariableValue(pos1);
        int temp_int = Integer.parseInt(solution.getVariableValueString(pos1));
        solution.setVariableValue(pos1, solution.getVariableValue(temp_int));
        solution.setVariableValue(temp_int, temp);

      }
    }
  }
}
