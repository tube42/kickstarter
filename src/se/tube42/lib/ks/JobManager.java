package se.tube42.lib.ks;

/* package */ class RunnableJobPool extends KSPool<RunnableJob>
{
    public RunnableJob createNew() 
    { 
        RunnableJob ret = new RunnableJob();
        ret.pool = this;
        return ret;
    }
}

/* package */ class MessageJobPool extends KSPool<MessageJob>
{
    public MessageJob createNew() 
    { 
        MessageJob ret = new MessageJob();
        ret.pool = this;
        return ret;
    }
}

/**
 * JobManager is a class for scheduling jobs to be run
 * at some point in future
 */

public final class JobManager 
{
    private final RunnableJobPool run_pool = new RunnableJobPool();
    private final MessageJobPool msg_pool = new MessageJobPool();
    
    /**
     * when enabled is false, the job manager is paused and no jobs will be dispatched
     */
    public boolean enabled = true;    
    
    private long time;
    private Job root = null;
    
    /**
     * reset time and start over, this will affect all pending jobs 
     * (and probably create a mess)
     */
    public void reset()
    {
        time = 0;
    }
    
    
    // ------------------------------------------------------------------
    
    /**
     * delete this job
     */
    public final void remove(Job job) 
    {
        if(job != null)
            job.stop = true;        
    }
    
    
    // ------------------------------------------------------------------
    
    /**
     * Add a Job (possibly a custom job)
     */    
    public final Job add(Job job, long time_start) 
    {
        job.time_start = Math.max(1, time_start) + time;        
        job.tail = null;
        insert_job(job);
        
        job.onAdd();
        return job;        
    }
        
    /**
     * RUN-JOB: run Runnable after time delay
     */    
    public final Job add(Runnable callback, long time) 
    {        
        RunnableJob job = run_pool.get();
        job.set(callback);        
        return add(job, time);        
    }	
    
    /**
     * MESSAGE-JOB: send msg to ml after time delay
     */
    public Job add(MessageListener ml, long time, int msg)
    {
        return add(ml, time, msg, 0, null, null);
    }	
    
    /**
     * MESSAGE-JOB: send <msg, data0, data1> from sender to ml after time delay 
     */
    public Job add(MessageListener ml, long time, 
              int msg, int data0, Object data1, Object sender)
    {        
        MessageJob job = msg_pool.get();        
        job.set(ml, msg, data0, data1, sender);
        return add(job, time);
    }
    
    // --------------------------------------------------
    
    private void insert_job(Job job)
    {   
        // record current frame time
        job.time_frame = job.time_start - time;
        
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
    
    /**
     * service function for the manager.
     * call it in your game loop with the frame time in [ms] as argument
     */
    public void service(long dt)
    {
        if(!enabled) return;
        
        time += dt;        
        while(root != null && root.time_start <= time) {
            // fire up the first from the queue:
            Job job = root;
            long next = -1;
            
            if(!job.stop)
                next = job.execute(time - job.time_start);            
            
            boolean stop = job.stop; // save it for now
            // remove it from queue for now
            root = root.next;
            job.next = null;
            
            if(next > 0) {
                job.time_start = Math.max(time + 1, job.time_start + next);
                insert_job(job);
            } else {
                // call onFinish
                job.onFinish();
                
                // insert possible tail
                final Job tail = job.tail;
                
                if(tail != null) {
                    // remember old data
                    final Job tmp = tail.tail;
                    stop |= tail.stop;
                    
                    // add
                    add(tail, tail.time_tail);
                    
                    // re-insert old stuff
                    tail.tail = tmp;
                    tail.stop = stop;
                }
                
                // reset used job
                job.reset();                
                
                // return back to pool?
                if(job.pool != null) 
                    job.pool.put(job);
                
            }            
        }
    }
 
}
