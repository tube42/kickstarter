package se.tube42.lib.ks;

/**
 * Simple message listener interface
 */
public interface MessageListener 
{    
    /** receive one message */
    public void onMessage(int msg, int data0, Object data1, Object sender);
}
