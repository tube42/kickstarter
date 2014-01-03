
package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.ks.*;


/* package */ class DummyStateMachine extends StateMachine
{
    public int update_count = 0;    
    public int event0_count = 0;
    public long time_total = 0;
    public long time_state = 0;
    public long time_frame = 0;
    
    public int next_state = 0;
    
    public long update(long time_total, long time_state, long dt)
    {
        this.time_total = time_total;
        this.time_state = time_state;
        this.time_frame = dt;   
        
        this.update_count ++;
        if(check(0))
            event0_count++;
           
              
        this.state = next_state;        
        
        if(check(1)) return 0;       
        
        return 20;
    }    
}


@RunWith(JUnit4.class)
public class TestStateMachine
{    
    @Test public void testUpdateTime() 
    {        
        JobManager jm = new JobManager();
        
        DummyStateMachine sm1 = new DummyStateMachine();                
        jm.add(sm1, 20);        
        
        // nortmal update
        jm.update(25);        
        Assert.assertEquals("SM1 time total (1)",  sm1.time_total, 25);
        Assert.assertEquals("SM1 time state (1)",  sm1.time_state, 25);
        Assert.assertEquals("SM1 time frame (1)",  sm1.time_frame, 25);
        Assert.assertEquals("SM1 update count (1)",  sm1.update_count, 1);
        Assert.assertEquals("SM1 state (1)",  sm1.getState(), 0);
        
        // update with ststa change
        sm1.next_state = 1;        
        jm.update(25);        
        Assert.assertEquals("SM1 time total (2)",  sm1.time_total, 50);
        Assert.assertEquals("SM1 time state (2)",  sm1.time_state, 50);
        Assert.assertEquals("SM1 time frame (2)",  sm1.time_frame, 25);
        Assert.assertEquals("SM1 state (2)",  sm1.getState(), 1);
        
        // check if the state was really changed
        jm.update(20);
        Assert.assertEquals("SM1 time total after state change (3)",  sm1.time_total, 70);
        Assert.assertEquals("SM1 time state after state change (3)",  sm1.time_state, 20);
        Assert.assertEquals("SM1 time frame after state change (3)",  sm1.time_frame, 20);        
        
        jm.update(30);
        Assert.assertEquals("SM1 time total after state change (4)",  sm1.time_total, 100);
        Assert.assertEquals("SM1 time state after state change (4)",  sm1.time_state, 50);
        Assert.assertEquals("SM1 time frame after state change (4)",  sm1.time_frame, 30);        
        Assert.assertEquals("SM1 update count (4)",  sm1.update_count, 4);        
        Assert.assertEquals("SM1 state (4)",  sm1.getState(), 1);        
    }    
       
    @Test public void testEvents()
    {        
        JobManager jm = new JobManager();
        
        DummyStateMachine sm1 = new DummyStateMachine();                
        jm.add(sm1, 20);        
        
        // init test
        Assert.assertEquals("SM1 update count (0)",  sm1.update_count, 0);
        Assert.assertEquals("SM1 event count (0)",  sm1.event0_count, 0);
        
        // first update
        sm1.fire(0);
        sm1.fire(0); // should not have any effect
        sm1.fire(0); // should not have any effect
        
        jm.update(25); // event0 is available
        jm.update(25); // event0 is not available
        jm.update(25); // event0 is not available
        
        Assert.assertEquals("SM1 update count (1)",  sm1.update_count, 3);
        Assert.assertEquals("SM1 event count (1)",  sm1.event0_count, 1);
        
        
        // second update
        sm1.fire(0);
        jm.update(25);                
        Assert.assertEquals("SM1 update count (2)",  sm1.update_count, 4);
        Assert.assertEquals("SM1 event count (2)",  sm1.event0_count, 2);
        
        // stop execution
        sm1.fire(1);
        jm.update(25);        
        Assert.assertEquals("SM1 update count (3)",  sm1.update_count, 5);
        
        // should be stopped now
        for(int i = 0; i < 10; i++) jm.update(25);        
        Assert.assertEquals("SM1 update count (4)",  sm1.update_count, 5);
    }    
}
