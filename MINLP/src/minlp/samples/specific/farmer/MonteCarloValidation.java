/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.samples.specific.farmer;

import java.util.Locale;
import java.util.Random;

/**
 *
 * @author Marcio
 */
public class MonteCarloValidation {
    public static Random rnd = new Random();
    
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        FarmerDeterministic model = FarmerDeterministic.build();
        
        model.mip.exportModel("model.lp");
        
        if(model.mip.solve()){
            System.out.println(model.mip.getStatus());
            System.out.println("==================================[ cenario ideal ]=========================================");
            System.out.printf("lucro = %1.1f\n",model.mip.getObjValue());
            System.out.printf("%6s %6s %6s %6s %6s %6s %6s %6s\n", "itens", "xi", "wi", "zi", "yi", "Pi*xi", "Di", "Pi");
            model.mip.forAll(model.I, (i)->{
                System.out.printf("%6d %6.1f %6.1f %6.1f %6.1f %6.1f %6.1f %6.1f\n", i, 
                        model.x[i].value(), model.w[i].value(), model.z[i].value(), model.y[i].value(), 
                        model.x[i].prod(model.Pi[i]).value(), model.Di[i], model.Pi[i]);
            });
            double X[] = model.mip.tryValues(model.x);
            MonteCarloValidation MC = new MonteCarloValidation();
            MC.validate(X, model.Pi, 0.2, model.mip.getObjValue(), 1000, 1);
        }else{
            System.out.println(model.mip.getStatus());
        }
        model.mip.delete();
    }
    /**
     * O modelo acima é considerado uma solução deterministica, entretanto o parametro de produção da fazenda Pi
     * é dada em termos médios, sendo muito influenciado por diversos fatores como por exemplo o clima e as temporadas
     * de chuva e seca. Sendo assim o valor de Pi real (rPi) não sabemos, mas supomos que siga uma distribuição uniforme
     * entre rPi \in [Pi*0.8 e Pi*1.2] (20% de variação em torno do valor médio)
     * Assim será feita uma simulação de monte carlo considerando a solução ótima acima e vendo qual é o verdadeiro lucro 
     * do fazendeiro para diversas simulações
     */
    public void validate(double X[], double avgPi[], double variation, double objective, int N, int PRINT) throws Exception{
        rnd = new Random(2);
        if(PRINT==1){
            System.out.printf("%6s %6s %6s %6s %6s %6s %6s %6s %6s %6s %6s %6s %6s %8s %8s\n", "n", "rP0", "rP1", "rPi2", "w0", "w1", "w2", "z0", "z1", "z2", "y0", "y1", "y2","objective", "precentage");
        }
        double media = 0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        
        for(int n=0; n<N; n++){//numero de simulações
            //sorteando aletoriamente um possível valor de Pi dentro do intervalo estabelecido
            double rPi[] = uniform(avgPi, variation);
            if(PRINT<=0){
                System.out.printf("----------------------------------[ monte carlo %2d ]-----------------------------------------\n", n);
            }
            FarmerDeterministic sim = new FarmerDeterministic(rPi);
            sim.mip.setOut(null);
            sim.mip.fix(sim.x, X);
            if(sim.mip.solve()){
                if(PRINT<=0){
                    System.out.printf("lucro = %1.1f\n",sim.mip.getObjValue());
                    System.out.printf("%6s %6s %6s %6s %6s %6s %6s %6s\n", "itens", "xi", "wi", "zi", "yi", "Pi*xi", "Di", "rPi");
                    sim.mip.forAll(sim.I, (i)->{
                        System.out.printf("%6d %6.1f %6.1f %6.1f %6.1f %6.1f %6.1f %6.1f\n", i, sim.x[i].value(), sim.w[i].value(), sim.z[i].value(), sim.y[i].value(), sim.x[i].prod(rPi[i]).value(), sim.Di[i], rPi[i]);
                    });
                }else if(PRINT<=1){
                    System.out.printf("%6d %6.2f %6.2f %6.2f %6.1f %6.1f %6.0f %6.1f %6.1f %6.1f %6.1f %6.1f %6.1f %8.0f %8.2f\n", 
                            n, rPi[0], rPi[1], rPi[2], 
                            sim.w[0].value(), sim.w[1].value(), sim.w[2].value(), 
                            sim.z[0].value(), sim.z[1].value(), sim.z[2].value(),
                            sim.y[0].value(), sim.y[1].value(), sim.y[2].value(),
                            sim.mip.getObjValue(), (sim.mip.getObjValue()-objective)*100.0/objective);
                }
                min = Math.min(min, sim.mip.getObjValue());
                max = Math.max(max, sim.mip.getObjValue());
                media += sim.mip.getObjValue();
            }
        }
        media /= N;
        if(PRINT<=2){
            System.out.printf("average = %12.2f -> %6.1f %%\n", media, (media-objective)*100.0/objective);
            System.out.printf("maximum = %12.2f -> %6.1f %%\n", max, (max-objective)*100.0/objective);
            System.out.printf("minimum = %12.2f -> %6.1f %%\n", min, (min-objective)*100.0/objective);
        }
    }
    
    
    public static double[] uniform(double ref[], double variation){
        double r[] = new double[ref.length];
        for(int i=0; i<ref.length; i++){
            r[i] = uniform(ref[i], variation);
        }
        return r;
    }
    public static double uniform(double ref, double variation){
        return ref*(1 + variation*(rnd.nextDouble()*2-1));
    }
    
}
