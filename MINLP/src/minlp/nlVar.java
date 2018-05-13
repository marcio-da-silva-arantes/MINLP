/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;

/**
 *
 * @author Marcio
 */
public class nlVar {
    public final Var[] y;
    public final Var value;
    
    private final MINLP mip;
    private final double lb, precision;
    private final String name;
    
    public nlVar(MINLP mip, double lb, double ub, int nBits, String name) throws Exception {
        if(nBits<1 || nBits>60){
            throw new Exception("Out of bound, number of bits must be between 1 and 60");
        }
        this.mip = mip;
        this.lb = lb;
        this.name = name;
        this.precision = (ub-lb)/(Math.pow(2, nBits)-1);
        this.y = new Var[nBits];
        for(int i=0; i<nBits; i++){
            this.y[i] = mip.boolVar(name+".bits("+i+")");
        }
        Expr sum = mip.constant(lb);
        long base = 1;
        for (Var yi : y) {
            sum = mip.sum(sum, mip.prod(base*precision, yi));
            base *= 2;
        }
        this.value = mip.numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name);
        mip.addEq(value, sum);
    }
    
    private int count = 0;
    public Expr linerizedProd(Expr x) throws Exception{
        Expr sum = mip.prod(lb, x);
        long base = 1;
        for(int i=0; i<y.length; i++){
            Var vi = mip.linerizedProd(x, y[i], name+".prod("+count+","+i+")");
            sum = mip.sum(sum, mip.prod(base*precision, vi));
            base *= 2;
        }
        count++;
        return sum;
    }
}
