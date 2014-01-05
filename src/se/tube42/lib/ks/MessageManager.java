package se.tube42.lib.ks;


/* package */ class Message
{
    public int msg, data0;
    public Object sender, data1;
    public Message next;
    public boolean processed;
    
    public void set(int msg, int data0, Object data1, Object sender)
    {
        this.msg = msg;
        this.data0 = data0;
        this.data1 = data1;
        this.sender = sender;
        this.next = null;
        this.processed = false;
    }
}

/* package */ class MessagePool extends KSPool<Message>
{
    public Message createNew() { return new Message(); }
}


/**
 * Message manager is a simple class for moving messages across
 * different threads.
 * 
 * Note that this is the only thread-safe class in this library!
 */
public class MessageManager
{
    private Message first, last;
    private MessagePool pool;  
    private Object lock_p, lock_q;
    
    public MessageManager()
    {   
        this.pool = new MessagePool();
        this.lock_p = new Object();
        this.lock_q = new Object();
        this.first = null;
        this.last = null;
    }
    
    // ------------------------------------
    // pool
    
    // get a single message form the pool
    private Message pool_get()
    {
        synchronized(lock_p) {
            return pool.get();
        }
    }
    
    // return a chain of messages to the pool
    private void pool_put_chain(Message m)
    {
        synchronized(lock_p) {
            while(m != null) {
                Message tmp = m;
                m = m.next;
                pool.put(tmp);
            }
        }
    }
    
    
    // ------------------------------------
    // write
    
    /**
     * Add a message to the pool
     * @param msg the message
     */
    public void add(int msg)
    {   
        add(msg, 0, null, null);
    }
    
    /**
     * Add a message to the pool
     * @param msg the message
     * @param data0 first data (int)
     * @param data1 first data (Objetc)
     * @param sender message sender
     */    
    public void add(int msg, int data0, Object data1, Object sender)
    {   
        Message m = pool_get();
        m.set(msg, data0, data1, sender);        
        add_new(m);
    }
    
        
    private void add_new(Message m)
    {
        synchronized(lock_q) {
            if(first == null) {
                first = last = m;
            } else  {
                last.next = m;
                last = m;
            }
        }
    }        
        
    // ------------------------------------
    // read
    
    /**
     * service all pending messages
     * @param listener the MessageListener to receive the messages
     */
    public void service(final MessageListener listener)
    {       
        // get the standing jobs
        Message chain;
        synchronized(lock_q) {
            chain = first;
            first = last = null;
        }
        
        // run them all
        for(Message tmp = chain; tmp != null; tmp = tmp.next) {
            listener.onMessage(tmp.msg, tmp.data0, tmp.data1, tmp.sender);
        }
        
        pool_put_chain(chain);
        
    }
    
    // -------------------------------------------------
    // debugging
    
    /** used for debugging and unit tests only */
    public KSPool debugGetPool() { return pool; }
}

