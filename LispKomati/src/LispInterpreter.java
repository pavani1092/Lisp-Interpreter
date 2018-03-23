
import java.util.Scanner;

/**
 * @author Pavani Komati
 *
 */
public class LispInterpreter {
	private static int count = 1;
	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		System.out.println("Enter the List Expressions seperated by $:");
		Scanner sc = new Scanner(System.in);
		StringBuilder ip = new StringBuilder();
		
		//taking input until string ends with $$
		String next = sc.nextLine();
		while(true) {
			if(next.equals("$$")) {
				parse(ip.toString());
				break;
			}else if(next.equals("$")) {
				parse(ip.toString());
				ip.setLength(0);
			}else {
				ip.append(" ");
				ip.append(next);
			}	
			next = sc.nextLine();
		}
		
		sc.close();
		
	}
	
	private static void parse(String e) {
		if(e== null || e.trim().length()==0)
			System.out.println((count++)+". Empty Input");
		else {
			ParseInput parser = new ParseInput(e);
			//System.out.print((count++)+". ");
			try {
				Interpreter in = new Interpreter();
				SExpr parse = parser.process();
				System.out.println("dot notation "+parse.toString());
				SExpr res = in.eval(parse);
				System.out.println(res.toString());
			}catch(MyException ex) {
				System.out.println("Error: "+ ex.getMessage());
			}
				
		}
		
	}

}
