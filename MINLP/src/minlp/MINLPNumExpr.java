/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;

import ilog.concert.IloCopyManager;
import ilog.concert.IloCopyable;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;

/**
 *
 * @author marcio
 */
public class MINLPNumExpr{
    protected final IloNumExpr expr;
    private final MINLP cplex;
    public MINLPNumExpr(MINLP cplex, IloNumExpr expr) {
        this.cplex = cplex;
        this.expr = expr;
    }
    public MINLPNumExpr(MINLP cplex, MINLPNumExpr expr) {
        this.cplex = cplex;
        this.expr = expr.expr;
    }
    public MINLPNumExpr plus(MINLPNumExpr expr) throws IloException{
        return new MINLPNumExpr(cplex, cplex.sum(this.expr, expr.expr));
    }
    public MINLPNumExpr minus(MINLPNumExpr expr) throws IloException{
        return new MINLPNumExpr(cplex, cplex.sum(this.expr, cplex.prod(-1, expr.expr)));
    }
    public MINLPNumExpr times(double coef) throws IloException{
        return new MINLPNumExpr(cplex, cplex.prod(coef, this.expr));
    }
    public IloNumExpr expr(){
        return expr;
    }
}
