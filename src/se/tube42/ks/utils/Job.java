package se.tube42.ks.utils;

/**
 * This class represents a Job. 
 * You can create a custom job by overriding execute()
 */

public class Job
{
    /* package */ final static int 
          TYPE_MESSAGE = 0,
          TYPE_RUNNABLE = 1,
          TYPE_USER = 2;
    
    // shared for all
    /* package */ int type = TYPE_USER;
    /* package */ boolean stop;    
    /* package */ Job next, tail;
    /* package */ long time_start, time_tail;  
    private int count_repeat;
    private long time_repeat;
    
    // Message jobs:
    /* package */ MessageListener listener;
    /* package */ int msg, data0;;
    /* package */ Object sender, data1;
    
    // Runable
    /* package */ Runnable callback;    
    
    
    public Job() 
    { 
        reset();
    }
    
    // -------------------------------------------
    
    /* package */ void reset() 
    { 
        // clean up data and also help GC
        callback = null;
        listener = null;
        sender = data1 = null;
        stop = false;
        tail = null;
    }
    
    /* package */ void prepar_insert()
    {
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
    public final void repeat(int count, long time)
    {
        this.count_repeat = Math.max(1, count);
        this.time_repeat = Math.max(1, time);
    }
    
    /**
     * Add a another job to be run after this job.
     * Note that this job will start exactly AFTER the current job, 
     * i.e. timing error (jitter) of the current job is forwarded.
     */
    public final Job tail(Job j, long time)
    {
        if(type == TYPE_USER) {
            j.time_tail = time;
            this.tail = j;
        }
        return j;
    }
        
    /** 
     * override this in your code. 
     * return the time [ms] to next call or a negative number to quite
     */
    public long execute()
    {        
        switch(type) {
        case Job.TYPE_MESSAGE:
            listener.onMessage(msg, data0, data1, sender);
            break;
        case Job.TYPE_RUNNABLE:
            callback.run();
            break;
        default:
            // nothing
        }
                
        return count_repeat-- > 1 ? time_repeat : -1;
    }
}
