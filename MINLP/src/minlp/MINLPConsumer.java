/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;

/**
 *
 * @author marcio
 */
public interface MINLPConsumer<T> {
    public void accept(T t) throws Exception;
}
