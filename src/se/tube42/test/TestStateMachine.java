
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
    public int cnt_finish = 0;
        
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
    
    public void onFinish()
    {
        cnt_finish ++;
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
        jm.service(25);        
        Assert.assertEquals("SM1 time total (1)",  sm1.time_total, 25);
        Assert.assertEquals("SM1 time state (1)",  sm1.time_state, 25);
        Assert.assertEquals("SM1 time frame (1)",  sm1.time_frame, 25);
        Assert.assertEquals("SM1 update count (1)",  sm1.update_count, 1);
        Assert.assertEquals("SM1 state (1)",  sm1.getState(), 0);
        
        // update with ststa change
        sm1.next_state = 1;        
        jm.service(25);        
        Assert.assertEquals("SM1 time total (2)",  sm1.time_total, 50);
        Assert.assertEquals("SM1 time state (2)",  sm1.time_state, 50);
        Assert.assertEquals("SM1 time frame (2)",  sm1.time_frame, 25);
        Assert.assertEquals("SM1 state (2)",  sm1.getState(), 1);
        
        // check if the state was really changed
        jm.service(20);
        Assert.assertEquals("SM1 time total after state change (3)",  sm1.time_total, 70);
        Assert.assertEquals("SM1 time state after state change (3)",  sm1.time_state, 20);
        Assert.assertEquals("SM1 time frame after state change (3)",  sm1.time_frame, 20);        
        
        jm.service(30);
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
        
        jm.service(25); // event0 is available
        jm.service(25); // event0 is not available
        jm.service(25); // event0 is not available
        
        Assert.assertEquals("SM1 update count (1)",  sm1.update_count, 3);
        Assert.assertEquals("SM1 event count (1)",  sm1.event0_count, 1);
        
        
        // second update
        sm1.fire(0);
        jm.service(25);                
        Assert.assertEquals("SM1 update count (2)",  sm1.update_count, 4);
        Assert.assertEquals("SM1 event count (2)",  sm1.event0_count, 2);
        
        // stop execution
        sm1.fire(1);
        jm.service(25);        
        Assert.assertEquals("SM1 update count (3)",  sm1.update_count, 5);
        
        // should be stopped now
        for(int i = 0; i < 10; i++) jm.service(25);        
        Assert.assertEquals("SM1 update count (4)",  sm1.update_count, 5);
    }    
    
    
    @Test public void testReset()
    {        
        JobManager jm = new JobManager();
        
        // create a job that transitions to state 666 and exists
        DummyStateMachine sm1 = new DummyStateMachine();                
        
        // add it and run it
        jm.add(sm1, 0);        
        
        sm1.fire(1);
        sm1.next_state = 666;
              
        for(int i = 0; i < 10; i++)
            jm.service(100);
        
        Assert.assertEquals("SM1 state (1)", 666, sm1.getState());
        Assert.assertEquals("SM1 count (1)", 1, sm1.update_count);
        Assert.assertEquals("SM1 time_total (1)", 100, sm1.time_total);
        
        
        // now re-insert it with 555 as goal
        sm1.next_state = 555;
        sm1.time_total = 0;
        
        // check state:
        jm.add(sm1, 0);        
        sm1.fire(1);        
        Assert.assertEquals("SM1 state (2)", 0, sm1.getState());
        Assert.assertEquals("SM1 time_total (2)", 0, sm1.time_total);
        
        // run it and check again
        for(int i = 0; i < 10; i++)
            jm.service(100);        
        Assert.assertEquals("SM1 state (3)", 555, sm1.getState());
        Assert.assertEquals("SM1 count (3)", 2, sm1.update_count);
        Assert.assertEquals("SM1 time_total (3)", 100, sm1.time_total);                
    }        
    
    
    @Test public void testFinish()
    {
        JobManager jm = new JobManager();                
        DummyStateMachine sm1 = new DummyStateMachine();                
        
        // add it and run it
        jm.add(sm1, 0);                
        sm1.fire(1);
        Assert.assertEquals("SM1 cnt_finish (1)", 0, sm1.cnt_finish);
        
        for(int i = 0; i < 10; i++)
            jm.service(100);
        
        Assert.assertEquals("SM1 cnt_finish (2)", 1, sm1.cnt_finish);        
    }        

    @Test public void testTail()
    {
        JobManager jm = new JobManager();                
        DummyStateMachine sm1 = new DummyStateMachine();                
        DummyStateMachine sm2 = new DummyStateMachine();                
        
        // add it and run it
        jm.add(sm1, 50).tail(sm2, 40);
        
        sm1.fire(1);
        sm2.fire(1);
                
        Assert.assertEquals("SM1 cnt_finish (1)", 0, sm1.cnt_finish);
        Assert.assertEquals("SM2 cnt_finish (1)", 0, sm2.cnt_finish);
        
        jm.service(50);        
        Assert.assertEquals("SM1 cnt_finish (2)", 1, sm1.cnt_finish);
        Assert.assertEquals("SM2 cnt_finish (2)", 0, sm2.cnt_finish);
        
        jm.service(50);
        Assert.assertEquals("SM1 cnt_finish (3)", 1, sm1.cnt_finish);
        Assert.assertEquals("SM2 cnt_finish (3)", 1, sm2.cnt_finish);
    }            
}
