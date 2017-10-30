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

/**
 *
 * @author marcio
 */
public class Cont {
    public final IloIntVar[] y;
    private final double lb, ub, precision;
    public final IloNumVar val;
    private final MINLP cplex;
    public Cont(MINLP cplex, double lb, double ub, int size) throws IloException {
        this.cplex = cplex;
        this.lb = lb;
        this.ub = ub;
        this.precision = (ub-lb)/(Math.pow(2, size)-1);
        this.y = cplex.boolVarArray(size);
        IloNumExpr sum = cplex.constant(lb);
        int base = 1;
        for (IloIntVar v : y) {
            sum = cplex.sum(sum, cplex.prod(base*precision, v));
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
            sum = cplex.sum(sum, cplex.prod(base*precision, vi));
            base *= 2;
        }
        return sum;
    }
}
