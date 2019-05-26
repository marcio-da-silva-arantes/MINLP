/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples.medium;

import minlp.Expr;
import minlp.MINLP;
import minlp.Set;
import minlp.Var;
import minlp.cplex.CPLEX;
import minlp.glpk.GLPK;
import minlp.gurobi.Gurobi;

/**
 *
 * @author Marcio
 */
public class TSP {
    public static void main(String[] args) throws Exception {
        MINLP mip = new GLPK(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        //conjunto das cidades i, j \in N 
        Set N = mip.range(5);
       
        //C : custo de ir da cidade i para cidade j
        double C[][] = {
            {0, 	89, 	47, 	9, 	52},
            {65, 	0, 	64, 	3, 	17},
            {40, 	76, 	0, 	77, 	36},
            {5, 	15, 	49, 	0, 	21},
            {10,	8, 	94, 	13, 	0}
        };     

        //x >= 0 forall i, j
        Var x[][] = mip.boolVarArray(N, N, "x");
        Var u[] = mip.numVarArray(N, 0, Double.POSITIVE_INFINITY, "u");
        
        //obj = sum_{i,j}{Cij * Xij}
        Expr obj = mip.sum(N,N, (i,j) -> mip.prod(C[i][j],x[i][j]));
        
        mip.addMinimize(obj);
        
        //for all j in N
        mip.forAll(N, (j)->{
            //sum_i{Xij} == 1 
            mip.addEq(mip.sum(N, i -> x[i][j]), 1);
        });
        
        //for all i in N
        mip.forAll(N, (i)->{
            //sum_j{Xij} == 1 
            mip.addEq(mip.sum(N, j -> x[i][j]), 1);
        });

        //for all i in N and j in N with i > 0 and j > 0
        mip.forAll(N, N, (i,j)->{
            if(i<N.size()-1 && j<N.size()-1 && i!=j){
                
                //u[i] - u[j] + N*x[i][j] <= N-1
                Expr aux[] = new Expr[3];
                aux[0] = mip.prod(+1, u[i]);
                aux[1] = mip.prod(-1, u[j]);
                aux[2] = mip.prod(N.size(), x[i][j]);
                mip.addLe(mip.sum(aux), N.size()-1);
                
                
//                u[i].sum(u[j].prod(-1))
//                    .sum(x[i][j].prod(N.size()))
//                    .addLe(N.size()-1);
//                
//                u[i].sumProd(-1, u[j])
//                    .sumProd(N.size(), x[i][j])
//                    .addLe(N.size()-1);
                
                //mip.addLe(u[i].minus(u[j]).sumProd(N.size(),x[i][j]), N.size()-1);
                //u[i] - u[j] + N.size()*x[i][j] <= N.size()-1;
                //mip.addConstraint("u[i] - u[j] + N*x[i][j] <= N-1", i, j);    //in future
            }
        });
        
        //for all i in N
        mip.forAll(N, (i)->{
            x[i][i].setUB(0);
        });
        
        mip.exportModel("model.lp");
        
        if(mip.solve()){
            System.out.println(mip.getStatus());
            System.out.println(mip.getObjValue());
            System.out.println("---------------- x ------------------");
            mip.forAll(N, (i)->{
                mip.forAll(N, (j)->{
                    System.out.printf("%5.2f ", mip.getValue(x[i][j]));
                });
                System.out.println();
            });
            System.out.println("---------------- u ------------------");
            mip.forAll(N, (i)->{
                if(i<N.size()-1){
                    System.out.printf("%5f ", mip.getValue(u[i]));
                }
            });
            System.out.println();
        }else{
            System.out.println(mip.getStatus());
        }
        mip.delete();
    }
}
