package info.jeffkit.rabbitmq.benchmark;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class Consumer {
	
	private Channel channel;

	private String queue;
	
	private boolean autoAck;
	
	public boolean isPersistence() {
		return persistence;
	}

	public void setPersistence(boolean persistence) {
		this.persistence = persistence;
	}

	private boolean persistence;
	
	private int qos;
	
	private String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public Channel getChannel() {
		return channel;
	}

	public boolean isAutoAck() {
		return autoAck;
	}

	public void setAutoAck(boolean autoAck) {
		this.autoAck = autoAck;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	
	public Consumer(String queue) {
		super();
		this.queue = queue;
	}

	public void init() throws Exception{
		if(null == channel){
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost("localhost");
		    Connection connection = factory.newConnection();
		    channel = connection.createChannel();
		}

	    channel.queueDeclare(queue, persistence, false, false, null);

	    channel.basicQos(qos);
	    
	    QueueingConsumer consumer = new QueueingConsumer(channel);
	    channel.basicConsume(queue, autoAck, consumer);
	    
	    //取100000条数据每次
	    long start = System.currentTimeMillis();
	    int count = 0;
	    
	    while(true){
	    	count ++;
	    	try{
	    	QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	    	proccessMessage(delivery.getBody());
	    	if (!autoAck)
	    		channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	    	}catch(InterruptedException e){
	    		System.out.println("interrupted!");
	    		continue;
	    	}
	    	if(count%10000 == 0){
	    		long end = System.currentTimeMillis();
	    		System.out.println(this.id + " : it took " + (end - start) + " ms to consum " + count + " messages");
	    		//start = System.currentTimeMillis();
	    	}
	    }
	}
	
	public void proccessMessage(byte[] msg){
		new String(msg);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 * 从队列中接收消息。接受五个参数：
	 * 一、队列名，默认为single
	 * 二、消费者个数，默认为1个
	 * 三、QOS，默认为0，即无限制
	 * 四、autoAck,默认为true，即自动发送回执，无事务。
	 * 五、是否持久化，默认为否。
	 */
	public static void main(String[] args) throws Exception {
		String queue = "single";
		Integer cc = 1;
		Integer qos = 0;
		Boolean autoAck = true;
		Boolean persistence = false;
		
		if(args.length > 0){
			queue = args[0];
			System.out.println(queue);
		}
		
		if(args.length > 1){
			cc = Integer.valueOf(args[1]);
		}

		if(args.length > 2)
			qos = Integer.valueOf(args[2]);
		
		if(args.length > 3){
			autoAck = Boolean.valueOf(args[3]);
		}
		
		if(args.length > 4){
			persistence = Boolean.valueOf(args[4]);
		}
		
		System.out.println("there are " + cc + " comsumers");
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    
		for(int i = 0 ; i < cc ;i ++){
			final Consumer sr = new Consumer(queue);
			sr.setId(queue + i);
			sr.setQos(qos);
			sr.setAutoAck(autoAck);
			sr.setPersistence(persistence);
			
		    Channel channel = connection.createChannel();
		    sr.setChannel(channel);
		    Runnable ra = new Runnable(){
				@Override
				public void run() {
					try {
						sr.init();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		    	
		    };
			Thread t = new Thread(ra);
			t.start();
		}
		
	}

}
