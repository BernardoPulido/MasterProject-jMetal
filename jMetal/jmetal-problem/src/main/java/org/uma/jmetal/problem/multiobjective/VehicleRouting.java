package org.uma.jmetal.problem.multiobjective;


import org.uma.jmetal.problem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.ArrayList;

/**
 * Class representing a bi-objective TSP (Traveling Salesman Problem) problem.
 * It accepts data files from TSPLIB:
 *   http://www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/tsp/
 */
@SuppressWarnings("serial")
public class VehicleRouting extends AbstractIntegerPermutationProblem {
  protected int         numberOfCities ;
  protected double [][] distanceMatrix ;
  protected double [][] costMatrix;
  protected int [][] adjacenciasMatrix;
  protected int init_node = 0;
  protected int destine_node = 24;

  public VehicleRouting(String distanceFile) throws IOException {
    readProblem(distanceFile);
    llenarConCeros();
    //imprimirMatrices();
    setNumberOfVariables(numberOfCities);
    setNumberOfObjectives(2);
    setName("MultiobjectiveVRP");
  }

  private void llenarConCeros() {
    for(int i=0; i<this.numberOfCities;i++){
      for(int j=0; j<this.numberOfCities;j++){
          if(adjacenciasMatrix[i][j]!=1){
            adjacenciasMatrix[i][j]=0;
          }
      }
    }
  }

  public void imprimirMatrices(){
    for(int i=0; i<this.distanceMatrix.length;i++){
      for(int j=0; j<this.distanceMatrix.length;j++){
          System.out.print(""+this.adjacenciasMatrix[i][j]+" ");
      }
      System.out.println();
    }
  }

  public void evaluate(PermutationSolution<Integer> solution){

    double fitness1   ;
    double fitness2   ;

    fitness1 = 0.0 ;
    fitness2 = 0.0 ;

    ArrayList<Integer> nodes_visitados = new ArrayList<Integer>();

    for(int i=0; i<this.numberOfCities;i++){
      System.out.print(solution.getVariableValue(i)+" ");
    }
    System.out.println();

    int current_node = init_node;

    while(current_node!=destine_node){

      System.out.println(current_node+" Current node");
      int max=-1;
      int pos_max=current_node;
      for(int i=0; i<this.numberOfCities;i++){
        if(adjacenciasMatrix[current_node][i]==1){
            if((max < solution.getVariableValue(i)) && (nodes_visitados.indexOf(i)==-1)){
               max= solution.getVariableValue(i);
               pos_max = i;
            }
        }
      }
      if(pos_max!=current_node){
        fitness1 +=distanceMatrix[current_node][pos_max];
        fitness2 +=costMatrix[current_node][pos_max];
        nodes_visitados.add(current_node);
        current_node=pos_max;
      }else{
        System.err.println("El grafo no es conexo");
        //System.exit(0);
        fitness1+=1000;
        fitness2+=1000;
        current_node=destine_node;
      }
    }

    solution.setObjective(0, fitness1);
    solution.setObjective(1, fitness2);
  }

  private void readProblem(String file) throws IOException {
    int [][] matrix = null;
    double [][] matrix_penalizacion = null;
    double [][] matrix_distancias = null;

    InputStream in = getClass().getResourceAsStream(file);
    InputStreamReader isr = new InputStreamReader(in);
    BufferedReader br = new BufferedReader(isr);

    StreamTokenizer token = new StreamTokenizer(br);
    try {
      boolean found ;
      found = false ;

      token.nextToken();
      while(!found) {
        if ((token.sval != null) && ((token.sval.compareTo("DIMENSION") == 0)))
          found = true ;
        else
          token.nextToken() ;
      }

      token.nextToken() ;
      token.nextToken() ;

      numberOfCities =  (int)token.nval ;

      matrix = new int[numberOfCities][numberOfCities] ;
      matrix_penalizacion = new double[numberOfCities][numberOfCities] ;
      matrix_distancias = new double[numberOfCities][numberOfCities] ;

      //Find the string ARISTAS
      found = false ;
      token.nextToken();
      while(!found) {
        if ((token.sval != null) && ((token.sval.compareTo("ARISTAS") == 0)))
          found = true ;
        else
          token.nextToken() ;
      }
      token.nextToken() ;
      token.nextToken() ;

      int edges = (int)token.nval;

      // Find the string SECTION  
      found = false ;
      token.nextToken();
      while(!found) {
        if ((token.sval != null) &&
            ((token.sval.compareTo("SECTION") == 0)))
          found = true ;
        else
          token.nextToken() ;
      }


      for (int i = 0; i < edges; i++) {
        token.nextToken();
        int j = (int)token.nval;

        token.nextToken();
        int k = (int)token.nval;

        token.nextToken();
        int distancia = (int)token.nval;

        token.nextToken();
        int penalizacion = (int)token.nval;

        matrix[j-1][k-1] = 1;
        matrix[k-1][j-1] = 1;

        matrix_distancias[j-1][k-1]= distancia;
        matrix_distancias[k-1][j-1] = distancia;

        matrix_penalizacion[j-1][k-1]= penalizacion;
        matrix_penalizacion[k-1][j-1] = penalizacion;

      }
    } catch (Exception e) {
      new JMetalException("TSP.readProblem(): error when reading data file " + e);
    }
    this.adjacenciasMatrix = matrix;
    this.distanceMatrix=matrix_distancias;
    this.costMatrix=matrix_penalizacion;
  }

  @Override public int getPermutationLength() {
    return numberOfCities ;
  }
}
