import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Pavani Komati
 * Tokenizes the given string and forms a valid S-Expression with the tokens
 */
public class Tokenizer {
	
	private String rawString;
	private ArrayList<String> tokenList;
	private int braceCount;
	private String ErrorMessage = "error!!";
	private SExpr prev,current; // save the previous and current values of tokens
	private boolean skippedSpace; // to check if there is a space previously
	public Tokenizer(String str) {
		rawString = str;
	}
	
	private boolean hasMoreTokens() {
		return tokenList.size() > 0;
	}
	
	private String nextToken() {
		return tokenList.remove(0);
	}
	/*
	 * This method always returns a valid SExpr & ., ), ( or 
	 * null if it encounters an invalid SExpr
	 * e.g returns complete (2.3) or 2 or abc etc..
	 */
	private SExpr readToken() {
		int nxtToken = Utility.ERROR;
		checkNextToken();
		if(current!=null)
			nxtToken = current.type;
		switch(nxtToken) {
			case Utility.OPEN: 
				boolean tempSkippedSpace = skippedSpace;
				braceCount++;
				if(readToken() == null)// read for car
					return null;
				SExpr car = current;
				if(current.type == Utility.CLOSE) { // encountered ")" 
					current = new SExpr("NIL", Utility.SYM_ATOM);
					skippedSpace = tempSkippedSpace;
					return current; //----------> returns NIL for ()
				}
				SExpr dt = readToken();
				if(dt == null) // read for "."
					return null;
				if(current.type == Utility.DOT) {// take cdr
					if(readToken() == null )
						return null;
					if(readToken() == null)// for skipping )
						return null; 
					if(current.type != Utility.CLOSE) {
						ErrorMessage += "Missing parenthesis )";
						return null;
					}
					current = new SExpr(car, prev);
					skippedSpace = tempSkippedSpace;
					return current;//-----------> returns valid (a.b)
					
				}else {// list notation
					ArrayList<SExpr> list = new ArrayList<SExpr>();
					list.add(car);
					while(current.type != Utility.CLOSE ){
						if(!skippedSpace) {
							ErrorMessage += " SPACE missing in list.";
							return null;
						}
							
						list.add(current);
						if(readToken() == null)
							return null;
						
					}
					skippedSpace = tempSkippedSpace;
					current = formList(list);
					return current; //----------> returns valid list (a b c)
				}
				
			case Utility.CLOSE:
				if(braceCount==0) {
					ErrorMessage += "Invalid parenthesis ) ";
					return null;
				}
				braceCount--;
				return current;
			case Utility.DOT:
				//checking if dot is followed by a valid S-Expr
				if(prev== null || !(prev.type==Utility.INT_ATOM || prev.type==Utility.SYM_ATOM || prev.type==Utility.NON_ATOM)) {
					ErrorMessage += " . followed by invalid exp ";
					return null;
				}
				return current;
			case Utility.INT_ATOM:
				return current;
			case Utility.SYM_ATOM:
				return current;
			case Utility.ERROR:
				return null;
			default:
				return null;
		}
		
	}
	//Forms SExpr from the given list
	private SExpr formList(ArrayList<SExpr> list) {
		if(list.size()==0)
			return new SExpr("NIL", Utility.SYM_ATOM);
		return new SExpr(list.remove(0), formList(list));
	}
	
	//Reads the next available token and skips a space
	private SExpr checkNextToken() {
		prev = current;
		current = null;
		skippedSpace = false;
		if(hasMoreTokens()) {
			String str = nextToken();
			if(str.isEmpty()||str.equals("")||str.equals(" ")) {
				SExpr temp = prev;
				current = checkNextToken();
				prev = temp;
				skippedSpace = true;
			}else if(str.equals("("))
				current = new SExpr("(", Utility.OPEN);
			else if(str.equals(")"))
				current = new SExpr(")", Utility.CLOSE);
			else if(str.equals("."))
				current = new SExpr(".", Utility.DOT);
			else if(str.matches("^[-+]?\\d+?$")) {
				current = new SExpr(str,Utility.INT_ATOM);
			}
			else if(str.matches("^[A-Za-z]+[0-9]*[A-Za-z]*?$")) {
				str = str.toUpperCase();
				if(SExpr.lookup.containsKey(str))
					current = SExpr.lookup.get(str);
				else
					current = new SExpr(str, Utility.SYM_ATOM);
			}else {
				ErrorMessage += "Invalid token "+str;
			}
		}else {
			ErrorMessage += "Empty input ";
		}
		return current;
	}
	
	public String getError() {
		return ErrorMessage;
	}
	public SExpr process() {
		rawString.trim();
		//replace extra spaces and tabs with single space
		rawString = rawString.replaceAll("[\\t\\n\\r]+"," ");
		//splitting the string on "(", ")"," ","." 
		String[] tokens = rawString.split("(\\s)|(?<=\\s)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|(?<=\\.)|(?=\\.)");	
		tokenList = new ArrayList<String>(Arrays.asList(tokens));
		
		if(readToken()== null) {
			return null;
		}
			
		if(braceCount>0) {
			ErrorMessage += "Extra Braces ( found ";
			return null;
		}
		
		if(hasMoreTokens()) {
			ErrorMessage += " Extra input after "+ current.toString();
			return null;
		}
			
		return current;
	}

}
