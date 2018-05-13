/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.gurobi;

import gurobi.GRBLinExpr;
import minlp.Expr;

/**
 *
 * @author Marcio
 */
public class GurobiExpr implements Expr{
    protected final GRBLinExpr expr;
    protected GurobiExpr(GRBLinExpr expr) {
        this.expr = expr;
    }
    @Override
    public String toString() {
        return this.expr.toString();
    }
}
