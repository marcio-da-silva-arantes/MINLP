/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples.specific.global_optimization;

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
public class AluffiPentini1985 {
    /**
     * <pre>
     * function Aluffi-Pentini's Problem (AP) (Aluffi-Pentini et al., 1985)
     * 
     * let x1, x2 in R    / [-10, +10]
     * 
     * minimize 0.25*x1<sup>4</sup> - 0.5*x1<sup>2</sup> + 0.1*x1 + 0.5*x2<sup>2</sup>
     * 
     * optimal: x1 = 1.0465, x2 = 0 with cost = -0.3523 
     * 
     * source : M. Ali, C. Khompatraporn, Z. Zabinsky, 2005, 
     *          A Numerical Evaluation of Several Stochastic Algorithms on 
     *          Selected Continuous Global Optimization Test Problems, 
     *          Jornal of Global Optimization, pp. 635-672.
     * </pre>
     */
    public static void main(String[] args) throws Exception {
        System.out.println("===================================================");
        System.out.println("This code solve the minimum value of the Aluffi-Pentini's Problem (AP):");
        System.out.println("    let x1, x2 in R   / [-10, +10] ");
        System.out.println("    minimize 0.25*x1^4 - 0.5*x1^2 + 0.1*x1 + 0.5*x2^2");
        System.out.println("    optimal: x1 = 1.0465, x2 = 0 with cost = -0.3523 ");
        System.out.println("===================================================");
        
        MINLP mip = new Gurobi(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        
        // x1,x2 in R   / [-10, +10]    using 32 bits of precision
        nlVar x1 = new nlVar(mip, -10, +10, 16, "x1");
        nlVar x2 = new nlVar(mip, -10, +10, 16, "x2");
        
        Expr p1x1 = x1.value;
        Expr p2x1 = x1.linerizedProd(p1x1);
        Expr p3x1 = x1.linerizedProd(p2x1);
        Expr p4x1 = x1.linerizedProd(p3x1);
        
        Expr p1x2 = x2.value;
        Expr p2x2 = x2.linerizedProd(p1x2);
        
        mip.addMinimize(p4x1.prod(0.25).sumProd(-0.5, p2x1).sumProd(0.1, p1x1).sumProd(0.5, p2x2)); 
        
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
            System.out.printf("x1 = %8.6f\n", mip.getValue(x1.value));
            System.out.printf("x2 = %8.6f\n", mip.getValue(x2.value));
        }else{
            System.out.println("status = "+mip.getStatus());
        }
    }
    
}
