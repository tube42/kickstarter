
package se.tube42.ks.utils;


public class KSStack<T>
{
    private KSList<T> stack;
    
    public KSStack()
    {
        this.stack = new KSList<T>();
    }
    
    
    public final void push(T s)
    {
        stack.add(s);
    }
    
    public final T pop()
    {
        final int size = stack.getSize();
        if(size == 0) return null;
        
        T ret = stack.get(size-1);
        stack.remove(size-1);
        return ret;
    }
    
    public final T peek()
    {
        final int size = stack.getSize();
        return (size == 0) ? null : stack.get(size-1);
    }
    
    public final int getSize()
    {
        return stack.getSize();
    }

}
