/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples.specific.others;

import minlp.Expr;
import minlp.MINLP;
import minlp.Set;
import minlp.Var;
import minlp.cplex.CPLEX;
import minlp.gurobi.Gurobi;

/**
 *
 * @author Marcio
 */
public class RodolfosProblem {
    private static final double contas[]    = {1040,	1028,	144,	1093,	242,	79,	664,	500};
    private static final double peso[]      = {1,	1,	1,	1,	2,	2,	2,	2};
    private static final double fontes[]    = {2593,	1800,	1000};
    
    public static void main(String[] args) throws Exception {
        MINLP mip = new CPLEX();
        
        Set F = mip.range(fontes.length);
        Set C = mip.range(contas.length);
        
        Var x[][] = mip.boolVarArray(F, C, "x");
        
        Expr obj = mip.sum(F, C, (f,c) -> mip.prod(contas[c]*peso[c], x[f][c]));
        
        mip.addMaximize(obj);
        
        
        mip.forAll(F, (f) -> {
            mip.addLe(mip.sum(C, c -> mip.prod(contas[c], x[f][c])), fontes[f]);
        });
        mip.forAll(C, (c) -> {
            mip.addLe(mip.sum(F, f -> x[f][c]), 1);
        });
        
        mip.exportModel("rofolfos.lp");
        
        if(mip.solve()){
            System.out.println("status: "+mip.getStatus());
            System.out.println("obj   : "+mip.getObjValue());
            mip.forAll(F, (f) -> {
                mip.forAll(C, (c) -> {
                    System.out.printf("%s ", mip.getValue(x[f][c])>0.5 ? "x" : ".");
                });
                System.out.printf(" -> %1.2f\n",mip.getValue(mip.sum(C,c->mip.prod(contas[c],x[f][c]))));
            });
        }else{
            System.out.println("dont have solution");
        }
    }
}
