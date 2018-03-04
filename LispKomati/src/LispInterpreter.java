
import java.util.Scanner;

/**
 * @author Pavani Komati
 *
 */
public class LispInterpreter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		System.out.println("Enter the List Expressions seperated by $:");
		Scanner sc = new Scanner(System.in);
		StringBuilder ip = new StringBuilder();
		
		//taking input until string ends with $$
		do {
			ip.append(" ");
			ip.append(sc.nextLine());
		}while(ip.length()<2 || !(ip.substring(ip.length()-2).equals("$$")));
		
		String[] exps = (ip.toString()).split("\\$");
		int i =1;
		for(String e:exps) {
			ParseInput parser = new ParseInput(e);
			SExpr res = parser.process();
			System.out.print((i++)+". ");
			if(res == null)
				System.out.println(parser.getError());
			else 
				System.out.println(res.toString());
		}
		
		
		sc.close();
		
	}

}
