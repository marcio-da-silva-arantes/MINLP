/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.gurobi;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
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
public class Gurobi extends MINLP{
    private final Map<Integer, Var> map;
    
    public final GRBEnv env;
    public final GRBModel mip;
    
    public Gurobi() throws Exception {
        this(1e5);
    }
    public Gurobi(double bigM) throws Exception {
        super(bigM);
        env = new GRBEnv();
        mip = new GRBModel(env);
        map = new TreeMap();
    }
    @Override
    public void setTimeLimit(double timeLimit) throws Exception {
        mip.set(GRB.DoubleParam.TimeLimit, timeLimit);
    }
    
    
    @Override
    public Var numVar(double lb, double ub, String name) throws Exception {
        n_cols++;
        GurobiVar var = new GurobiVar(this, mip.addVar(lb, ub, 0.0, GRB.CONTINUOUS, name), n_cols, lb, ub, name);
        map.put(n_cols, var);
        return var;
    }
    @Override
    public Var boolVar(String name) throws Exception {
        n_cols++;
        GurobiVar var = new GurobiVar(this, mip.addVar(0, 1, 0.0, GRB.BINARY, name), n_cols, 0, 1, name);
        map.put(n_cols, var);
        return var;
    }
    @Override
    public Var intVar(int lb, int ub, String name) throws Exception {
        n_cols++;
        GurobiVar var = new GurobiVar(this, mip.addVar(lb, ub, 0.0, GRB.INTEGER, name), n_cols, lb, ub, name);
        map.put(n_cols, var);
        return var;
    }
    @Override
    public Expr constant(double d) throws Exception {
        GRBLinExpr expr = new GRBLinExpr();
        expr.addConstant(d);
        return new GurobiExpr(this, expr);
    }
    
    @Override
    public Expr prod(double coef, Expr expr) throws Exception {
        if(expr instanceof GurobiExpr){
            GRBLinExpr r = new GRBLinExpr();
            r.multAdd(coef, ((GurobiExpr)expr).expr);
            return new GurobiExpr(this, r);
        }else if(expr instanceof GurobiVar){
            GRBLinExpr r = new GRBLinExpr();
            r.addTerm(coef, ((GurobiVar)expr).var);
            return new GurobiExpr(this, r);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    @Override
    public Expr prod(Expr expr1, Expr expr2) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public Expr sum(Expr expr1, Expr expr2) throws Exception {
        if(expr1 instanceof GurobiExpr && expr2 instanceof GurobiExpr){
            GRBLinExpr r = new GRBLinExpr();
            r.add(((GurobiExpr)expr1).expr);
            r.add(((GurobiExpr)expr2).expr);
            return new GurobiExpr(this, r);
        }else if(expr1 instanceof GurobiExpr && expr2 instanceof GurobiVar){
            GRBLinExpr r = new GRBLinExpr();
            r.add(((GurobiExpr)expr1).expr);
            r.addTerm(1, ((GurobiVar)expr2).var);
            return new GurobiExpr(this, r);
        }else if(expr1 instanceof GurobiVar && expr2 instanceof GurobiExpr){
            GRBLinExpr r = new GRBLinExpr();
            r.addTerm(1, ((GurobiVar)expr1).var);
            r.add(((GurobiExpr)expr2).expr);
            return new GurobiExpr(this, r);
        }else if(expr1 instanceof GurobiVar && expr2 instanceof GurobiVar){
            GRBLinExpr r = new GRBLinExpr();
            r.addTerm(1, ((GurobiVar)expr1).var);
            r.addTerm(1, ((GurobiVar)expr2).var);
            return new GurobiExpr(this, r);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void addMinimize(Expr obj) throws Exception {
        mip.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
        if(obj instanceof GurobiExpr){
            mip.setObjective(((GurobiExpr)obj).expr);
        }else{
            GRBLinExpr r = new GRBLinExpr();
            r.addTerm(1, ((GurobiVar)obj).var);
            mip.setObjective(r);
        }
    }
    @Override
    public void addMaximize(Expr obj) throws Exception {
        mip.set(GRB.IntAttr.ModelSense, GRB.MAXIMIZE);
        if(obj instanceof GurobiExpr){
            mip.setObjective(((GurobiExpr)obj).expr);
        }else{
            GRBLinExpr r = new GRBLinExpr();
            r.addTerm(1, ((GurobiVar)obj).var);
            mip.setObjective(r);
        }
    }
    @Override
    public void addGe(Expr expr1, Expr expr2, String name) throws Exception {
        addRow(expr1, GRB.GREATER_EQUAL, expr2, name);
    }
    @Override
    public void addLe(Expr expr1, Expr expr2, String name) throws Exception {
        addRow(expr1, GRB.LESS_EQUAL, expr2, name);
    }
    @Override
    public void addEq(Expr expr1, Expr expr2, String name) throws Exception {
        addRow(expr1, GRB.EQUAL, expr2, name);
    }
    private void addRow(Expr expr1, final char SENSE, Expr expr2, String name) throws Exception{
        n_rows++;
        if(expr1 instanceof GurobiExpr && expr2 instanceof GurobiExpr){
            mip.addConstr(((GurobiExpr)expr1).expr, SENSE, ((GurobiExpr)expr2).expr, name);
        }else if(expr1 instanceof GurobiExpr && expr2 instanceof GurobiVar){
            mip.addConstr(((GurobiExpr)expr1).expr, SENSE, ((GurobiVar)expr2).var, name);
        }else if(expr1 instanceof GurobiVar && expr2 instanceof GurobiExpr){
            mip.addConstr(((GurobiVar)expr1).var, SENSE, ((GurobiExpr)expr2).expr, name);
        }else if(expr1 instanceof GurobiVar && expr2 instanceof GurobiVar){
            mip.addConstr(((GurobiVar)expr1).var, SENSE, ((GurobiVar)expr2).var, name);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    @Override
    public void exportModel(String fname) throws Exception {
        mip.write(fname);
    }

    @Override
    public boolean solve() throws Exception {
        mip.optimize();
        return mip.get(GRB.IntAttr.Status) == GRB.Status.OPTIMAL ||  mip.get(GRB.IntAttr.Status) == GRB.Status.SUBOPTIMAL;
    }

    @Override
    public String getStatus() throws Exception {
        int status = mip.get(GRB.IntAttr.Status);
        switch(status){
            case GRB.Status.OPTIMAL: return "Optimal";
            case GRB.Status.SUBOPTIMAL: return "Feasible";
            case GRB.Status.INFEASIBLE: return "Infeasible";
            case GRB.Status.UNBOUNDED: return "Unbounded";
            case GRB.Status.INF_OR_UNBD: return "INF_OR_UNBD";
            case GRB.Status.TIME_LIMIT: return "TIME_LIMIT";
        }
        return ""+status;
    }

    @Override
    public double getObjValue() throws Exception {
        return mip.get(GRB.DoubleAttr.ObjVal);
    }

    @Override
    public double getValue(Var x) throws Exception {
        return ((GurobiVar)x).var.get(GRB.DoubleAttr.X);
    }
    @Override
    public double getValue(Expr x) throws Exception {
        return ((GurobiExpr)x).expr.getValue();
    }
    @Override
    public void delete() throws Exception {
        mip.dispose();
        env.dispose();
    }

    /**
     * This method on Gurobi solver only will be used to enable/disable the output:<br>
     * <pre>
     * if(stream==null){
     *      setting output off
     * }else{
     *      setting output on
     * }
     * </pre>
     * @param stream
     * @throws Exception 
     */
    @Override
    public void setOut(PrintStream stream) throws Exception {
        System.err.println("MINLP warning: setOut on Gurobi solver only will be used to enable/disable the output:");
        if(stream==null){
            System.err.println("setting output off");
            mip.set(GRB.IntParam.OutputFlag, 0);
        }else{
            System.err.println("setting output on");
            mip.set(GRB.IntParam.OutputFlag, 1);
        }
    }

    /**
     * setWarning is not suported with Gurobi solver, nothing will change
     * @param stream
     * @throws Exception 
     */
    @Override
    public void setWarning(PrintStream stream) throws Exception {
        System.err.println("MINLP warning: setWarning is not suported with Gurobi solver, nothing will change");
    }
    @Override
    public int getNcols() throws Exception {
        return mip.get(GRB.IntAttr.NumVars);
    }

    @Override
    public int getNrows() throws Exception {
        return mip.get(GRB.IntAttr.NumConstrs);
    }

    @Override
    public int getNbinVars() throws Exception {
        return mip.get(GRB.IntAttr.NumBinVars);
    }
    @Override
    public int getNintVars() throws Exception {
        return mip.get(GRB.IntAttr.NumIntVars);
    }

    @Override
    public Var getVar(int col) throws Exception {
        col++;
        return map.get(col);
    }
    
}
