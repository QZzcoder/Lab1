package lab1;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.ImageIcon;
//git test2
//git in B2
public class MyDraw {
	int loc[][] = new int[1000][2];//各个圆心坐标
	BufferedImage image;
	int radiu;//排列半径
	int huiqiu = 100;
	void init(int num) {
		double pi = Math.PI;
		radiu = (int)(150/Math.sin(pi/num));
		image = new BufferedImage(2*radiu+huiqiu*2, 2*radiu+huiqiu*2, BufferedImage.TYPE_INT_RGB);
		for(int i = 0;i < num;i++) {
			loc[i][0] = radiu+huiqiu+(int)(radiu*Math.sin(2*i*pi/num));
			loc[i][1] = radiu+huiqiu+(int)(radiu*Math.cos(2*i*pi/num));
		}
	}
	
	void drawWord(int num,Map<Integer, String> IntHash) {
		Graphics graphics = image.getGraphics();
		for(int i = 1;i <num;i++) {
			graphics.setColor(Color.gray);
			graphics.fillOval(loc[i][0]-huiqiu,loc[i][1]-huiqiu,huiqiu*2,2*huiqiu);
			graphics.setColor(Color.yellow);
			Font f = new Font("宋体",Font.BOLD ,60);
			graphics.setFont(f);
			graphics.drawString(IntHash.get(i), loc[i][0]-huiqiu, loc[i][1]);
		}
		graphics.dispose();
	}
	
	void drawArrow(int x1,int y1,int x2,int y2,int w,Color ac) {
		double b = huiqiu/Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		int ax = (int)(x1*b+(1-b)*x2);
		int ay = (int)(y1*b+(1-b)*y2);
		Graphics graphics = image.getGraphics();
		Graphics2D g2d = (Graphics2D)graphics;
		BasicStroke bs=new BasicStroke(5);//width是线宽,float型
		g2d.setStroke(bs);
		
		graphics.setColor(Color.RED);
		graphics.fillOval(ax-15,ay-15,30 , 30);
		graphics.setColor(ac);
		g2d.drawLine(x1, y1, x2, y2);
		Font f = new Font("宋体",Font.BOLD ,60);
		graphics.setFont(f);
		graphics.drawString(""+w,(x1+x2)/2,(y1+y2)/2);
		graphics.dispose();
	}
	void createImage(String fileLocation) {
		try {
			FileOutputStream fos = new FileOutputStream(fileLocation);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
			encoder.encode(image);
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	void showDirectedGraph(Main myGra) {
		init(myGra.num);
		for(int i = 0;i < myGra.num;i++) {
			for(int j = 0;j < myGra.num;j++) {
				int w = myGra.DirectedGraph[i][j];
				System.out.print(myGra.DirectedGraph[i][j]);
				System.out.print(" ");
				if(w != 0) {
					drawArrow(loc[i][0], loc[i][1], loc[j][0], loc[j][1], w,Color.white);
				}
			}
			System.out.println();
		}
		drawWord(myGra.num,myGra.IntHash);
		createImage("F:\\first.jpg");
	}
	void calcShortestPath(String word1, String word2,Main myGra) {
		int path[] = new int[1000];
		int ret[] = new int[1000];
		int que[] = new int[1000];
		int in = 0,out = 0;
		int first = myGra.StringHash.get(word1);
		int end = myGra.StringHash.get(word2);
		que[in++] = first;
		path[first] = -1;
		int length[] = new int[1000];
		for(int i = 0; i< 1000;i++) {
			length[i] = 999;
		}
		length[first] = 0;
		while(in != out) {
			int n = que[out++];
			for(int i = 0;i < myGra.num;i++) {
				if(myGra.DirectedGraph[n][i] != 0){
					if(length[i] > (length[n]+myGra.DirectedGraph[n][i])) {
						length[i] = (length[n]+myGra.DirectedGraph[n][i]);
						path[i] = n;
						que[in++] = i;
					}
				}
			}
		}
		if(length[end] >= 999) {
			System.out.println("not find path:"+word1+" and "+word2);
			return;
		}
		int k = 0,end1 = end;
		do {
			ret[k] = end1;
			end1 = path[end1];
			k++;
		}while(end1 != -1);
		System.out.println("length is "+k);
		for(int i = 0;i < k;i++) {
			if(i == k-1) {
				System.out.print(myGra.IntHash.get(ret[i]));
			}else {
				System.out.print(myGra.IntHash.get(ret[i])+"<-");
			}
		}
		init(myGra.num);
		for(int i = 0;i < myGra.num;i++) {
			for(int j = 0;j < myGra.num;j++) {
				int w = myGra.DirectedGraph[i][j];
				if(w != 0) {
					drawArrow(loc[i][0], loc[i][1], loc[j][0], loc[j][1], w,Color.white);
				}
			}
		}
		for(int i = 0;i < k-1;i++) {
			int w1 = ret[i+1];
			int w2 = ret[i];
			int w = myGra.DirectedGraph[w1][w2];
			drawArrow(loc[w1][0], loc[w1][1], loc[w2][0], loc[w2][1], w,Color.red);
		}
		drawWord(myGra.num,myGra.IntHash);
		createImage("F:\\second.jpg");
	}
	void showRandPath(int path[],Main myGra) throws FileNotFoundException {
		FileOutputStream fs = new FileOutputStream(new File("F:\\last.txt"));
		PrintStream p = new PrintStream(fs);
		int i = 0;
		int j;
		do{
			p.print(myGra.IntHash.get(path[i])+" ");
			System.out.println(myGra.IntHash.get(path[i]));
			i++;
			System.out.println("1 to continue, else to end");
			Scanner in = new Scanner(System.in);
			j = in.nextInt();
		}while(myGra.IntHash.get(path[i]) != null && j == 1);
		p.close();
	}
}
