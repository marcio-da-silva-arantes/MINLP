/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp.stream;

import minlp.Expr;

/**
 *
 * @author marcio
 */
public interface Function2p {
    public Expr accept(Integer i, Integer j) throws Exception;
}
