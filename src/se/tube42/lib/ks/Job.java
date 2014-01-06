package se.tube42.lib.ks;

/* package */ class RunnableJob extends Job
{
    
    /* package */ Runnable callback;    
    
    public void set(Runnable callback) 
    {
        this.callback = callback;
    }
    public void reset()
    {
        this.callback = null;
        super.reset();
    }
    
    public long execute(long dt_error)
    {        
        callback.run();                
        return super.execute(dt_error);
    }    
}

/* package */ class  MessageJob extends Job
{    
    /* package */ MessageListener listener;
    /* package */ int msg, data0;
    /* package */ Object sender, data1;
    
    public void set(MessageListener listener, int msg, 
              int data0, Object data1, Object sender) 
    {
        this.listener = listener;
        this.msg = msg;
        this.data0 = data0;
        this.data1 = data1;
        this.sender = sender;
    }
    
    public void reset()
    {
        listener = null;
        data1 = sender = null;
        super.reset();
    }
    
    public long execute(long dt_error)
    {      
        listener.onMessage(msg, data0, data1, sender);
        return super.execute(dt_error);
    }    
}



/**
 * This class represents a Job. 
 * You can create a custom job by overriding execute()
 */

public class Job
{    
    /* package */ KSPool pool = null;   
    /* package */ boolean stop;    
    /* package */ Job next, tail;
    /* package */ long time_start, time_tail, time_frame;
    
    private int count_repeat;
    private long time_repeat;
    
    public Job() 
    { 
        reset();
    }
    
    
    // -------------------------------------------
    /** 
     * called when job is added to the manager
     */
    public void onAdd()
    {
    }
    
    /** 
     * called when job is finished
     */    
    public void onFinish()
    {
    }
    
    // -------------------------------------------
    
    /* package */ void reset() 
    { 
        // clean up data and also help GC
        stop = false;
        tail = null;
        repeat(0, 0);        
    }
    
    
    // -------------------------------------------
    
    /** stop this job */
    public final void stop()
    {
        this.stop = true;
    }
    
    /** 
     * configure repeat, works only if you don't modify
     * the value returned by execute()
     */
    public final Job repeat(int count, long time)
    {
        this.count_repeat = Math.max(1, count);
        this.time_repeat = Math.max(1, time);
        return this;
    }
    
    /**
     * Add a another job to be run after this job.
     * Note that this job will start exactly AFTER the current job, 
     * i.e. timing error (jitter) of the current job is forwarded.
     */
    public final Job tail(Job j, long time)
    {
        if(tail == null) {            
            j.time_tail = time;
            tail = j;
        }
        return j;
    }
        
    /** 
     * override this in your code. 
     * @return time [ms] to next call or a negative number to quite.
     * @param dt_error timing difference between requested release time and actual time    
     */
    public long execute(long dt_error)
    {        
        return count_repeat-- > 1 ? time_repeat : -1;
    }              
}
