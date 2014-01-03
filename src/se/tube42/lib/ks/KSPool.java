package se.tube42.lib.ks;

/**
 * generic object pool
 */

public abstract class KSPool<T>
{    
    private KSStack<T> stack;
    private int max_size;
    
    /** create an infinitely large pool */
    public KSPool()
    {
        init(Integer.MAX_VALUE);
    }
    
    /** create a pool with the specified max size */     
    public KSPool(int max_size)
    {
        init(max_size);
    }
    
    //     
    private void init(int max_size)
    {        
        this.stack = new KSStack<T>();
        this.max_size = max_size;
    }
    
    /** get one object from the pool */
    public final T get()
    {        
        T ret = stack.pop();
        
        if(ret == null) {
            ret = createNew();
        }
        
        return ret;
    }
    
    /** put item back into the list */
    public final void put(T t)
    {
        if(stack.getSize() < max_size)
            stack.push(t);
    }
    
    /** object creation item for the pool. you must implement this */
    public abstract T createNew();    
}
