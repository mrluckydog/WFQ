/*
 * @author Lijie
 * Date: 2015-6-2
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Router {
	//router的端口号
	public static final int ROUTER_PORT = 7000;
	
	public static void main(String[] args) throws Exception {
		InetAddress address = InetAddress.getLocalHost();
		System.out.println(address);
		DatagramSocket ds = new DatagramSocket(ROUTER_PORT, address);
		//存放flow1中每个数据包的data
		List<Byte> dpListFlowIdOne = Collections.synchronizedList(new ArrayList<Byte>());
		//存放flow2中每个数据包的data
		List<Byte> dpListFlowIdTwo = Collections.synchronizedList(new ArrayList<Byte>());
		//存放flow3中每个数据包的data
		List<Byte> dpListFlowIdThree = Collections.synchronizedList(new ArrayList<Byte>());
		//存放flow1中每个数据包的长度及weight
		List<Integer[]> dpLenListOne = Collections.synchronizedList(new ArrayList<Integer[]>());
		//存放flow2中每个数据包的长度及weight
		List<Integer[]> dpLenListTwo = Collections.synchronizedList(new ArrayList<Integer[]>());
		//存放flow3中每个数据包的长度及weight
		List<Integer[]> dpLenListThree = Collections.synchronizedList(new ArrayList<Integer[]>());
		//创建数据包接收线程
		new RouterReceiveThread(ds, dpListFlowIdOne, dpListFlowIdTwo, dpListFlowIdThree, dpLenListOne, dpLenListTwo, dpLenListThree).start();
		//创建数据包发送线程
		new RouterForwardThread(ds, dpListFlowIdOne, dpListFlowIdTwo, dpListFlowIdThree, dpLenListOne, dpLenListTwo, dpLenListThree).start();
	} 
}

/****************************************
 * 数据包接收线程类
 * @param DtatagramPacket ds
 * @param List<Byte> dpListFlowIdOne 
 * @param List<Byte> dpListFlowIdTwo 
 * @param List<Byte> dpListFlowIdThree 
 * @param List<Integer> dpLenListOne
 * @param List<Integer> dpLenListTwo 
 * @param List<Integer> dpLenListThree
 *
 */
class RouterReceiveThread extends Thread {
	//最大缓冲区大小
	public static final int MAX_BUFSIZE = 1024;
	//包头大小
	public static final int LEN_HEADER = 24;
	//包头中source ip、destination ip、source port、destination port、weight、flow id所占字节大小
	public static final int LEN_DATA_IN_HEADER = 4;
	DatagramSocket ds;
	List<Byte> dpListFlowIdOne;
	List<Byte> dpListFlowIdTwo;
	List<Byte> dpListFlowIdThree;
	List<Integer[]> dpLenListOne;
	List<Integer[]> dpLenListTwo;
	List<Integer[]> dpLenListThree;
	
	public RouterReceiveThread(DatagramSocket ds, List<Byte> dpListFlowIdOne, List<Byte> dpListFlowIdTwo, List<Byte> dpListFlowIdThree, List<Integer[]> dpLenListOne, List<Integer[]> dpLenListTwo, List<Integer[]> dpLenListThree) {
		this.ds = ds;
		this.dpListFlowIdOne = dpListFlowIdOne;  
		this.dpListFlowIdTwo = dpListFlowIdTwo;
		this.dpListFlowIdThree = dpListFlowIdThree;
		this.dpLenListOne = dpLenListOne;
		this.dpLenListTwo = dpLenListTwo;
		this.dpLenListThree = dpLenListThree;
	}
	
	public void receivePackets(){
		byte[] buf = new byte[MAX_BUFSIZE];
		int length = buf.length;
		
		DatagramPacket dp = new DatagramPacket(buf, length);
		PortNumber portNumber = new PortNumber();
		try{
			while(true) {
				ds.receive(dp);
				byte[] data = dp.getData();
				byte[] weightByte = new byte[LEN_DATA_IN_HEADER];
				byte[] flowIdByte = new byte[LEN_DATA_IN_HEADER];
				
				for(int i = LEN_DATA_IN_HEADER * 4; i < LEN_DATA_IN_HEADER * 5; i++) {
					weightByte[i - LEN_DATA_IN_HEADER * 4] = data[i];
				}
				for(int i = LEN_DATA_IN_HEADER * 5; i < LEN_DATA_IN_HEADER * 6; i++) {
					flowIdByte[i - LEN_DATA_IN_HEADER * 5] = data[i];
				}
				
				int packetLen = dp.getLength();
				int weight = portNumber.byte2int(weightByte);
				int flowId = portNumber.byte2int(flowIdByte);
				Integer[] lenWeight = new Integer[]{packetLen, weight};
				
				//分别将3个flow发来的数据存在3个不同的队列
				switch(flowId) {
					case 1:
						for(int i = 0; i < dp.getLength(); i++)
							dpListFlowIdOne.add(data[i]);
						dpLenListOne.add(lenWeight);
						break;
					case 2:
						for(int i = 0; i < dp.getLength(); i++)
							dpListFlowIdTwo.add(data[i]);
						dpLenListTwo.add(lenWeight);
						break;
					case 3:
						for(int i = 0; i < dp.getLength(); i++)
							dpListFlowIdThree.add(data[i]);
						dpLenListThree.add(lenWeight);
						break;
					default:
						break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void run() {
		try {
			receivePackets();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


/****************************************
 * 数据包转发线程类
 * @param DtatagramPacket ds
 * @param List<Byte> dpListFlowIdOne 
 * @param List<Byte> dpListFlowIdTwo 
 * @param List<Byte> dpListFlowIdThree 
 * @param List<Integer> dpLenListOne
 * @param List<Integer> dpLenListTwo 
 * @param List<Integer> dpLenListThree
 *
 */

class RouterForwardThread extends Thread {
	public static final int LEN_DATA_IN_HEADER = 4;
	public static final int MAX_BUFSIZE = 1024;
	public static final int LEN_HEADER = 24;
	public static final int LINK_CAPACITY = 10;
	//当前的round number
	public static int currentRound = 0;
	//上一次轮询的时间戳
	public static long checkTime = 0;
	//活跃的队列数
	public static int activeNumber = 0;
	//表示flow1是否为空,如果为空则置0，否则置1
	public static int statusFlowOne = 0;
	//表示flow2是否为空,如果为空则置0，否则置1
	public static int statusFlowTwo = 0;
	//表示flow3是否为空,如果为空则置0，否则置1
	public static int statusFlowThree = 0;
	DatagramSocket ds;
	List<Byte> dpListFlowIdOne;
	List<Byte> dpListFlowIdTwo;
	List<Byte> dpListFlowIdThree;
	List<Integer[]> dpLenListOne;
	List<Integer[]> dpLenListTwo;
	List<Integer[]> dpLenListThree;
	//flow1中第一个数据包的finish number
	int finishNumberOne = 0;
	//flow2中第一个数据包的finish number
	int finishNumberTwo = 0;
	//flow3中第一个数据包的finish number
	int finishNumberThree = 0;
	//flow1中最近一个发送的数据包的finish number
	int preFinishNumberOne = 0;
	//flow2中最近一个发送的数据包的finish number
	int preFinishNumberTwo = 0;
	//flow3中最近一个发送的数据包的finish number
	int preFinishNumberThree = 0;
	
	public RouterForwardThread(DatagramSocket ds, List<Byte> dpListFlowIdOne, List<Byte> dpListFlowIdTwo, List<Byte> dpListFlowIdThree, List<Integer[]> dpLenListOne, List<Integer[]> dpLenListTwo, List<Integer[]> dpLenListThree) {
		this.ds = ds;
		this.dpListFlowIdOne = dpListFlowIdOne;
		this.dpListFlowIdTwo = dpListFlowIdTwo;
		this.dpListFlowIdThree = dpListFlowIdThree;
		this.dpLenListOne = dpLenListOne;
		this.dpLenListTwo = dpLenListTwo;
		this.dpLenListThree = dpLenListThree;
	}
	
	/*
	 * 更新round number
	 */
	public void updateRoundNumber() {
		//两次轮询的时间差
		int diff = 0;
		
		if(!dpLenListOne.isEmpty())
			statusFlowOne = 1;
		
		if(!dpLenListTwo.isEmpty())
			statusFlowTwo = 1;
		
		if(!dpLenListThree.isEmpty())
			statusFlowThree = 1;
		
		activeNumber = statusFlowOne + statusFlowTwo + statusFlowThree;
		
		if(activeNumber == 0) {
			currentRound = 0;
			checkTime = System.currentTimeMillis();
		}else {
			long currentTime = System.currentTimeMillis();
			diff = (int)(currentTime - checkTime);
			if(checkTime == 0)
				diff = 0;
			currentRound = currentRound + diff * LINK_CAPACITY / activeNumber;
//			currentRound += 5;
			checkTime = currentTime;
		}
	}
	
	/*
	 * 计算每个flow中第一个数据包的finish number
	 */
	public int computeFinishNumber(List<Integer[]> dpLenList, int preFinishNumber) {
		int finishNumber = 0;
		int packetLength = dpLenList.get(0)[0];
		int weight = dpLenList.get(0)[1];
		
		if(currentRound == 0)
			finishNumber = currentRound + packetLength / weight;
		else
			finishNumber = preFinishNumber + packetLength / weight;
		
//		System.out.println("dpLen:" + packetLength + " preFinishNumber:" + preFinishNumber);
//		System.out.println("finishNumber:" + finishNumber);
		return finishNumber;
	}
	
	public String[] getDestAddrPort(byte[] data) throws Exception {
		PortNumber portNumber = new PortNumber();
		byte[] destAddrByte = new byte[LEN_DATA_IN_HEADER];
		byte[] destPortByte = new byte[LEN_DATA_IN_HEADER];
		
		//获取目标ip地址
		for(int i = LEN_DATA_IN_HEADER; i < LEN_DATA_IN_HEADER * 2; i++) {
			destAddrByte[i - LEN_DATA_IN_HEADER] = data[i];
		}
		//获取目标端口号
		for(int i = LEN_DATA_IN_HEADER * 3; i < LEN_DATA_IN_HEADER * 4; i++) {
			destPortByte[i - LEN_DATA_IN_HEADER * 3] = data[i];
		}
		
		String destAddr = InetAddress.getByAddress(destAddrByte).toString();
		destAddr = destAddr.substring(1);
		int destPort = portNumber.byte2int(destPortByte);
		String[] destAddrPort = new String[]{destAddr, destPort + ""};
		return destAddrPort;
	}
	
	/*
	 * 实现数据包发送的功能
	 */
	public void sendPackets(byte[] data) throws Exception {
		String[] destAddrPort = getDestAddrPort(data);
		InetAddress destAddr = InetAddress.getByName(destAddrPort[0]);
		int destPort = Integer.parseInt(destAddrPort[1]);
		DatagramPacket dp = new DatagramPacket(data, data.length, destAddr, destPort);
		ds.send(dp);
		System.out.println("Router forward：dest " + destAddrPort[0] + " " + destAddrPort[1] + " " + data.length);
	}
	
	/*
	 * 转发数据包
	 */
	public void forwardPackets()  throws Exception {
		for(;;) {
			updateRoundNumber();
//			System.out.println("currentRound: " + currentRound);
			
			if(dpListFlowIdOne.isEmpty())
				preFinishNumberOne = currentRound;
			
			if(dpListFlowIdTwo.isEmpty()) 
				preFinishNumberTwo = currentRound;
			
			if(dpListFlowIdThree.isEmpty()) 
				preFinishNumberThree = currentRound;
			
			if(!dpListFlowIdOne.isEmpty() && !dpLenListOne.isEmpty())
				finishNumberOne = computeFinishNumber(dpLenListOne, preFinishNumberOne);
			
			if(!dpListFlowIdTwo.isEmpty() && !dpLenListTwo.isEmpty())
				finishNumberTwo = computeFinishNumber(dpLenListTwo, preFinishNumberTwo);
			
			if(!dpListFlowIdThree.isEmpty() && !dpLenListThree.isEmpty())
				finishNumberThree = computeFinishNumber(dpLenListThree, preFinishNumberThree);
			
			if(currentRound >= finishNumberOne && !dpListFlowIdOne.isEmpty() && !dpLenListOne.isEmpty()) {
				int dataLen = dpLenListOne.get(0)[0];
				byte[] data = new byte[dataLen];
				//取出flow1中的数据
				for(int i = 0; i < dataLen; i++) {
					data[i] = dpListFlowIdOne.get(0);
					dpListFlowIdOne.remove(0);
				}
				sendPackets(data);
				//设置flow1中前一个数据包的finish number为current round
				preFinishNumberOne = currentRound;
//				System.out.println("finishNumber 1: " + finishNumberOne);
				dpLenListOne.remove(0);
				//更改flow1状态标识
				if(dpLenListOne.isEmpty())
					statusFlowOne = 0;
			}
			if(currentRound >= finishNumberTwo && !dpListFlowIdTwo.isEmpty() && !dpLenListTwo.isEmpty()) {
				int dataLen = dpLenListTwo.get(0)[0];
				byte[] data = new byte[dataLen];
				//取出flow2中的数据
				for(int i = 0; i < dataLen; i++) {
					data[i] = dpListFlowIdTwo.get(0);
					dpListFlowIdTwo.remove(0);
				}
				sendPackets(data);
				//设置flow2中前一个数据包的finish number为current round
				preFinishNumberTwo = currentRound;
//				System.out.println("finishNumber 2: " + finishNumberOne);
				dpLenListTwo.remove(0);
				//更改flow2状态标识
				if(dpLenListTwo.isEmpty())
					statusFlowTwo = 0;
			}
			if(currentRound >= finishNumberThree && !dpListFlowIdThree.isEmpty() && !dpLenListThree.isEmpty()) {
				int dataLen = dpLenListThree.get(0)[0];
				byte[] data = new byte[dataLen];
				//取出flow3中的数据
				for(int i = 0; i < dataLen; i++) {
					data[i] = dpListFlowIdThree.get(0);
					dpListFlowIdThree.remove(0);
				}
				sendPackets(data);
				//设置flow3中前一个数据包的finish number为current round
				preFinishNumberThree = currentRound;
//				System.out.println("finishNumber 3: " + finishNumberOne);
				dpLenListThree.remove(0);
				//更改flow3状态标识
				if(dpLenListThree.isEmpty())
					statusFlowThree = 0;
			}
//			System.out.println("----------");
			Thread.sleep(2);
		}
	}
	
	public void run() {
		try {
			forwardPackets();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
