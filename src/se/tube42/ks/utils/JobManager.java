package se.tube42.ks.utils;


/* package*/  class Job
{
    public MessageListener listener;
    public int msg;
    public Object sender;
    public Object data1;
    public int data0;
    public boolean stop;
    public Runnable callback;
    
    public int count_repeat;
    public long time_repeat;
    public long time_start;
    
    public Job next;
    
    public Job() 
    { 
        reset();
    }
    
    public void reset() 
    { 
        // clean up data and also help GC
        callback = null;
        listener = null;
        sender = data1 = null;
        stop = false;
    }
}

/* package */ class JobPool extends KSPool<Job>
{
    public Job createNew() { return new Job(); }    
}

/**
 * JobManager is a class for scheduling jobs to be run
 * at some point in future
 */

public final class JobManager 
{
    
    public boolean enabled = true;
    
    private final JobPool pool = new JobPool();
    private long time;
    private Job root = null;
    
    public void reset()
    {
        time = 0;
    }
    
    public void update(long dt)
    {
        if(!enabled) return;
        
        
        time += dt;
        
        while(root != null && root.time_start <= time) {
            // fire up the first from the queue:
            Job job = root;
            
            if(!job.stop) {
                if(job.callback != null)
                    job.callback.run();
                else
                    job.listener.onMessage(job.msg, job.data0, job.data1, job.sender);
            }
            // remove it from queue for now
            root = root.next;
            job.next = null;
            
            // free it if no more cycles, otherwise re-insert it
            job.count_repeat --;
            if(!job.stop && job.count_repeat > 0) {
                job.time_start += job.time_repeat;
                insert_job(job);
            } else {
                job.reset();
                pool.put(job);
            }
            
        }
    }
    
    // ------------------------------------------------------------------
    /**
     * delete all jobs associated with this callback
     */
    public void remove(Runnable callback) 
    {
        if(callback == null) return;
        
        Job tmp = root;
        while(tmp != null) {
            if(tmp.callback == callback) tmp.stop = true;
            tmp = tmp.next;
        }
    }
    
    /**
     * delete all jobs associated with this listener
     */
    public void remove(MessageListener ml) 
    {
        if(ml == null) return;
        
        Job tmp = root;
        while(tmp != null) {
            if(tmp.listener == ml) tmp.stop = true;
            tmp = tmp.next;
        }
    }
    
    /**
     * delete all jobs associated with this listener and message
     */
    public void remove(MessageListener ml, int msg) 
    {
        if(ml == null) return;
        
        Job tmp = root;
        while(tmp != null) {
            if(tmp.listener == ml && tmp.msg == msg) tmp.stop = true;
            tmp = tmp.next;
        }
    }
    
    // ------------------------------------------------------------------
    
    public void add(Runnable callback, long time_delay) 
    {
        add(callback, time_delay, 0, 1);
    }	
    
    public void add(Runnable callback, long time_start, long time_repeat, int count_repeat) 
    {
        Job job = pool.get();
        
        job.listener = null;
        job.callback = callback;
        job.count_repeat = count_repeat;
        job.time_repeat = time_repeat;
        job.time_start = time + time_start;
        
        insert_job(job);		
    }	
    
    /**
     * send msg to ml after time delay
     */
    public void add(MessageListener ml, long time, int msg)
    {
        add(ml, time, 0, 1, msg, 0, null, null);
    }	
    
    /**
     * send <msg, data0, data1> from sender to ml after time delay 
     */
    public void add(MessageListener ml, long time, 
              int msg, int data0, Object data1, Object sender)
    {
        add(ml, time, 0, 1, msg, data0, data1, sender);
    }
    
    public void add(MessageListener ml, long time_delay, long time_repeat, int count_repeat, 
              int msg, int data0, Object data1, Object sender)
    {
        if(time_delay < 0 ) time_delay = 0;
        if(time_repeat < 0) time_repeat = 0;
        if(count_repeat < 1) count_repeat = 1;
        
        Job job = pool.get();
        
        job.listener = ml;
        job.msg = msg;
        job.sender = sender;
        job.data0 = data0;
        job.data1 = data1;
        job.callback = null;
        job.count_repeat = count_repeat;
        job.time_repeat = time_repeat;
        job.time_start = time + time_delay;
        
        insert_job(job);
    }
    
    private void insert_job(Job job)
    {
        // insert it somewhere in the list
        if(root == null) {
            job.next = null;
            root = job;
        } else {
            Job prev = null, curr = root;
            while(curr != null && curr.time_start <= job.time_start) {
                prev = curr;
                curr = curr.next;
            }
            if(prev != null) {
                prev.next = job;
            }  else {
                root = job;
            }
            job.next = curr;
        }
    }
}
