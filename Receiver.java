/*
 * @author Lijie
 * Date: 2015-6-2
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class Receiver extends JFrame {
	
	private static final long serialVersionUID = 7045777290518418868L;
	//初始化flow1的数据包总个数
	public static int totalPacketsFlowIdOne = 0;
	//初始化flow2的数据包总个数
	public static int totalPacketsFlowIdTwo = 0;
	//初始化flow3的数据包总个数
	public static int totalPacketsFlowIdThree = 0;
	public static final int PANEL_WIDTH = 700;
	public static final int PANEL_HEIGHT = 455;
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 700;
	public static Panel brokenLinePanel;
	public static Panel axisPanel;
	public static JButton btnStart;
	public static JButton btnStop;
	public static JTextField portOneText;
	public static JTextField portTwoText;
	public static JTextField portThreeText;
	
	public Receiver() {
		this.setLayout(null);
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);
		//画折线的panel
		brokenLinePanel = new Panel();
		brokenLinePanel.setBounds(60, 50, PANEL_WIDTH, PANEL_HEIGHT);
//		brokenLinePanel.setBackground(new Color(200, 200, 200));
		//画坐标轴的panel
		axisPanel = new Panel();
		axisPanel.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT - 90);
//		axisPanel.setBackground(new Color(223, 223, 223));
		//输入端口号的panel
		Panel inputPanel = new Panel();
		inputPanel.setBounds(0, FRAME_HEIGHT - 80, FRAME_WIDTH, 80);
//		inputPanel.setBackground(new Color(200, 200, 200));
		JLabel portOneLabel = new JLabel("Port 1:");
		portOneLabel.setBounds(60, 10, 60, 20);
		portOneText = new JTextField("6001");
		portOneText.setBounds(100, 10, 60, 20);
		JLabel portTwoLabel = new JLabel("Port 2:");
		portTwoLabel.setBounds(180, 10, 60, 20);
		portTwoText = new JTextField("6002");
		portTwoText.setBounds(220, 10, 60, 20);
		JLabel portThreeLabel = new JLabel("Port 3:");
		portThreeLabel.setBounds(300, 10, 60, 20);
		portThreeText = new JTextField("6003");
		portThreeText.setBounds(340, 10, 60, 20);
		btnStart = new JButton("Start");
		btnStart.setBounds(FRAME_WIDTH - 220, 6, 80, 30);
		btnStop = new JButton("Stop");
		btnStop.setBounds(FRAME_WIDTH - 120, 6, 80, 30);
		inputPanel.add(portOneLabel);
		inputPanel.add(portOneText);
		inputPanel.add(portTwoLabel);
		inputPanel.add(portTwoText);
		inputPanel.add(portThreeLabel);
		inputPanel.add(portThreeText);
		inputPanel.add(btnStart);
		inputPanel.add(btnStop);
		this.add(inputPanel);
		this.add(brokenLinePanel);
		this.add(axisPanel);
	}
	
	public static void main(String[] args) throws Exception {
		new Receiver();
		TotalPacketsNumber totalPacketsNumber = new TotalPacketsNumber(totalPacketsFlowIdOne, totalPacketsFlowIdTwo, totalPacketsFlowIdThree);
		DrawCoordinateAxisThread drawCoordinateAxisThread = new DrawCoordinateAxisThread(axisPanel, totalPacketsNumber);
		DrawBrokenLineThread drawBrokenLineThread = new DrawBrokenLineThread(brokenLinePanel, totalPacketsNumber);
		drawCoordinateAxisThread.start();
		drawBrokenLineThread.start();
		
		ReceiverThread receiverThreadOne = null;
		ReceiverThread receiverThreadTwo = null;
		ReceiverThread receiverThreadThree = null;
		
		TextFieldCache textFieldCache = new TextFieldCache(portOneText, portTwoText, portThreeText);
		ThreadCache threadCache = new ThreadCache(receiverThreadOne, receiverThreadTwo, receiverThreadThree);
		StartActionListener startActionListener = new StartActionListener(drawCoordinateAxisThread, drawBrokenLineThread, textFieldCache, totalPacketsNumber, threadCache);
		btnStart.addActionListener(startActionListener);
		StopActionListener stopActionListener = new StopActionListener(drawCoordinateAxisThread, drawBrokenLineThread, threadCache);
		btnStop.addActionListener(stopActionListener);
	}
}

/*
 * 储存JTextField对象的类
 * @param JTextField portOneText
 * @param JTextField portTwoText
 * @param JTextField portThreeText
 */
class TextFieldCache {
	JTextField portOneText;
	JTextField portTwoText;
	JTextField portThreeText;
	
	public TextFieldCache(JTextField portOneText, JTextField portTwoText, JTextField portThreeText) {
		this.portOneText = portOneText;
		this.portTwoText = portTwoText;
		this.portThreeText = portThreeText;
	}
}

/*
 * 储存线程的类
 * @param ReceiverThread receiverThreadOne
 * @param ReceiverThread receiverThreadTwo
 * @param ReceiverThread receiverThreadThree
 */
class ThreadCache {
	public ReceiverThread receiverThreadOne;
	public ReceiverThread receiverThreadTwo;
	public ReceiverThread receiverThreadThree;

	public ThreadCache(ReceiverThread receiverThreadOne, ReceiverThread receiverThreadTwo, ReceiverThread receiverThreadThree) {
		this.receiverThreadOne = receiverThreadOne;
		this.receiverThreadTwo = receiverThreadTwo;
		this.receiverThreadThree = receiverThreadThree;
	}
}

/*
 * 开始按钮事件的类
 * @param DrawCoordinateAxisThread drawCoordinateAxisThread
 * @param DrawBrokenLineThread drawBrokenLineThread
 * @param TextFieldCache textFieldCache
 * @param TotalPacketsNumber totalPacketsNumber
 * @param ThreadCache threadCache
 * 
 */
class StartActionListener implements ActionListener {
	DrawCoordinateAxisThread drawCoordinateAxisThread;
	DrawBrokenLineThread drawBrokenLineThread;
	TextFieldCache textFieldCache;
	TotalPacketsNumber totalPacketsNumber;
	ThreadCache threadCache;
	
	public StartActionListener(DrawCoordinateAxisThread drawCoordinateAxisThread, DrawBrokenLineThread drawBrokenLineThread, TextFieldCache textFieldCache, TotalPacketsNumber totalPacketsNumber, ThreadCache threadCache) {
		this.drawCoordinateAxisThread = drawCoordinateAxisThread;
		this.drawBrokenLineThread  = drawBrokenLineThread;
		this.textFieldCache = textFieldCache;
		this.totalPacketsNumber = totalPacketsNumber;
		this.threadCache = threadCache;
	}
	public void actionPerformed(ActionEvent e) {
		//同步锁
		String lock = new String("lock");
		String portOneStr = textFieldCache.portOneText.getText();
		String portTwoStr = textFieldCache.portTwoText.getText();
		String portThreeStr = textFieldCache.portThreeText.getText();
		if(!portOneStr.equals("") && !portTwoStr.equals("") && !portThreeStr.equals("")) {
			int portOne = Integer.parseInt(portOneStr);
			int portTwo = Integer.parseInt(portTwoStr);
			int portThree = Integer.parseInt(portThreeStr);
			drawCoordinateAxisThread.setFlagStart();
			drawBrokenLineThread.setFlagStart();
			if(threadCache.receiverThreadOne == null) {
				threadCache.receiverThreadOne = new ReceiverThread(portOne, totalPacketsNumber, lock);
				threadCache.receiverThreadOne.start();
			}
			if(threadCache.receiverThreadTwo == null) {
				threadCache.receiverThreadTwo = new ReceiverThread(portTwo, totalPacketsNumber, lock);
				threadCache.receiverThreadTwo.start();
			}
			if(threadCache.receiverThreadThree == null) {
				threadCache.receiverThreadThree = new ReceiverThread(portThree, totalPacketsNumber, lock);
				threadCache.receiverThreadThree.start();
			}
			threadCache.receiverThreadOne.setFlagStart();
			threadCache.receiverThreadTwo.setFlagStart();
			threadCache.receiverThreadThree.setFlagStart();
		}
	} 
}

/*
 * 停止按钮事件的类
 * @param DrawCoordinateAxisThread drawCoordinateAxisThread
 * @param DrawBrokenLineThread drawBrokenLineThread
 * @param ThreadCache threadCache
 */
class StopActionListener implements ActionListener {
	DrawCoordinateAxisThread drawCoordinateAxisThread;
	DrawBrokenLineThread drawBrokenLineThread;
	ThreadCache threadCache;
	
	public StopActionListener(DrawCoordinateAxisThread drawCoordinateAxisThread, DrawBrokenLineThread drawBrokenLineThread, ThreadCache threadCache) {
		this.drawCoordinateAxisThread = drawCoordinateAxisThread;
		this.drawBrokenLineThread  = drawBrokenLineThread;
		this.threadCache = threadCache;
	}
	
	public void actionPerformed(ActionEvent e) {
		drawCoordinateAxisThread.setFlagStop();
		drawBrokenLineThread.setFlagStop();
		threadCache.receiverThreadOne.setFlagStop();
		threadCache.receiverThreadTwo.setFlagStop();
		threadCache.receiverThreadThree.setFlagStop();
	}
}

/*
 * 保存接收到的数据包所有个数的类，分别保存flow1、flow2、flow3的数据包总数
 * @param int totalPacketsFlowIdOne 表示flow1的数据包总数
 * @param int totalPacketsFlowIdTwo  表示flow2的数据包总数
 * @param int totalPacketsFlowIdThree  表示flow3的数据包总数
 */
class TotalPacketsNumber {
	public int totalPacketsFlowIdOne;
	public int totalPacketsFlowIdTwo;
	public int totalPacketsFlowIdThree;
	
	public TotalPacketsNumber(int totalPacketsFlowIdOne, int totalPacketsFlowIdTwo, int totalPacketsFlowIdThree) {
		this.totalPacketsFlowIdOne = totalPacketsFlowIdOne;
		this.totalPacketsFlowIdTwo = totalPacketsFlowIdTwo;
		this.totalPacketsFlowIdThree = totalPacketsFlowIdThree;
	}
}

/*
 * 数据包接收线程
 * @param int receiverPort 表示接收数据包的端口号
 * @param TotalPacketsNumber totalPacketsNumber
 * @param  String lock 表示同步锁
 */
class ReceiverThread extends Thread {
	public static final int MAX_BUFSIZE = 1024;
	public static final int LEN_DATA_IN_HEADER = 4;
	public static final int LEN_HEADER = 24;
	private int receiverPort = 0; 
	TotalPacketsNumber totalPacketsNumber;
	String lock;
	//是否接收数据包标识符
	boolean isStart = false;
	
	public ReceiverThread(int receiverPort, TotalPacketsNumber totalPacketsNumber, String lock) {
		this.receiverPort = receiverPort;
		this.totalPacketsNumber = totalPacketsNumber;
		this.lock = lock;
	}
	
	public void setFlagStart() {
		this.isStart = true;
	}
	
	public void setFlagStop() {
		this.isStart = false;
	}
	
	public void receivePackets() throws Exception {
		byte[] buf = new byte[MAX_BUFSIZE];
		
		DatagramSocket ds = new DatagramSocket(receiverPort);
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		PortNumber portNumber = new PortNumber();
		for(;;) {
			if(!isStart) {
				Thread.sleep(500);
				continue;
			}
			ds.receive(dp);
			buf = dp.getData();
			byte[] data = dp.getData();
			byte[] srcAddrByte = new byte[LEN_DATA_IN_HEADER];
			byte[] destAddrByte = new byte[LEN_DATA_IN_HEADER];
			byte[] srcPortByte = new byte[LEN_DATA_IN_HEADER];
			byte[] destPortByte = new byte[LEN_DATA_IN_HEADER];
			byte[] weightByte = new byte[LEN_DATA_IN_HEADER];
			byte[] flowIdByte = new byte[LEN_DATA_IN_HEADER];
			for(int i = 0; i < LEN_DATA_IN_HEADER; i++) {
				srcAddrByte[i] = data[i];
			}
			for(int i = LEN_DATA_IN_HEADER; i < LEN_DATA_IN_HEADER * 2; i++) {
				destAddrByte[i - LEN_DATA_IN_HEADER] = data[i];
			}
			for(int i = LEN_DATA_IN_HEADER * 2; i < LEN_DATA_IN_HEADER * 3; i++) {
				srcPortByte[i - LEN_DATA_IN_HEADER * 2] = data[i];
			}
			for(int i = LEN_DATA_IN_HEADER * 3; i < LEN_DATA_IN_HEADER * 4; i++) {
				destPortByte[i - LEN_DATA_IN_HEADER * 3] = data[i];
			}
			for(int i = LEN_DATA_IN_HEADER * 4; i < LEN_DATA_IN_HEADER * 5; i++) {
				weightByte[i - LEN_DATA_IN_HEADER * 4] = data[i];
			}
			for(int i = LEN_DATA_IN_HEADER * 5; i < LEN_DATA_IN_HEADER * 6; i++) {
				flowIdByte[i - LEN_DATA_IN_HEADER * 5] = data[i];
			}
			String srcAddr = InetAddress.getByAddress(srcAddrByte).toString();
			String destAddr = InetAddress.getByAddress(destAddrByte).toString();
			int srcPort = portNumber.byte2int(srcPortByte);
			int destPort = portNumber.byte2int(destPortByte);
			int weight = portNumber.byte2int(weightByte);
			int flowId = portNumber.byte2int(flowIdByte);
			
			//线程同步区
			synchronized (lock) {
				switch(flowId) {
					case 1:
						totalPacketsNumber.totalPacketsFlowIdOne++;
						break;
					case 2:
						totalPacketsNumber.totalPacketsFlowIdTwo++;
						break;
					case 3:
						totalPacketsNumber.totalPacketsFlowIdThree++;
						break;
					default:
						break;
				}
			}
			System.out.println("data：" + new String(buf, LEN_HEADER, buf.length - LEN_HEADER));
			System.out.println("source ip：" + srcAddr);
			System.out.println("destination ip：" + destAddr);
			System.out.println("source port：" + srcPort);
			System.out.println("destination port：" + destPort);
			System.out.println("weight：" + weight);
			System.out.println("flow id：" + flowId);
			System.out.println("------------------");
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

/*
 * 画坐标轴的线程类
 * @param Panel axis 表示画坐标轴的panel
 * @param TotalPacketsNumber totalPacketsNumber
 */
class DrawCoordinateAxisThread extends Thread {
	static final int MAX_AXISX = 750; 
	static final int MAX_AXISY = 510; 
	static final int MIN_AXISX = 50;
	static final int MIN_AXISY = 50;
	static final int INTERVAL_X = 30;
	static final int INTERVAL_Y = 15;
	//y轴每个间距的packets差
	static final int DIFF_INTERVAL_Y = 10;
	//x轴每个间距的时间差单位(s)
	static final float DIFF_INTERVAL_X = (float) 0.5;
	static final String TOTAL_PACKETS = "total packets";
	static final String TIME = "s";
	Panel axis;
	//启动后的总时间,单位（s）
	float totalTime = 0;
	//x轴标出的最大时间值
	float maxTimeX = 0;
	//停止时x轴坐标的初始值
	float stopValueX = 0;
	//表示是否已经启动接收数据包线程
	boolean isStart = false;
	TotalPacketsNumber totalPacketsNumber;
	
	public DrawCoordinateAxisThread(Panel axis, TotalPacketsNumber totalPacketsNumber) {
		this.axis = axis;
		this.totalPacketsNumber = totalPacketsNumber;
	}
	//表示开始接收数据包
	public void setFlagStart() {
		this.isStart = true;
		this.totalTime = 0;
	}
	//表示停止接收数据包
	public void setFlagStop() {
		this.isStart =false;
	}
	
	public void run() {
		Color red = new Color(255, 0, 0);
		Color green = new Color(0, 255, 0);
		Color blue = new Color(0, 0, 255);
		Color black = new Color(0, 0, 0);
		for(;;) {
			float valueX = 0;
			int valueY = 0;
			Graphics graph = axis.getGraphics();
			graph.drawLine(MIN_AXISX - 5, MIN_AXISY + 5, MIN_AXISX, MIN_AXISY); //Y轴箭头
			graph.drawLine(MIN_AXISX + 5, MIN_AXISY + 5, MIN_AXISX, MIN_AXISY); //Y轴箭头
			graph.drawLine(MIN_AXISX, MIN_AXISY, MIN_AXISX, MAX_AXISY);//Y轴
			graph.drawLine(MIN_AXISX, MAX_AXISY, MAX_AXISX, MAX_AXISY); //X轴
			graph.drawLine(MAX_AXISX - 5, MAX_AXISY - 5, MAX_AXISX, MAX_AXISY); //X轴箭头
			graph.drawLine(MAX_AXISX - 5, MAX_AXISY + 5, MAX_AXISX, MAX_AXISY); //X轴箭头
			graph.drawString(TOTAL_PACKETS, MIN_AXISX - 30, MIN_AXISY - 20);//Y轴名称
			graph.drawString(TIME, MAX_AXISX + 20, MAX_AXISY + 15);
			//Y轴坐标分段
			for(int i = 0; i < MAX_AXISY - MIN_AXISY; i += INTERVAL_Y) {
				valueY += DIFF_INTERVAL_Y;
				String strValueY = valueY + "";
				graph.drawLine(MIN_AXISX, MIN_AXISY + i, MIN_AXISX + 5, MIN_AXISY + i);
				//y轴坐标数值
				graph.drawString(strValueY, MIN_AXISX - 30, MAX_AXISY - 5 - i);
			}
			//清空画坐标值的区域
			graph.clearRect(MIN_AXISX, MAX_AXISY + 5, 720, 50);
			//当启动后的总时间大于x轴标出的最大时间值时，令初始的x轴坐标值为两者之差
			if(totalTime > maxTimeX && isStart) {
				valueX += (totalTime - maxTimeX);
				stopValueX = valueX;
			} else if(totalTime > maxTimeX) {
				valueX = stopValueX;
			}
			//X轴坐标分段
			for(int i = 0; i < MAX_AXISX - MIN_AXISX; i += INTERVAL_X) {
				String strValueX;
				valueX += DIFF_INTERVAL_X;
				if(i == 0) {
					valueX -= 0.5;
					strValueX = "0";
				} else
					strValueX = valueX + "";
				graph.drawLine(MIN_AXISX + i, MAX_AXISY - 5, MIN_AXISX + i, MAX_AXISY);
				//x轴坐标数值
				graph.drawString(strValueX, MIN_AXISX - 8 + i, MAX_AXISY + 15);
			}
			maxTimeX = valueX;
			
			graph.setColor(red);
			graph.drawLine(MIN_AXISX, MAX_AXISY + 50, MIN_AXISX + 50, MAX_AXISY + 50);
			graph.setColor(green);
			graph.drawLine(MIN_AXISX, MAX_AXISY + 70, MIN_AXISX + 50, MAX_AXISY + 70);
			graph.setColor(blue);
			graph.drawLine(MIN_AXISX, MAX_AXISY + 90, MIN_AXISX + 50, MAX_AXISY + 90);
			graph.setColor(black);
			graph.drawString("Flow 1:", MIN_AXISX + 60, MAX_AXISY + 55);
			graph.drawString("Flow 2:", MIN_AXISX + 60, MAX_AXISY + 75);
			graph.drawString("Flow 3:", MIN_AXISX + 60, MAX_AXISY + 95);
			
			int totalPacketsFlowIdOne = totalPacketsNumber.totalPacketsFlowIdOne;
			int totalPacketsFlowIdTwo = totalPacketsNumber.totalPacketsFlowIdTwo;
			int totalPacketsFlowIdThree = totalPacketsNumber.totalPacketsFlowIdThree;
			graph.clearRect(MIN_AXISX + 110, MAX_AXISY + 50, 50, 60);
			graph.drawString(totalPacketsFlowIdOne + "", MIN_AXISX + 110, MAX_AXISY + 55);
			graph.drawString(totalPacketsFlowIdTwo + "", MIN_AXISX + 110, MAX_AXISY + 75);
			graph.drawString(totalPacketsFlowIdThree + "", MIN_AXISX + 110, MAX_AXISY + 95);
			
			
			try{
				int sleepTime = (int)(DIFF_INTERVAL_X * 1000);
				Thread.sleep(sleepTime);
				totalTime += (float) sleepTime / 1000;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}

/*
 * 画折线类，该类描绘的是数据包总个数关于时间的折线
 * @param Panel p 表示画折线的panel
 * @param TotalPacketsNumber totalPacketsNumber 表示存放数据包总个数的对象
 */
class DrawBrokenLineThread extends Thread {
	static final int MAX_AXISX = 750; 
	static final int INTERVAL_X = 30;
	//y轴每个间隔之间的距离
	static final int INTERVAL_Y = 15;
	//y轴每个间距的packets差
	static final int DIFF_INTERVAL_Y = 10;
	static final int SLEEP_TIME = 500;
	//x轴每个间距的时间单位(ms)
	static final int TIME_INTERVAL_X = 500;
	//y轴的高度
	static final int PANEL_HEIGHT = 455;
	//x轴的长度
	static final int PANEL_WIDTH = 700;
	//表示是否已经启动接收数据包线程
	boolean isStart = false;
	Panel p;
	TotalPacketsNumber totalPacketsNumber;
	List<Point> listFlowIdOne;
	List<Point> listFlowIdTwo;
	List<Point> listFlowIdThree;
	Graphics g;
	
	public DrawBrokenLineThread(Panel p, TotalPacketsNumber totalPacketsNumber) {
		this.p = p;
		this.totalPacketsNumber = totalPacketsNumber;
		g = p.getGraphics();
	}
	
	//表示开始接收数据包
	public void setFlagStart() {
		this.isStart = true;
		listFlowIdOne.clear();
		listFlowIdTwo.clear();
		listFlowIdThree.clear();
		totalPacketsNumber.totalPacketsFlowIdOne = 0;
		totalPacketsNumber.totalPacketsFlowIdTwo = 0;
		totalPacketsNumber.totalPacketsFlowIdThree = 0;
		g.clearRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
	}
	//表示停止接收数据包
	public void setFlagStop() {
		this.isStart = false;
	}
	
	public void run(){
		listFlowIdOne = new ArrayList<Point>();
		listFlowIdTwo = new ArrayList<Point>();
		listFlowIdThree = new ArrayList<Point>();
		Color red = new Color(255, 0, 0);
		Color green = new Color(0, 255, 0);
		Color blue = new Color(0, 0, 255);
		int x = 0;
		//flow1的y轴坐标值
		float yFlowIdOne = 0;
		//flow2的y轴坐标值
		float yFlowIdTwo = 0;
		//flow3的y轴坐标值
		float yFlowIdThree = 0;
		//y轴每个间隔的实际间距与标出的packets差的倍数关系
		float multiple = (float) INTERVAL_Y / DIFF_INTERVAL_Y;
		//调教误差
		int error = 8;
		for(;;) {
			if(isStart) {
				//当折线的x轴坐标值超出范围时，令折线往前移动
				if(x > PANEL_WIDTH) {
					for(int i = 0; i < listFlowIdOne.size(); i++) {
						listFlowIdOne.get(i).x = listFlowIdOne.get(i).x - INTERVAL_X * SLEEP_TIME / TIME_INTERVAL_X;
					}
					for(int i = 0; i < listFlowIdTwo.size(); i++) {
						listFlowIdTwo.get(i).x = listFlowIdTwo.get(i).x - INTERVAL_X * SLEEP_TIME / TIME_INTERVAL_X;
					}
					for(int i = 0; i < listFlowIdThree.size(); i++) {
						listFlowIdThree.get(i).x = listFlowIdThree.get(i).x - INTERVAL_X * SLEEP_TIME / TIME_INTERVAL_X;
					}
					x -= INTERVAL_X * SLEEP_TIME / TIME_INTERVAL_X;
					listFlowIdOne.remove(0);
					listFlowIdTwo.remove(0);
					listFlowIdThree.remove(0);
					g.clearRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
				}
				//每次画折线坐标x轴增加的距离
				x += INTERVAL_X * SLEEP_TIME / TIME_INTERVAL_X;
				yFlowIdOne = (float) (PANEL_HEIGHT - totalPacketsNumber.totalPacketsFlowIdOne * multiple) + error;
				yFlowIdTwo = (float) (PANEL_HEIGHT - totalPacketsNumber.totalPacketsFlowIdTwo * multiple) + error;
				yFlowIdThree = (float) (PANEL_HEIGHT - totalPacketsNumber.totalPacketsFlowIdThree * multiple) + error;
				Point pointFlowIdOne = new Point(x, (int)yFlowIdOne);
				Point pointFlowIdTwo = new Point(x, (int)yFlowIdTwo);
				Point pointFlowIdThree = new Point(x, (int)yFlowIdThree);
				listFlowIdOne.add(pointFlowIdOne);
				listFlowIdTwo.add(pointFlowIdTwo);
				listFlowIdThree.add(pointFlowIdThree);
			} else {
				//x轴坐标初始化
				x = 0;
			}
			//画flow1总数据包个数的折线图
			for(int i = 0; i < listFlowIdOne.size() - 1; i++) {
				Point point01 = listFlowIdOne.get(i);
				Point point02 = listFlowIdOne.get(i+1);
				int x1 = (int) point01.getX();
				int y1 = (int) point01.getY();
				int x2 = (int) point02.getX();
				int y2 = (int) point02.getY();
				g.setColor(red);
				g.drawLine(x1, y1, x2, y2);
			}
			//画flow2总数据包个数的折线图
			for(int i = 0; i < listFlowIdTwo.size() - 1; i++) {
				Point point01 = listFlowIdTwo.get(i);
				Point point02 = listFlowIdTwo.get(i+1);
				int x1 = (int) point01.getX();
				int y1 = (int) point01.getY();
				int x2 = (int) point02.getX();
				int y2 = (int) point02.getY();
				g.setColor(green);
				g.drawLine(x1, y1, x2, y2);
			}
			//画flow3总数据包个数的折线图
			for(int i = 0; i < listFlowIdThree.size() - 1; i++) {
				Point point01 = listFlowIdThree.get(i);
				Point point02 = listFlowIdThree.get(i+1);
				int x1 = (int) point01.getX();
				int y1 = (int) point01.getY();
				int x2 = (int) point02.getX();
				int y2 = (int) point02.getY();
				g.setColor(blue);
				g.drawLine(x1, y1, x2, y2);
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
