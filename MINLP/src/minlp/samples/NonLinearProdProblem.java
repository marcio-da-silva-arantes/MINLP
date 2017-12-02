/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import minlp.Cont;
import minlp.MINLP;

/**
 *
 * @author marcio
 */
public class NonLinearProdProblem {
    /**
     * This code test the multiplication of continhous variables:
     * let x in R   / [-4, +3] 
     * let y in R   / [-5.5, +6.5]
     * 
     * v = x*y 
     * 
     * @param args
     * @throws IloException 
     */
    public static void main(String[] args) throws IloException {
        // TODO code application logic here
        MINLP cplex = new MINLP();
        // x in R   / [-4, +3] 
        IloNumVar x = cplex.numVar(-4, 3);
        // y in R   / [-5.5, +6.5]
        Cont y = new Cont(cplex, -5.5, 6.5, 10);
        // v = x*y
        IloNumExpr v = y.addProd(x);
        
        //------------------[ tests fix some thing ]-----------------
        x.setLB(-3);
        x.setUB(+2);
        cplex.addMaximize(v);
        
        if(cplex.solve()){
            System.out.println("status = "+cplex.getStatus());
            System.out.println("objective = "+cplex.getObjValue());
            System.out.println("x = "+cplex.getValue(x));
            System.out.println("y = "+cplex.getValue(y.val));
            System.out.println("v = "+cplex.getValue(v));
        }else{
            System.out.println("status = "+cplex.getStatus());
        }
    }
    
}
