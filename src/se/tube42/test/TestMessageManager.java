
package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.ks.*;

// -----------------------------------------------------
// for book keeping    
/* package */ class DummyMessageListener implements MessageListener
{
    public int last_msg, last_data0, count;
    public Object last_data1, last_sender;
    public int [] history;
    
    public DummyMessageListener()
    { 
        history = new int[100];        
        reset(); 
    } 
    
    public void reset()
    {
        last_msg = last_data0 = -1;
        last_data1 = last_sender = null;
        count = 0;
    }
    
    public void onMessage(int msg, int data0, Object data1, Object sender)
    {
        if(count < history.length)
            history[count] = msg;
        
        count++;
        last_msg  = msg;
        last_data0 = data0;
        last_data1 = data1;
        last_sender = sender;
    }
    
}    


// -----------------------------------------------------
// the tests

@RunWith(JUnit4.class)
public class TestMessageManager
{
        
    @Test public void testSingleMessage() 
    {
        MessageManager mm = new MessageManager();
        DummyMessageListener dml = new DummyMessageListener();
              
        // start clean
        dml.reset();
        Assert.assertEquals("message count  (0)",  0, dml.count);
        
        // simple message
        mm.add(555);
        mm.service(dml);        
        Assert.assertEquals("msg",  555, dml.last_msg);        
        Assert.assertEquals("message count  (1)",  1, dml.count);
                
        // complex message
        Object d0 = new Object();
        Object d1 = new Object();
        mm.add(123, 666, d0, d1);
        mm.service(dml);
        Assert.assertEquals("msg (2)",  123, dml.last_msg);
        Assert.assertEquals("data0 (2)",  666, dml.last_data0);
        Assert.assertEquals("data1 (2)",  d0, dml.last_data1);
        Assert.assertEquals("sender (2)",  d1, dml.last_sender);
        Assert.assertEquals("message count  (2)",  2, dml.count);        
        
        // see if message history is working
        Assert.assertEquals("message history  (1)",  555, dml.history[0]);
        Assert.assertEquals("message history  (2)",  123, dml.history[1]);        
    }
    
    @Test public void testChainOrder() 
    {
        MessageManager mm = new MessageManager();
        DummyMessageListener dml = new DummyMessageListener();
        
        // start clean
        dml.reset();
        Assert.assertEquals("message count  (0)",  0, dml.count);
        
        // simple message
        Object d0 = new Object();
        Object d1 = new Object();
        Object d2 = new Object();
        Object d3 = new Object();
        
        mm.add(555);
        mm.add(666, 99, d0, d1);
        mm.add(777, 98, d2, d3);
        mm.service(dml);
        
        Assert.assertEquals("message count",  3, dml.count);                
        Assert.assertEquals("data0 (3)",  98, dml.last_data0);
        Assert.assertEquals("data1 (3)",  d2, dml.last_data1);
        Assert.assertEquals("sender (3)",  d3, dml.last_sender);        
        Assert.assertEquals("message history  (1)",  555, dml.history[0]);
        Assert.assertEquals("message history  (2)",  666, dml.history[1]);
        Assert.assertEquals("message history  (3)",  777, dml.history[2]);        
    }
        
    
    @Test public void testPool() 
    {
        MessageManager mm = new MessageManager();
        DummyMessageListener dml = new DummyMessageListener();
        
        // process 10 messages
        for(int i = 0; i < 10; i++)
            mm.add(i);
        mm.service(dml);
        
        
        // process 10 more messages
        for(int i = 0; i < 10; i++)
            mm.add(i);
        mm.service(dml);;        
        
        // check the pool stats
        KSPool pool = mm.debugGetPool();
        Assert.assertEquals("pool get",  20, pool.debugCountGet());
        Assert.assertEquals("pool put",  20, pool.debugCountPut());
        Assert.assertEquals("pool new",  10, pool.debugCountNew());        
    }
    
}
