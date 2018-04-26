/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import minlp.Cont;
import minlp.Cont2;
import minlp.MINLP;

/**
 *
 * @author marcio
 */
public class Equation3nd {
    /**
     * This code solve a non linear equation like:
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
    public static void main(String[] args) throws IloException {
        MINLP cplex = new MINLP(1e4);
        
        // x in R   / [-10, +10]    using 16 bits of precision
        Cont2 var = new Cont2(cplex, -10, +10, 32);
        
        IloNumExpr x1 = var.val;         //linear term: is directily the value of x
        IloNumExpr x2 = var.addProd(x1); //quadratic term: is the product of x to the linear term
        IloNumExpr x3 = var.addProd(x2); //cubic term: is the product of x to the quadratic term 
        
        
        //build the main expression
        IloNumExpr expr = cplex.constant(-6);               //constant term
        expr = cplex.sum(expr, cplex.prod(+11, x1));   //linear term
        expr = cplex.sum(expr, cplex.prod(-6, x2));    //quadratic term
        expr = cplex.sum(expr, cplex.prod(+1, x3));    //cubic term
        
        
        //cplex.addRange(-0.01, expr, 0.01);
        cplex.addEq(expr, 0);
        
        cplex.addMinimize(var.val);   //find the smaller x first
        
        cplex.setOut(null);
        cplex.setWarning(null);
        
        //finding the three solutions if exists
        for(int i=0; i<3; i++){
            if(cplex.solve()){
                double val_x = cplex.getValue(var.val);
                
                System.out.printf("x%d = %1.2f  -> error = %g\n", i+1, val_x, cplex.getValue(expr));
                
                cplex.addGe(var.val, val_x+0.01);   //set to ignore this solution and find next x
            }else{
                System.out.printf("x%d = ????  -> status = %s\n", i+1, cplex.getStatus());
            }
            
        
        }
        
            
    }
    
}
