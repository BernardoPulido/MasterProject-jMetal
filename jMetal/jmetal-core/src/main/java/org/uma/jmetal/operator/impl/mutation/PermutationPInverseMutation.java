package org.uma.jmetal.operator.impl.mutation;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a scramble mutation. The solution type of the solution
 * must be Permutation.
 *
 * @author Luis Bernardo Pulido lpulido@cicese.edu.mx>
 */
@SuppressWarnings("serial")
public class PermutationPInverseMutation<T> implements MutationOperator<PermutationSolution<T>> {
  private double mutationProbability ;
  private RandomGenerator<Double> mutationRandomGenerator ;
  private BoundedRandomGenerator<Integer> positionRandomGenerator ;

  /**
   * Constructor
   */
  public PermutationPInverseMutation(double mutationProbability) {
	  this(mutationProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a,  b));
  }

  /**
   * Constructor
   */
  public PermutationPInverseMutation(double mutationProbability, RandomGenerator<Double> randomGenerator) {
	  this(mutationProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
  }

  /**
   * Constructor
   */
  public PermutationPInverseMutation(double mutationProbability, RandomGenerator<Double> mutationRandomGenerator, BoundedRandomGenerator<Integer> positionRandomGenerator) {
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
          int pos1=0;
          int pos2=0;

          for(int i=0; i<permutationLength;i++){
            if(solution.getVariableValue(i).equals(0)){
              pos1=i;
            }
            else if(solution.getVariableValue(i).equals(permutationLength-1)){
              pos2=i;
            }
          }
          if(pos1>pos2){
              int temp = pos2;
              pos2=pos1;
              pos1 = temp;
          }

          int mid = pos1 + ((pos2 + 1) - pos1) / 2;
          int endCount = pos2;

          for (int i = pos1; i < mid; i++) {
              T tmp = solution.getVariableValue(i);
              solution.setVariableValue(i, solution.getVariableValue(endCount));
              solution.setVariableValue(endCount, tmp);
              endCount--;
          }

      }
    }
  }
}
