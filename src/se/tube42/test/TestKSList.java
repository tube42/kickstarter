
package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.ks.*;

@RunWith(JUnit4.class)
public class TestKSList
{
    
    @Test public void test() 
    {
        KSList<Object> sl = new KSList<Object>();
        
        Object [] data = new Object[4];
        for(int i = 0; i < data.length; i++) 
            data[i] = new Object();
        
        Assert.assertEquals("Empty at start", 0, sl.getSize());
        
        // 
        sl.add(data[0]);
        sl.add(data[1]);
        sl.add(data[2]);
        Assert.assertEquals("Three elements", 3, sl.getSize());
        
        Assert.assertEquals("Element -1?", null, sl.get(-1));        
        Assert.assertEquals("Element 0", data[0], sl.get(0));
        Assert.assertEquals("Element 1", data[1], sl.get(1));
        Assert.assertEquals("Element 2", data[2], sl.get(2));
        Assert.assertEquals("Element 3?", null, sl.get(3));
                
        //
        sl.remove(1);
        Assert.assertEquals("Two elements", 2, sl.getSize());
        
        Assert.assertEquals("Element -1?", null, sl.get(-1));        
        Assert.assertEquals("Element 0", data[0], sl.get(0));
        Assert.assertEquals("Element 1", data[2], sl.get(1));
        Assert.assertEquals("Element 2?", null, sl.get(2));
        
        //
        sl.add(data[3]);
        Assert.assertEquals("Three elements", 3, sl.getSize());
        
        Assert.assertEquals("Element -1?", null, sl.get(-1));        
        Assert.assertEquals("Element 0", data[0], sl.get(0));
        Assert.assertEquals("Element 1", data[2], sl.get(1));
        Assert.assertEquals("Element 2", data[3], sl.get(2));
        Assert.assertEquals("Element 3?", null, sl.get(3));
                
    }
    
    @Test public void testAddArray() 
    {
        KSList<Object> sl = new KSList<Object>();
        
        Object [] data = new Object[4];
        for(int i = 0; i < data.length; i++) 
            data[i] = new Object();
        
        Assert.assertEquals("Empty at start", 0, sl.getSize());
        
        // 
        sl.add(data);
        Assert.assertEquals("correct number of elements",
                  data.length, sl.getSize());
        
        for(int i = 0; i < data.length; i++)
            Assert.assertEquals("Element i", data[i], sl.get(i));
    }
        
    
    @Test public void testGrow() 
    {
        final int cnt = 10000;
        KSList<Integer> sl = new KSList<Integer>();
        
        for(int i = 0; i < cnt; i++)
            sl.add( new Integer(i));
        
        //
        for(int i = 0; i < cnt; i++) {
            Integer n = sl.get(i);
            Assert.assertEquals("member check after grow",
                      i, n.intValue());
        }
    }    
}
