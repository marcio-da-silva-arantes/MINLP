/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import minlp.Expr;
import minlp.MINLP;
import minlp.Var;
import minlp.cplex.CPLEX;
import minlp.glpk.GLPK;
import minlp.gurobi.Gurobi;
import minlp.nlVar;

/**
 *
 * @author marcio
 */
public class Equation4nd {
    /**
     * This code solve a non linear equation like:
     * let x in R   / [-3, +8] 
     * 
     * minimize x^4 - 7x^3 - 13x^2 + 79x + 84
     * 
     * roots: -3, -1, 4, 7
     * optimal: x = 5.7834 with cost = -129.273
     * @param args
     * @throws IloException 
     */
    public static void main(String[] args) throws Exception {
        MINLP mip = new Gurobi(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        
        // x in R   / [-10, +10]    using 32 bits of precision
        nlVar var = new nlVar(mip, -3, +8, 32, "x");
        
        Expr x1 = var.value;         //linear term: is directily the value of x
        Expr x2 = var.linerizedProd(x1); //quadratic term: is the product of x to the linear term
        Expr x3 = var.linerizedProd(x2); //cubic term: is the product of x to the quadratic term 
        Expr x4 = var.linerizedProd(x3); //4nd term: is the product of x to the cubic term 
        
        
        //build the main expression
        Expr expr = mip.constant(84);              //constant term
        expr = mip.sum(expr, mip.prod(+79, x1));   //linear term
        expr = mip.sum(expr, mip.prod(-13, x2));   //quadratic term
        expr = mip.sum(expr, mip.prod(-7, x3));    //cubic term
        expr = mip.sum(expr, mip.prod(1, x4));     //4nd term
        
        mip.addMinimize(expr);
        
        mip.exportModel("model.lp");
        //mip.setOut(null);
        //mip.setWarning(null);
        
        if(mip.solve()){
            for(int i=0; i<mip.getNcols(); i++){
                Var v = mip.getVar(i);
                System.out.printf("col[%3d] = %6.2f  | %s\n", i, mip.getValue(v), v.getName());
            }
            
            System.out.println(mip.getStatus());
            System.out.println(mip.getObjValue());
            System.out.printf("x = %8.4f\n", mip.getValue(var.value));
            System.out.printf("x^2 = %8.4f\n", mip.getValue(x2));
            System.out.printf("x^3 = %8.4f\n", mip.getValue(x3));
            System.out.printf("x^4 = %8.4f\n", mip.getValue(x4));
            System.out.printf("expr = %8.4f\n", mip.getValue(expr));
            
        }else{
            System.out.println("status = "+mip.getStatus());
        }
    }
    
}
