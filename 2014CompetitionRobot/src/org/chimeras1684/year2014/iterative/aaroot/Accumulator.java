/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chimeras1684.year2014.iterative.aaroot;

/**
 *
 * @author Arhowk
 */
public class Accumulator {
    
    double[] values;
    
    public Accumulator(int capacity){
        values = new double[capacity];
    }
    
    public void add(double val){
        int size = values.length;
        while(size --> 1){
            values[size-1] = values[size];
        }
        values[values.length - 1] = val;
    }
    
    public double get(){
        int size = values.length;
        double total = 0;
        while(size --> 0){
            total += values[size] / values.length;
        }
        return total;
    }
    
}
