/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.glpk;

import minlp.MIPVar;
import minlp.Var;
import org.gnu.glpk.GLPKConstants;
import static org.gnu.glpk.GLPK.glp_set_col_bnds;
import static org.gnu.glpk.GLPK.glp_set_col_name;
import org.gnu.glpk.glp_prob;

/**
 *
 * @author Marcio
 */
public class GLPKVar extends MIPVar{
    private final glp_prob glp;
    protected final int col;
    private double lb, ub;
    private String name;
    
    public GLPKVar(GLPK glpk, int col, double lb, double ub, String name) {
        super(glpk);
        this.glp = glpk.mip;
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
    public void setLB(double lb) throws Exception {
        this.lb = lb;
        glp_set_col_bnds(glp, col, GLPKConstants.GLP_LO, lb, 0);
    }

    @Override
    public void setUB(double ub) throws Exception {
        this.lb = ub;
        glp_set_col_bnds(glp, col, GLPKConstants.GLP_UP, 0, ub);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        glp_set_col_name(glp, col, name);
    }

}
