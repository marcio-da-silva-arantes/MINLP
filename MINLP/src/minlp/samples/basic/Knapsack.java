/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples.basic;

import minlp.Expr;
import minlp.MINLP;
import minlp.Set;
import minlp.Var;
import minlp.cplex.CPLEX;
import minlp.glpk.GLPK;
import minlp.gurobi.Gurobi;

/**
 * Sample of Knapsack binary problem
 * @see <a href="https://en.wikipedia.org/wiki/Knapsack_problem">https://en.wikipedia.org/wiki/Knapsack_problem</a>
 * @author Marcio da Silva Arantes
 */
public class Knapsack {
    
    public static void main(String[] args) throws Exception {
        
        MINLP mip = new GLPK(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        //N: set of itens {0, 1, 2, 3, 4, 5}
        Set N = mip.range(5);
        
        //vi: value of each item i
        double v[] = {1.0, 2.0, 2.0, 10.0, 4.0};     
        //wi: weight of each item i
        double w[] = {1.0, 1.0, 2.0, 4.0, 12.0};
        //W: maximum weight capacity
        double W = 15;
        
        //xi >= 0
        Var x[] = mip.boolVarArray(N, "x");
        //Var x[] = mip.numVarArray(N, 0, 1, "x");
        
        //obj = sum_i{vi * xi}
        Expr obj = mip.sum(N, i -> mip.prod(v[i],x[i]));
        mip.addMaximize(obj);
        
        //sum_i{vi*xi} <= W
        mip.addLe(mip.sum(N, i-> mip.prod(w[i], x[i])), W);
        
        mip.exportModel("model.lp");
        
        if(mip.solve()){
            System.out.printf("status = %s\n", mip.getStatus());
            System.out.printf("cost = %f\n", mip.getObjValue());
            mip.forAll(N, (i)->{
                System.out.printf("x[%d] = %f\n", i, mip.getValue(x[i]));
            });
            System.out.printf("weigth = %f\n", mip.getValue(mip.sum(N, i->mip.prod(w[i], x[i]))));
        }else{
            System.out.println(mip.getStatus());
        }
        mip.delete();
    }
}
