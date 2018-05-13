/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.gurobi;

import gurobi.GRB;
import gurobi.GRBVar;
import minlp.Var;

/**
 *
 * @author Marcio
 */
public class GurobiVar implements Var{
    protected final GRBVar var;
    protected final int col;
    private double lb, ub;
    private String name;
    protected GurobiVar(GRBVar var, int col, double lb, double ub, String name) {
        this.var = var;
        this.col = col;
        this.lb = lb;
        this.ub = ub;
        this.name = name;
    }
    @Override
    public double getLB() throws Exception {
        return lb;
    }
    @Override
    public double getUB() throws Exception {
        return ub;
    }
    @Override
    public String getName() throws Exception{
        return name;
    }

    @Override
    public void setLB(double lb) throws Exception {
       var.set(GRB.DoubleAttr.LB, lb);
    }

    @Override
    public void setUB(double ub) throws Exception {
        var.set(GRB.DoubleAttr.UB, ub);
    }
    @Override
    public void setName(String name) throws Exception {
        var.set(GRB.StringAttr.VarName, name);
    }
}
