/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples.non_linear;

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
public class EquationXY {
    /**
     * This code solve the minimum value of the non linear equation:
     * let x in R   / [-3, +9] 
     * let y in R   / [-4, +8] 
     * minimize x*y
     * 
     * optimal: x = 9, y=-4 with cost = -36
     * @param args
     * @throws IloException 
     */
    public static void main(String[] args) throws Exception {
        System.out.println("===================================================");
        System.out.println("This code solve the minimum value of the non linear equation:");
        System.out.println("    let x in R   / [-3, +8] ");
        System.out.println("    minimize x^4 - 7x^3 - 13x^2 + 79x + 84");
        System.out.println("    optimal: x = 5.7834 with cost = -129.273");
        System.out.println("===================================================");
        
        MINLP mip = new CPLEX(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        
        // x in R   / [-10, +10]    using 32 bits of precision
        nlVar x = new nlVar(mip, -3, +9, 32, "x");
        nlVar y = new nlVar(mip, -4, +8, 32, "x");
        
        mip.addMinimize(x.linerizedProd(y.value));//min x*y    x+y>=6
        //mip.addGe(x.value.sum(y.value), 6);
        
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
            System.out.printf("x = %8.4f\n", mip.getValue(x.value));
            System.out.printf("y = %8.4f\n", mip.getValue(y.value));
            
        }else{
            System.out.println("status = "+mip.getStatus());
        }
    }
    
}
