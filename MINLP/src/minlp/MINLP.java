/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class MINLP extends IloCplex{
    private final double M;    //big M

    public MINLP() throws IloException {
        this(1e6);
    }
    public MINLP(double bigM) throws IloException {
        this.M = bigM;
    }
    /**
     * create a new continuous variable with default bounds.<br>[lb,ub] &harr [-&#8734,+&#8734]
     * @return
     * @throws IloException 
     */
    public IloNumVar numVar() throws IloException{
        return numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
    /**
     * creates a array of new continuous variables with default bounds.<br>[lb,ub] &harr [0,+&#8734]
     * @param s
     * @return
     * @throws IloException 
     */
    public IloNumVar[] numVarArrayPos(Set s) throws IloException{
        return numVarArray(s.size(), 0, Double.POSITIVE_INFINITY);
    }
    
    
    private int count_v = 0;
    /**<pre>
     * define: 
     *      v = addProd(x,y)  &harr v = x*y
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
     * </pre>
     * @param x a linear expression
     * @param y a boolean variable {0,1}
     * @return v as a new continous variable
     */
    public IloNumVar addProd(IloNumExpr x, IloIntVar y) throws IloException{
        if(y.getMax()>1 || y.getMin()<0){
            throw new IloException("y must be boolean but has interval lb="+y.getMin()+" , ub="+y.getMax()); 
        }
        IloNumVar v = numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "v"+count_v);
        count_v++;
        //v <= x - M(y-1)
        addLe(v, sum(x,prod(-M,sum(y,-1))));
        //v >= x + M(y-1)
        addGe(v, sum(x,prod(+M,sum(y,-1))));
        //v <= +M*y
        addLe(v, prod(+M,y));
        //v >= -M*y
        addGe(v, prod(-M,y));
        return v;
    }
   
    public Set<Integer> range(int n){
        return range(0, n-1);
    }
    public Set<Integer> range(int begin, int end){
        LinkedList<Integer> list = new LinkedList<>();
        for(int i=begin; i<=end; i++){
            list.addLast(i);
        }
        return new Set<>(this, list);
    }
    
    /*public <T> Stream forAll(Function<T, ?> mapper){
        return set.stream().map((e) -> e);
    }*/
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
     * @param <T> type of set index
     * @param set set of indexes
     * @param action the action to be performed
     */
    public <T> void forAll(Set<T> set, MINLPConsumer<? super T> action){
        set.forAll(action);
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
     * @param <T>
     * @param set
     * @param mapper
     * @return 
     */
    public <T> IloNumExpr sum(Set<T> set, MINLPFunction<? super T, IloNumExpr> mapper){
        return set.sum(mapper);
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
    public IloNumExpr sum(int start, int end, MINLPFunction<Integer, IloNumExpr> mapper){
        return range(start, end).sum(mapper);
    }
}
