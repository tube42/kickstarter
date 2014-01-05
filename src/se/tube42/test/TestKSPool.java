
package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.ks.*;


@RunWith(JUnit4.class)
public class TestKSPool
{
    
    @Test public void test() 
    {
        KSPool<Integer> sp = new KSPool<Integer>() {  
            public int cnt = 0;
            public Integer createNew() { return new Integer(cnt++); }
        };
        
        
        // 
        Integer i0 = sp.get();
        Integer i1 = sp.get();
        Assert.assertEquals("First element", 0, i0.intValue() );
        Assert.assertEquals("Second element", 1, i1.intValue() );
        
        
        // 
        sp.put(i1);
        Integer i2 = sp.get();
        Integer i3 = sp.get();
        Assert.assertEquals("Third element",  1, i2.intValue() );
        Assert.assertEquals("Fourth element", 2, i3.intValue() );
        
        //
        sp.put(i0);
        sp.put(i3);
        Integer i4 = sp.get();
        Integer i5 = sp.get();
        Integer i6 = sp.get();
        
        Assert.assertEquals("5th element",  2, i4.intValue() );
        Assert.assertEquals("6th element",  0, i5.intValue() );
        Assert.assertEquals("7th element",  3, i6.intValue() );                
    }
    
    @Test public void testStats() 
    {
        Object [] tmp = new Object[10];
        
        KSPool<Object> pool = new KSPool<Object>() {  
            public Object createNew() { return new Object(); }
        };
        
        
        // init
        Assert.assertEquals("pool get (0)", 0, pool.debugCountGet());
        Assert.assertEquals("pool put (0)", 0, pool.debugCountPut());
        Assert.assertEquals("pool new (0)", 0, pool.debugCountNew());        
        
        // first 10
        for(int i = 0; i < tmp.length; i++)
            tmp[i] = pool.get();
        
        Assert.assertEquals("pool get (1)", 10, pool.debugCountGet());
        Assert.assertEquals("pool put (1)", 0, pool.debugCountPut());
        Assert.assertEquals("pool new (1)", 10, pool.debugCountNew());        
        
        for(int i = 0; i < tmp.length; i++)
            pool.put( tmp[i] );
        
        Assert.assertEquals("pool get (2)", 10, pool.debugCountGet());
        Assert.assertEquals("pool put (2)", 10, pool.debugCountPut());
        Assert.assertEquals("pool new (2)", 10, pool.debugCountNew());        
        
        
        // next 10        
        for(int i = 0; i < tmp.length; i++)
            tmp[i] = pool.get();
        
        Assert.assertEquals("pool get (3)", 20, pool.debugCountGet());
        Assert.assertEquals("pool put (3)", 10, pool.debugCountPut());
        Assert.assertEquals("pool new (3)", 10, pool.debugCountNew());        
        
        for(int i = 0; i < tmp.length; i++)
            pool.put( tmp[i] );
        
        Assert.assertEquals("pool get (4)", 20, pool.debugCountGet());
        Assert.assertEquals("pool put (4)", 20, pool.debugCountPut());
        Assert.assertEquals("pool new (4)", 10, pool.debugCountNew());        
    }
    
}
