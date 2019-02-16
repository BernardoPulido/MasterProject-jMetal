package org.uma.jmetal.runner.bernardo;

import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class UtilidadesRelativeCosts {
    private int conexiones_por_nodo = 4;
    private int cantidad_de_nodos_deseado = 300;
    private int cantidad_nodos;
    private double distancia_minima = 0;
    private double distancia_maxima = 0;
    private double penalizacion_minima = 0;
    private double penalizacion_maxima = 0;
    private double consumo_minimo = 0;
    private double consumo_maximo = 0;
    private ArrayList<ArrayList<Double>> datos;
    private double factor =1;
    private String file = "/experiments/ol_temp.txt";

    /**
     * Método para generar un grafo fuertamente conexo, esto a partir de un conjunto de datos reales
     */
    public void GenerarDataSet(){
        Random r = new Random();
        int contador =0;
        int contador2 =0;
        datos = new ArrayList<ArrayList<Double>>();
        leerArchivo();
        distancia_maxima = obtenerMaximos(2);
        distancia_minima = obtenerMinimos(2);
        penalizacion_maxima = obtenerMaximos(3);
        penalizacion_minima = obtenerMinimos(3);

        for(int i=0; i<datos.size();i++){
            if(datos.get(i).get(0)<=cantidad_de_nodos_deseado && datos.get(i).get(1) <= cantidad_de_nodos_deseado && datos.get(i).get(0)>0 && datos.get(i).get(1)>0){
                System.out.println(Math.round(datos.get(i).get(0))+" "+Math.round(datos.get(i).get(1))+" "+datos.get(i).get(2)+" "+datos.get(i).get(3));
                contador++;
            }
        }

        /*
        //Inlcuir arcos para generar grafo fuertamente conexo
        for(int i=1; i<=this.cantidad_de_nodos_deseado;i++){
            for(int j=0;j<this.conexiones_por_nodo;j++){
                double dist = distancia_minima + (distancia_maxima - distancia_minima) * r.nextDouble();
                double pen = penalizacion_minima + (penalizacion_maxima - penalizacion_minima) * r.nextDouble();
                int destino = (int) (Math.random() * cantidad_de_nodos_deseado) + 1;
                System.out.println(i+" "+destino+" "+String.format("%.2f", dist)+" "+String.format("%.2f", pen));
                contador2++;
            }
        }
*/
        System.out.println();
        System.out.println("EL archivo contiene: "+contador);
        System.out.println("Se generaron: "+contador2);
        int total = contador+contador2;
        System.out.println("Total: "+total);
    }

    /**
     * valor = 2 distnacia
     * valor = 3 penalizacion
     * valor = 4 consumo
     * @param valor
     */
    public double obtenerMaximos(int valor){
        double maximo =0;
        for(int i=0; i<datos.size();i++){
            if(datos.get(i).get(valor)>maximo){
                maximo = datos.get(i).get(valor);
            }
        }
        return maximo;
    }

    public double obtenerMinimos(int valor){
        double minimo =Double.POSITIVE_INFINITY;
        for(int i=0; i<datos.size();i++){
            if(datos.get(i).get(valor)<minimo){
                minimo = datos.get(i).get(valor);
            }
        }
        return minimo;
    }

    public void leerArchivo(){

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

            cantidad_nodos =  (int)token.nval ;

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
                //int penalizacion = (int) (Math.random() * 100) + 1;

                ArrayList<Double> temporal = new ArrayList<Double>();
                temporal.add((double)j);
                temporal.add((double)k);
                //temporal.add((double)distancia);
                temporal.add((double)(distancia*factor+distancia*penalizacion));
                temporal.add((double)penalizacion);
                datos.add(temporal);
            }

        } catch (Exception e) {
            new JMetalException("TSP.readProblem(): error when reading data file " + e);
        }
    }

    public static void main(String[] args) throws JMetalException, FileNotFoundException, IOException {
            UtilidadesRelativeCosts u = new UtilidadesRelativeCosts();
            u.GenerarDataSet();
    }
}
