/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.glpk;

import minlp.Var;
import org.gnu.glpk.GLPKConstants;
import static org.gnu.glpk.GLPK.glp_set_col_bnds;
import static org.gnu.glpk.GLPK.glp_set_col_name;
import org.gnu.glpk.glp_prob;

/**
 *
 * @author Marcio
 */
public class GLPKVar implements Var{
    private final glp_prob mip;
    protected final int col;
    private final double lb, ub;
    private final String name;
    
    public GLPKVar(glp_prob mip, int col, double lb, double ub, String name) {
        this.mip = mip;
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
        glp_set_col_bnds(mip, col, GLPKConstants.GLP_LO, lb, 0);
    }

    @Override
    public void setUB(double ub) throws Exception {
        glp_set_col_bnds(mip, col, GLPKConstants.GLP_UP, 0, ub);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        glp_set_col_name(mip, col, name);
    }

}
