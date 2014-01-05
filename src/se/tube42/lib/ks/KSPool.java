package se.tube42.lib.ks;

/**
 * generic object pool
 */

public abstract class KSPool<T>
{    
    private KSStack<T> stack;
    private int max_size;
    private int dbg_get, dbg_put, dbg_new;
    
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
        this.max_size = max_size;               
        stack = new KSStack<T>();
        dbg_get = dbg_put = dbg_new;
    }
    
    /** get one object from the pool */
    public final T get()
    {   
        dbg_get++;
        T ret = stack.pop();
        
        if(ret == null) {
            dbg_new ++;
            ret = createNew();
        }
        
        return ret;
    }
    
    /** put item back into the list */
    public final void put(T t)
    {
        dbg_put ++;
        if(stack.getSize() < max_size)
            stack.push(t);
    }
    
    /** object creation item for the pool. you must implement this */
    public abstract T createNew();    
    
    
    // ----------------------------------------------------
    // statistics
    
    /** @return number of put operations */
    public final int debugCountPut() { return dbg_put; }
    
    /** @return number of get operations */    
    public final int debugCountGet() { return dbg_get; }
    
    /** @return number of objects created */    
    public final int debugCountNew() { return dbg_new; }
}
