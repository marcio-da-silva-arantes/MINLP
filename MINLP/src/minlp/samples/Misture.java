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
import minlp.gurobi.Gurobi;

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
        MINLP mip = new CPLEX(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        //conjunto dos ingredientes I = {0, 1, 2}   <->   {Osso, Soja, Peixe}
        Set I = mip.range(3);
        //conjunto dos nutrientes   J = {0, 1}      <->   {Proteina, Calcio}
        Set J = mip.range(2);
        
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
        Var x[] = mip.numVarArray(I, 0, Double.POSITIVE_INFINITY, "x");
        
        //obj = sum_i{Ci * xi}
        Expr obj = mip.sum(I, i -> mip.prod(C[i],x[i]));
        
        //System.out.println(obj);
        
        mip.addMinimize(obj);
        
        //for all j in J
        mip.forAll(J, (j)->{
            //sum_i{Aji * xi} >= Bj
            mip.addGe(mip.sum(I, i -> mip.prod(A[j][i], x[i])), B[j]);
        });
        //sum_i{xi} = 1
        mip.addEq(mip.sum(I, i-> x[i]), 1);
        
        mip.exportModel("model.lp");
        
        if(mip.solve()){
            System.out.println(mip.getStatus());
            System.out.println(mip.getObjValue());
            mip.forAll(I, (i)->{
                System.out.printf("x[%d] = %f\n", i, mip.getValue(x[i]));
            });
        }else{
            System.out.println(mip.getStatus());
        }
        mip.delete();
    }
}
