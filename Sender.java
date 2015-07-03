/*
 * @author Lijie
 * Date: 2015/6/2
 */

import java.awt.Container;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Sender extends JFrame {
	private static final long serialVersionUID = -400684274667631551L;
	public static final int FRAME_WIDTH = 360;
	public static final int FRAME_HEIGHT = 500;
	public static JButton btnSend;
	public static TextField srcAddrText;
	public static TextField routerAddrText;
	public static TextField destAddrText;
	public static TextField srcPortOneText;
	public static TextField srcPortTwoText;
	public static TextField srcPortThreeText;
	public static TextField flowOnePortText;
	public static TextField flowOneWeightText;
	public static TextField flowTwoPortText;
	public static TextField flowTwoWeightText;
	public static TextField flowThreePortText;
	public static TextField flowThreeWeightText;
	public static TextField packetsOneText;
	public static TextField packetsTwoText;
	public static TextField packetsThreeText;
	
	public Sender() {
		this.setLayout(null);
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		panel.setLayout(null);
		JLabel srcAddrLabel = new JLabel("Source IP Address:");
		srcAddrLabel.setBounds(6, 10, 120, 20);
		srcAddrText = new TextField("10.20.215.76");
		srcAddrText.setBounds(130, 10, 200, 20);
		JLabel routerAddrLabel = new JLabel("Router IP Address:");
		routerAddrLabel.setBounds(6, 35, 120, 20);
		routerAddrText = new TextField("10.20.215.76");
		routerAddrText.setBounds(130, 35, 200, 20);
		JLabel destAddrLabel = new JLabel("Destion IP Address:");
		destAddrLabel.setBounds(6, 60, 120, 20);
		destAddrText = new TextField("10.20.215.76");
		destAddrText.setBounds(130, 60, 200, 20);
		JLabel flowOneLabel = new JLabel("Flow 1");
		flowOneLabel.setBounds(6, 90, 100, 20);
		JLabel srcPortOneLabel = new JLabel("Source Port:");
		srcPortOneLabel.setBounds(6, 110, 80, 20);
		srcPortOneText = new TextField("5001");
		srcPortOneText.setBounds(100, 110, 50, 20);
		JLabel flowOnePortLabel = new JLabel("Destination Port:");
		flowOnePortLabel.setBounds(180, 110, 100, 20);
		flowOnePortText = new TextField("6001");
		flowOnePortText.setBounds(280, 110, 50, 20);
		JLabel flowOneWeightLabel = new JLabel("Weight:");
		flowOneWeightLabel.setBounds(6, 140, 90, 20);
		flowOneWeightText = new TextField("1");
		flowOneWeightText.setBounds(100, 140, 50, 20);
		JLabel packetsOneLabel = new JLabel("Packets:");
		packetsOneLabel.setBounds(180, 140, 90, 20);
		packetsOneText = new TextField("50");
		packetsOneText.setBounds(280, 140, 50, 20);
		JLabel flowTwoLabel = new JLabel("Flow 2");
		flowTwoLabel.setBounds(6, 180, 100, 20);
		JLabel srcPortTwoLabel = new JLabel("Source Port:");
		srcPortTwoLabel.setBounds(6, 200, 90, 20);
		srcPortTwoText = new TextField("5002");
		srcPortTwoText.setBounds(100, 200, 50, 20);
		JLabel flowTwoPortLabel = new JLabel("Destination Port:");
		flowTwoPortLabel.setBounds(180, 200, 100, 20);
		flowTwoPortText = new TextField("6002");
		flowTwoPortText.setBounds(280, 200, 50, 20);
		JLabel flowTwoWeightLabel = new JLabel("Weight:");
		flowTwoWeightLabel.setBounds(6, 230, 80, 20);
		flowTwoWeightText = new TextField("2");
		flowTwoWeightText.setBounds(100, 230, 50, 20);
		JLabel packetsTwoLabel = new JLabel("Packets:");
		packetsTwoLabel.setBounds(180, 230, 90, 20);
		packetsTwoText = new TextField("50");
		packetsTwoText.setBounds(280, 230, 50, 20);
		JLabel flowThreeLabel = new JLabel("Flow 3");
		flowThreeLabel.setBounds(6, 270, 80, 20);
		JLabel srcPortThreeLabel = new JLabel("Source Port:");
		srcPortThreeLabel.setBounds(6, 290, 90, 20);
		srcPortThreeText = new TextField("5003");
		srcPortThreeText.setBounds(100, 290, 50, 20);
		JLabel flowThreePortLabel = new JLabel("Destination Port:");
		flowThreePortLabel.setBounds(180, 290, 100, 20);
		flowThreePortText = new TextField("6003");
		flowThreePortText.setBounds(280, 290, 50, 20);
		JLabel flowThreeWeightLabel = new JLabel("Weight:");
		flowThreeWeightLabel.setBounds(6, 320, 80, 20);
		flowThreeWeightText = new TextField("3");
		flowThreeWeightText.setBounds(100, 320, 50, 20);
		JLabel packetsThreeLabel = new JLabel("Packets:");
		packetsThreeLabel.setBounds(180, 320, 100, 20);
		packetsThreeText = new TextField("50");
		packetsThreeText.setBounds(280, 320, 50, 20);
		btnSend = new JButton("Send");
		btnSend.setBounds(FRAME_WIDTH - 100, FRAME_HEIGHT - 90, 70, 30);
		panel.add(srcAddrLabel);
		panel.add(srcAddrText);
		panel.add(routerAddrLabel);
		panel.add(routerAddrText);
		panel.add(flowOneLabel);
		panel.add(srcPortOneLabel);
		panel.add(srcPortOneText);
		panel.add(flowTwoLabel);
		panel.add(srcPortTwoLabel);
		panel.add(srcPortTwoText);
		panel.add(flowThreeLabel);
		panel.add(srcPortThreeLabel);
		panel.add(srcPortThreeText);
		panel.add(destAddrLabel);
		panel.add(destAddrText);
		panel.add(flowOnePortLabel);
		panel.add(flowOneWeightLabel);
		panel.add(flowOnePortText);
		panel.add(flowOneWeightText);
		panel.add(flowTwoPortLabel);
		panel.add(flowTwoWeightLabel);
		panel.add(flowTwoPortText);
		panel.add(flowTwoWeightText);
		panel.add(flowThreePortLabel);
		panel.add(flowThreeWeightLabel);
		panel.add(flowThreePortText);
		panel.add(flowThreeWeightText);
		panel.add(packetsOneLabel);
		panel.add(packetsTwoLabel);
		panel.add(packetsThreeLabel);
		panel.add(packetsOneText);
		panel.add(packetsTwoText);
		panel.add(packetsThreeText);
		panel.add(btnSend);
		contentPane.add(panel);
	}

	public static void main(String[] args){
		new Sender();
		btnSend.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String srcAddr = srcAddrText.getText();
				String routerAddr = routerAddrText.getText();
				String destAddr = destAddrText.getText();
				String srcPortOneStr = srcPortOneText.getText();
				String srcPortTwoStr = srcPortTwoText.getText();
				String srcPortThreeStr = srcPortThreeText.getText();
				String flowOnePortStr = flowOnePortText.getText();
				String flowTwoPortStr = flowTwoPortText.getText();
				String flowThreePortStr = flowThreePortText.getText();
				String flowOneWeightStr = flowOneWeightText.getText();
				String flowTwoWeightStr = flowTwoWeightText.getText();
				String flowThreeWeightStr = flowThreeWeightText.getText();
				String flowOnePacketsStr = packetsOneText.getText();
				String flowTwoPacketsStr = packetsTwoText.getText();
				String flowThreePacketsStr = packetsThreeText.getText();
				int srcPortOne = Integer.parseInt(srcPortOneStr);
				int srcPortTwo = Integer.parseInt(srcPortTwoStr);
				int srcPortThree = Integer.parseInt(srcPortThreeStr);
				int flowOnePort = Integer.parseInt(flowOnePortStr);
				int flowTwoPort = Integer.parseInt(flowTwoPortStr);
				int flowThreePort = Integer.parseInt(flowThreePortStr);
				int flowOneWeight = Integer.parseInt(flowOneWeightStr);
				int flowTwoWeight = Integer.parseInt(flowTwoWeightStr);
				int flowThreeWeight = Integer.parseInt(flowThreeWeightStr);
				int flowOnePackets = Integer.parseInt(flowOnePacketsStr);
				int flowTwoPackets = Integer.parseInt(flowTwoPacketsStr);
				int flowThreePackets = Integer.parseInt(flowThreePacketsStr);
				
				new SendThread(srcAddr, routerAddr, destAddr, 7000, srcPortOne, flowOnePort, flowOneWeight, 1, flowOnePackets).start();
				new SendThread(srcAddr, routerAddr, destAddr, 7000, srcPortTwo, flowTwoPort, flowTwoWeight, 2, flowTwoPackets).start();
				new SendThread(srcAddr, routerAddr, destAddr, 7000, srcPortThree, flowThreePort, flowThreeWeight, 3, flowThreePackets).start();
				System.out.println(srcAddr + " " + destAddr + " " + srcPortOne + " " + flowOnePort + " " + flowOneWeight + " " + flowOnePackets);
				System.out.println(srcAddr + " " + destAddr + " " + srcPortTwo + " " + flowTwoPort + " " + flowTwoWeight + " " + flowTwoPackets);
				System.out.println(srcAddr + " " + destAddr + " " + srcPortThree + " " + flowThreePort + " " + flowThreeWeight + " " + flowThreePackets);
			}
		});
	}
}

/*
 * 发送数据包线程的类
 * @param String srcAddr 表示源ip地址
 * @param String routerAddr 表示router的ip地址
 * @param String destAddr 表示目的ip地址
 * @param int routerPort  表示router的端口号
 * @param int localPort   表示源端口号
 * @param int destPort    表示目的端口号
 * @param int weight    表示权值
 * @param int flowId  表示流的ID号
 */
class SendThread extends Thread {
	public static final int MAX_BUFSIZE = 1024;
	public static final int LEN_HEADER = 24;
	public static final int LEN_DATA_IN_HEADER = 4;
	public static final int MIN_LEN_DATA  = 1;
	public String srcAddr = "";
	public String routerAddr = "";
	public String destAddr = "";
	public int routerPort = 0;
	public int localPort = 0;
	public int destPort = 0;
	public int weight = 0;
	public int flowId = 0;
	public int packets = 0;
	
	public SendThread(String srcAddr, String routerAddr, String destAddr, int routerPort, int localPort, int destPort, int weight, int flowId, int packets) {
		this.srcAddr = srcAddr;
		this.routerAddr = routerAddr;
		this.destAddr = destAddr;
		this.routerPort = routerPort;
		this.localPort = localPort;
		this.destPort = destPort;
		this.weight = weight;
		this.flowId = flowId;
		this.packets = packets;
//		System.out.println(srcAddr + " " + destAddr + " " + routerPort + " " + localPort + " " + destPort + " " + weight + " " + flowId);
	}
	
	/*
	 * 随机产生一个字母
	 */
	public char createLetter() {
		String letters = "abcdefghijklmnopqrstuvwxyz";
		int length = letters.length();
		int index = (int)( Math.random() * 100 % length);
		char letter = letters.charAt(index);
		return letter;
	}
	
	/*
	 * 生成一个UDP数据包
	 */
	public DatagramPacket createPacket() throws Exception {
		InetAddress localHost = InetAddress.getByName(srcAddr);
		InetAddress routerHost = InetAddress.getByName(routerAddr);
		InetAddress destHost = InetAddress.getByName(destAddr);
		int dataLength = (int) (Math.random() * 10000 % (MAX_BUFSIZE - LEN_HEADER) + MIN_LEN_DATA);
		byte[] srcAddrByte = localHost.getAddress();
		byte[] destAddrByte = destHost.getAddress();
		byte[] srcPortByte = new byte[LEN_DATA_IN_HEADER];
		byte[] destPortByte = new byte[LEN_DATA_IN_HEADER];
		byte[] weightByte = new byte[LEN_DATA_IN_HEADER];
		byte[] flowIdByte = new byte[LEN_DATA_IN_HEADER];
		byte[] dataByte = new byte[MAX_BUFSIZE - LEN_HEADER];
		PortNumber portNumber = new PortNumber();
		srcPortByte = portNumber.int2byte(localPort);
		destPortByte = portNumber.int2byte(destPort);
		weightByte = portNumber.int2byte(weight);
		flowIdByte = portNumber.int2byte(flowId);
		
		for(int i = 0; i < dataLength; i++) {
			dataByte[i] = (byte) createLetter();  
		}
		byte[] buf = new byte[LEN_HEADER + dataLength];
		System.arraycopy(srcAddrByte, 0, buf, 0, LEN_DATA_IN_HEADER);
		System.arraycopy(destAddrByte, 0, buf, LEN_DATA_IN_HEADER, LEN_DATA_IN_HEADER);
		System.arraycopy(srcPortByte, 0, buf, LEN_DATA_IN_HEADER * 2, LEN_DATA_IN_HEADER);
		System.arraycopy(destPortByte, 0, buf, LEN_DATA_IN_HEADER * 3, LEN_DATA_IN_HEADER);
		System.arraycopy(weightByte, 0, buf, LEN_DATA_IN_HEADER * 4, LEN_DATA_IN_HEADER);
		System.arraycopy(flowIdByte, 0, buf, LEN_DATA_IN_HEADER * 5, LEN_DATA_IN_HEADER);
		System.arraycopy(dataByte, 0, buf, LEN_DATA_IN_HEADER * 6, dataLength);
		DatagramPacket dp = new DatagramPacket(buf, buf.length, routerHost, routerPort);
		return dp;
	}
	
	/*
	 * 发送数据包
	 */
	public void sendPackets() throws Exception {
		DatagramSocket ds = new DatagramSocket(localPort);
		DatagramPacket dp = createPacket();
		ds.send(dp);
		ds.close();
	}
	
	public void run() {
		try {
			for(int i = 0; i < packets; i++) {
				sendPackets();
				System.out.println(i);
				Thread.sleep(30);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
