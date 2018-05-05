/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.glpk;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import minlp.Expr;

/**
 *
 * @author Marcio
 */
public class GLPKExpr implements Expr{
    
    protected class Base{
        protected final double coef;
        protected final GLPKVar var;
        public Base(double coef, GLPKVar var) {
            this.coef = coef;
            this.var = var;
        }
    }
    
    private final Map<Integer, Base> map = new TreeMap<Integer, Base>();
    protected final double constant;
    protected GLPKExpr(double constant) {
        this.constant = constant;
    }
    protected GLPKExpr(GLPKVar var) {
        mapAdd(1, var);
        this.constant = 0;
    }
    protected GLPKExpr(double coef, GLPKExpr expr) {
        expr.map.forEach((key, base)->{
            mapAdd(coef*base.coef, base.var);
        });
        this.constant = coef*expr.constant;
    }
    protected GLPKExpr(GLPKExpr expr1, GLPKExpr expr2) {
        expr1.map.forEach((key, base)->{
            mapAdd(base.coef, base.var);
        });
        expr2.map.forEach((key, base)->{
            mapAdd(base.coef, base.var);
        });
        this.constant = expr1.constant+expr2.constant;
    }
    private final void mapAdd(double coef, GLPKVar var){
        Base old = this.map.get(var.col);
        if(old!=null){
            this.map.put(var.col, new Base(coef + old.coef, var));
        }else{
            this.map.put(var.col, new Base(coef, var));
        }
    }
   
    @Override
    public String toString() {
        String str = "";
        for(Base b : map.values()){
            str += b.coef < 0 ? " - " : " + ";
            str += String.format("%g*%s", Math.abs(b.coef), b.var.getName());
        }
        return str;
    }
    public final int size(){
        return map.size();
    }
    public final Collection<Base> values(){
        return map.values();
    }
}
