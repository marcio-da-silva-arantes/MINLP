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
public interface Expr {
    public Expr prod(double coef) throws Exception;
    public Expr prod(Expr expr2) throws Exception;
    
    public Expr sum(Expr expr2) throws Exception;
    public Expr sum(double constant) throws Exception;
    public Expr minus(Expr expr2) throws Exception;
    public Expr minus(double constant) throws Exception;
    public Expr sumProd(double constant, Expr expr2) throws Exception;
    
    
    public void addMinimize() throws Exception;
    public void addMaximize() throws Exception;
    
    public void addGe(Expr expr2) throws Exception;
    public void addLe(Expr expr2) throws Exception;
    public void addEq(Expr expr2) throws Exception;
    public void addGe(double constant) throws Exception;
    public void addLe(double constant) throws Exception;
    public void addEq(double constant) throws Exception;
    
    public double value() throws Exception;
}
