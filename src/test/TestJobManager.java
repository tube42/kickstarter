
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


/* package */ class DummyCustom extends Job
{
    public int count = 0;    
    public boolean custom_repeat;
    public long error;
    
    public DummyCustom(boolean custom_repeat)
    {
        this.custom_repeat = custom_repeat;
    }
    
    public long execute(long error)
    {
        this.count++;
        this.error = error;
        if(custom_repeat) 
            return count * 10;
        
        return super.execute(error);
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
        jm.add(dr1, 200).repeat(3, 100);
        jm.add(dr2, 300);
        
        jm.update(199);
        
        for(int i = 0; i < 3; i++) {
            Assert.assertEquals("DR1 repeat state", dr1.count, i);
            Assert.assertEquals("DR2 repeat state", dr2.count, i > 1 ? 1 : 0);
            jm.update(100);
        }        
        
        jm.update(1000);
        jm.update(1000);
        jm.update(1000);
        Assert.assertEquals("DR1 final state", dr1.count, 3);
        Assert.assertEquals("DR2 final state", dr2.count, 1);        
    }
        
    
    @Test public void testCustom() 
    {
        JobManager jm = new JobManager();        
        DummyCustom dc1 = new DummyCustom(false);
        DummyCustom dc2 = new DummyCustom(true);
        
        // 
        jm.add(dc1, 200).repeat(2, 1000);
        jm.add(dc2, 200);
        
        
        jm.update(200);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 1);
        Assert.assertEquals("DC2 repeat state", dc2.count, 1);
        
        jm.update(10);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 1);
        Assert.assertEquals("DC2 repeat state", dc2.count, 2);
        
        jm.update(20);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 1);
        Assert.assertEquals("DC2 repeat state", dc2.count, 3);
        
        jm.update(30);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 1);
        Assert.assertEquals("DC2 repeat state", dc2.count, 4);
        
        
        jm.update(1000);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 2);
        
        jm.update(1000);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 2);        
    }
    
    @Test public void testTail()
    {
        JobManager jm = new JobManager();        
        DummyCustom dc1 = new DummyCustom(false);
        DummyCustom dc2 = new DummyCustom(false);
        DummyCustom dc3 = new DummyCustom(false);
        
        // 
        jm.add(dc1, 200).tail(dc2, 300).tail(dc3, 400);
        
        for(int i = 0; i < 30; i++) {
            int t = 50 * i;
            Assert.assertEquals("DC1 repeat state", dc1.count, t < 200 ? 0 : 1);
            Assert.assertEquals("DC2 repeat state", dc2.count, t < 500 ? 0 : 1);
            Assert.assertEquals("DC3 repeat state", dc3.count, t < 900 ? 0 : 1);
            jm.update(50); 
            
        }
    }
    
    @Test public void testError()
    {
        JobManager jm = new JobManager();        
        DummyCustom dc1 = new DummyCustom(false);
        DummyCustom dc2 = new DummyCustom(false);
        
        jm.add(dc1, 100);
        jm.add(dc2, 200);
        
        jm.update(50);  // 50
        jm.update(100); // 150
        jm.update(60);  // 210
        
        Assert.assertEquals("DC1 timing error", dc1.error, 50);
        Assert.assertEquals("DC2 timing error", dc2.error, 10);
    }
    
}
