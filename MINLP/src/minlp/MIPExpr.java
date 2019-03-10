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
public class MIPExpr implements Expr{
    protected final MINLP mip;
    protected MIPExpr(MINLP mip) {
        this.mip = mip;
    }
    @Override
    public Expr prod(double coef) throws Exception {
        return mip.prod(coef, this);
    }

    @Override
    public Expr prod(Expr expr2) throws Exception {
        return mip.prod(this, expr2);
    }

    @Override
    public Expr sum(Expr expr2) throws Exception {
        return mip.sum(this, expr2);
    }
    
    @Override
    public Expr sum(double constant) throws Exception {
        return mip.sum(constant, this);
    }
    @Override
    public Expr sumProd(double coef, Expr expr2) throws Exception {
        return mip.sum(this, mip.prod(coef,expr2));
    }
    @Override
    public Expr minus(Expr expr2) throws Exception {
        return mip.sum(this, mip.prod(-1, expr2));
    }
    @Override
    public Expr minus(double constant) throws Exception {
        return mip.sum(-constant, this);
    }
    
    @Override
    public void addMinimize() throws Exception {
        mip.addMinimize(this);
    }

    @Override
    public void addMaximize() throws Exception {
        mip.addMaximize(this);
    }

    @Override
    public void addGe(Expr expr2) throws Exception {
        mip.addGe(this, expr2);
    }

    @Override
    public void addLe(Expr expr2) throws Exception {
        mip.addLe(this, expr2);
    }

    @Override
    public void addEq(Expr expr2) throws Exception {
        mip.addEq(this, expr2);
    }

    @Override
    public void addGe(double constant) throws Exception {
        mip.addGe(this, constant);
    }

    @Override
    public void addLe(double constant) throws Exception {
        mip.addLe(this, constant);
    }

    @Override
    public void addEq(double constant) throws Exception {
        mip.addEq(this, constant);
    }
}
