/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 *
 * @author marcio
 */
public class MistureProblemOld {
    /**
     * This code test the tradicional way to encode model with Java API
     * It's used a sample of mixture problem to demonstrate that
     * @param args
     * @throws IloException 
     * @see MistureProblemNew
     */
    public static void main(String[] args) throws IloException {
        IloCplex cplex = new IloCplex();
        
        //conjunto dos ingredientes I = {0, 1, 2}   <->   {Osso, Soja, Peixe}
        int I = 3;
        //conjunto dos nutrientes   J = {0, 1}      <->   {Proteina, Calcio}
        int J = 2;
        
        //Ci : custo por kg de ingrediente i
        double C[] = {0.56, 0.81, 0.46};     
        //Aji: quantia do nutriente j por kg de ingrediente i
        double A[][] = {
            {0.2, 0.5, 0.4},
            {0.6, 0.4, 0.4},
        };
        //Bj: quantia minima de nutriente j na racao
        double B[] = {0.3, 0.5};
        
        //xi >= 0
        IloNumVar x[] = cplex.numVarArray(I, 0, Double.POSITIVE_INFINITY);
        
        //obj = sum_i{Ci * xi}
        IloNumExpr obj = null;
        for(int i=0; i<I; i++){
            if(obj==null){
                obj = cplex.prod(C[i],x[i]);
            }else{
                obj = cplex.sum(obj, cplex.prod(C[i],x[i]));
            }
        }
        cplex.addMinimize(obj);
        
        //for all j in J
        for(int j=0; j<J; j++){
            //sum_i{Aji * xi} >= Bj
            IloNumExpr expr1 = null;
            for(int i=0; i<I; i++){
                if(expr1==null){
                    expr1 = cplex.prod(A[j][i], x[i]);
                }else{
                    expr1 = cplex.sum(expr1, cplex.prod(A[j][i], x[i]));
                }
            }
            cplex.addGe(expr1, B[j]);
        }
        
        //sum_i{xi} = 1
        IloNumExpr expr2 = null;
        for(int i=0; i<I; i++){
            if(expr2==null){
                expr2 = x[i];
            }else{
                expr2 = cplex.sum(expr2, x[i]);
            }
        }
        cplex.addEq(expr2, 1);
        
        cplex.exportModel("model.lp");
        
        if(cplex.solve()){
            System.out.println(cplex.getStatus());
            System.out.println(cplex.getObjValue());
            for(int i=0; i<I; i++){
                System.out.printf("x[%d] = %f\n", i, cplex.getValue(x[i]));
            };
        }else{
            System.out.println(cplex.getStatus());
        }
    }
}
