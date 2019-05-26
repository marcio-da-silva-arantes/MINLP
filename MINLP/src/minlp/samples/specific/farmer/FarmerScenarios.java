/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples.specific.farmer;

import java.util.Locale;
import minlp.Expr;
import minlp.MINLP;
import minlp.Set;
import minlp.Var;
import minlp.cplex.CPLEX;
import static minlp.samples.specific.farmer.FarmerDeterministic.*;
/**
 *
 * @author Marcio
 */
public class FarmerScenarios {

    public final MINLP mip;
    public final Set I;
    public final Set S;
    public final Var x[];
    public final Var w[][];
    public final Var z[][];
    public final Var y[][];
    public final double vPsi[][];
    
    
    public FarmerScenarios(int nS, double variation) throws Exception {
        mip = new CPLEX(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        //conjunto dos itens I = {0, 1, 2}   <->   {Trigo, Milho, Beterraba}
        I = mip.range(3);
        //conjunto dos cenarios
        S = mip.range(nS);
        
        //xi >= 0, quantia em acres dedicada a plantacao de cada item
        x = mip.numVarArray(I, 0, Double.POSITIVE_INFINITY, "x");
        
        //wi >= 0, quantia em tons vendida abaixo da cota
        w = mip.numVarArray(S, I, 0, Double.POSITIVE_INFINITY, "w");
        
        //zi >= 0, quantia em tons vendida acima da cota
        z = mip.numVarArray(S, I, 0, Double.POSITIVE_INFINITY, "z");
        
        //yi >= 0, quantia em tons comprada para atender a demanda interna
        y = mip.numVarArray(S, I, 0, Double.POSITIVE_INFINITY, "y");
        
        final double ps = 1.0/S.size(); //probabilidade de acontecer cada cenario
        
        //obj = sum_i{-CPi * xi + CVi * wi + GVi * zi - CCi * yi}
        Expr stage1 = mip.sum(I, i -> mip.prod(-CPi[i], x[i]));
        Expr stage2 = mip.sum(I, S, (i,s) -> mip.prod(CVi[i], w[s][i]).sumProd(GVi[i], z[s][i]).sumProd(-CCi[i], y[s][i]).prod(ps));
        
        mip.addMaximize(stage1.sum(stage2));
        
        vPsi = new double[S.size()][I.size()];
        //atendimento da demanda 
        //Pi*xi + yi = Di + wi + zi
        mip.forAll(S,I, (s,i)->{
            vPsi[s][i] = MonteCarloValidation.uniform(Pi[i], variation);
            mip.prod(vPsi[s][i], x[i]).sum(y[s][i]).minus(w[s][i]).minus(z[s][i]).addEq(Di[i]);
        });
        
        //limite das terras 
        //sum_i{xi} <= area
        mip.sum(I, (i)-> x[i]).addLe(area);
        
        //limite de cota do governo
        //wi <= GLi
        mip.forAll(S,I, (s,i)->{
            w[s][i].setUB(GLi[i]);
        });
    }
    
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        FarmerScenarios model = new FarmerScenarios(1000, 0.2);
        model.mip.exportModel("model-scenarios.lp");
        if(model.mip.solve()){
            System.out.println(model.mip.getStatus());
            System.out.printf("lucro = %1.1f\n",model.mip.getObjValue());
            System.out.printf("%6s %6s %6s %6s %6s %6s %6s %6s\n", "itens", "xi", "avgWi", "avgZi", "avgYi", "avgPro", "Di", "avgPi");
            model.mip.forAll(model.I, (i)->{
                double avgPsi = model.mip.sum(model.S, s->model.mip.constant(model.vPsi[s][i])).value()/model.S.size();
                double avgProd = model.mip.sum(model.S, s->model.x[i].prod(model.vPsi[s][i])).value()/model.S.size();
                double avgW = model.mip.sum(model.S, s->model.w[s][i]).value()/model.S.size();
                double avgZ = model.mip.sum(model.S, s->model.z[s][i]).value()/model.S.size();
                double avgY = model.mip.sum(model.S, s->model.y[s][i]).value()/model.S.size();
                
                System.out.printf("%6d %6.1f %6.1f %6.1f %6.1f %6.1f %6.1f %6.1f\n", i, 
                        model.x[i].value(), avgW, avgZ, avgY, avgProd, Di[i], avgPsi);
            });
            
            double X[] = model.mip.tryValues(model.x);
            MonteCarloValidation MC = new MonteCarloValidation();
            MC.validate(X, Pi, 0.2, model.mip.getObjValue(), 10000, 2);
        }else{
            System.out.println(model.mip.getStatus());
        }
        model.mip.delete();
    }
    
}
