/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.tests;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBExpr;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

/**
 *
 * @author Marcio
 */
public class TestGurobi {

    public static void main(String[] args) {
        try {
            String nutrients[] = new String[]{"Proteina", "Calcio"};
            String ingredients[] = new String[]{"Osso", "Soja", "Peixe"};
            
            //Ci : custo por kg de ingrediente i
            double C[] = {0.56, 0.81, 0.46};     
            //Aji: quantia do nutriente j por kg de ingrediente i
            double A[][] = {
                {0.2, 0.5, 0.4},
                {0.6, 0.4, 0.4},
            };
            //Bj: quantia minima de nutriente j na racao
            double B[] = {0.3, 0.5};
            
            // Model
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);
            
            model.set(GRB.StringAttr.ModelName, "mixture");


            // Create decision variables for the foods to buy
            GRBVar[] x = new GRBVar[ingredients.length];
            for (int i = 0; i < x.length; ++i) {
                x[i] = model.addVar(0, GRB.INFINITY, C[i], GRB.INTEGER, ingredients[i]);
            }

            // The objective is to minimize the costs
            model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);

            // Nutrition constraints
            for (int j = 0; j < nutrients.length; ++j) {
                GRBLinExpr sum = new GRBLinExpr();
                for (int i = 0; i < x.length; ++i) {
                    sum.addTerm(A[j][i], x[i]);
                }
                model.addConstr(sum, GRB.GREATER_EQUAL, B[j]*5, nutrients[j]);
            }

            
            GRBLinExpr sum1 = new GRBLinExpr();
            for (int i = 0; i < x.length; ++i) {
                sum1.addTerm(1, x[i]);
            }
            model.addConstr(sum1, GRB.EQUAL, 5, "one");
            
            model.write("model.lp");
            // Solve
            
            model.optimize();
            
            if (model.get(GRB.IntAttr.Status) == GRB.Status.OPTIMAL) {
                System.out.println("Cost: " + model.get(GRB.DoubleAttr.ObjVal));
                System.out.println("\nX:");
                for (int i = 0; i < x.length; ++i) {
                    System.out.printf("x[%d] = %g\n", i, x[i].get(GRB.DoubleAttr.X));
                }
            } else {
                System.out.println("No solution");
            }

            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". "
                    + e.getMessage());
        }
    }

}
