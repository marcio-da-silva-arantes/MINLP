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
    
    public MINLP() throws IloException {
    }

    public IloNumVar numVar() throws IloException{
        return numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IloException {
        // TODO code application logic here
        MINLP cplex = new MINLP();
        // x in R   / [-4, +3] 
        IloNumVar x = cplex.numVar(-4, 3);
        // y in {0,1}
        IloIntVar y = cplex.boolVar();
        
        //-------------------[ x*y ]------------------
        //x*y = v
        //dedution: if(y=1){ v=x }else{ v=0 }
        //linear transformation:
        //  (y-1)*M + x <= v <= x - M(y-1)
        //  -M*y <= v <= M*y
        IloNumVar v = cplex.numVar();
        //v <= x - M(y-1)
        cplex.addLe(v, cplex.sum(x,cplex.prod(-M,cplex.sum(y,-1))));
        //v >= x + M(y-1)
        cplex.addGe(v, cplex.sum(x,cplex.prod(+M,cplex.sum(y,-1))));
        //v <= +M*y
        cplex.addLe(v, cplex.prod(+M,y));
        //v >= -M*y
        cplex.addGe(v, cplex.prod(-M,y));
        
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
    
    
}
