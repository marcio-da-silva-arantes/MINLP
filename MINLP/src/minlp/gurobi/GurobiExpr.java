/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.gurobi;

import gurobi.GRBLinExpr;
import minlp.MIPExpr;

/**
 *
 * @author Marcio
 */
public class GurobiExpr extends MIPExpr{
    protected final GRBLinExpr expr;
    protected GurobiExpr(Gurobi mip, GRBLinExpr expr) {
        super(mip);
        this.expr = expr;
    }
    @Override
    public String toString() {
        return this.expr.toString();
    }
}
