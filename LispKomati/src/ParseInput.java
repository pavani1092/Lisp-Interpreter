import java.util.ArrayList;
/**
 * @author Pavani Komati
 * Parses the input raw string with the help of tokeniser
 */
public class ParseInput {
	private Tokenizer myTokenizer;
	private int braceCount;
	public static SExpr prev,current; // save the previous and current values of tokens
	public ParseInput(String rawString) {
		prev = current = null;
		myTokenizer = new Tokenizer(rawString);
	}
	public SExpr process() throws MyException {
		
		readInput();
			
		if(braceCount>0) {
			throw new MyException("Extra Braces ( found ") ;
		}
		
		if(myTokenizer.hasMoreTokens()) {
			throw new MyException(" Extra input after "+ current.toString()) ;
		}
			
		return current;
	}
	
	/*
	 * This method always returns a valid SExpr & ., ), ( or 
	 * null if it encounters an invalid SExpr
	 * e.g returns complete (2.3) or 2 or abc etc..
	 */
	private SExpr readInput() throws MyException {
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
						throw new MyException( "Missing parenthesis )");
					}
					current = new SExpr(car, prev);
					myTokenizer.skippedSpace = tempSkippedSpace;
					return current;//-----------> returns valid (a.b)
					
				}else {// list notation
					ArrayList<SExpr> list = new ArrayList<SExpr>();
					list.add(car);
					while(current.type != Utility.CLOSE ){
						if(!myTokenizer.skippedSpace) {
							throw new MyException( " SPACE missing in list before "+current.toString());
						}
							
						list.add(current);
						SExpr s = readInput();
						if(s == null || s.type == Utility.DOT)
							return null;
						
					}
					myTokenizer.skippedSpace = tempSkippedSpace;
					current = formList(list);
					return current; //----------> returns valid list (a b c)
				}
				
			case Utility.CLOSE:
				if(braceCount==0) {
					throw new MyException( "Invalid parenthesis ) ");
				}
				braceCount--;
				return current;
			case Utility.DOT:
				//checking if dot is followed by a valid S-Expr
				if(prev== null || !(prev.type==Utility.INT_ATOM || prev.type==Utility.SYM_ATOM || prev.type==Utility.NON_ATOM)) {
					throw new MyException( " . followed by invalid exp ");
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
	
}
