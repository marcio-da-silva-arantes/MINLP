/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.cplex;

import ilog.cplex.IloCplex;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import minlp.Expr;
import minlp.MINLP;
import minlp.Var;

/**
 * CPLEX is the class used to create and solve a large variety of Mathematical Programming models.
 * @see ilog.cplex.IloCplex
 * @author Marcio
 */
public class CPLEX extends MINLP{
    private final Map<Integer, Var> map;
    
    public final IloCplex mip;
    public CPLEX() throws Exception {
        this(1e5);
    }
    public CPLEX(double bigM) throws Exception {
        super(bigM);
        mip = new IloCplex();
        map = new TreeMap();
    }

    @Override
    public void setTimeLimit(double timeLimit) throws Exception {
        mip.setParam(IloCplex.DoubleParam.TimeLimit, timeLimit);
    }
    
    @Override
    public Var numVar(double lb, double ub, String name) throws Exception {
        n_cols++;
        CPLEXVar var = new CPLEXVar(this, mip.numVar(lb, ub, name), n_cols);
        map.put(n_cols, var);
        return var;
    }
     @Override
    public Var boolVar(String name) throws Exception {
        n_cols++;
        CPLEXVar var = new CPLEXVar(this, mip.boolVar(name), n_cols);
        map.put(n_cols, var);
        return var;
    }
    @Override
    public Var intVar(int lb, int ub, String name) throws Exception {
        n_cols++;
        CPLEXVar var = new CPLEXVar(this, mip.intVar(lb, ub, name), n_cols);
        map.put(n_cols, var);
        return var;
    }
    @Override
    public Expr constant(double d) throws Exception {
        return new CPLEXExpr(this, mip.constant(d));
    }
    
    @Override
    public Expr prod(double coef, Expr expr) throws Exception {
        if(expr instanceof CPLEXExpr){
            return new CPLEXExpr(this, mip.prod(coef, ((CPLEXExpr)expr).expr));
        }else if(expr instanceof CPLEXVar){
            return new CPLEXExpr(this, mip.prod(coef, ((CPLEXVar)expr).var));
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    @Override
    public Expr prod(Expr expr1, Expr expr2) throws Exception {
        if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXExpr){
            return new CPLEXExpr(this, mip.prod(((CPLEXExpr)expr1).expr, ((CPLEXExpr)expr2).expr));
        }else if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXVar){
            return new CPLEXExpr(this, mip.prod(((CPLEXExpr)expr1).expr, ((CPLEXVar)expr2).var));
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXExpr){
            return new CPLEXExpr(this, mip.prod(((CPLEXVar)expr1).var, ((CPLEXExpr)expr2).expr));
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXVar){
            return new CPLEXExpr(this, mip.prod(((CPLEXVar)expr1).var, ((CPLEXVar)expr2).var));
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    @Override
    public Expr sum(Expr expr1, Expr expr2) throws Exception {
        if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXExpr){
            return new CPLEXExpr(this, mip.sum(((CPLEXExpr)expr1).expr, ((CPLEXExpr)expr2).expr));
        }else if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXVar){
            return new CPLEXExpr(this, mip.sum(((CPLEXExpr)expr1).expr, ((CPLEXVar)expr2).var));
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXExpr){
            return new CPLEXExpr(this, mip.sum(((CPLEXVar)expr1).var, ((CPLEXExpr)expr2).expr));
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXVar){
            return new CPLEXExpr(this, mip.sum(((CPLEXVar)expr1).var, ((CPLEXVar)expr2).var));
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void addMinimize(Expr obj) throws Exception {
        if(obj instanceof CPLEXExpr){
            mip.addMinimize(((CPLEXExpr)obj).expr);
        }else{
            mip.addMinimize(((CPLEXVar)obj).var);
        }
    }
    @Override
    public void addMaximize(Expr obj) throws Exception {
        if(obj instanceof CPLEXExpr){
            mip.addMaximize(((CPLEXExpr)obj).expr);
        }else{
            mip.addMaximize(((CPLEXVar)obj).var);
        }
    }

    @Override
    public void addGe(Expr expr1, Expr expr2, String name) throws Exception {
        if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXExpr){
            mip.addGe(((CPLEXExpr)expr1).expr, ((CPLEXExpr)expr2).expr, name);
        }else if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXVar){
            mip.addGe(((CPLEXExpr)expr1).expr, ((CPLEXVar)expr2).var, name);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXExpr){
            mip.addGe(((CPLEXVar)expr1).var, ((CPLEXExpr)expr2).expr, name);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXVar){
            mip.addGe(((CPLEXVar)expr1).var, ((CPLEXVar)expr2).var, name);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    @Override
    public void addLe(Expr expr1, Expr expr2, String name) throws Exception {
        if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXExpr){
            mip.addLe(((CPLEXExpr)expr1).expr, ((CPLEXExpr)expr2).expr, name);
        }else if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXVar){
            mip.addLe(((CPLEXExpr)expr1).expr, ((CPLEXVar)expr2).var, name);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXExpr){
            mip.addLe(((CPLEXVar)expr1).var, ((CPLEXExpr)expr2).expr, name);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXVar){
            mip.addLe(((CPLEXVar)expr1).var, ((CPLEXVar)expr2).var, name);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    @Override
    public void addEq(Expr expr1, Expr expr2, String name) throws Exception {
        if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXExpr){
            mip.addEq(((CPLEXExpr)expr1).expr, ((CPLEXExpr)expr2).expr, name);
        }else if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXVar){
            mip.addEq(((CPLEXExpr)expr1).expr, ((CPLEXVar)expr2).var, name);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXExpr){
            mip.addEq(((CPLEXVar)expr1).var, ((CPLEXExpr)expr2).expr, name);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXVar){
            mip.addEq(((CPLEXVar)expr1).var, ((CPLEXVar)expr2).var, name);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    
    @Override
    public void exportModel(String fname) throws Exception {
        mip.exportModel(fname);
    }

    @Override
    public boolean solve() throws Exception {
        return mip.solve();
    }

    @Override
    public String getStatus() throws Exception {
        return mip.getStatus().toString();
    }

    @Override
    public double getObjValue() throws Exception {
        return mip.getObjValue();
    }

    @Override
    public double getValue(Var x) throws Exception {
        return mip.getValue(((CPLEXVar)x).var);
    }
    @Override
    public double getValue(Expr x) throws Exception {
        return mip.getValue(((CPLEXExpr)x).expr);
    }
    @Override
    public void delete() throws Exception {
        mip.end();
    }

    /**
     * Sets the default output stream of the invoking IloCplex object.
     * After this call, all logging output will be output via the new stream. Passing null as the new output stream will turn off all logging output.
     * @param stream    The new default output stream.
     * @throws Exception 
     */
    @Override
    public void setOut(PrintStream stream) throws Exception {
        mip.setOut(stream);
    }

    /**
     * Sets the warning stream of the invoking IloCplex object. After this call, all warnings will be output via the new stream. Passing null as the new output stream will turn off all warnings.
     * @param stream
     * @throws Exception 
     */
    @Override
    public void setWarning(PrintStream stream) throws Exception {
        mip.setWarning(stream);
    }

    @Override
    public int getNcols() throws Exception {
        return mip.getNcols();
    }

    @Override
    public int getNrows() throws Exception {
        return mip.getNrows();
    }

    @Override
    public int getNbinVars() throws Exception {
        return mip.getNbinVars();
    }
    @Override
    public int getNintVars() throws Exception {
        return mip.getNintVars();
    }

    @Override
    public Var getVar(int col) throws Exception {
        col++;
        return map.get(col);
    }
    
}
