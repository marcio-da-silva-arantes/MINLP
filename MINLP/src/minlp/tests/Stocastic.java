/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.tests;

import minlp.MINLP;
import minlp.Var;
import minlp.cplex.CPLEX;
import minlp.glpk.GLPK;
import minlp.stocastic.Distribution;
import static minlp.stocastic.Distribution.normal;

/**
 *
 * @author Marcio
 */
public class Stocastic {
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        MINLP mip = new CPLEX(); 
        
        
        
        Var x1 = mip.numVar(0, 10, "x1");
        Var x2 = mip.numVar(0, 10, "x2");
        
        Var p1 = mip.numVar(0, 1, "p1");
        Var p2 = mip.numVar(0, 1, "p2");
        
        
        mip.addMinimize(mip.sum(mip.prod(x1,x1), mip.prod(x2, x2)));
        mip.addLe(mip.sum(p1,p2), 0.1);
        
        //uma forma diferente para codificar 
        //x1.prod(x1).sum(x2.prod(x2)).addMinimize();
        //p1.sum(p2).le(0.1);
           
        
        mip.addEq(mip.sum(0.5, mip.prod(-0.1, x1)), p1);
        mip.addEq(mip.sum(0.5, mip.prod(-0.1, x2)), p2);
        
        
//        rVar y1 = mip.uniformVar(0, 10);
//        rVar y2 = mip.uniformVar(0, 10);
//        
//        mip.addMinimize(mip.sum(y1, y2));
//        
//        mip.addProbOr(mip.probLe(y1, 0), mip.addLe(y2, 0)).le(0.1);
        //y1.ple(0).or(y2.ple(0)).le(0.1);
        
        //r1 = N(x1, s1)
        //r2 = N(x2, s2)
        //P(r1>=7||r2>=8) <= 0.15
        //x1>=7+invGe(d1)
        //x2>=8+invGe(d2)
        //d1+d2<=0.15
        //mip.addProbability(mip.sum(x1,x2), 0.15);
        
        mip.exportModel("model.lp");
        
        if(mip.solve()){
            System.out.println("status = "+mip.getStatus());
            System.out.println("objective = "+mip.getObjValue());
            System.out.println("x1 = "+mip.getValue(x1));
            System.out.println("x2 = "+mip.getValue(x2));
            System.out.println("p1 = "+mip.getValue(p1));
            System.out.println("p2 = "+mip.getValue(p2));
            
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
