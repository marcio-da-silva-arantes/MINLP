/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.cplex.IloCplex;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author marcio
 */
public class Set<T> {
    private final IloCplex cplex;
    protected final LinkedList<T> set;
    public Set(IloCplex cplex, LinkedList<T> set) {
        this.cplex = cplex;
        this.set = set;
    }
    public Set(IloCplex cplex, T ...array) {
        this.cplex = cplex;
        this.set = new LinkedList<>();
        for(T e: array){
            this.set.add(e);
        }
    }
    
    public int size(){
        return set.size();
    }
    /*public void forAll(Consumer<? super T> action){
        set.stream().forEach(action);
    }*/
    public void forAll(MINLPConsumer<? super T> action){
        set.stream().forEach((e)->{
            try {
                action.accept(e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    public IloNumExpr sum(final MINLPFunction<? super T, IloNumExpr> mapper){
        return set.stream().map((e)->{
            try {
                return mapper.accept(e);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }).reduce(null, (r, e) -> {
            try {
                return r==null ? e : cplex.sum(r,e);
            } catch (IloException ex) {
                ex.printStackTrace();
                return null;
            }
        });
        //return set.stream().reduce(null, (r, e) -> r+e);
    }
}
