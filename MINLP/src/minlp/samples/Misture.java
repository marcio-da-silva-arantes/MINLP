/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import minlp.Expr;
import minlp.MINLP;
import minlp.Set;
import minlp.Var;
import minlp.cplex.CPLEX;
import minlp.glpk.GLPK;

/**
 *
 * @author marcio
 */
public class Misture {
    /**
     * This code test a more easy way to encode model with Java API
     * It's used a sample of mixture problem to demonstrate that
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        MINLP lp = new GLPK(); // or new CPLEX();
        //conjunto dos ingredientes I = {0, 1, 2}   <->   {Osso, Soja, Peixe}
        Set I = lp.range(3);
        //conjunto dos nutrientes   J = {0, 1}      <->   {Proteina, Calcio}
        Set J = lp.range(2);
        
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
        Var x[] = lp.numVarArray(I, 0, Double.POSITIVE_INFINITY, "x");
        
        //obj = sum_i{Ci * xi}
        Expr obj = lp.sum(I, i -> lp.prod(C[i],x[i]));
        
        //System.out.println(obj);
        
        lp.addMinimize(obj);
        
        //for all j in J
        lp.forAll(J, (j)->{
            //sum_i{Aji * xi} >= Bj
            lp.addGe(lp.sum(I, i -> lp.prod(A[j][i], x[i])), B[j]);
        });
        //sum_i{xi} = 1
        lp.addEq(lp.sum(I, i-> x[i]), 1);
        
        lp.exportModel("model.lp");
        
        if(lp.solve()){
            System.out.println(lp.getStatus());
            System.out.println(lp.getObjValue());
            lp.forAll(I, (i)->{
                System.out.printf("x[%d] = %f\n", i, lp.getValue(x[i]));
            });
        }else{
            System.out.println(lp.getStatus());
        }
        lp.delete();
    }
}
