import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class parser {	
	
	static Stack<String> stack = new Stack<String>();
	static ArrayList<String> wordlist = new ArrayList<String>();
	static ArrayList<String> typelist = new ArrayList<String>();
	
	static int tokenidx = 0, typeidx = 0;
	static boolean success = true;
	
	public static void treatError() {
		success = false;
		System.out.println(wordlist.get(tokenidx));
		System.out.println("Parsing Error");
	}
	
	public static void match(String token) {
		System.out.println(token);
		stack.pop();
		tokenidx += 1;
		typeidx += 1;
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Parser Running...");
		
		scanner scan = new scanner();
		scanner.runScanner(args);
		try {
			Scanner input = new Scanner(new File("scannerOut.txt"));
			String line;
			while(input.hasNextLine()) {
				line = input.nextLine();
				//System.out.println(line);
				String [] ary = line.split("\\|");
				String word = ary[0];
				boolean relevant = false;
				if(ary.length>1) {
					relevant = true;
					String type = ary[1];
					//System.out.println("word " + word);
					//System.out.println("type " + type);
					wordlist.add(word);
					typelist.add(type);
				}else {
					wordlist.add(word);
					typelist.add("error");
				}
				
			}
		}
		catch(FileNotFoundException e) { System.out.println("Error1\n"); }
		
		/*for(int i=0; i<wordlist.size(); i++) {
			System.out.print("WORD " + wordlist.get(i));
			System.out.println("\tTYPE " + typelist.get(i));
		}*/
		
		wordlist.add("$");
		typelist.add("end");
		
		stack.push("$");
		stack.push("program");
		
		
		String top = stack.peek();
		String token = wordlist.get(tokenidx);
		String tokentype = typelist.get(typeidx);
		while((!top.equals("$"))&&(!tokentype.equals("end"))) {
			/*(!top.equals("$"))&&(!tokentype.equals("end"))*/
			top = stack.peek();
			token = wordlist.get(tokenidx);
			tokentype = typelist.get(typeidx);
			//System.out.println("TOP " + top + " " + tokenidx + " TOKEN " + token + " " + typeidx + " TYPE " + tokentype);
			if(top.equals("program")) { //NT
				if(token.equals("class")) {
					stack.pop();
					stack.push("}");
					stack.push("content");
					stack.push("cmt");
					stack.push("{");
					stack.push("variable");
					stack.push("class");
				}else {
					treatError(); break;
				}
			}else if(top.equals("variable")) {
				if(tokentype.equals("keyword") || tokentype.equals("identifier")) {
					match(token);
				}else {
					treatError(); break;
				}
			}else if(top.equals("content")) {
				if(token.equals("main")) {
					stack.pop();
					stack.push("}");
					stack.push("inmain");
					stack.push("cmt");
					stack.push("{");
					stack.push(")");
					stack.push("(");
					stack.push("main");
				}else {
					treatError(); break;
				}
			}else if(top.equals("cmt")) {
				if(tokentype.equals("comment")) {
					match(token);
				}else if(tokentype.equals("keyword") || tokentype.equals("identifier") || token.equals("main") || token.equals("if") || token.equals("while") || token.equals("for") || token.equals("out.println") || token.equals("int") || token.equals("}")){
					stack.pop();
				}else {
					treatError(); break;
				}
			}else if(top.equals("inmain")) {
				if(tokentype.equals("keyword") || tokentype.equals("identifier") || tokentype.equals("comment") || token.equals("if") || token.equals("while") || token.equals("for") || token.equals("out.println") || token.equals("int") || token.equals("}")) {
					stack.pop();
					stack.push("inmain'");
				}else {
					treatError(); break;
				}
			}else if(top.equals("inmain'")) {
				if(token.equals("if") || token.equals("while") || token.equals("for") || token.equals("out.println") || token.equals("int") || tokentype.equals("keyword") || tokentype.equals("identifier")) {
					stack.pop();
					stack.push("inmain'");
					stack.push("statementsq");
				}else if(tokentype.equals("comment")) {
					stack.pop();
					stack.push("inmain'");
					stack.push("cmt");
				}else if(token.equals("}")) {
					stack.pop();
				}else {
					treatError(); break;
				}
			}else if(top.equals("statementsq")) {
				if(tokentype.equals("keyword") || tokentype.equals("identifier") || tokentype.equals("comment") || token.equals("if") || token.equals("while") || token.equals("for") || token.equals("out.println") || token.equals("int") || token.equals("}")) {
					stack.pop();
					stack.push("statementsq'");
				}else {
					treatError(); break;
				}
			}else if(top.equals("statementsq'")) {
				if(token.equals("if")) {
					stack.pop();
					stack.push("statementsq'");
					stack.push("ifst");
				}else if(token.equals("while")) {
					stack.pop();
					stack.push("statementsq'");
					stack.push("whilest");
				}else if(token.equals("for")) {
					stack.pop();
					stack.push("statementsq'");
					stack.push("forst");
				}else if(token.equals("out.println")) {
					stack.pop();
					stack.push("statementsq'");
					stack.push("printst");
				}else if(tokentype.equals("keyword") || tokentype.equals("identifier") || token.equals("int")) {
					stack.pop();
					stack.push("statementsq'");
					stack.push("statementsc");
				}else if(tokentype.equals("comment") || token.equals("}")) {
					stack.pop();
				}else {
					treatError(); break;
				}
			}else if(top.equals("ifst")) {
				if(token.equals("if")) {
					stack.pop();
					stack.push("elsest");
					stack.push("elifst");
					stack.push("}");
					stack.push("statementsq'");
					stack.push("{");
					stack.push(")");
					stack.push("condition");
					stack.push("(");
					stack.push("if");
				}else {
					treatError(); break;
				}
			}else if(top.equals("elifst")) {
				if(token.equals("else if") || token.equals("else")) {
					stack.pop();
					stack.push("elifst'");
				}else {
					treatError(); break;
				}
			}else if(top.equals("elifst'")) {
				if(token.equals("else if")) {
					stack.pop();
					stack.push("elifst'");
					stack.push("}");
					stack.push("statementsq'");
					stack.push("{");
					stack.push(")");
					stack.push("condition");
					stack.push("(");
					stack.push("else if");
				}else if(token.equals("else")) {
					stack.pop();
				}else {
					treatError(); break;
				}
			}else if(top.equals("elsest")) {
				if(token.equals("else")) {
					stack.pop();
					stack.push("}");
					stack.push("statementsq");
					stack.push("{");
					stack.push("else");
				}else if(token.equals("if") || token.equals("while") || token.equals("for") || token.equals("out.println") || token.equals("int") || tokentype.equals("keyword") || tokentype.equals("identifier")) {
					stack.pop();
				}else {
					treatError(); break;
				}
			}else if(top.equals("condition")) {
				if(tokentype.equals("keyword") || tokentype.equals("identifier")) {
					stack.pop();
					stack.push("term");
					stack.push("cpop");
					stack.push("variable");
				}else {
					treatError(); break;
				}
			}else if(top.equals("cpop")) {
				if(token.equals(">") || token.equals("<") || token.equals("==") || token.equals("<=") || token.equals(">=")) {
					match(token);
				}else {
					treatError(); break;
				}
			}else if(top.equals("whilest")) {
				if(token.equals("while")) {
					stack.pop();
					stack.push("}");
					stack.push("statementsq");
					stack.push("{");
					stack.push(")");
					stack.push("condition");
					stack.push("(");
					stack.push("while");
				}else {
					treatError(); break;
				}
			}else if(top.equals("forst")) {
				if(token.equals("for")) {
					stack.pop();
					stack.push("}");
					stack.push("statementsq");
					stack.push("{");
					stack.push(")");
					stack.push("statement");
					stack.push(";");
					stack.push("condition");
					stack.push(";");
					stack.push("declaration");
					stack.push("(");
					stack.push("for");
				}else {
					treatError(); break;
				}
			}else if(top.equals("printst")) {
				if(token.equals("out.println")) {
					stack.pop();
					stack.push(";");
					stack.push(")");
					stack.push("printval");
					stack.push("(");
					stack.push("out.println");
				}else {
					treatError(); break;
				}
			}else if(top.equals("printval")) {
				if(tokentype.equals("double quote symbol")) {
					stack.pop();
					stack.push("double quote symbol");
					stack.push("string");
					stack.push("double quote symbol");
				}else if(tokentype.equals("keyword") || tokentype.equals("identifier")) {
					stack.pop();
					stack.push("variable");
				}else {
					treatError(); break;
				}
			}else if(top.equals("declaration")) {
				if(token.equals("int")) {
					stack.pop();
					stack.push("number");
					stack.push("=");
					stack.push("variable");
					stack.push("int");
				}else {
					treatError(); break;
				}
			}else if(top.equals("statementsc")) {
				if(token.equals("int") || tokentype.equals("keyword") || tokentype.equals("identifier")) {
					stack.pop();
					stack.push("cmt");
					stack.push(";");
					stack.push("statement");
				}else {
					treatError(); break;
				}
			}else if(top.equals("statement")) {
				if(token.equals("int")) {
					stack.pop();
					stack.push("declaration");
				}else if(tokentype.equals("keyword") || tokentype.equals("identifier")) {
					stack.pop();
					stack.push("statement'");
					stack.push("variable");
				}else {
					treatError(); break;
				}
			}else if(top.equals("statement'")) {
				if(token.equals("++") || token.equals("--")) {
					stack.pop();
					stack.push("instantop");
				}else if(token.equals("=")) {
					stack.pop();
					stack.push("operation");
					stack.push("term");
					stack.push("=");
				}else {
					treatError(); break;
				}
			}else if(top.equals("instantop")) {
				if(token.equals("++") || token.equals("--")) match(token);
				else{
					treatError(); break;
				}
			}else if(top.equals("operation")) {
				if(token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
					stack.pop();
					stack.push("term");
					stack.push("operator");
				}else if(token.equals(")") || token.equals(";")) {
					stack.pop();
				}else {
					treatError(); break;
				}
			}else if(top.equals("operator")) {
				if(token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) match(token);
				else{
					treatError(); break;
				}
			}else if(top.equals("term")) {
				if(tokentype.equals("number literal")) {
					match(token);
				}else if(tokentype.equals("keyword") || tokentype.equals("identifier")) {
					stack.pop();
					stack.push("variable");
				}else {
					treatError(); break;
				}
			}else if(top.equals("double quote symbol")) { /* TREATING NON TERMINAL FROM HERE */
				if(tokentype.equals("double quote symbol")) match(token);
			}else if(top.equals("left parenthesis")) {
				if(tokentype.equals("left parenthesis")) match(token);
				else {
					treatError(); break;
				}
			}else if(top.equals("right parenthesis")) {
				if(tokentype.equals("right parenthesis")) match(token);
				else {
					treatError(); break;
				}
			}else if(top.equals("comment")) {
				if(tokentype.equals("comment")) match(token);
				else {
					treatError(); break;
				}
			}else if(top.equals("number")) {
				if(tokentype.equals("number literal")) match(token);
				else {
					treatError(); break;
				}
			}else if(top.equals("string")) {
				if(tokentype.equals("string literal")) match(token);
				else {
					treatError(); break;
				}
			}else {
				if(top.equals(token)) {
					match(token);
				}else{
					treatError(); break;
				}
			}
			top = stack.peek();
			/*token = wordlist.get(tokenidx);
			tokentype = typelist.get(typeidx);
			System.out.println("end TOP " + top + " " + tokenidx + " TOKEN " + token + " " + typeidx + " TYPE " + tokentype);*/
			/*try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		if(success) System.out.println("Parsing OK");
	}

}
