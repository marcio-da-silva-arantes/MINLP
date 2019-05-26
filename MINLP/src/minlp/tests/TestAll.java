/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.tests;

import minlp.samples.non_linear.Equation3nd;
import minlp.samples.non_linear.Equation4nd;
import minlp.samples.basic.Misture;
import minlp.samples.non_linear.NonLinearProd;
import minlp.samples.medium.TSP;

/**
 *
 * @author Marcio
 */
public class TestAll {
    public static void main(String[] args) throws Exception {
        Misture.main(args);
        TSP.main(args);
        NonLinearProd.main(args);
        Equation3nd.main(args);
        Equation4nd.main(args);
    }
}
