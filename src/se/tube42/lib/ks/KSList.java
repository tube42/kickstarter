package se.tube42.lib.ks;

import java.util.*;
import java.lang.reflect.Array;

/**
 * KSList is a self-growing list that allows you to directly access 
 * the native [] array for best performance.
 * 
 * NOTE that the list is NOT ordered and will be reorganised at will
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
    
    /** add all items to this list */
    public final void add(T [] ts)
    {
        for(T t : ts) add(t);
    }
    
    /** add item to the end of this list - O(1) */
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
    
    /** 
     * remove item at this index from the list - O(1).
     * NOTE: this will re-arrange the list
     */
    public final void remove(int index)
    {
        if(index < 0 || index >= size) return;
        
        size--;
        if(size > 0)
            data[index] = data[size];
    }
    
    /** return size of the list */
    public final int getSize()
    {
        return size;
    }
    
    /** return the list representing all items (note: use getSize() instead of getAll().length ) */
    public final T[] getAll()
    {
        return data;
    }
    
    /** return item at index or null if out of range - O(1) */
    public final T get(int index)
    {
        return index < 0 || index >= size ? null : data[index];
    }        
}
