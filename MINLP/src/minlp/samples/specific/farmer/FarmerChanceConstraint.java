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
public class FarmerChanceConstraint {
    
    
    public final MINLP mip;
    public final Set I;
    public final Var x[];
    public final Var w[];
    public final Var z[];
    public final Var y[];
    
    
    public FarmerChanceConstraint(double Delta, double variation) throws Exception {
        mip = new CPLEX(); //to diferent solvers use: CPLEX or Gurobi or GLPK;
        //conjunto dos itens I = {0, 1, 2}   <->   {Trigo, Milho, Beterraba}
        I = mip.range(3);
        
        //xi >= 0, quantia em acres dedicada a plantacao de cada item
        x = mip.numVarArray(I, 0, Double.POSITIVE_INFINITY, "x");
        
        //wi >= 0, quantia em tons vendida abaixo da cota
        w = mip.numVarArray(I, 0, Double.POSITIVE_INFINITY, "w");
        
        //zi >= 0, quantia em tons vendida acima da cota
        z = mip.numVarArray(I, 0, Double.POSITIVE_INFINITY, "z");
        
        //yi >= 0, quantia em tons comprada para atender a demanda interna
        y = mip.numVarArray(I, 0, Double.POSITIVE_INFINITY, "y");
        
        //obj = sum_i{-CPi * xi + CVi * wi + GVi * zi - CCi * yi}
        Expr obj = mip.sum(I, i -> mip.prod(-CPi[i], x[i]).sumProd(CVi[i], w[i]).sumProd(GVi[i], z[i]).sumProd(-CCi[i], y[i]));
        
        mip.addMaximize(obj);
        
        //atendimento da demanda 
        //Pi*xi >= Di + wi + zi - yi
        //Pr(Pi*xi < Di+wi+zi-yi) <= 0.001
        //(Di+wi+zi-yi)-avg(Pi*xi) <= cdf^{-1}(0.001)*std(Pi*xi)
        
        //p(rPi) = 1/0.4
        //Pr(rPi<v) = cdf(v) = (v-(Pi*0.8))/0.4
        //Pr(rPi<v) <= 0.001  <==>  cdf(v)>=0.001
        //(v-(Pi*0.8))/0.4 >= 0.001
        //v >= 0.001*0.4 + Pi*0.8
        
        //Pr(vPi < (Di+wi+zi-yi)/xi) <= 0.001   <==> cdf( (Di+wi+zi-yi)/xi ) <= 0.001
        //((Di+wi+zi-yi)/xi - (Pi*0.8) / 0.4) <= 0.001
        //(Di+wi+zi-yi)/xi - (Pi*0.8) <= 0.001*0.4
        //(Di+wi+zi-yi) - (Pi*0.8)*xi <= 0.001*0.4*xi
        mip.forAll(I, (i)->{
            //mip.sum(Di[i], w[i], z[i]).minus(y[i]).sumProd(-Pi[i]*(1-variation), x[i]).addLe(mip.prod(Delta*2*variation*Pi[i], x[i]));
            
            //(Di+wi+zi-yi) <= xi*Pi*(0.8 + 0.001*0.4)
            mip.sum(Di[i], w[i], z[i]).minus(y[i]).addLe(x[i].prod(Pi[i]*((1-variation)+Delta*2*variation)));
        });
        
        
        //obj>=110000
        //-CPi * xi + CVi * wi + GVi * zi - CCi * yi >= 110000
        //Pi*xi = Di + wi + zi - yi
        //-CPi*(Di + wi + zi - yi) + Pi*CVi*wi + Pi*GVi*zi - Pi*CCi*yi >= 110000*Pi
        //Pi*(CVi*wi + GVi*zi - CCi*yi-110000) >= CPi*(Di + wi + zi - yi)
        //Pi >= CPi*(Di + wi + zi - yi)/(CVi*wi + GVi*zi - CCi*yi-110000)
        //Pi >= v
        //Pr(rPi<v) = cdf(v) = (v-Pi*0.8)/0.4
        //Pr(rPi<v) <= 0.001   <==> v >= Pi*0.8 + 0.001*0.4 
        //(CPi*(Di + wi + zi - yi) >= (Pi*0.8 + 0.001*0.4)*(CVi*wi + GVi*zi - CCi*yi-110000)
//        mip.forAll(I, (i)->{
//            Expr expr = mip.prod(CVi[i], w[i]).sumProd(GVi[i], z[i]).sumProd(-CCi[i], y[i]).minus(125000);
//            mip.sum(Di[i], w[i], z[i]).minus(y[i]).prod(CPi[i]).addGe(mip.prod((Pi[i]*(1-variation) + Pi[i]*Delta*2*variation), expr));
//        });
        
        
        
        //Pr(obj<110000) <= 0.001
        //Pr(-CPi * xi + CVi * wi + GVi * zi - CCi * (Di+wi+zi-rPi*xi) < 110000) <= 0.001
        //Pr( CCi*rPi*xi < 110000 + CPi * xi + (CCi - CVi) * wi + (CCi - GVi) * zi + CCi * Di) <= 0.001
        //Pr( rPi < (110000 + CPi * xi + (CCi - CVi) * wi + (CCi - GVi) * zi + CCi * Di)/ (CCi*xi) ) <= 0.001
        //110000 + CPi * xi + (CCi - CVi) * wi + (CCi - GVi) * zi -(Pi*0.8)*(CCi*xi) + CCi * Di  <= 0.001*0.4*(CCi*xi)
//        mip.forAll(I, (i)->{
//            mip.constant(110000).sumProd(CPi[i], x[i]).sumProd(CCi[i]-CVi[i], w[i]).sumProd(CCi[i]-GVi[i], z[i]).sumProd(-Pi[i]*(1-variation)*CCi[i], x[i]).sum(CCi[i]*Di[i]).addLe(mip.prod(Delta*2*variation*Pi[i], x[i]));
//        });


        //atendimento da demanda 
        //Pi*xi + yi = Di + wi + zi
//        mip.forAll(I, (i)->{
//            mip.prod(Pi[i], x[i]).sum(y[i]).minus(w[i]).minus(z[i]).addGe(Di[i]);
//        });
        
        
        
        // y = (v-(Pi*0.8))/0.4
        // y*0.4 + Pi*0.8 = v = cdf^{-1}(y)
        //cdf^{-1}(y) = y*0.4 + Pi*0.8 
        //Pr(vPi < (Di+wi+zi-yi)/xi) <= 0.001 <==> 
        //(Di+wi+zi-yi)*0.4/xi + Pi*0.8 <= 0.001
        //(Di+wi+zi-yi)*0.4 + Pi*0.8*xi <= 0.001*xi
        /*mip.forAll(I, (i)->{
            mip.sum(Di[i], w[i], z[i]).minus(y[i]).prod(2*variation).sumProd(Pi[i]*(1-variation), x[i]).addLe(mip.prod(Delta, x[i]));
        });*/
        
        //limite das terras 
        //sum_i{xi} <= area
        mip.sum(I, (i)-> x[i]).addLe(area);
        
        //limite de cota do governo
        //wi <= GLi
        mip.forAll(I, (i)->{
            w[i].setUB(GLi[i]);
        });
    }
    
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        FarmerChanceConstraint model = new FarmerChanceConstraint(0.001, 0.2);
        model.mip.exportModel("model-chance.lp");
        if(model.mip.solve()){
            System.out.println(model.mip.getStatus());
            System.out.printf("lucro = %1.1f\n",model.mip.getObjValue());
            System.out.printf("%6s %6s %6s %6s %6s %6s %6s %6s\n", "itens", "xi", "wi", "zi", "yi", "Pi*xi", "Di", "Pi");
            model.mip.forAll(model.I, (i)->{
                System.out.printf("%6d %6.1f %6.1f %6.1f %6.1f %6.1f %6.1f %6.1f\n", i, 
                        model.x[i].value(), model.w[i].value(), model.z[i].value(), model.y[i].value(), model.x[i].prod(Pi[i]).value(), Di[i], Pi[i]);
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
