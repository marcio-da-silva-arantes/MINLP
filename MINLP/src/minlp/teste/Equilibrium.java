/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.teste;

import java.util.Locale;
import minlp.samples.basic.*;
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
public class Equilibrium {
    /**
     * This code test a more easy way to encode model with Java API
     * It's used a sample of mixture problem to demonstrate that
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        MINLP mip = new CPLEX(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        
        Set I = mip.range(10);
        double ref[] = {
            80.45 ,
            67.05 ,
            53.64 ,
            144.89,
            68.00 ,
            98.47 ,
            53.64 ,
            93.63 ,
            165.88,
            20.00
        };     
        double carga[] = {30,32,20,16,32,20,20,40,16,9};
       
        int target = 50521836;//505218.36

        //xi >= 0
        Var x[] = mip.intVarArray(I, 0, Integer.MAX_VALUE, "x");
        
        Var mensal[] = mip.intVarArray(I, 0, Integer.MAX_VALUE, "x");
        
        
        Var error[] = mip.numVarArray(I, 0, Double.POSITIVE_INFINITY, "e");
        
        Var folga = mip.numVar("folga");
        
        Expr obj = mip.sum(I, i -> mip.prod(error[i],error[i]));
        
        mip.addMinimize(obj.sumProd(10000.0, folga));
        
        //for all j in J
        mip.forAll(I, (i)->{
            //sum_i{Aji * xi} >= Bj
            mip.addGe(error[i], mip.sum(-ref[i]*100, x[i]));
            mip.addGe(error[i], mip.sum(+ref[i]*100, mip.prod(-1,x[i])));
        });
        //sum_i{xi} = 1
        mip.addEq(mip.sum(I, i-> mip.prod(6, mensal[i])), mip.sum(target, folga));
        
        
        mip.forAll(I, (i)->{
            mip.addGe(mensal[i], mip.sum(-0.5, mip.prod(carga[i]*4.4,x[i])));
            mip.addLe(mensal[i], mip.sum(+0.5, mip.prod(carga[i]*4.4,x[i])));
        });
        //y = arend(x)
        //y > x - 0.5
        //y < x + 0.5
        //x = 1.6
        //y > 1.1
        //y < 2.1   -> 2
        //x = 1.4
        //y > 0.9
        //y < 1.8   -> 1
        //mip.exportModel("model.lp");
        
        mip.setTimeLimit(10); 
        
        if(mip.solve()){
            System.out.println(mip.getStatus());
            System.out.println(mip.getObjValue());
            System.out.println(mip.getValue(folga));
            System.out.println(mip.getValue(obj));
            
            
            mip.forAll(I, (i)->{
                System.out.printf("%f\n", mip.getValue(x[i])/100.0);
            });
        }else{
            System.out.println(mip.getStatus());
        }
        mip.delete();
    }
}
