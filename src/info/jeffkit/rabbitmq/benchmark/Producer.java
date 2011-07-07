package info.jeffkit.rabbitmq.benchmark;

import info.jeffkit.rabbitmq.BytesMaker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.AMQP.BasicProperties;

public class Producer {
	/**
	 * @param args
	 * @throws Exception 
	 * 消息入队列测试，接受三个参数：
	 * 1、队列名，默认为single。
	 * 2、每条消息大小，，默认值为1kByte。
	 * 3、消息是否持久化，默认为不持久化。
	 * 使用示例：SignleSender myqueue 1024 true
	 */
	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		byte[] data = BytesMaker.make1k();
		boolean durable = false;
		String queue = "single";
		int count = 100000;
		
		if (args.length > 0){
			queue = args[0];
		}
		
		if (args.length > 1){
			data = BytesMaker.makeBytes(Integer.valueOf(args[1]));
			System.out.println(args[1] + " Bytes per msg");
		}
		
		if (args.length > 2){
			durable = Boolean.valueOf(args[2]);
		}
		
		if (args.length > 3){
			count = Integer.valueOf(args[3]);
		}
		
		BasicProperties props = durable ? MessageProperties.PERSISTENT_TEXT_PLAIN : null;
		
		channel.queueDeclare(queue, durable, false, false, null);
		
		
		long start = System.currentTimeMillis();
		for (int i = 0 ; i < count/10000 ; i ++){
			long inner_start = System.currentTimeMillis();
			for (int j = 0; j < 10000; j ++)
				channel.basicPublish("", queue, props, data);
			long inner_end = System.currentTimeMillis();
			System.out.println("round " + (i + 1) + " takes " + (inner_end - inner_start) + " millseconds ");
		}
		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("It takes " + time + " millseconds to send " + count + " message to queue");
		channel.close();
		connection.close();
	}

}
