
package se.tube42.ks.utils;

import java.util.*;
import java.lang.reflect.Array;

/**
 * Simple self-growing array.
 * 
 * The only reason we have implemented this is to have 
 * direct access to the [] array :)
 */

public class KSList<T>
{
    private T [] data;
    private int size;
    
    public KSList()
    {
        this.data = null;
        this.size = 0;
    }
    
    public final void add(T s)
    {
        if(data == null) {
//            this.data = (T []) new Object[32];            
            this.data = (T []) Array.newInstance(s.getClass(), 32);
        } else if(size == data.length) {
            this.data = Arrays.copyOf( data, data.length * 4);
        }
        
        data[size++] = s;
    }
    
    public final void remove(int index)
    {
        if(index < 0 || index >= size) return;
        
        size--;
        if(size > 0)
            data[index] = data[size];
    }
    
    public final int getSize()
    {
        return size;
    }
    
    public final T[] getAll()
    {
        return data;
    }
    
    public final T get(int index)
    {
        return index < 0 || index >= size ? null : data[index];
    }        
}
