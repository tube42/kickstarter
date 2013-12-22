package se.tube42.ks.utils;

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
    private final JobPool pool = new JobPool();
    
    /**
     * when enabled is false, the job manager is paused and no jobs will be dispatched
     */
    public boolean enabled = true;    
    
    private long time;
    private Job root = null;
    
    public void reset()
    {
        time = 0;
    }
    
    /**
     * service function for the manager.
     * call it in your game loop with the frame time in [ms] as argument
     */
    public void update(long dt)
    {
        if(!enabled) return;
        
        time += dt;        
        while(root != null && root.time_start <= time) {
            // fire up the first from the queue:
            Job job = root;
            long next = -1;
            if(!job.stop)
                next = job.execute(time - job.time_start);
            
            // remove it from queue for now
            root = root.next;
            job.next = null;
                        
            if(next > 0) {
                job.time_start = Math.max(time + 1, job.time_start + next);
                insert_job(job);
            } else {                
                if(job.type != Job.TYPE_USER) {
                    job.reset();              
                    pool.put(job);
                } else {
                    final Job tail = job.tail;
                    if(tail != null) {
                        Job tmp = tail.tail;
                        add(tail, tail.time_tail);
                        tail.tail = tmp;
                    }
                }
            }            
        }
    }
    
    // ------------------------------------------------------------------
    
    /**
     * delete this job
     */
    public final void remove(Job job) 
    {
        if(job == null) return;        
        job.stop = true;
    }
    
    /**
     * delete all jobs associated with this callback
     */
    public final void remove(Runnable callback) 
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
    public final void remove(MessageListener ml) 
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
    public final void remove(MessageListener ml, int msg) 
    {
        if(ml == null) return;
        
        Job tmp = root;
        while(tmp != null) {
            if(tmp.listener == ml && tmp.msg == msg) tmp.stop = true;
            tmp = tmp.next;
        }
    }
    
    // ------------------------------------------------------------------
    
    /**
     * Add a Job (possibly a custom job)
     */
    
    public final Job add(Job job, long time_start) 
    {
        job.prepar_insert();        
        job.time_start = Math.max(1, time_start) + time;        
        job.tail = null;
        insert_job(job);
        return job;        
    }
        
    /**
     * RUN-JOB: run Runnable after time delay
     */    
    public final Job add(Runnable callback, long time) 
    {        
        Job job = pool.get();        
        job.type = Job.TYPE_RUNNABLE;
        job.callback = callback;
        return add(job, time);
    }	
    
    /**
     * MESSAGE-JOB: send msg to ml after time delay
     */
    public void add(MessageListener ml, long time, int msg)
    {
        add(ml, time, msg, 0, null, null);
    }	
    
    /**
     * MESSAGE-JOB: send <msg, data0, data1> from sender to ml after time delay 
     */
    public Job add(MessageListener ml, long time, 
              int msg, int data0, Object data1, Object sender)
    {        
        Job job = pool.get();        
        job.type = Job.TYPE_MESSAGE;        
        job.listener = ml;
        job.msg = msg;
        job.sender = sender;
        job.data0 = data0;
        job.data1 = data1;
        job.callback = null;        
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
    
}
