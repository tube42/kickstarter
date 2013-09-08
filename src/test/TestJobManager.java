
package test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.ks.utils.*;


/* package */ class DummyListener implements MessageListener
{
    public int msg, count, data0;
    public Object data1, sender;
    
    public DummyListener(int msg, int data0, Object data, Object sender)
    {
        this.msg = msg;
        this.data0 = data0;
        this.data1 = data1;
        this.sender = sender;
        this.count = 0;        
    }
    public void onMessage(int msg, int data0, Object data1, Object sender)
    {
        Assert.assertEquals("DummyListener correct msg",  this.msg, msg);
        Assert.assertEquals("DummyListener correct data0",  this.data0, data0);
        Assert.assertEquals("DummyListener correct data1",  this.data1, data1);
        Assert.assertEquals("DummyListener correct sender",  this.sender, sender);
        this.count++;
    }        
}

/* package */ class DummyRunnable implements Runnable
{
    public int count = 0;    
    public void run()
    {
        this.count++;
    }        
}



@RunWith(JUnit4.class)
public class TestJobManager
{
            
    @Test public void testMessageAdd() 
    {        
        DummyListener dl1 = new DummyListener(1, 11, new Object(), new Object());
        DummyListener dl2 = new DummyListener(2, 22, new Object(), new Object());
        DummyListener dl3 = new DummyListener(3, 33, new Object(), new Object());
        
        JobManager jm = new JobManager();
        
        jm.add(dl1, 50, dl1.msg, dl1.data0, dl1.data1, dl1.sender);
        jm.add(dl2, 60, dl2.msg, dl2.data0, dl2.data1, dl2.sender);
        jm.add(dl3, 55, dl3.msg, dl3.data0, dl3.data1, dl3.sender);
        
        jm.update(45);
        
        for(int i = 0; i < 3; i++) {
            Assert.assertEquals("DL1 state",  dl1.count, i > 0 ? 1 : 0);
            Assert.assertEquals("DL2 state",  dl2.count, i > 2 ? 1 : 0);
            Assert.assertEquals("DL3 state",  dl3.count, i > 1 ? 1 : 0);
            jm.update(5);            
        }        
    }
    
    @Test public void testMessageRemove() 
    {        
        DummyListener dl1 = new DummyListener(1, 11, null, null);
        DummyListener dl2 = new DummyListener(2, 22, null, null);
        DummyListener dl3 = new DummyListener(3, 33, null, null);
        
        JobManager jm = new JobManager();
        
        
        jm.add(dl1, 10, dl1.msg, dl1.data0, dl1.data1, dl1.sender);
        jm.add(dl2, 20, dl2.msg, dl2.data0, dl2.data1, dl2.sender);
        jm.add(dl3, 30, dl3.msg, dl3.data0, dl3.data1, dl3.sender);
        
        jm.update(15);
        jm.remove(dl2);
        
        jm.update(10000);        
        Assert.assertEquals("DL1 state",  dl1.count, 1);
        Assert.assertEquals("DL2 state",  dl2.count, 0);
        Assert.assertEquals("DL3 state",  dl3.count, 1);
    }
    
    
    @Test public void testRunnableAdd() 
    {
        DummyRunnable dr1 = new DummyRunnable();
        DummyRunnable dr2 = new DummyRunnable();
        DummyRunnable dr3 = new DummyRunnable();
        JobManager jm = new JobManager();
        
        //
        jm.add(dr1, 200);
        jm.add(dr2, 300);
        jm.add(dr3, 250);
        
        jm.update(199);
        
        for(int i = 0; i < 3; i++) {
            Assert.assertEquals("DR1 state",  dr1.count, i > 0 ? 1 : 0);
            Assert.assertEquals("DR2 state",  dr2.count, i > 2 ? 1 : 0);
            Assert.assertEquals("DR3 state",  dr3.count, i > 1 ? 1 : 0);
            jm.update(50);            
        }
    }
    
    @Test public void testRunnableRemove() 
    {
        DummyRunnable dr1 = new DummyRunnable();
        DummyRunnable dr2 = new DummyRunnable();
        DummyRunnable dr3 = new DummyRunnable();
        JobManager jm = new JobManager();
        
        //
        jm.add(dr1, 10);
        jm.add(dr2, 20);
        jm.add(dr3, 30);
        
        
        jm.update(15);
        jm.remove(dr2);
        
        jm.update(1000);
        Assert.assertEquals("DR1 state",  dr1.count, 1);
        Assert.assertEquals("DR2 state",  dr2.count, 0);
        Assert.assertEquals("DR3 state",  dr3.count, 1);
    }
    
    @Test public void testRunnableRepeat() 
    {
        DummyRunnable dr1 = new DummyRunnable();
        DummyRunnable dr2 = new DummyRunnable();
        JobManager jm = new JobManager();
        
        //
        jm.add(dr1, 200, 100, 3);
        jm.add(dr2, 300);
        
        jm.update(199);
        
        for(int i = 0; i < 3; i++) {
            Assert.assertEquals("DR1 repeat state", dr1.count, i);
            Assert.assertEquals("DR2 repeat state", dr2.count, i > 1 ? 1 : 0);
            jm.update(100);
        }        
        
        jm.update(1000);
        Assert.assertEquals("DR1 final state", dr1.count, 3);
        Assert.assertEquals("DR2 final state", dr2.count, 1);
        
    }
        
     
}
