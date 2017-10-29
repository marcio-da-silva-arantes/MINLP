/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 *
 * @author marcio
 */
public class Int {
    public final IloIntVar[] y;
    private final int lb, ub;
    public final IloNumVar val;
    private final MINLP cplex;
    public Int(MINLP cplex, int lb, int ub) throws IloException {
        this.cplex = cplex;
        this.lb = lb;
        this.ub = ub;
        int num = ub-lb+1;
        int size = (int)(Math.log(num)/Math.log(2)+0.999999);
        System.out.println(size);
        this.y = cplex.boolVarArray(size);
        
        IloNumExpr sum = cplex.constant(lb);
        int base = 1;
        for (IloIntVar v : y) {
            sum = cplex.sum(sum, cplex.prod(base, v));
            base *= 2;
        }
        this.val = cplex.numVar(lb, ub);
        cplex.addEq(val, sum);
    }
    public IloNumExpr addProd(IloNumExpr x) throws IloException{
        IloNumExpr sum = cplex.prod(lb, x);
        int base = 1;
        for (IloIntVar yi : y) {
            IloNumVar vi = cplex.addProd(x, yi);
            sum = cplex.sum(sum, cplex.prod(base, vi));
            base *= 2;
        }
        return sum;
    }
}
