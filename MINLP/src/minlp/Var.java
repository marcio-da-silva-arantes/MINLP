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
public interface Var extends Expr{
    //public Type getType() throws Exception;

    public double getLB() throws Exception;

    public double getUB() throws Exception;

    public void setLB(double lb) throws Exception;

    public void setUB(double ub) throws Exception;

    public String getName() throws Exception;

    public void setName(String name) throws Exception;
    
    public double value() throws Exception;

    public void fix(double value) throws Exception;

}
