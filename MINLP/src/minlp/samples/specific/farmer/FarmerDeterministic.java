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

/**
 *
 * @author Marcio
 */
public class FarmerDeterministic {
    /*
    Um fazendeiro deseja tomar a melhor decisão quanto ao plantio de Trigo, Milho e Beterraba.
    Ele possui 500 acres de terra e precisa tomar a melhor decisão do quando em area vai dedicar
    a plantação de cada um destes plantio. Chamaremos Xi a quantia em area (acres) dedicadas a 
    plantação de cada um destes itens. Cada item possui um custo de plantio CPi dado em ($/acres),
    o fazendeiro também conhece o quanto rende em média produção de sua fazenda para cada um destes
    itens chamada de Pi que é dada em (tons/acres). Após a colheita o fanzendeiro precisa guardar
    uma parcela dedicada a demanda interna para alimentação do gado Di dada em (tons). Todo excedente
    o fazendeiro poderá vender no mercado, a um custo de venda CVi dado em ($/tons). Porem o governo 
    estabeleceu uma cota limite do governno de GLi em (tons), acima da cota o governo estabelece um preço menor 
    e recolhe a diferença como inposto do governo, o preço do governo é dado por GVi em ($/tons). 
    Caso o fazendeiro, não produza o suficiente para alimentação do seu proprio gado ele deverá 
    comprar os itens no mercado interno a um custo de compra CCi dado em ($/tons)
    */    
    public static double area  = 500.0;                          //area de plantio  (acres)     o fazendeiro tem 500 acres de terras para dedicar ao plantio
    public static double CPi[] = {150.0, 230.0,  260.0};         //custo de plantio ($/acre)    custos em $/acre no plantio de Trigo, Milho e Beterraba respectivamente
    public static double Pi[]  = {  2.5,   3.0,   20.0};         //producao media   (tons/acre) capacidade média produtiva da fazenda em tons/acre para Trigo, Milho e Beterraba 
    public static double Di[]  = {200.0, 240.0,    0.0};         //demanda interna  (tons)      demanda do gado em tons para Trigo, Milho
    public static double CVi[] = {170.0, 150.0,   36.0};         //custo de venda   ($/tons)    custo de venda no mercado abaixo da cota limite para Trigo, Milho, Beterraba
    public static double GLi[] = {  1e9,   1e9, 6000.0};         //governo limite   (tons)      cota limite do governo apenas para Beterraba (1e9 indica infinito, sem limites)
    public static double GVi[] = {  0.0,   0.0,   10.0};         //governo venda    ($/tons)    custo de venda no mercado acima da cota limite para Beterraba
    public static double CCi[] = {238.0, 210.0,    1e9};         //custo de compra  ($/tons)    custo de compra no mercado para Trigo e Milho
    
    /*
    public static double area  = 500.0;                          //area de plantio  (acres)     o fazendeiro tem 500 acres de terras para dedicar ao plantio
    public static double CPi[] = {150.0, 230.0,  260.0};         //custo de plantio ($/acre)    custos em $/acre no plantio de Trigo, Milho e Beterraba respectivamente
    public static double Pi[]  = {  2.5,   3.0,   20.0};         //producao media   (tons/acre) capacidade média produtiva da fazenda em tons/acre para Trigo, Milho e Beterraba 
    public static double Di[]  = {200.0, 240.0,    0.0};         //demanda interna  (tons)      demanda do gado em tons para Trigo, Milho
    public static double CVi[] = {170.0, 150.0,   36.0};         //custo de venda   ($/tons)    custo de venda no mercado abaixo da cota limite para Trigo, Milho, Beterraba
    public static double GLi[] = {  1e9,   1e9, 6000.0};         //governo limite   (tons)      cota limite do governo apenas para Beterraba (1e9 indica infinito, sem limites)
    public static double GVi[] = {  0.0,   0.0,   10.0};         //governo venda    ($/tons)    custo de venda no mercado acima da cota limite para Beterraba
    public static double CCi[] = {2380.0, 2100.0,  1e9}; //(big) //custo de compra  ($/tons)    custo de compra no mercado para Trigo e Milho
    */
    
    public final MINLP mip;
    public final Set I;
    public final Var x[];
    public final Var w[];
    public final Var z[];
    public final Var y[];
    
    
    public FarmerDeterministic(double rPi[]) throws Exception {
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
        //Pi*xi + yi = Di + wi + zi
        mip.forAll(I, (i)->{
            mip.prod(rPi[i], x[i]).sum(y[i]).minus(w[i]).minus(z[i]).addEq(Di[i]);
        });
        
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
        FarmerDeterministic model = new FarmerDeterministic(Pi);
        model.mip.exportModel("model-deterministic.lp");
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
            MC.validate(X, model.Pi, 0.2, model.mip.getObjValue(), 10000, 2);
            
        }else{
            System.out.println(model.mip.getStatus());
        }
        model.mip.delete();
    }
    
    public static FarmerDeterministic build() throws Exception{
        return new FarmerDeterministic(Pi);
    }
}
