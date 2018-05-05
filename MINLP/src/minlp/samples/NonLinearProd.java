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
import minlp.nlVar;

/**
 *
 * @author marcio
 */
public class NonLinearProd {
    /**
     * This code test the multiplication of continhous variables:
     * let x in R   / [-4, +3] 
     * let y in R   / [-5.5, +6.5]
     * 
     * v = x*y 
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        MINLP mip = new CPLEX();     //do not work for GLPK yet
        // x in R   / [-4, +3] 
        Var x = mip.numVar(-4, 3, "x");
        // y in R   / [-5.5, +6.5]
        nlVar y = new nlVar(mip, -5.5, 6.5, 3, "y");
        // v = x*y
        Expr v = y.linerizedProd(x);
        
        //------------------[ tests fix some thing ]-----------------
        x.setLB(-3);
        x.setUB(+2);
        
        mip.addMaximize(v);
        
        mip.exportModel("model.lp");
        
        if(mip.solve()){
            System.out.println("status = "+mip.getStatus());
            System.out.println("objective = "+mip.getObjValue());
            System.out.println("x = "+mip.getValue(x));
            System.out.println("y = "+mip.getValue(y.value));
            System.out.println("v = "+mip.getValue(v));
            
            
            System.out.println("cols = "+mip.getNcols());
            System.out.println("bin = "+mip.getNbinVars());
            System.out.println("int = "+mip.getNintVars());
            System.out.println("rows = "+mip.getNrows());
            
            for(int i=0; i<mip.getNcols(); i++){
                Var var = mip.getVar(i);
                System.out.println(var.getName());
                System.out.printf("col[%d] = %6.2f  %s\n", i, mip.getValue(var), var.getName());
            }
        }else{
            System.out.println("status = "+mip.getStatus());
        }
    }
    
}
