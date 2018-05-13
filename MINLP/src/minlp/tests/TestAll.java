/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.tests;

import minlp.samples.Equation3nd;
import minlp.samples.Equation4nd;
import minlp.samples.Misture;
import minlp.samples.NonLinearProd;
import minlp.samples.TSP;

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
