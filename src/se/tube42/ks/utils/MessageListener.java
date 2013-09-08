package se.tube42.ks.utils;

public interface MessageListener 
{
    public static final int
          MSG_START = -1
          ;
    
    
    public void onMessage(int msg, int data0, Object data1, Object sender);
}
