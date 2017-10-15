package lab1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
//git test1
//git in B2
public class Main {
	int num = 0;
	int DirectedGraph[][];
	Map<String, Integer> StringHash = new HashMap<String, Integer>();
	Map<Integer, String> IntHash = new HashMap<Integer, String>();
	
	public static void main(String[] args) throws FileNotFoundException {
		Main myGra = new Main();
		MyDraw md = new MyDraw();
		String word1,word2;
		int randPath[];
		String[] ansStrings;
		
		System.out.println("####welcome####\nplease input path:");
		Scanner in = new Scanner(System.in);
		String pathString = in.nextLine();
		String text = ReadText(pathString);
		System.out.println(text);
		myGra.CreateDirectedGraph(text);
		md.showDirectedGraph(myGra);

		System.out.println("jpg has been created at path'F:/first.jpg'");
		System.out.println("功能3：桥接词");
		myGra.num--;
		System.out.println("input word1 and word2");
		word1 = in.nextLine();
		word2 = in.nextLine();
		ansStrings = myGra.queryBridgeWords(word1, word2);

		if(ansStrings==null){
			if(myGra.StringHash.get(word1)==null&&myGra.StringHash.get(word2)==null){
				System.out.println("No  "+word1+" and "+word2+" in the graph!");
			}
			else if(myGra.StringHash.get(word1)==null){
				System.out.println("No "+word1+" in the graph!");
			}
			else if(myGra.StringHash.get(word2)==null){
				System.out.println("No "+word2+" in the graph!");
			}
		}else if(ansStrings[0]==null)
			System.out.println("No bridge words from "+word1+" to "+word2);
		else{
			System.out.println("The bridge words from "+word1+" to "+word2+" is:");
			for(int i=0;i<ansStrings.length;i++){
				if(ansStrings[i]!=null)
					System.out.printf("%s ",ansStrings[i]);
				}
			System.out.println("");
		}
		System.out.println("功能4：桥接词填充");
		System.out.println("input new text");
		String newtext = in.nextLine();
		myGra.generateNewText(newtext);
		

		System.out.println("\n功能5：最短路径");
		myGra.num++;
		System.out.println("input word1");
		word1 = in.next();
		System.out.println("input word2");
		word2 = in.next();
		md.calcShortestPath(word1, word2, myGra);
		

		System.out.println("\n功能6：随机游走");
		randPath = myGra.randomWalk();
		md.showRandPath(randPath,myGra);
	}

	static String ReadText(String path) {
		StringBuilder text = new StringBuilder("");
		File file = new File(path);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				text.append(' ');
				text.append(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return text.toString();

	}

	void CreateDirectedGraph(String text) {
		String dic[] = text.replaceAll("[\\u4e00-\\u9fa5]", "").split("\\W+");
		for (int i = 0; i < dic.length; i++) {
			dic[i].toLowerCase();
		}
		for (int i = 0; i < dic.length; i++) {
			if (dic[i] == "")
				continue;
			else {
				if (StringHash.get(dic[i].toLowerCase()) == null) {
					StringHash.put(dic[i].toLowerCase(), num);
					IntHash.put(num, dic[i].toLowerCase());
					num++;
				}
			}
		}
		DirectedGraph = new int[num][num];
		int start = 1;
		for (int i = 2; i < dic.length; i++) {
			int end = StringHash.get(dic[i].toLowerCase());
			DirectedGraph[start][end]++;
			start = end;
		}
	}
	int[] randomWalk() {
		boolean visit[][] = new boolean[num][num];
		int ret[] = new int[1000];
		int rstep = 0,j = 0;
		ret[rstep] = (int)(Math.random()*(num-1)); 
		System.out.println();
		while(true) {
			for(int i = 0;i < num;i++) {
				if(this.DirectedGraph[ret[rstep]][i] != 0) {
					j++;
				}
			}
			if(j == 0) {
				ret[1+rstep] = -1;
				return ret;
			}
			int o = ret[rstep];
			int n;
			do{
				n = (int)(Math.random()*(num-1));
			}while(this.DirectedGraph[o][n] == 0);
			ret[++rstep] = n;
			if(!visit[o][n]) {
				visit[o][n] = true;
			}else {
				ret[1+rstep] = -1;
				return ret;
			}
		}
	}
	String[] queryBridgeWords(String word1, String word2){
		String[] ansStrings =new String[num+1];

		int index=0;
		if(StringHash.get(word1)==null||StringHash.get(word2)==null){
			return null;
		}

		int word1_num = StringHash.get(word1);
		int word2_num = StringHash.get(word2);
		for(int i=0;i<=num;i++){
			if(DirectedGraph[word1_num][i]!=0&&DirectedGraph[i][word2_num]!=0)
				ansStrings[index++]=IntHash.get(i);
		}
		return ansStrings;
		
	}
	String generateNewText(String inputText){
		String dic[] = inputText.split("\\W+");
		List<String> changeList = Arrays.asList(dic);
		int index=1;
		ArrayList<String> changeList2 = new ArrayList<String>(changeList);
		for(int i=0;i<dic.length-1;i++){
			String[] ansStrings=queryBridgeWords(dic[i].toLowerCase(), dic[i+1].toLowerCase());
			if(ansStrings==null||ansStrings[0]==null)
				continue;
			else{
				changeList2.add(i+index, ansStrings[0]);
				index++;
			}
		}
		for(String str:changeList2)
			System.out.print(str+" ");
		return inputText;
	}
}
