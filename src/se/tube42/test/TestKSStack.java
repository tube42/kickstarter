
package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.ks.*;


@RunWith(JUnit4.class)
public class TestKSStack
{
    
    @Test public void test() 
    {
        KSStack<Integer> ss = new KSStack<Integer>();
        
        // 
        Assert.assertEquals("empty at start",  0, ss.getSize());
        Assert.assertEquals("empty at start",  null, ss.peek());
        Assert.assertEquals("empty at start",  null, ss.pop());
        
        // 
        for(int i = 0; i < 4; i++)
            ss.push(new Integer(i));
        
        for(int i = 0; i < 4; i++) {        
            Assert.assertEquals("added N",  4-i, ss.getSize());
            Assert.assertEquals("N-1 at peeked",  3-i, (int)ss.peek());
            Assert.assertEquals("N-1 at poped",  3-i, (int)ss.pop());
        }
        
        //
        Assert.assertEquals("empty at end",  0, ss.getSize());
        Assert.assertEquals("empty at end",  null, ss.peek());
        Assert.assertEquals("empty at end",  null, ss.pop());
    }
     
}
