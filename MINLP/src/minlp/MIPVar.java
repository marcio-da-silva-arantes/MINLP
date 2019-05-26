/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;

/**
 *
 * @author Marcio
 */
public abstract class MIPVar extends MIPExpr implements Var{
    protected MIPVar(MINLP mip) {
        super(mip);
    }
    @Override
    public double value() throws Exception {
        return mip.getValue(this);
    }
    @Override
    public void fix(double value) throws Exception {
        this.setLB(value);
        this.setUB(value);
    }
}
