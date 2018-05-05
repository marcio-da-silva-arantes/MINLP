/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples;

import minlp.Expr;
import minlp.MINLP;
import minlp.Set;
import minlp.Var;
import minlp.cplex.CPLEX;
import minlp.glpk.GLPK;

/**
 *
 * @author Marcio
 */
public class TSP {
    public static void main(String[] args) throws Exception {
        MINLP lp = new GLPK();
        //conjunto das cidades i, j \in N 
        Set N = lp.range(5);
        
        //C : custo de ir da cidade i para cidade j
        double C[][] = {
            {0, 	89, 	47, 	9, 	52},
            {65, 	0, 	64, 	3, 	17},
            {40, 	76, 	0, 	77, 	36},
            {5, 	15, 	49, 	0, 	21},
            {10,	8, 	94, 	13, 	0}
        };     

        //x >= 0 forall i, j
        Var x[][] = lp.boolVarArray(N, N, "x");
        Var u[] = lp.numVarArray(N, 0, Double.POSITIVE_INFINITY, "u");
        
        //obj = sum_{i,j}{Cij * Xij}
        Expr obj = lp.sum(N,N, (i,j) -> lp.prod(C[i][j],x[i][j]));
        
        lp.addMinimize(obj);
        
        //for all j in N
        lp.forAll(N, (j)->{
            //sum_i{Xij} == 1 
            lp.addEq(lp.sum(N, i -> x[i][j]), 1);
        });
        
        //for all i in N
        lp.forAll(N, (i)->{
            //sum_j{Xij} == 1 
            lp.addEq(lp.sum(N, j -> x[i][j]), 1);
        });

        //for all i in N and j in N with i > 0 and j > 0
        lp.forAll(N, N, (i,j)->{
            if(i<N.size()-1 && j<N.size()-1 && i!=j){
                Expr aux[] = new Expr[3];
                aux[0] = lp.prod(+1, u[i]);
                aux[1] = lp.prod(-1, u[j]);
                aux[2] = lp.prod(N.size(), x[i][j]);
                lp.addLe(lp.sum(aux), N.size()-1);
            }
        });
        
        //for all i in N
        lp.forAll(N, (i)->{
            x[i][i].setUB(0);
        });
        
        lp.exportModel("model.lp");
        
        if(lp.solve()){
            System.out.println(lp.getStatus());
            System.out.println(lp.getObjValue());
            System.out.println("---------------- x ------------------");
            lp.forAll(N, (i)->{
                lp.forAll(N, (j)->{
                    System.out.printf("%5.2f ", lp.getValue(x[i][j]));
                });
                System.out.println();
            });
            System.out.println("---------------- u ------------------");
            lp.forAll(N, (i)->{
                if(i<N.size()-1){
                    System.out.printf("%5f ", lp.getValue(u[i]));
                }
            });
            System.out.println();
        }else{
            System.out.println(lp.getStatus());
        }
        lp.delete();
    }
}
