package test;

import java.io.Closeable;
import java.util.List;
import redis.clients.jedis.Jedis;



public class JredisDao {

    static int MAX_MESSAGE_QUEUE = 10;
    JedisUtils impl = new JedisUtilsImpl();
    public void SaveMessage(String userId, String message)
    {
        impl.lpushOneString(userId,message);
        impl.ltrim(userId, 0, MAX_MESSAGE_QUEUE);
    }

    public void PrintRecentMessage(String userId)
    {
        
        List<String> messageList = impl.getStrings(userId, MAX_MESSAGE_QUEUE);
        if(messageList == null) return;
        for (int i = 0; i < messageList.size(); ++i)
        {
            System.out.println(messageList.get(i));
        }
    }

    public void SAMPLE_MessageQueue()
    { 	
    	for (int i = 0; i < 1000; i++)
        {
              String message = "Hello World " + i + " !";
              SaveMessage( "test", message);
              PrintRecentMessage(  "test");
        }
    }
    
    public static void main(String[] args) {
    	JredisDao test = new JredisDao();
    	test.SAMPLE_MessageQueue();
	}
}