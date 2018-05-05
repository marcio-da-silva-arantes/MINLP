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
 *
 * @author Marcio
 */
public class CPLEX extends MINLP{
    private final Map<Integer, Var> map;
    
    public IloCplex cpx;
    private int n_cols = 0;
    public CPLEX() throws Exception {
        super();
        cpx = new IloCplex();
        map = new TreeMap();
    }
    
    
    
    @Override
    public Var numVar(double lb, double ub, String name) throws Exception {
        n_cols++;
        CPLEXVar var = new CPLEXVar(cpx.numVar(lb, ub, name), n_cols);
        map.put(n_cols, var);
        return var;
    }
     @Override
    public Var boolVar(String name) throws Exception {
        n_cols++;
        CPLEXVar var = new CPLEXVar(cpx.boolVar(name), n_cols);
        map.put(n_cols, var);
        return var;
    }

    @Override
    public Expr constant(double d) throws Exception {
        return new CPLEXExpr(cpx.constant(d));
    }
    
    @Override
    public Expr prod(double coef, Expr expr) throws Exception {
        if(expr instanceof CPLEXExpr){
            return new CPLEXExpr(cpx.prod(coef, ((CPLEXExpr)expr).expr));
        }else if(expr instanceof CPLEXVar){
            return new CPLEXExpr(cpx.prod(coef, ((CPLEXVar)expr).var));
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public Expr sum(Expr expr1, Expr expr2) throws Exception {
        if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXExpr){
            return new CPLEXExpr(cpx.sum(((CPLEXExpr)expr1).expr, ((CPLEXExpr)expr2).expr));
        }else if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXVar){
            return new CPLEXExpr(cpx.sum(((CPLEXExpr)expr1).expr, ((CPLEXVar)expr2).var));
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXExpr){
            return new CPLEXExpr(cpx.sum(((CPLEXVar)expr1).var, ((CPLEXExpr)expr2).expr));
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXVar){
            return new CPLEXExpr(cpx.sum(((CPLEXVar)expr1).var, ((CPLEXVar)expr2).var));
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void addMinimize(Expr obj) throws Exception {
        if(obj instanceof CPLEXExpr){
            cpx.addMinimize(((CPLEXExpr)obj).expr);
        }else{
            cpx.addMinimize(((CPLEXVar)obj).var);
        }
    }
    @Override
    public void addMaximize(Expr obj) throws Exception {
        if(obj instanceof CPLEXExpr){
            cpx.addMaximize(((CPLEXExpr)obj).expr);
        }else{
            cpx.addMaximize(((CPLEXVar)obj).var);
        }
    }

    @Override
    public void addGe(Expr expr, double d) throws Exception {
        if(expr instanceof CPLEXExpr){
            cpx.addGe(((CPLEXExpr)expr).expr, d);
        }else{
            cpx.addGe(((CPLEXVar)expr).var, d);
        }
    }
    @Override
    public void addGe(Expr expr1, Expr expr2) throws Exception {
        if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXExpr){
            cpx.addGe(((CPLEXExpr)expr1).expr, ((CPLEXExpr)expr2).expr);
        }else if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXVar){
            cpx.addGe(((CPLEXExpr)expr1).expr, ((CPLEXVar)expr2).var);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXExpr){
            cpx.addGe(((CPLEXVar)expr1).var, ((CPLEXExpr)expr2).expr);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXVar){
            cpx.addGe(((CPLEXVar)expr1).var, ((CPLEXVar)expr2).var);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    @Override
    public void addLe(Expr expr, double d) throws Exception {
        if(expr instanceof CPLEXExpr){
            cpx.addLe(((CPLEXExpr)expr).expr, d);
        }else{
            cpx.addLe(((CPLEXVar)expr).var, d);
        }
        
    }
    @Override
    public void addLe(Expr expr1, Expr expr2) throws Exception {
        if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXExpr){
            cpx.addLe(((CPLEXExpr)expr1).expr, ((CPLEXExpr)expr2).expr);
        }else if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXVar){
            cpx.addLe(((CPLEXExpr)expr1).expr, ((CPLEXVar)expr2).var);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXExpr){
            cpx.addLe(((CPLEXVar)expr1).var, ((CPLEXExpr)expr2).expr);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXVar){
            cpx.addLe(((CPLEXVar)expr1).var, ((CPLEXVar)expr2).var);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void addEq(Expr expr, double d) throws Exception {
        if(expr instanceof CPLEXExpr){
            cpx.addEq(((CPLEXExpr)expr).expr, d);
        }else{
            cpx.addEq(((CPLEXVar)expr).var, d);
        }
    }
    @Override
    public void addEq(Expr expr1, Expr expr2) throws Exception {
        if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXExpr){
            cpx.addEq(((CPLEXExpr)expr1).expr, ((CPLEXExpr)expr2).expr);
        }else if(expr1 instanceof CPLEXExpr && expr2 instanceof CPLEXVar){
            cpx.addEq(((CPLEXExpr)expr1).expr, ((CPLEXVar)expr2).var);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXExpr){
            cpx.addEq(((CPLEXVar)expr1).var, ((CPLEXExpr)expr2).expr);
        }else if(expr1 instanceof CPLEXVar && expr2 instanceof CPLEXVar){
            cpx.addEq(((CPLEXVar)expr1).var, ((CPLEXVar)expr2).var);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    
    @Override
    public void exportModel(String fname) throws Exception {
        cpx.exportModel(fname);
    }

    @Override
    public boolean solve() throws Exception {
        return cpx.solve();
    }

    @Override
    public String getStatus() throws Exception {
        return cpx.getStatus().toString();
    }

    @Override
    public double getObjValue() throws Exception {
        return cpx.getObjValue();
    }

    @Override
    public double getValue(Var x) throws Exception {
        return cpx.getValue(((CPLEXVar)x).var);
    }
    @Override
    public double getValue(Expr x) throws Exception {
        return cpx.getValue(((CPLEXExpr)x).expr);
    }
    @Override
    public void delete() throws Exception {
        cpx.end();
    }

    @Override
    public void setOut(PrintStream stream) throws Exception {
        cpx.setOut(stream);
    }

    @Override
    public void setWarning(PrintStream stream) throws Exception {
        cpx.setWarning(stream);
    }

    @Override
    public int getNcols() throws Exception {
        return cpx.getNcols();
    }

    @Override
    public int getNrows() throws Exception {
        return cpx.getNrows();
    }

    @Override
    public int getNbinVars() throws Exception {
        return cpx.getNbinVars();
    }
    @Override
    public int getNintVars() throws Exception {
        return cpx.getNintVars();
    }

    @Override
    public Var getVar(int col) throws Exception {
        col++;
        return map.get(col);
    }
    
}
