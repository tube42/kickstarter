
package se.tube42.ks.utils;


public abstract class SimplePool<T>
{    
    private SimpleStack<T> stack;
    private int max_size;
    
    public SimplePool()
    {
        init(Integer.MAX_VALUE);
    }
    
    public SimplePool(int max_size)
    {
        init(max_size);
    }
    
    //     
    private void init(int max_size)
    {        
        this.stack = new SimpleStack<T>();
        this.max_size = max_size;
    }
    
    public final T get()
    {        
        T ret = stack.pop();
        
        if(ret == null) {
            ret = createNew();
        }
        
        return ret;
    }
    
    public final void put(T t)
    {
        if(stack.getSize() < max_size)
            stack.push(t);
    }
    
    public abstract T createNew();    
}
