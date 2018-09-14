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
        double totalGe = Ge(lb);
        if(Math.abs(totalGe-1)>precision){
            throw new Exception("This is not a valid distribution, total probability found "+totalGe);
        }
        double totalLe = Le(ub);
        if(Math.abs(totalLe-1)>precision){
            throw new Exception("This is not a valid distribution, total probability found "+totalLe);
        }
        System.out.println("Total probability found Ge "+totalGe);
        System.out.println("Total probability found Le "+totalLe);
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
        //System.out.printf("m = %f -> x = %f\n", m, x);
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
        //System.out.printf("m = %f -> x = %f\n", m, x);
        if(Math.abs(ub-lb)<tolerance){
            return m;
        }else if(x>p){
            return invLeRec(p, lb, m, tolerance);
        }else{
            return invLeRec(p, m, ub, tolerance);
        }
    }
    public static Distribution normal(double avg, double std) throws Exception{
        return normal(avg, std, avg-std*10, avg+std*10, 1000);
    }
    public static Distribution normal(final double avg, final double std, double lb, double ub, int discretization) throws Exception{
        return new Distribution(lb, ub, discretization) {  //normal distribution
            @Override
            public double P(double x) throws Exception {
                return Math.exp(-Math.pow((x-avg)/std, 2)/2)/(std*Math.sqrt(2*Math.PI));
            }
        };   
    }
    public static Distribution uniform(double lb, double ub) throws Exception{
        return uniform(lb, ub, 1000);
    }
    public static Distribution uniform(double lb, double ub, int discretization) throws Exception{
        return new Distribution(lb, ub, discretization) {    //uniform distribution
            @Override
            public double P(double x) throws Exception {
                return 1.0/(ub-lb);
            }
        };
    }
    public static Distribution growing(double lb, double ub) throws Exception{
        return growing(lb, ub, 1000);
    }
    public static Distribution growing(double lb, double ub, int discretization) throws Exception{
        return new Distribution(lb, ub, discretization) {  //crescente distribution
            @Override
            public double P(double x) throws Exception {
                return (x-lb)*2.0/Math.pow(ub-lb, 2);
            }
        };
    }
    public static Distribution triangular(final double center, double lb, double ub) throws Exception{
        return triangular(center, lb, ub, 1000);
    }
    public static Distribution triangular(final double center, double lb, double ub, int discretization) throws Exception{
        return new Distribution(lb, ub, discretization) {  //triangular distribution
            @Override
            public double P(double x) throws Exception {
                if(x<=center){
                    return (x-lb)/Math.pow(center-lb, 2);
                }else{
                    return (ub-x)/Math.pow(ub-center, 2);
                }
            }
        };
    }
    public static void main(String[] args) throws Exception {
        //Distribution u = uniform(-3, +3);
        Distribution u = normal(0, 1);
        //Distribution u = growing(-3, 3);
        //Distribution u = triangular(0, -3, +3); 
        
        
        //r = N(x,s); s = 1
        //P(r>2) <= 0.15
        //x<=2-invGe(d)
        //d<=0.15
        
        //r1 = N(x1, s1)
        //r2 = N(x2, s2)
        //P(r1>=7||r2>=8) <= 0.15
        //x1>=7+invGe(d1)
        //x2>=8+invGe(d2)
        //d1+d2<=0.15
        //mip.addProbability(mip.sum(x1,x2), 0.15);
        
        
        
        for(double v=-3.0; v<=3.001; v+=0.1){
            System.out.printf("v = %5.2f | p(v) = %8.6f |  p(x>v) = %8.6f | p(x<v) = %8.6f\n", v, u.P(v), u.Ge(v), u.Le(v));
        }
        
        
        for(double p=0; p<=1.0001; p+=0.05){
            System.out.printf("p = %5.2f | i(y>p) = %8.6f | p(y<p) = %8.6f\n", p, u.invGe(p), u.invLe(p));
        }

    }
}
