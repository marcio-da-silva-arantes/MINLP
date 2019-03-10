/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.cplex;

import ilog.concert.IloNumVar;
import minlp.MIPVar;

/**
 *
 * @author Marcio
 */
public class CPLEXVar extends MIPVar{ 
    protected final IloNumVar var;
    protected final int col;
    protected CPLEXVar(CPLEX mip, IloNumVar var, int col) {
        super(mip);
        this.var = var;
        this.col = col;
    }
    @Override
    public double getLB() throws Exception {
        return var.getLB();
    }
    @Override
    public double getUB() throws Exception {
        return var.getUB();
    }
    @Override
    public void setLB(double lb) throws Exception {
        var.setLB(lb);
    }
    @Override
    public void setUB(double ub) throws Exception {
        var.setUB(ub);
    }
    @Override
    public String getName() {
        return var.getName();
    }
    @Override
    public void setName(String name) {
        var.setName(name);
    }
}
