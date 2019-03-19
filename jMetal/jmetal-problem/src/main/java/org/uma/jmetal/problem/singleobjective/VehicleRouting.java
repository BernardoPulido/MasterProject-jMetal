package org.uma.jmetal.problem.singleobjective;


import org.uma.jmetal.problem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.ArrayList;

/**
 * Class representing a three-objective VRP (Vehicle Routing Problem) problem, in a mono-objective version (weight sum).
 */
@SuppressWarnings("serial")
public class VehicleRouting extends AbstractIntegerPermutationProblem {
  protected int         numberOfCities ;
  protected double [][] distanceMatrix ;
  protected double [][] costMatrix;
  protected double [][] combustibleMatrix;
  protected int [][] adjacenciasMatrix;
  protected int [] init_node;
  protected int [] destine_node;
  protected int cantidad_vehiculos = 0;
  protected double min1 = 195.0;
  protected double max1 = 4470.0;
  protected double min2 = 4.0;
  protected double max2 = 28.0;
  protected double min3 = 0.0;
  protected double max3 = 239.0;

  public VehicleRouting(String distanceFile) throws IOException {
    readProblem(distanceFile);
    llenarConCeros();
    //imprimirMatrices();
    setNumberOfVariables(numberOfCities);
    setNumberOfObjectives(1);
    setName("VRP");
  }

  public VehicleRouting(String distanceFile, String costFile) throws IOException {
    distanceMatrix = readProblemGrafoCompleto(distanceFile) ;
    costMatrix     = readProblemGrafoCompleto(costFile);
    this.adjacenciasMatrix = new int[numberOfCities][numberOfCities];
    llenarConUnos();

    setNumberOfVariables(numberOfCities);
    setNumberOfObjectives(2);
    setName("VRP");
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
  private void llenarConUnos() {
    for(int i=0; i<this.numberOfCities;i++){
      for(int j=0; j<this.numberOfCities;j++){

          adjacenciasMatrix[i][j]=1;

      }
    }
  }

  public void imprimirMatrices(){

    for(int i=0; i<this.distanceMatrix.length;i++){
      for(int j=0; j<this.distanceMatrix.length;j++){
          System.out.print(""+this.distanceMatrix[i][j]+" ");
      }
      System.out.println();
    }
  }

  public void evaluate(PermutationSolution<Integer> solution){

        //TO DO: Ciclo que recorra todos los vehículos, ya que se cuente con una representación que los soporte
        double fitness1   ;
        double fitness2   ;
        double fitness3   ;

        fitness1 = 0.0 ;
        fitness2 = 0.0 ;
        fitness3 = 0.0 ;

        ArrayList<Integer> nodes_visitados = new ArrayList<Integer>();
        ArrayList<Integer> nodes_no_visitados = new ArrayList<Integer>();
        int current_node = init_node[0];

        while(current_node!=destine_node[0]){
          int max=-1;
          int pos_max=current_node;
          for(int i=0; i<this.numberOfCities;i++){
            if(adjacenciasMatrix[current_node][i]==1){
              if((max < solution.getVariableValue(i)) && (nodes_visitados.indexOf(i)==-1) && (nodes_no_visitados.indexOf(i)==-1)){
                max= solution.getVariableValue(i);
                pos_max = i;
              }
            }
          }
          if(pos_max!=current_node){
            fitness1 +=distanceMatrix[current_node][pos_max];
            fitness2 +=costMatrix[current_node][pos_max];
            fitness3 +=combustibleMatrix[current_node][pos_max];
            nodes_visitados.add(current_node);
            current_node=pos_max;
          }else{
            //Recursión en decodificación de cromosoma
            if(nodes_visitados.indexOf(current_node)==-1){
              fitness1-=distanceMatrix[nodes_visitados.get(nodes_visitados.size()-1)][current_node];
              fitness2-=costMatrix[nodes_visitados.get(nodes_visitados.size()-1)][current_node];
              fitness3-=combustibleMatrix[nodes_visitados.get(nodes_visitados.size()-1)][current_node];
              nodes_no_visitados.add(current_node);
              current_node = nodes_visitados.get(nodes_visitados.size()-1);
            }else{
              nodes_no_visitados.add(current_node);
              int por_eliminar = nodes_visitados.get(nodes_visitados.size()-1);
              nodes_visitados.removeIf(i -> i == por_eliminar);
              fitness1-=distanceMatrix[current_node][nodes_visitados.get(nodes_visitados.size()-1)];
              fitness2-=costMatrix[current_node][nodes_visitados.get(nodes_visitados.size()-1)];
              fitness3-=combustibleMatrix[current_node][nodes_visitados.get(nodes_visitados.size()-1)];
              current_node = nodes_visitados.get(nodes_visitados.size()-1);
            }
          }
        }

        fitness1 = (fitness1 - min1)/(max1-min1);
        fitness2 = (fitness2 - min2)/(max2-min2);
        fitness3 = (fitness3 - min3)/(max3-min3);
        double fitness_all = (fitness1*(1.0/3.0)) + (fitness2*(1.0/3.0)) + (fitness3*(1.0/3.0));

        solution.setObjective(0, fitness_all);
  }

  /**
   * Esta función genera una matriz de costos, dadas las coordanadas de cada nodo.
   * Utiliza una matriz de adyacencias llena (solo uno's)
   */
  private double [][] readProblemGrafoCompleto(String file) throws IOException {
    double [][] matrix = null;


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

      matrix = new double[numberOfCities][numberOfCities] ;


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

      double [] c = new double[2*numberOfCities] ;

      for (int i = 0; i < numberOfCities; i++) {
        token.nextToken() ;
        int j = (int)token.nval ;

        token.nextToken() ;
        c[2*(j-1)] = token.nval ;
        token.nextToken() ;
        c[2*(j-1)+1] = token.nval ;
      } // for

      double dist ;
      for (int k = 0; k < numberOfCities; k++) {
        matrix[k][k] = 0;
        for (int j = k + 1; j < numberOfCities; j++) {
          dist = Math.sqrt(Math.pow((c[k*2]-c[j*2]),2.0) +
                  Math.pow((c[k*2+1]-c[j*2+1]), 2));
          dist = (int)(dist + .5);
          matrix[k][j] = dist;
          matrix[j][k] = dist;
        }
      }
    } catch (Exception e) {
      new JMetalException("TSP.readProblem(): error when reading data file " + e);
    }
    return matrix;
  }


  private void readProblem(String file) throws IOException {
    int [][] matrix = null;
    double [][] matrix_penalizacion = null;
    double [][] matrix_distancias = null;
    double [][] matrix_combustible = null;

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
      matrix_combustible = new double[numberOfCities][numberOfCities] ;

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

        token.nextToken();
        int combustible = (int)token.nval;

        //if(matrix[j-1][k-1]!=1){
          matrix[j-1][k-1] = 1;
          matrix_distancias[j-1][k-1]= distancia;
          matrix_penalizacion[j-1][k-1]= penalizacion;
          matrix_combustible[j-1][k-1]= combustible;
        //}

        //if(matrix[k-1][j-1]!=1){
          matrix[k-1][j-1] = 1;
          matrix_distancias[k-1][j-1] = distancia;
          matrix_penalizacion[k-1][j-1] = penalizacion;
          matrix_combustible[k-1][j-1] = combustible;
        //}
      }

      // Buscar VEHICLES
      found = false ;
      token.nextToken();
      while(!found) {
        if ((token.sval != null) &&
                ((token.sval.compareTo("VEHICLES") == 0)))
          found = true ;
        else
          token.nextToken() ;
      }
      token.nextToken() ;
      token.nextToken() ;

      this.cantidad_vehiculos =  (int)token.nval ;

      init_node = new int[cantidad_vehiculos];
      destine_node = new int[cantidad_vehiculos];

      //Almacenar nodos de inicio y destino por vehículo
      for(int i=0; i<this.cantidad_vehiculos; i++){
        token.nextToken() ;
        init_node[i] = (int)token.nval;
        token.nextToken() ;
        destine_node[i] = (int)token.nval;
      }

    } catch (Exception e) {
      new JMetalException("TSP.readProblem(): error when reading data file " + e);
    }
    this.adjacenciasMatrix = matrix;
    this.distanceMatrix=matrix_distancias;
    this.costMatrix=matrix_penalizacion;
    this.combustibleMatrix=matrix_combustible;
  }

  @Override public int getPermutationLength() {
    return numberOfCities ;
  }
}
