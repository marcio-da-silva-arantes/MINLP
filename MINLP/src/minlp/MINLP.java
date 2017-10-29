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

/**
 *
 * @author marcio
 */
public class MINLP extends IloCplex{
    private static final double M = 1e6;    //big M

    public static void main(String[] args) throws IloException {
        // TODO code application logic here
        MINLP cplex = new MINLP();
        // x in R   / [-4, +3] 
        IloNumVar x = cplex.numVar(-4, 3);
        // y in {0,1}
        IloIntVar y = cplex.boolVar();
        // v = x*y
        IloNumVar v = cplex.addProd(x, y);
        
        //------------------[ tests fix some thing ]-----------------
        x.setLB(-2);
        x.setUB(-2);
        cplex.addMinimize(v);
        
        if(cplex.solve()){
            System.out.println("status = "+cplex.getStatus());
            System.out.println("objective = "+cplex.getObjValue());
            System.out.println("x = "+cplex.getValue(x));
            System.out.println("y = "+cplex.getValue(y));
            System.out.println("v = "+cplex.getValue(v));
        }else{
            System.out.println("status = "+cplex.getStatus());
        }
    }
    
    
    public MINLP() throws IloException {
    }
    /**
     * create a new continuous variable with default bounds.<br>[lb,ub] &harr [-&#8734,+&#8734]
     * @return
     * @throws IloException 
     */
    public IloNumVar numVar() throws IloException{
        return numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
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
     *      let v in R
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
        IloNumVar v = numVar();
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
    
}
