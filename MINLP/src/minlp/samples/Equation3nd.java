/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import minlp.Expr;
import minlp.MINLP;
import minlp.cplex.CPLEX;
import minlp.glpk.GLPK;
import minlp.gurobi.Gurobi;
import minlp.nlVar;

/**
 *
 * @author marcio
 */
public class Equation3nd {
    /**
     * This code find the root by solve the non linear equation:
     * let x in R   / [-10, +10] 
     * 
     * subject to:
     * 
     * x^3 - 6x^2 + 11x - 6 = 0
     * 
     * 
     * @param args
     * @throws IloException 
     */
    public static void main(String[] args) throws Exception {
        System.out.println("===================================================");
        System.out.println("This code find the root by solve the non linear equation:");
        System.out.println("    let x in R   / [-10, +10]");
        System.out.println("    subject to:");
        System.out.println("    x^3 - 6x^2 + 11x - 6 = 0");
        System.out.println("    optimal: x in {1,2,3}");
        System.out.println("===================================================");
        
        MINLP mip = new CPLEX(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        
        // x in R   / [-10, +10]    using 32 bits of precision
        nlVar var = new nlVar(mip, -10, +10, 32, "x");
        
        Expr x1 = var.value;         //linear term: is directily the value of x
        Expr x2 = var.linerizedProd(x1); //quadratic term: is the product of x to the linear term
        Expr x3 = var.linerizedProd(x2); //cubic term: is the product of x to the quadratic term 
        
        
        //build the main expression
        Expr expr = mip.constant(-6);               //constant term
        expr = mip.sum(expr, mip.prod(+11, x1));   //linear term
        expr = mip.sum(expr, mip.prod(-6, x2));    //quadratic term
        expr = mip.sum(expr, mip.prod(+1, x3));    //cubic term
        
        
        //cplex.addRange(-0.01, expr, 0.01);
        mip.addEq(expr, 0);
        
        mip.addMinimize(var.value);   //find the smaller x first
        
        mip.setOut(null);
        mip.setWarning(null);
        
        //finding the three solutions if exists
        for(int i=0; i<3; i++){
            if(mip.solve()){
                double val_x = mip.getValue(var.value);
                
                System.out.printf("x%d = %1.2f  -> error = %g\n", i+1, val_x, mip.getValue(expr));
                
                mip.addGe(var.value, val_x+0.01);   //set to ignore this solution and find next x
            }else{
                System.out.printf("x%d = ????  -> status = %s\n", i+1, mip.getStatus());
            }
        }
    }
    
}
