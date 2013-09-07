
package se.tube42.ks.utils;


public class SimpleStack<T>
{
    private SimpleList<T> stack;
    
    public SimpleStack()
    {
        this.stack = new SimpleList<T>();
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
