import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Pavani Komati
 *
 */
public class Tokenizer {
	
	private String rawString;
	private ArrayList<String> tokenList;
	private int braceCount;
	private String ErrorMessage = "error!!";
	private SExpr prev,current;
	public Tokenizer(String str) {
		rawString = str;
	}
	
	private boolean hasMoreTokens() {
		while(tokenList.size()>0 && tokenList.get(0).length()==0)
			tokenList.remove(0);
		return tokenList.size() > 0;
	}
	
	private String nextToken() {
		return tokenList.remove(0);
	}
	
	private SExpr readToken() {
		int nxtToken = Utility.ERROR;
		checkNextToken();
		if(current!=null)
			nxtToken = current.type;
		switch(nxtToken) {
			case Utility.OPEN:
				braceCount++;
				if(readToken() == null)// read for car
					return null;
				SExpr car = current;
				if(current.type == Utility.CLOSE) { // encountered ) 
					current = new SExpr("NIL", Utility.SYM_ATOM);
					return current;
				}
				SExpr dt = readToken();
				if(dt == null) // read for .
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
					return current;
					
				}else {// list notation
					ArrayList<SExpr> list = new ArrayList<SExpr>();
					list.add(car);
					while(current.type != Utility.CLOSE ){
						list.add(current);
						if(readToken() == null)
							return null;
					}
					current = formList(list);
					return current;
				}
				
			case Utility.CLOSE:
				if(braceCount==0) {
					ErrorMessage += "Invalid parenthesis ) ";
					return null;
				}
				braceCount--;
				return current;
			case Utility.DOT:
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
	
	private SExpr formList(ArrayList<SExpr> list) {
		if(list.size()==0)
			return new SExpr("NIL", Utility.SYM_ATOM);
		return new SExpr(list.remove(0), formList(list));
	}
	
	private SExpr checkNextToken() {
		prev = current;
		current = null;
		if(hasMoreTokens()) {
			String str = nextToken();
			if(str.equals("("))
				current = new SExpr("(", Utility.OPEN);
			else if(str.equals(")"))
				current = new SExpr(")", Utility.CLOSE);
			else if(str.equals("."))
				current = new SExpr(".", Utility.DOT);
			else if(str.matches("^[-+]?\\d+?$")) {
				current = new SExpr(str,Utility.INT_ATOM);
			}
			else if(str.matches("^[A-Za-z]+[0-9]*[A-Za-z]*?$")) {
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
		rawString.replace("\n", "");
		//splitting the string on "(", ")"," ","." 
		String[] tokens = rawString.split("(\\s+)|(\\t+)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|(?<=\\.)|(?=\\.)");	
		tokenList = new ArrayList<String>(Arrays.asList(tokens));
		
		if(readToken()== null)
			return null;
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
