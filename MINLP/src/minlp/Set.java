/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;


import minlp.stream.Function;
import minlp.stream.Consumer;
import java.util.LinkedList;
/**
 *
 * @author marcio
 */
public class Set {
    private final MINLP mip;
    protected final LinkedList<Integer> set;
    public Set(MINLP mip, LinkedList<Integer> set) {
        this.mip = mip;
        this.set = set;
    }
    public Set(MINLP cplex, Integer ...array) {
        this.mip = cplex;
        this.set = new LinkedList<>();
        for(Integer e: array){
            this.set.add(e);
        }
    }
    
    public int size(){
        return set.size();
    }
    /*public void forAll(Consumer<? super T> action){
        set.stream().forEach(action);
    }*/
    public void forAll(Consumer action){
        set.stream().forEach((e)->{
            try {
                action.accept(e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    public Expr sum(final Function mapper){
        return set.stream().map((e)->{
            try {
                return mapper.accept(e);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }).reduce(null, (r, e) -> {
            try {
                return r==null ? e : mip.sum(r,e);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        });
        //return set.stream().reduce(null, (r, e) -> r+e);
    }
}
