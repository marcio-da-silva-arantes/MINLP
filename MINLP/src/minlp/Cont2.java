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
public class Cont2 {
    public final IloIntVar[] y;
    private final double lb,precision;
    public final IloNumVar val;
    private final MINLP cplex;
    public Cont2(MINLP cplex, double lb, double ub, int size) throws IloException {
        this.cplex = cplex;
        this.lb = lb;
        this.precision = (ub-lb)/(Math.pow(2, size)-1);
        this.y = new IloIntVar[size];
        for(int i=0; i<size; i++){
            this.y[i] = cplex.boolVar("y"+i);
        }
        IloNumExpr sum = cplex.constant(lb);
        int base = 1;
        for (IloIntVar yi : y) {
            sum = cplex.sum(sum, cplex.prod(base*precision, yi));
            base *= 2;
        }
        this.val = cplex.numVar();
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
