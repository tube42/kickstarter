
package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.ks.*;


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
    public int cnt_add, cnt_finish;
    
    public DummyCustom(boolean custom_repeat)
    {
        this.custom_repeat = custom_repeat;
        this.cnt_add = 0;
        this.cnt_finish = 0;
        
    }
    
    public long execute(long error)
    {
        this.count++;
        this.error = error;
        if(custom_repeat) 
            return count * 10;
        
        return super.execute(error);
    }
    
    public void onAdd()
    {
        cnt_add++;
    }
    public void onFinish()
    {
        cnt_finish++;
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
        
        jm.service(45);
        
        for(int i = 0; i < 3; i++) {
            Assert.assertEquals("DL1 state",  dl1.count, i > 0 ? 1 : 0);
            Assert.assertEquals("DL2 state",  dl2.count, i > 2 ? 1 : 0);
            Assert.assertEquals("DL3 state",  dl3.count, i > 1 ? 1 : 0);
            jm.service(5);            
        }        
    }
     
    
    @Test public void testMessageRemove() 
    {        
        DummyListener dl1 = new DummyListener(1, 11, null, null);
        DummyListener dl2 = new DummyListener(2, 22, null, null);
        DummyListener dl3 = new DummyListener(3, 33, null, null);
        
        JobManager jm = new JobManager();
        
        
        Job j1 = jm.add(dl1, 10, dl1.msg, dl1.data0, dl1.data1, dl1.sender);
        Job j2 = jm.add(dl2, 20, dl2.msg, dl2.data0, dl2.data1, dl2.sender);
        Job j3 = jm.add(dl3, 30, dl3.msg, dl3.data0, dl3.data1, dl3.sender);
        
        jm.service(15);
        jm.remove(j2);
        
        Assert.assertEquals("DL1 state . ",  dl1.count, 1);
        Assert.assertEquals("DL2 state . ",  dl2.count, 0);
        Assert.assertEquals("DL3 state . ",  dl3.count, 0);
        
        jm.service(10000);        
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
        
        jm.service(199);
        
        for(int i = 0; i < 3; i++) {
            Assert.assertEquals("DR1 state",  dr1.count, i > 0 ? 1 : 0);
            Assert.assertEquals("DR2 state",  dr2.count, i > 2 ? 1 : 0);
            Assert.assertEquals("DR3 state",  dr3.count, i > 1 ? 1 : 0);
            jm.service(50);            
        }
    }
    
    @Test public void testRunnableRemove() 
    {
        DummyRunnable dr1 = new DummyRunnable();
        DummyRunnable dr2 = new DummyRunnable();
        DummyRunnable dr3 = new DummyRunnable();
        JobManager jm = new JobManager();
        
        //
        Job j1 = jm.add(dr1, 10);
        Job j2 = jm.add(dr2, 20);
        Job j3 = jm.add(dr3, 30);
                        
        jm.service(15);
        jm.remove(j2);
        
        jm.service(1000);
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
        
        jm.service(199);
        
        for(int i = 0; i < 3; i++) {
            Assert.assertEquals("DR1 repeat state", dr1.count, i);
            Assert.assertEquals("DR2 repeat state", dr2.count, i > 1 ? 1 : 0);
            jm.service(100);
        }        
        
        jm.service(1000);
        jm.service(1000);
        jm.service(1000);
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
        
        
        jm.service(200);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 1);
        Assert.assertEquals("DC2 repeat state", dc2.count, 1);
        
        jm.service(10);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 1);
        Assert.assertEquals("DC2 repeat state", dc2.count, 2);
        
        jm.service(20);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 1);
        Assert.assertEquals("DC2 repeat state", dc2.count, 3);
        
        jm.service(30);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 1);
        Assert.assertEquals("DC2 repeat state", dc2.count, 4);
        
        
        jm.service(1000);        
        Assert.assertEquals("DC1 repeat state", dc1.count, 2);
        
        jm.service(1000);        
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
            jm.service(50); 
            
        }
    }
    
    @Test public void testTailRepeat()
    {
        JobManager jm = new JobManager();        
        DummyCustom dc1 = new DummyCustom(false);
        DummyCustom dc2 = new DummyCustom(false);
        DummyCustom dc3 = new DummyCustom(false);
        
        // 
        jm.add(dc1, 200).repeat(5, 1).
              tail(dc2, 300).repeat(6, 2).
              tail(dc3, 400).repeat(7, 3);
        
        for(int i = 0; i < 100; i++)            
            jm.service(50); 
        
        Assert.assertEquals("DC1 repeat tail", 5, dc1.count);
        Assert.assertEquals("DC2 repeat tail", 6, dc2.count);
        Assert.assertEquals("DC3 repeat tail", 7, dc3.count);
    }
    
    @Test public void testTailRemove()
    {
        JobManager jm = new JobManager();        
        DummyCustom dc1 = new DummyCustom(false);
        DummyCustom dc2 = new DummyCustom(false);
        DummyCustom dc3 = new DummyCustom(false);
        
        // 
        Job j1 = jm.add(dc1, 200);
        Job j2 = j1.tail(dc2, 300);
        Job j3 = j2.tail(dc3, 400);
        
        jm.remove(j2);
        
        for(int i = 0; i < 10; i++)
            jm.service(200); 
        
        Assert.assertEquals("DC1 remove tail", 1, dc1.count);
        Assert.assertEquals("DC2 remove tail", 0, dc2.count);
        Assert.assertEquals("DC3 remove tail", 0, dc3.count);        
    }
    
    @Test public void testError()
    {
        JobManager jm = new JobManager();        
        DummyCustom dc1 = new DummyCustom(false);
        DummyCustom dc2 = new DummyCustom(false);
        
        jm.add(dc1, 100);
        jm.add(dc2, 200);
        
        jm.service(50);  // 50
        jm.service(100); // 150
        jm.service(60);  // 210
        
        Assert.assertEquals("DC1 timing error", dc1.error, 50);
        Assert.assertEquals("DC2 timing error", dc2.error, 10);
    }
    
    
    @Test public void testStartFinish()
    {
        JobManager jm = new JobManager();        
        DummyCustom dc1 = new DummyCustom(false);
        
        
        jm.add(dc1, 0).repeat(0, 0);        
        Assert.assertEquals("DC1 add    (1)", 1, dc1.cnt_add);
        Assert.assertEquals("DC2 finish (1)", 0, dc1.cnt_finish);
                
        jm.service(100);
        Assert.assertEquals("DC1 add    (2)", 1, dc1.cnt_add);
        Assert.assertEquals("DC2 finish (2)", 1, dc1.cnt_finish);        
    }    
     
}
