/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import minlp.MINLP;
import minlp.Var;
import minlp.glpk.GLPK;

/**
 *
 * @author Marcio
 */
public class Stocastic {
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        MINLP mip = new GLPK(); 
        
        Var x1 = mip.numVar(0, 10, "x1");
        Var x2 = mip.numVar(0, 10, "x2");
        
        mip.addMaximize(mip.sum(x1, x2));
        
        //P(x1>7||x2>8) <= 0.15
        //mip.addProbability(mip.sum(x1,x2), 0.15);
        
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
