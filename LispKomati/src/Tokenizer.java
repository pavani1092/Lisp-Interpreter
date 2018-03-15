import java.util.ArrayList;
import java.util.Arrays;
/**
 * @author Pavani Komati
 * Tokenizes the given raw string, reads each token and returns as an S-Expression
 */
public class Tokenizer {
	private ArrayList<String> tokenList;
	public boolean skippedSpace; // to check if there is a space previously
	public Tokenizer(String rawString) {
		rawString.trim();
		//replace extra spaces and tabs with single space
		rawString = rawString.replaceAll("[\\t\\n\\r]+"," ");
		//splitting the string on "(", ")"," ","." 
		String[] tokens = rawString.split("(\\s)|(?<=\\s)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|(?<=\\.)|(?=\\.)");	
		tokenList = new ArrayList<String>(Arrays.asList(tokens));
	}
	
	public boolean hasMoreTokens() {
		return tokenList.size() > 0;
	}
	
	public String nextToken() {
		return tokenList.remove(0);
	}
	
	//Reads the next available token and skips a space
	public SExpr checkNextToken() throws MyException {
		ParseInput.prev = ParseInput.current;
		ParseInput.current = null;
		skippedSpace = false;
		if(hasMoreTokens()) {
			String str = nextToken();
			if(str.isEmpty()||str.equals("")||str.equals(" ")) {// skipping space
				SExpr temp = ParseInput.prev;// saving previous state
				ParseInput.current = checkNextToken();
				ParseInput.prev = temp;
				skippedSpace = true;
			}else if(str.equals("("))
				ParseInput.current = new SExpr("(", Utility.OPEN);
			else if(str.equals(")"))
				ParseInput.current = new SExpr(")", Utility.CLOSE);
			else if(str.equals("."))
				ParseInput.current = new SExpr(".", Utility.DOT);
			else if(str.matches("^[-+]?\\d+?$")) {
				if(str.length()>6) { // integer greater than 6 digits{
					ParseInput.current = null;
					throw new MyException(" Invalid int atom with more than 6 digits.  ") ;
				}else
					ParseInput.current = new SExpr(str,Utility.INT_ATOM);
			}
			else if(str.matches("^[A-Za-z]+[0-9]*[A-Za-z]*?$")) {
				if(str.length()>10) { // String greater than 10 letters{
					ParseInput.current = null;
					throw new MyException(" Invalid sym atom with more than 10 letters. ") ;
				}else {
					str = str.toUpperCase();
					if(SExpr.lookup.containsKey(str))
						ParseInput.current = SExpr.lookup.get(str);
					else
						ParseInput.current = new SExpr(str, Utility.SYM_ATOM);
				}
				
			}else {
				throw new MyException( "Invalid token "+str);
			}
		}else {
			throw new MyException( "Expected more tokens");
		}
		return ParseInput.current;
	}
}
