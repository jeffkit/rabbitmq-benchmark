package info.jeffkit.rabbitmq;

public class BytesMaker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Size is " + make1k().length);
	}
	
	public static byte[] make512(){
		return makeBytes(512);
	}
	
	public static byte[] make1k(){
		return makeBytes(1024);
	}
	
	public static byte[] make10k(){
		return makeBytes(10240);
	}
	
	public static byte[] makeBytes(Integer size){
		 byte[] bytes = new byte[size];
		for (int i = 0;i < size ;i ++){
			bytes[i] = new Byte("1");
		}
		return bytes;
	}

}
