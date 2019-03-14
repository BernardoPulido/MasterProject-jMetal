package org.uma.jmetal.operator.impl.crossover;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.List;


/**
 * This class allows to apply a priority-based OX crossover operator using two parent solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class pOXCrossover implements
    CrossoverOperator<PermutationSolution<Integer>> {
  private double crossoverProbability = 1.0;
  private BoundedRandomGenerator<Integer> cuttingPointRandomGenerator ;
  private RandomGenerator<Double> crossoverRandomGenerator ;

  /**
   * Constructor
   */
  public pOXCrossover(double crossoverProbability) {
	  this(crossoverProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
  }

  /**
   * Constructor
   */
  public pOXCrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
	  this(crossoverProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
  }

  /**
   * Constructor
   */
  public pOXCrossover(double crossoverProbability, RandomGenerator<Double> crossoverRandomGenerator, BoundedRandomGenerator<Integer> cuttingPointRandomGenerator) {
    if ((crossoverProbability < 0) || (crossoverProbability > 1)) {
      throw new JMetalException("Crossover probability value invalid: " + crossoverProbability) ;
    }
    this.crossoverProbability = crossoverProbability;
    this.crossoverRandomGenerator = crossoverRandomGenerator ;
    this.cuttingPointRandomGenerator = cuttingPointRandomGenerator ;
  }

  /* Getters */
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  /* Setters */
  public void setCrossoverProbability(double crossoverProbability) {
    this.crossoverProbability = crossoverProbability;
  }

  /**
   * Executes the operation
   *
   * @param parents An object containing an array of two solutions
   */
  public List<PermutationSolution<Integer>> execute(List<PermutationSolution<Integer>> parents) {
    if (null == parents) {
      throw new JMetalException("Null parameter") ;
    } else if (parents.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + parents.size()) ;
    }

    return doCrossover(crossoverProbability, parents) ;
  }

  /**
   * Perform the crossover operation
   *
   * @param probability Crossover probability
   * @param parents     Parents
   * @return An array containing the two offspring
   */
  public List<PermutationSolution<Integer>> doCrossover(double probability, List<PermutationSolution<Integer>> parents) {
    List<PermutationSolution<Integer>> offspring = new ArrayList<>(2);

    offspring.add((PermutationSolution<Integer>) parents.get(0).copy()) ;
    offspring.add((PermutationSolution<Integer>) parents.get(1).copy()) ;

    int permutationLength = parents.get(0).getNumberOfVariables() ;

    if (crossoverRandomGenerator.getRandomValue() < probability) {
      int cuttingPoint1=0;
      int cuttingPoint2=0;

      int cuttingPoint3=0;
      int cuttingPoint4=0;

      // STEP 1: Get two cutting points
      for(int i=0; i<permutationLength;i++){
        if(parents.get(0).getVariableValue(i).equals(0)){
          cuttingPoint1=i;
        }
        else if(parents.get(0).getVariableValue(i).equals(permutationLength-1)){
          cuttingPoint2=i;
        }

        if(parents.get(1).getVariableValue(i).equals(0)){
          cuttingPoint3=i;
        }
        else if(parents.get(1).getVariableValue(i).equals(permutationLength-1)){
          cuttingPoint4=i;
        }
      }

      if (cuttingPoint1 > cuttingPoint2) {
        int swap;
        swap = cuttingPoint1;
        cuttingPoint1 = cuttingPoint2;
        cuttingPoint2 = swap;
      }
      if (cuttingPoint3 > cuttingPoint4) {
        int swap;
        swap = cuttingPoint3;
        cuttingPoint3 = cuttingPoint4;
        cuttingPoint4 = swap;
      }

      //STEP 2: Copiar seccion entre puntos de cruce a hijos
      List<Integer> subset1=new ArrayList<Integer>();
      List<Integer> subset2=new ArrayList<Integer>();
      for(int i=cuttingPoint1; i<=cuttingPoint2;i++){
        subset1.add(parents.get(0).getVariableValue(i));
      }
      for(int i=cuttingPoint3; i<=cuttingPoint4;i++){
        subset2.add(parents.get(1).getVariableValue(i));
      }

      //STEP 3: Copiar del otro padre elementos no copiados en hijos
      int point = cuttingPoint2+1;
      if(point==permutationLength){
        point=0;
      }
      int point_add = cuttingPoint2+1;
      if(point_add==permutationLength){
        point_add=0;
      }
      while(subset1.size() < permutationLength){
        if(!subset1.contains(parents.get(1).getVariableValue(point))){
          subset1.add(parents.get(1).getVariableValue(point));
          offspring.get(0).setVariableValue(point_add, parents.get(1).getVariableValue(point));
          point_add++;
          if(point_add==permutationLength){
            point_add=0;
          }
        }else{
          point++;
          if(point==permutationLength){
            point=0;
          }
        }
      }

      //STEP 4: Realizar mismo procedimiento para otro hijo
      int point2 = cuttingPoint4+1;
      if(point2==permutationLength){
        point2=0;
      }
      int point_add2 = cuttingPoint4+1;
      if(point_add2==permutationLength){
        point_add2=0;
      }
      while(subset2.size() < permutationLength){
        if(!subset2.contains(parents.get(0).getVariableValue(point2))){
          subset2.add(parents.get(0).getVariableValue(point2));
          offspring.get(1).setVariableValue(point_add2, parents.get(0).getVariableValue(point2));
          point_add2++;
          if(point_add2==permutationLength){
            point_add2=0;
          }
        }else{
          point2++;
          if(point2==permutationLength){
            point2=0;
          }
        }
      }

    }
    return offspring;
  }

  @Override
  public int getNumberOfRequiredParents() {
    return 2 ;
  }

  @Override
  public int getNumberOfGeneratedChildren() {
    return 2;
  }
}
