/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import minlp.MINLP;
import minlp.Set;

/**
 *
 * @author marcio
 */
public class MistureProblemNew {
    /**
     * This code test a more easy way to encode model with Java API
     * It's used a sample of mixture problem to demonstrate that
     * @param args
     * @throws IloException 
     */
    public static void main(String[] args) throws IloException {
        MINLP cplex = new MINLP();
        
        //conjunto dos ingredientes I = {0, 1, 2}   <->   {Osso, Soja, Peixe}
        Set<Integer> I = cplex.range(3);
        //conjunto dos nutrientes   J = {0, 1}      <->   {Proteina, Calcio}
        Set<Integer> J = cplex.range(2);
        
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
        IloNumVar x[] = cplex.numVarArrayPos(I);
        
        //obj = sum_i{Ci * xi}
        IloNumExpr obj = cplex.sum(I, i -> cplex.prod(C[i],x[i]));
        
        cplex.addMinimize(obj);
        
        //for all j in J
        cplex.forAll(J, (j)->{
            //sum_i{Aji * xi} >= Bj
            cplex.addGe(cplex.sum(I, i -> cplex.prod(A[j][i], x[i])), B[j]);
        });
        
        //sum_i{xi} = 1
        cplex.addEq(cplex.sum(I, i-> x[i]), 1);
        
        cplex.exportModel("model.lp");
        
        if(cplex.solve()){
            System.out.println(cplex.getStatus());
            System.out.println(cplex.getObjValue());
            cplex.forAll(I, (i)->{
                System.out.printf("x[%d] = %f\n", i, cplex.getValue(x[i]));
            });
        }else{
            System.out.println(cplex.getStatus());
        }
    }
}
