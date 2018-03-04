
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
		ParseInput parser = new ParseInput(e);
		SExpr res = parser.process();
		System.out.print((count++)+". ");
		if(res == null)
			System.out.println(parser.getError());
		else 
			System.out.println(res.toString());
	}

}
