package org.uma.jmetal.operator.impl.mutation;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements a scramble mutation. The solution type of the solution
 * must be Permutation.
 *
 * @author Luis Bernardo Pulido lpulido@cicese.edu.mx>
 */
@SuppressWarnings("serial")
public class PermutationScrambleMutation<T> implements MutationOperator<PermutationSolution<T>> {
  private double mutationProbability ;
  private RandomGenerator<Double> mutationRandomGenerator ;
  private BoundedRandomGenerator<Integer> positionRandomGenerator ;

  /**
   * Constructor
   */
  public PermutationScrambleMutation(double mutationProbability) {
	  this(mutationProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a,  b));
  }

  /**
   * Constructor
   */
  public PermutationScrambleMutation(double mutationProbability, RandomGenerator<Double> randomGenerator) {
	  this(mutationProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
  }

  /**
   * Constructor
   */
  public PermutationScrambleMutation(double mutationProbability, RandomGenerator<Double> mutationRandomGenerator, BoundedRandomGenerator<Integer> positionRandomGenerator) {
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
        int pos2 = positionRandomGenerator.getRandomValue(0, permutationLength - 1);

        while (pos1 == pos2) {
          if (pos1 == (permutationLength - 1))
            pos2 = positionRandomGenerator.getRandomValue(0, permutationLength - 2);
          else
            pos2 = positionRandomGenerator.getRandomValue(pos1, permutationLength - 1);
        }

        List<T> subset=new ArrayList<T>();
        if(pos1<=pos2){
            for(int i=pos1; i<pos2+1; i++){
              subset.add(solution.getVariableValue(i));
            }
        }else{
            List<T> buffer1 = new ArrayList<T>();
            for(int i=pos1; i<permutationLength; i++){
              buffer1.add(solution.getVariableValue(i));
            }

            List<T> buffer2 = new ArrayList<T>();
            for(int i=0; i<pos2+1; i++){
              buffer2.add(solution.getVariableValue(i));
            }
            subset.addAll(buffer1);
            subset.addAll(buffer2);
        }

        Collections.shuffle(subset);

        if(pos1>pos2){
            int cont=0;
            for(int i=pos1; i<permutationLength;i++){
                solution.setVariableValue(i, subset.get(cont));
                cont++;
            }
            for(int i=0; i<pos2+1;i++){
                solution.setVariableValue(i, subset.get(cont));
                cont++;
            }
         }else {
          int cont = 0;
          for (int i = pos1; i < (pos2 + 1); i++) {
            solution.setVariableValue(i, subset.get(cont));
            cont++;
          }
        }
      }
    }
  }
}
