/*
 *用于端口号int类型和byte类型相互转换的类 
 */

public class PortNumber {
	/*
	 * 将int类型的端口号转换为byte类型
	 */
	public byte[] int2byte(int portNumber) {
		byte[] port = new byte[4];
		port[3] = (byte) (portNumber & 0xff);
		port[2] = (byte) ((portNumber >> 8) & 0xff);
		port[1] = (byte) ((portNumber >> 16) & 0xff);
		port[0] = (byte) ((portNumber >> 24) & 0xff);
		return port;
	}
	
	/*
	 * 将byte类型的端口号转换为int类型
	 */
	public int byte2int(byte[] port) {
		int value = 0;
		int temp = 0;
		for(int i = 0; i < 4; i++) {
			value <<= 8;
			temp = port[i] & 0xff;
			value |= temp;
		}
		return value;
	}
}
