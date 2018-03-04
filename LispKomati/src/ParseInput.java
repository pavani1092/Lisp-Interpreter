import java.util.ArrayList;
import java.util.Arrays;

public class ParseInput {
	private Tokenizer myTokenizer;
	private int braceCount;
	public static String ErrorMessage = "error!!";
	public static SExpr prev,current; // save the previous and current values of tokens
	public ParseInput(String rawString) {
		ErrorMessage = "error!!";
		prev = current = null;
		myTokenizer = new Tokenizer(rawString);
	}
	public SExpr process() {
		
		if(readInput()== null) {
			return null;
		}
			
		if(braceCount>0) {
			ErrorMessage += "Extra Braces ( found ";
			return null;
		}
		
		if(myTokenizer.hasMoreTokens()) {
			ErrorMessage += " Extra input after "+ current.toString();
			return null;
		}
			
		return current;
	}
	
	/*
	 * This method always returns a valid SExpr & ., ), ( or 
	 * null if it encounters an invalid SExpr
	 * e.g returns complete (2.3) or 2 or abc etc..
	 */
	private SExpr readInput() {
		int nxtToken = Utility.ERROR;
		myTokenizer.checkNextToken();
		if(current!=null)
			nxtToken = current.type;
		switch(nxtToken) {
			case Utility.OPEN: 
				boolean tempSkippedSpace = myTokenizer.skippedSpace;
				braceCount++;
				if(readInput() == null)// read for car
					return null;
				SExpr car = current;
				if(current.type == Utility.CLOSE) { // encountered ")" 
					current = new SExpr("NIL", Utility.SYM_ATOM);
					myTokenizer.skippedSpace = tempSkippedSpace;
					return current; //----------> returns NIL for ()
				}
				SExpr dt = readInput();
				if(dt == null) // read for "."
					return null;
				if(current.type == Utility.DOT) {// take cdr
					if(readInput() == null )
						return null;
					if(readInput() == null)// for skipping )
						return null; 
					if(current.type != Utility.CLOSE) {
						ErrorMessage += "Missing parenthesis )";
						return null;
					}
					current = new SExpr(car, prev);
					myTokenizer.skippedSpace = tempSkippedSpace;
					return current;//-----------> returns valid (a.b)
					
				}else {// list notation
					ArrayList<SExpr> list = new ArrayList<SExpr>();
					list.add(car);
					while(current.type != Utility.CLOSE ){
						if(!myTokenizer.skippedSpace) {
							ErrorMessage += " SPACE missing in list.";
							return null;
						}
							
						list.add(current);
						if(readInput() == null)
							return null;
						
					}
					myTokenizer.skippedSpace = tempSkippedSpace;
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
	
	public String getError() {
		return ErrorMessage;
	}
}
