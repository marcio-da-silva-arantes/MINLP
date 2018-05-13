/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import minlp.MINLP;
import minlp.Var;
import minlp.cplex.CPLEX;
import minlp.glpk.GLPK;

/**
 *
 * @author marcio
 */
public class TestGLPKFail {

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        MINLP mip = new GLPK();     //do not work for GLPK yet
        Var x1 = mip.boolVar("x1");
        Var x2 = mip.boolVar("x2");
        
        
 
        mip.addMaximize(mip.sum(x1, x2));
        
        mip.addLe(mip.sum(x1,x2), 1.5);
        
        mip.exportModel("model.lp");
        
        if(mip.solve()){
            System.out.println("status = "+mip.getStatus());
            System.out.println("objective = "+mip.getObjValue());
            System.out.println("x1 = "+mip.getValue(x1));
            System.out.println("x2 = "+mip.getValue(x2));
            
            
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
