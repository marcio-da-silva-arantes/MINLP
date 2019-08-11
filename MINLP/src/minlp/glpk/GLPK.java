/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.glpk;

import java.io.PrintStream;
import minlp.Expr;
import minlp.MINLP;
import minlp.Var;
import minlp.glpk.GLPKExpr.Base;
import static org.gnu.glpk.GLPK.*;
import org.gnu.glpk.GLPKConstants;
import static org.gnu.glpk.GLPKConstants.GLP_ON;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;

/**
 *
 * @author Marcio
 */
public class GLPK extends MINLP{
    protected final glp_prob mip;
    private final glp_smcp parm_smcp;
    private final glp_iocp parm_iocp;
    private boolean isLP = true; //if has any integer or binary variable this will change to false
    
    public GLPK() throws Exception {
        this(1e5, 1e-4);
    }
    public GLPK(double bigM, double epsilon) throws Exception {
        super(bigM, epsilon);
        mip = glp_create_prob();
        parm_smcp = new glp_smcp();
        parm_iocp = new glp_iocp();
    }
    @Override
    public void setTimeLimit(double timeLimit) throws Exception {
        parm_smcp.setTm_lim((int)timeLimit);
        parm_iocp.setTm_lim((int)timeLimit);
        //mip.set(GRB.DoubleParam.TimeLimit, timeLimit);
    }
    
    @Override
    public Var numVar(double lb, double ub, String name) throws Exception {
        n_cols++;
        glp_add_cols(mip, 1);
        glp_set_col_name(mip, n_cols, name);
        glp_set_col_kind(mip, n_cols, GLPKConstants.GLP_CV); //CV - Continuous Variable
        if(lb==Double.NEGATIVE_INFINITY && ub==Double.POSITIVE_INFINITY){
            glp_set_col_bnds(mip, n_cols, GLPKConstants.GLP_FR, 0, 0);
        }else if(ub==Double.POSITIVE_INFINITY){
            glp_set_col_bnds(mip, n_cols, GLPKConstants.GLP_LO, lb, 0);
        }else if(lb==Double.NEGATIVE_INFINITY){
            glp_set_col_bnds(mip, n_cols, GLPKConstants.GLP_UP, 0, ub);
        }else{
            glp_set_col_bnds(mip, n_cols, GLPKConstants.GLP_DB, lb, ub);
        }
        //glp_set_col_bnds(mip, n_cols, GLPKConstants.GLP_DB, lb, ub);
        return new GLPKVar(this, n_cols, lb, ub, name);
    }
    
    @Override
    public Var boolVar(String name) throws Exception {
        isLP = false;
        n_cols++;
        glp_add_cols(mip, 1);
        glp_set_col_name(mip, n_cols, name);
        glp_set_col_kind(mip, n_cols, GLPKConstants.GLP_BV); //BV - Binary Variable
        glp_set_col_bnds(mip, n_cols, GLPKConstants.GLP_DB, 0, 1);
        return new GLPKVar(this, n_cols, 0, 1, name);
    }

    @Override
    public Var intVar(int lb, int ub, String name) throws Exception {
        isLP = false;
        n_cols++;
        glp_add_cols(mip, 1);
        glp_set_col_name(mip, n_cols, name);
        glp_set_col_kind(mip, n_cols, GLPKConstants.GLP_IV); //BV - Binary Variable
        glp_set_col_bnds(mip, n_cols, GLPKConstants.GLP_DB, lb, ub);
        return new GLPKVar(this, n_cols, lb, ub, name);
    }
    

    @Override
    public Expr constant(double lb) throws Exception {
        return new GLPKExpr(this, lb);
    }
    
    private GLPKExpr cast(Expr expr) throws Exception{
        if(expr instanceof GLPKExpr){
            return (GLPKExpr) expr;
        }else if(expr instanceof GLPKVar){
            return new GLPKExpr(this, (GLPKVar) expr);
        }else{
            throw new Exception("Invalid expression type"); //To change body of generated methods, choose Tools | Templates.
        }
    }
    @Override
    public Expr prod(double coef, Expr expr) throws Exception {
        return new GLPKExpr(this, coef, cast(expr));
    }
    @Override
    public Expr prod(Expr expr1, Expr expr2) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Expr sum(Expr expr1, Expr expr2) throws Exception {
        return new GLPKExpr(this, cast(expr1), cast(expr2));
    }

    @Override
    public void addMinimize(Expr obj) throws Exception {
        // Define objective
        glp_set_obj_name(mip, "objective");
        glp_set_obj_dir(mip, GLPKConstants.GLP_MIN);
        
        GLPKExpr expr = cast(obj);
        for(Base b : expr.values()){
            glp_set_obj_coef(mip, b.var.col, b.coef);
        }
        //System.out.println("minimize "+obj);
    }
    @Override
    public void addMaximize(Expr obj) throws Exception {
        glp_set_obj_name(mip, "objective");
        glp_set_obj_dir(mip, GLPKConstants.GLP_MAX);
        
        GLPKExpr expr = cast(obj);
        for(Base b : expr.values()){
            glp_set_obj_coef(mip, b.var.col, b.coef);
        }
        
        //System.out.println("maximize "+obj);
    }
    
    @Override
    public void addGe(Expr expr1, Expr expr2, String name) throws Exception {
        GLPKExpr expr = cast(sum(expr1, prod(-1,expr2))); 
        double d = -expr.constant;
        
        n_rows++;
        // Create constraints
        glp_add_rows(mip, 1);
        glp_set_row_name(mip, n_rows, name);
        glp_set_row_bnds(mip, n_rows, GLPKConstants.GLP_LO, d, 0);
        
        SWIGTYPE_p_int ind = new_intArray(expr.size()+1);
        SWIGTYPE_p_double val = new_doubleArray(expr.size()+1);
        
        int i=1;
        for(Base b : expr.values()){
            intArray_setitem(ind, i, b.var.col);
            doubleArray_setitem(val, i, b.coef);
            i++;
        }
        glp_set_mat_row(mip, n_rows, expr.size(), ind, val);
    }
    @Override
    public void addLe(Expr expr1, Expr expr2, String name) throws Exception {
        GLPKExpr expr = cast(sum(expr1, prod(-1,expr2))); 
        
        double d = -expr.constant;
        
        n_rows++;
        // Create constraints
        glp_add_rows(mip, 1);
        glp_set_row_name(mip, n_rows, name);
        glp_set_row_bnds(mip, n_rows, GLPKConstants.GLP_UP, 0, d);
        
        SWIGTYPE_p_int ind = new_intArray(expr.size()+1);
        SWIGTYPE_p_double val = new_doubleArray(expr.size()+1);
        
        int i=1;
        for(Base b : expr.values()){
            intArray_setitem(ind, i, b.var.col);
            doubleArray_setitem(val, i, b.coef);
            i++;
        }
        glp_set_mat_row(mip, n_rows, expr.size(), ind, val);
    }
    @Override
    public void addEq(Expr expr1, Expr expr2, String name) throws Exception {
        addGe(expr1, expr2, name+".ge");
        addLe(expr1, expr2, name+".le");
    }
    @Override
    public void exportModel(String fname) throws Exception {
        glp_write_prob(mip, 0, fname);
    }
    @Override
    public boolean solve() throws Exception {
        if(isLP){
            
            //parm.setTm_lim(GLP_UP);
            glp_init_smcp(parm_smcp);
            return glp_simplex(mip, parm_smcp) == 0; //using simplex solver
        }else{
            //glp_iocp parm = new glp_iocp();
            glp_init_iocp(parm_iocp);
            parm_iocp.setPresolve(GLP_ON);
            return glp_intopt(mip, parm_iocp) == 0;  //using mip solver
        }
        // Solve model
        
//        glp_smcp parm = new glp_smcp();
//        glp_init_smcp(parm);
//        int ret = glp_exact(mip, parm);
//        
        /*
        glp_smcp parm = new glp_smcp();
        glp_init_smcp(parm);
        int ret = glp_simplex(mip, parm);
        */
//        glp_iocp parm = new glp_iocp();
//        glp_init_iocp(parm);
//        parm.setPresolve(GLP_ON);
//        int ret = glp_intopt(mip, parm);

//        glp_smcp parm = new glp_smcp();
//        glp_init_smcp(parm);
//        glp_simplex(mip, parm);
//
//        glp_iocp iocp = new glp_iocp();
//        glp_init_iocp(iocp);
//        iocp.setPresolve(GLPKConstants.GLP_ON);
//        int ret = glp_intopt(mip, iocp);
//
//        return ret==0;
    }

    
    
    @Override
    public String getStatus() throws Exception {
        int status = isLP ? glp_get_status(mip) : glp_mip_status(mip);
        /*
        GLP_OPT - solution is optimal; 
        GLP_FEAS - solution is feasible; 
        GLP_INFEAS - solution is infeasible; 
        GLP_NOFEAS - problem has no feasible solution; 
        GLP_UNBND - problem has unbounded solution; 
        GLP_UNDEF - solution is undefined.
        */
        if(status == GLPKConstants.GLP_OPT){
            return "Optimal";
        }else if(status == GLPKConstants.GLP_FEAS){
            return "Feasible";
        }else if(status == GLPKConstants.GLP_INFEAS){
            return "Infeasible";
        }else if(status == GLPKConstants.GLP_NOFEAS){
            return "Infeasible";
        }else if(status == GLPKConstants.GLP_UNBND){
            return "Unbounded";
        }else if(status == GLPKConstants.GLP_UNDEF){
            return "Undefined";
        } 
        return "";
    }

    @Override
    public double getObjValue() throws Exception {
        return isLP ? glp_get_obj_val(mip) : glp_mip_obj_val(mip);
        //return glp_get_obj_val(mip);
    }

    @Override
    public double getValue(Var x) throws Exception {
        GLPKVar x2 = (GLPKVar) x;
        return isLP ? glp_get_col_prim(mip, x2.col) : glp_mip_col_val(mip, x2.col);
    }
    @Override
    public double getValue(Expr expr) throws Exception {
        GLPKExpr glpk_expr = ((GLPKExpr) expr);
        return glpk_expr.values().stream().map((b)->isLP ? glp_get_col_prim(mip, b.var.col) : glp_mip_col_val(mip, b.var.col) ).reduce(glpk_expr.constant, Double::sum);
    }


    @Override
    public void delete() throws Exception {
        glp_delete_prob(mip);
    }
    
    /**
     * This method on GPLK solver only will be used to enable/disable the output:<br>
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
        System.err.println("MINLP warning: setOut on GLPK solver only will be used to enable/disable the output:");
        if(stream==null){
            System.err.println("setting output off");
            glp_term_out(GLPKConstants.GLP_OFF);
        }else{
            System.err.println("setting output on");
            glp_term_out(GLPKConstants.GLP_ON);
        }
    }

    /**
     * setWarning is not suported with GLPK solver, nothing will change
     * @param stream
     * @throws Exception 
     */
    @Override
    public void setWarning(PrintStream stream) throws Exception {
        System.err.println("MINLP warning: setWarning is not suported with GLPK solver, nothing will change");
    }

    @Override
    public int getNcols() throws Exception {
        return glp_get_num_cols(mip);
    }

    @Override
    public int getNrows() throws Exception {
        return glp_get_num_rows(mip);
    }

    @Override
    public int getNbinVars() throws Exception {
        return glp_get_num_bin(mip);
    }

    @Override
    public int getNintVars() throws Exception {
        return glp_get_num_int(mip);
    }

    @Override
    public Var getVar(int i) throws Exception {
        i++;
        return new GLPKVar(this, i, glp_get_col_lb(mip, i), glp_get_col_ub(mip, i), glp_get_col_name(mip, i));
    }
}
