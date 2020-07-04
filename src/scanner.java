import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class scanner {
	static FileOutputStream out;
	
	public static void filePrint(String towrite) throws IOException {
		out.write(towrite.getBytes());
		return;
	}
	
	public static boolean checkKeyword(ArrayList <String> keyword, String word) {
		boolean result = false;
		for(String key : keyword) {
			if(word.equals(key)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public static boolean checkOperator(ArrayList <String> keyword, String word) {
		boolean result = false;
		for(String key : keyword) {
			if(word.equals(key)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public static void printToken(String word, String type) throws IOException {
		filePrint(word+"|"+type + "\n");
	}
	
	public static void runScanner(String [] args) throws IOException {
		
		out = new FileOutputStream("scannerOut.txt");
		
		if(args.length == 0)
	    {
	        filePrint("Please enter file name e.g. java scanner test.txt");
	        System.exit(0);
	    }
		ArrayList<String> keyword = new ArrayList<String>();
		ArrayList<String> operator = new ArrayList<String>();
		keyword.add("class");
		keyword.add("main");
		keyword.add("int");
		keyword.add("if");
		keyword.add("else");
		keyword.add("while");
		keyword.add("for");
		keyword.add("else if");
		keyword.add("out.println");
		
		operator.add("+");
		operator.add("-");
		operator.add("/");
		operator.add("*");
		operator.add("++");
		operator.add("--");
		
		
		
		String fileName = args[0];
		Scanner inputStream = null;
		
		try {
			inputStream = new Scanner(new File(fileName));
		}
		catch(FileNotFoundException e) {
			System.out.println("File " + fileName + " does not exist");
			System.exit(0);
		}
		boolean terminate = false;
		while(inputStream.hasNextLine()) {
			String type = "none";
			String line = inputStream.nextLine();
			
			int state = 0;
			int startSub = 0;
			for(int i=0; i<line.length(); i++){
				char cur = line.charAt(i);
				
				if(state==0){ // start state
					if(cur == '/') {
						state = 1;
					}else if(cur == '{') {
						type = "left curly brace";
						printToken("{", type);
						type = "none";
						startSub = i+1;
					}else if(cur == '(') {
						type = "left parenthesis";
						printToken("(", type);
						type = "none";
						startSub = i+1;
					}else if(cur == '}') {
						type = "right curly brace";
						printToken("}", type);
						type = "none";
						startSub = i+1;
					}else if(cur == ')') {
						type = "right parenthesis";
						printToken(")", type);
						type = "none";
						startSub = i+1;
					}else if(cur == ';') {
						type = "statement terminator";
						printToken(";", type);
						type = "none";
						startSub = i+1;
					}else if(cur=='_'){
						state = 2;
					}else if(cur=='$'||Character.isAlphabetic(cur)) {
						state = 3;
						type = "identifier";
					}else if(cur=='*'||cur=='/'||cur=='%') {
						type = "operator symbol";
						printToken(Character.toString(cur), type);
						type = "none";
						startSub = i+1;
					}else if(cur=='+') {
						type = "operator symbol";
						state = 7;
					}else if(cur=='-') {
						type = "operator symbol";
						state = 8;
					}else if(cur=='<') {
						type = "operator symbol";
						state = 9;
					}else if(cur=='>') {
						type = "operator symbol";
						state = 10;
					}else if(cur=='=') {
						type = "operator symbol";
						state = 11;
					}else if(Character.isDigit(cur)) {
						state = 4;
						type = "number literal";
					}else if(cur=='"') {
						state = 5;
						type = "string literal";
						printToken("\"", "double quote symbol");
						startSub = i+1;
					}else if(cur==' '||cur=='\t') {
						startSub = i+1;
						continue;
					}
				}else if(state==1){ // one
					if(cur == '/') {
						state = 6;
						type = "comment";
						startSub = i-1;
					}
				}else if(state==2) { //id q_
					if(cur=='$'||cur=='_'||Character.isAlphabetic(cur)||Character.isDigit(cur)) {
						state = 3;
						type = "identifier";
					}else if(cur==' '||i==line.length()-1) {
						state = 0;
						type = "illegal";
					}
				}else if(state==3) { //id final
					if(cur=='e') {
						String sub = line.substring(startSub, i+1);
						if(sub.equals("else")) {
							state=12;
						}
					}else if(cur=='$'||cur=='_'||Character.isAlphabetic(cur)||Character.isDigit(cur)) {
						state = 3;
					}else if(cur == ' ') {
						String sub = line.substring(startSub, i);
						if(checkKeyword(keyword, sub)) {
							type = "keyword";
						}else {
							keyword.add(sub);
						}
						filePrint(sub+"|"+type+"\n");
						state = 0;
						startSub = i+1;
						type = "none";
					}else if(cur == '(') {
						String sub = line.substring(startSub, i);
						if(checkKeyword(keyword, sub)) {
							type = "keyword";
						}else {
							keyword.add(sub);
						}
						filePrint(sub+"|"+type+"\n");
						state = 0;
						startSub = i+1;
						type = "left parenthesis";
						filePrint("(|" + type+"\n");
					}else if(cur == ')') {
						String sub = line.substring(startSub, i);
						if(checkKeyword(keyword, sub)) {
							type = "keyword";
						}else {
							keyword.add(sub);
						}
						filePrint(sub+"|"+type+"\n");
						state = 0;
						startSub = i+1;
						type = "right parenthesis";
						filePrint(")|" + type + "\n");
					}else if(cur == '.') {
						String sub = line.substring(startSub, i);
						if(!sub.equals("out")) type = "illegal";
					}else if(cur == '+') {
						String sub = line.substring(startSub, i);
						if(checkKeyword(keyword, sub)) {
							type = "keyword";
						}else {
							keyword.add(sub);
						}
						filePrint(sub+"|"+type+"\n");
						state = 7;
						type = "operator symbol";
					}else if(cur == '-') {
						String sub = line.substring(startSub, i);
						if(checkKeyword(keyword, sub)) {
							type = "keyword";
						}else {
							keyword.add(sub);
						}
						filePrint(sub+"|"+type+"\n");
						state = 8;
						type = "operator symbol";
					}else if(cur=='<') {
						String sub = line.substring(startSub, i);
						if(checkKeyword(keyword, sub)) {
							type = "keyword";
						}else {
							keyword.add(sub);
						}
						filePrint(sub+"|"+type+"\n");
						state = 9;
						type = "operator symbol";
					}else if(cur=='>') {
						String sub = line.substring(startSub, i);
						if(checkKeyword(keyword, sub)) {
							type = "keyword";
						}else {
							keyword.add(sub);
						}
						filePrint(sub+"|"+type+"\n");
						state = 10;
						type = "operator symbol";
					}else if(cur=='=') {
						String sub = line.substring(startSub, i);
						if(checkKeyword(keyword, sub)) {
							type = "keyword";
						}else {
							keyword.add(sub);
						}
						filePrint(sub+"|"+type+"\n");
						state = 11;
						type = "operator symbol";
					}else if(cur==';') {
						String sub = line.substring(startSub, i);
						if(checkKeyword(keyword, sub)) {
							type = "keyword";
						}else {
							keyword.add(sub);
						}
						filePrint(sub+"|"+type+"\n");
						state = 0;
						startSub = i+1;
						type = "state terminator";
					}else {
						type = "illegal";
					}
				}else if(state==4) { //num
					if(cur==')') {
						String sub = line.substring(startSub, i);
						printToken(sub, type);
						type = "right parenthesis";
						printToken(")", type);
						type = "none";
						state = 0;
					}else if(cur==';') {
						String sub = line.substring(startSub, i);
						printToken(sub, type);
						type = "statement terminator";
						printToken(";", type);
						state = 0;
					}else if(!Character.isDigit(cur)) {
						type = "illegal";
					}
				}else if(state==5) { // string literal
					if(cur=='"') {
						String sub = line.substring(startSub, i);
						printToken(sub, type);
						type = "double quote symbol";
						printToken("\"", type);
						state = 0;
					}
				}else if(state==6) { //comment final state
					if(i==line.length()-1) {
						String sub = line.substring(startSub, i+1);
						printToken(sub, type);
						state = 0;
					}
				}else if(state==7) { //+
					if(cur=='+') {
						printToken("++", type);
						state = 0;
						type = "none";
						startSub = i+1;
					}else {
						printToken("+", type);
						state = 0;
						type = "none";
						i-=1;
					}
				}else if(state==8) { //-
					if(cur=='-') {
						printToken("--", type);
						state = 0;
						type = "none";
						startSub = i+1;
					}else {
						printToken("-", type);
						state = 0;
						type = "none";
						i-=1;
					}
				}else if(state==9) { //<
					if(cur=='=') {
						printToken("<=", type);
						state = 0;
						type = "none";
						startSub = i+1;
					}else {
						printToken("<", type);
						state = 0;
						type = "none";
						i-=1;
					}
				}else if(state==10){ //>
					if(cur=='=') {
						printToken(">=", type);
						state = 0;
						type = "none";
						startSub = i+1;
					}else {
						printToken(">", type);
						state = 0;
						type = "none";
						i-=1;
					}
				}else if(state==11) { // = 
					if(cur=='=') {
						printToken("==", type);
						state = 0;
						type = "none";
						startSub = i+1;
					}else {
						printToken("=", type);
						state = 0;
						type = "none";
						i-=1;
					}
				}else if(state==12) { //else
					if(cur==' ') {
						state=13;
					}else {
						state=3;
					}
				}else if(state==13) { //else space
					if(cur=='i') {
						state=14;
					}else {
						printToken("else", "keyword");
						startSub = i;
						state = 0;
						type = "none";
						i-=1;
					}
				}else if(state==14) { //else i
					if(cur=='f') {
						printToken("else if", "keyword");
						state = 0;
						startSub = i+1;
					}else {
						printToken("else", "keyword");
						startSub = i-1;
						state = 0;
						type = "none";
						i-=2;
					}
				}
				if(type == "illegal") {
					int endSub = i;
					for(int j=i+1; j<line.length(); j++) {
						char end = line.charAt(j);
						if(end==')'||end==';'||end==' '||end=='{'||end==' ') {
							endSub = j;
							break;
						}
					}
					String sub = line.substring(startSub, endSub);
					printToken(sub, type);
					filePrint("Stop Scanning");
					//inputStream.close();
					terminate = true;
					break;
				}
			}
			if(terminate) break;
		}
		inputStream.close();
		out.close();
	}
}
