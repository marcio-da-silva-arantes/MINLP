/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.stocastic;

/**
 *
 * @author Marcio
 */
public abstract class Distribution {
    public final double lb, ub, precision;
    public final int N;

    public Distribution(double lb, double ub, int N) throws Exception {
        this(lb, ub, N, 1e-9);
    }
    public Distribution(double lb, double ub, int N, double precision) throws Exception {
        this.lb = lb;
        this.ub = ub;
        this.N = N;
        this.precision = precision;
        check();
    }
    
    public final void check() throws Exception{
        double total = Ge(lb);
        if(Math.abs(total-1)>precision){
            throw new Exception("This is not a valid distribution, total probability found "+total);
        }
        System.out.println("Total probability found "+total);
    }
    
    public abstract double P(double x) throws Exception;
    
    public final double Ge(double x) throws Exception{
        double sum = 0;
        double dx = (ub-x)/N;
        for(int i=0; i<N; i++){
            double p1 = P(x);
            double p2 = P(x+dx);
            sum += (p1+p2)*dx/2.0;
            x += dx;
        }
        //System.out.println(x);
        return sum;
    }
    public final double Le(double x) throws Exception{
        double sum = 0;
        double dx = (x-lb)/N;
        for(int i=0; i<N; i++){
            double p1 = P(x);
            double p2 = P(x-dx);
            sum += (p1+p2)*dx/2.0;
            x -= dx;
        }
        //System.out.println(x);
        return sum;
    }
    
    public double invGe(double p) throws Exception{
        return invGeRec(p, lb, ub, (ub-lb)/N);
    }
    private final double invGeRec(double p, double lb, double ub, double tolerance) throws Exception{
        double m = (lb+ub)/2;
        double x = Ge(m);
        System.out.printf("m = %f -> x = %f\n", m, x);
        if(Math.abs(ub-lb)<tolerance){
            return m;
        }else if(x>p){
            return invGeRec(p, m, ub, tolerance);
        }else{
            return invGeRec(p, lb, m, tolerance);
        }
    }
    public double invLe(double p) throws Exception{
        return invLeRec(p, lb, ub, (ub-lb)/N);
    }
    private final double invLeRec(double p, double lb, double ub, double tolerance) throws Exception{
        double m = (lb+ub)/2;
        double x = Le(m);
        System.out.printf("m = %f -> x = %f\n", m, x);
        if(Math.abs(ub-lb)<tolerance){
            return m;
        }else if(x>p){
            return invLeRec(p, lb, m, tolerance);
        }else{
            return invLeRec(p, m, ub, tolerance);
        }
    }
    
    
    public static void main(String[] args) throws Exception {
//        Distribution u = new Distribution(-10, 10, 1000) {    //uniform distribution
//            @Override
//            public double P(double x) throws Exception {
//                return 1.0/(ub-lb);
//            }
//        };
        Distribution u = new Distribution(-10, 10, 1000) {  //normal distribution
            private final double avg = 0.0;
            private final double std = 1.0;
            @Override
            public double P(double x) throws Exception {
                return Math.exp(-Math.pow((x-avg)/std, 2)/2)/(std*Math.sqrt(2*Math.PI));
            }
        };   
//        Distribution u = new Distribution(-10, 10, 1000) {  //crescente distribution
//            @Override
//            public double P(double x) throws Exception {
//                return (x-lb)*2.0/Math.pow(ub-lb, 2);
//            }
//        };
//        Distribution u = new Distribution(-10, 10, 1000) {  //triangular distribution
//            private final double center = 0;
//            @Override
//            public double P(double x) throws Exception {
//                if(x<=center){
//                    return (x-lb)/Math.pow(center-lb, 2);
//                }else{
//                    return (ub-x)/Math.pow(ub-center, 2);
//                }
//            }
//        };
    
        double x = 3.0;
        System.out.printf("Probability of %f is %f\n", x, u.P(x));
        System.out.printf("Probability of be >= %f is %f\n", x, u.Ge(x));
        System.out.printf("Probability of be <= %f is %f\n", x, u.Le(x));
        
        
        for(double v=-3.0; v<=3.0; v+=0.1){
            System.out.printf("v = %5.2f | p(x>v) = %8.6f | p(x<v) = %8.6f\n", v, u.Ge(v), u.Le(v));
        }
        
        System.out.printf("invGe(%f)=%f\n", 0.022751, u.invGe(0.022751));
        System.out.printf("invGe(%f)=%f\n", 0.4, u.invGe(0.4));
        System.out.printf("invGe(%f)=%f\n", 0.5, u.invGe(0.5));
        
        
        
    }
}
