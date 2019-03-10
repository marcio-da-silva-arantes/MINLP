/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.cplex;

import ilog.concert.IloNumExpr;
import minlp.MIPExpr;

/**
 *
 * @author Marcio
 */
public class CPLEXExpr extends MIPExpr{
    protected final IloNumExpr expr;
    protected CPLEXExpr(CPLEX mip, IloNumExpr expr) {
        super(mip);
        this.expr = expr;
    }
    @Override
    public String toString() {
        return this.expr.toString();
    }
}
