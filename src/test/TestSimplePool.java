
package test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.ks.utils.*;

@RunWith(JUnit4.class)
public class TestSimplePool
{
    
    @Test public void test() 
    {
        SimplePool<Integer> sp = new SimplePool<Integer>() {  
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
     
}
