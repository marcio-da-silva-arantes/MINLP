/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;

import java.io.PrintStream;
import minlp.stream.Function;
import minlp.stream.Consumer;
import java.util.LinkedList;
import minlp.stream.Consumer2p;
import minlp.stream.Consumer3p;
import minlp.stream.Consumer4p;
import minlp.stream.Function2p;
import minlp.stream.Function3p;
import minlp.stream.Function4p;

/**
 *
 * @author Marcio
 */
public abstract class MINLP {
    private final double M;    //big M

    public MINLP() throws Exception {
        this(1e2);
    }
    public MINLP(double bigM) throws Exception {
        this.M = bigM;
    }
    
    public abstract int getNcols() throws Exception;
    public abstract int getNrows() throws Exception;
    public abstract int getNbinVars() throws Exception;
    public abstract int getNintVars() throws Exception;
    
    public abstract Var getVar(int i)  throws Exception;
    
    /**
     * create a new continuous variable with a specified name and bounds.
     */
    public abstract Var numVar(double lb, double ub, String name) throws Exception;

    /**
     * create a new bynary variable with a specified name.
     */
    public abstract Var boolVar(String name) throws Exception;

    /**
     * creates a array of new continuous variables with a specified name and bounds.
     */
    public final Var[] numVarArray(Set s, double lb, double ub, String name) throws Exception{
        Var array[] = new Var[s.size()];
        s.forAll(i -> {
            array[i] = numVar(lb, ub, name+"["+i+"]");
        });
        return array;
    }
    /**
     * creates a array of new continuous variables with a specified name and bounds.
     */
    public final Var[][] numVarArray(Set s1, Set s2, double lb, double ub, String name) throws Exception{
        Var array[][] = new Var[s1.size()][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, lb, ub, name+"["+i+"]");
        });
        return array;
    }
    /**
     * creates a array of new continuous variables with a specified name and bounds.
     */
    public final Var[][][] numVarArray(Set s1, Set s2, Set s3, double lb, double ub, String name) throws Exception{
        Var array[][][] = new Var[s1.size()][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, lb, ub, name+"["+i+"]");
        });
        return array;
    }
    /**
     * creates a array of new continuous variables with a specified name and bounds.
     */
    public final Var[][][][] numVarArray(Set s1, Set s2, Set s3, Set s4, double lb, double ub, String name) throws Exception{
        Var array[][][][] = new Var[s1.size()][][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, s4, lb, ub, name+"["+i+"]");
        });
        return array;
    }
    
    
    /**
     * creates a array of new bynary variables with a specified name.
     */
    public final Var[] boolVarArray(Set s, String name) throws Exception{
        Var array[] = new Var[s.size()];
        s.forAll(i -> {
            array[i] = boolVar(name+"["+i+"]");
        });
        return array;
    }
    /**
     * creates a array of new bynary variables with a specified name.
     */
    public final Var[][] boolVarArray(Set s1, Set s2, String name) throws Exception{
        Var array[][] = new Var[s1.size()][];
        s1.forAll(i -> {
            array[i] = boolVarArray(s2, name+"["+i+"]");
        });
        return array;
    }
    /**
     * creates a array of new bynary variables with a specified name.
     */
    public final Var[][][] boolVarArray(Set s1, Set s2, Set s3, String name) throws Exception{
        Var array[][][] = new Var[s1.size()][][];
        s1.forAll(i -> {
            array[i] = boolVarArray(s2, s3, name+"["+i+"]");
        });
        return array;
    }
    /**
     * creates a array of new bynary variables with a specified name.
     */
    public final Var[][][][] boolVarArray(Set s1, Set s2, Set s3, Set s4, String name) throws Exception{
        Var array[][][][] = new Var[s1.size()][][][];
        s1.forAll(i -> {
            array[i] = boolVarArray(s2, s3, s4, name+"["+i+"]");
        });
        return array;
    }
    
    
    public abstract Expr prod(double coef, Expr expr) throws Exception;
    
    public abstract Expr sum(Expr expr1, Expr expr2) throws Exception;
    
    /**<pre>
     * define: 
     *      v = linerizedProd(x,y)  &harr v = x*y
     * dedution: 
     *      if(y=1){
     *          v=x
     *      }else{ 
     *          v=0 
     *      }
     * linear transformation:
     *      let v &isin R
     *      M*(y-1) + x &le v &le x - M*(y-1)
     *      -M*y &le v &le M*y
     * where:
     *      M is a big positive constant (M &ge max(|ub(x)|,|lb(x)|) 
     * </pre>
     * @param expr a linear expression
     * @param y a boolean variable {0,1}
     * @return a new continous variable thats represent this linearization
     */
    public Var linerizedProd(Expr expr, Var y, String name) throws Exception{
        if(y.getLB()>1 || y.getUB()<0){
            throw new Exception("variable "+y.getName()+"must be boolean but has the bounds lb="+y.getLB()+" , ub="+y.getUB()); 
        }
        Var v = numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name);
        //v <= x - M(y-1)
        addLe(v, sum(expr,prod(-M,sum(-1, y))));
        //v >= x + M(y-1)
        addGe(v, sum(expr,prod(+M,sum(-1, y))));
        //v <= +M*y
        addLe(v, prod(+M,y));
        //v >= -M*y
        addGe(v, prod(-M,y));
        return v;
    }
   
    public final Set range(int n){
        return range(0, n-1);
    }
    public final Set range(int begin, int end){
        LinkedList<Integer> list = new LinkedList<>();
        for(int i=begin; i<=end; i++){
            list.addLast(i);
        }
        return new Set(this, list);
    }
    
    /**
     * <pre>
     * Interpratation:
     *      for all j in J {
     *          //do something here
     *      }
     * Simtax:
     *      cplex.forAll(J, (j)->{
     *          //do something here
     *      });
     * Sample:
     *      cplex.forAll(J, (j)->{
     *          cplex.addGe(x[j], 1);
     *      });
     * </pre>
     * @param set set of indexes
     * @param action the action to be performed
     */
    public void forAll(Set set, Consumer action){
        set.forAll(action);
    }
    /**
     * <pre>
     * Interpratation:
     *      for all(i in I and j in J) {
     *          //do something here
     *      }
     * Simtax:
     *      cplex.forAll(I, J, (i, j)->{
     *          //do something here
     *      });
     * Sample:
     *      cplex.forAll(I, J, (i, j)->{
     *          cplex.addGe(x[i][j], 1);
     *      });
     * </pre>
     * @param set set of indexes
     * @param action the action to be performed
     */
    public void forAll(Set s1, Set s2, Consumer2p action) {
        s1.forAll(i -> 
            s2.forAll(j -> 
                action.accept(i, j)
            )
        );
    }
    public void forAll(Set s1, Set s2, Set s3, Consumer3p action) {
        s1.forAll(i -> 
            s2.forAll(j -> 
                s3.forAll(k -> 
                    action.accept(i, j, k)
                )
            )
        );
    }
    public void forAll(Set s1, Set s2, Set s3, Set s4, Consumer4p action) {
        s1.forAll(i -> 
            s2.forAll(j ->
                s3.forAll(k ->
                    s4.forAll(m ->
                        action.accept(i, j, k, m)
                    )
                )
            )
        );
    }
    /**
     * <pre>
     * Interpratation:
     *      sum_{i in I}{expression}
     * Simtax:
     *      cplex.sum(I, i -> expression)
     * Sample:
     *      cplex.sum(I, i -> cplex.prod(C[i],x[i]))
     * </pre>
     * @param set
     * @param mapper
     * @return 
     */
    public Expr sum(Set set, Function mapper){
        return set.sum(mapper);
    }
    /**
     * <pre>
     * Interpratation:
     *      sum_{i in I, j in J}{expression}
     * Simtax:
     *      cplex.sum(I, J, (i,j) -> expression)
     * Sample:
     *      cplex.sum(I, J, (i,j) -> cplex.prod(C[i][j],x[i][j]))
     * </pre>
     * @param set
     * @param mapper
     * @return 
     */
    public Expr sum(Set s1, Set s2, Function2p mapper){
        return 
            s1.sum(i -> 
                s2.sum(j -> 
                    mapper.accept(i, j)
                )
            ); 
    }
    public Expr sum(Set s1, Set s2, Set s3, Function3p mapper){
        return 
            s1.sum(i -> 
                s2.sum(j -> 
                    s3.sum(k -> 
                        mapper.accept(i, j, k)
                    )
                )
            ); 
    }
    public Expr sum(Set s1, Set s2, Set s3, Set s4, Function4p mapper){
        return 
            s1.sum(i -> 
                s2.sum(j -> 
                    s3.sum(k -> 
                        s4.sum(m -> 
                            mapper.accept(i, j, k, m)
                        )
                    )
                )
            ); 
    }
    /**
     * <pre>
     * Interpratation:
     *      sum_{i=start to end}{expression}
     * Simtax:
     *      cplex.sum(start, end, i -> expression)
     * Sample:
     *      cplex.sum(0, 5, i -> cplex.prod(C[i],x[i]))
     * </pre>
     * @param start
     * @param end
     * @param mapper
     * @return 
     */
    public Expr sum(int start, int end, Function mapper){
        return range(start, end).sum(mapper);
    }
    
    public Expr sum(Expr... array) {
        return sum(0, array.length-1, (i)->array[i]);
    }
    public Expr sum(double d, Expr... array) throws Exception {
        return sum(constant(d), sum(array));
    }

    public abstract void addMinimize(Expr obj) throws Exception;
    public abstract void addMaximize(Expr obj) throws Exception;
    
    public abstract Expr constant(double lb) throws Exception;

    public abstract void addGe(Expr expr, double d) throws Exception;
    public abstract void addGe(Expr expr1, Expr expr2) throws Exception;
    
    public abstract void addLe(Expr expr, double d) throws Exception;
    public abstract void addLe(Expr expr1, Expr expr2) throws Exception;
    
    public abstract void addEq(Expr expr, double d) throws Exception;
    public abstract void addEq(Expr expr1, Expr expr2) throws Exception;
    
    public abstract void exportModel(String fname) throws Exception;

    public abstract boolean solve() throws Exception;

    public abstract String getStatus() throws Exception;

    public abstract double getObjValue() throws Exception;
    
    public abstract double getValue(Var var) throws Exception;

    public abstract double getValue(Expr expr) throws Exception;

    public abstract void delete() throws Exception;

    public abstract void setOut(PrintStream stream) throws Exception;

    public abstract void setWarning(PrintStream stream) throws Exception;



    

    

    
    
}
