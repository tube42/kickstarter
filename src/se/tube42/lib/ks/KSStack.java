package se.tube42.lib.ks;

/**
 * generic stack
 */

public class KSStack<T>
{
    private KSList<T> stack;
    
    public KSStack()
    {
        this.stack = new KSList<T>();
    }
    
    /** push item to the stack */
    public final void push(T s)
    {
        stack.add(s);
    }
    
    /** pop item. returns null if stack is empty */
    public final T pop()
    {
        final int pos = stack.getSize() - 1;
        final T ret = stack.get(pos);
        if(pos >= 0) stack.remove(pos);        
        return ret;
    }
    
    /** return peek of stack, null if empty */
    public final T peek()
    {
        final int pos = stack.getSize() - 1;
        return stack.get(pos);
    }
    
    /** return size of the stack */
    public final int getSize()
    {
        return stack.getSize();
    }

}
