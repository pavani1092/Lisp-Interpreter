import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;



public class Interpreter {
	
	class Func{
		String name;
		ArrayList<String> parameters;
		SExpr body;
		public Func() {
		}
		private void process(SExpr exp) throws MyException {
			if(exp.isAtom())
				throw new MyException("Invalid function: definition without parameters and body");
			SExpr n = car(exp);
			if(n.type != Utility.SYM_ATOM) {
				throw new MyException("invalid function name");
			}
			this.name = n.name;;
			SExpr fn = cdr(exp);
			if(fn.isAtom())
				throw new MyException("Invalid function: definition without body");
			this.parameters = getParameters(car(fn));
			this.body = car(cdr(fn));
			
		}
		
		private ArrayList<String> getParameters(SExpr sExpr) throws MyException{
			ArrayList<String> res = new ArrayList<String>();
			if(sExpr.isAtom()) {
				if(!isNIL(sExpr))
					res.add(getParam(sExpr));
			}else {
				res.add(getParam(car(sExpr)));
				res.addAll(getParameters(cdr(sExpr)));
			}
			return res;
		}
		private String getParam(SExpr sExpr) throws MyException {
			if(sExpr.type != Utility.SYM_ATOM)
				throw new MyException("Invalid parameter. Parameter cannot be a int atom");
/*			if(isNIL(sExpr)|| isT(sExpr))
				throw new MyException("Invalid parameter "+ sExpr.name);*/
			
			return sExpr.name;
		}
		
	}
	
	public Interpreter() {
	}
	
	HashMap<String,Stack<String>> alist = new HashMap<>();
	static HashMap<String,Stack<Func>> dlist = new HashMap<>();
	
	int getVal(SExpr a) throws MyException {
		if(a.type == Utility.INT_ATOM )
			return Integer.parseInt(a.name);
		else {
			return getFrom_alist(a.name);
		}
			
	}
	private int getFrom_alist(String name) throws MyException {
		if(alist.containsKey(name)) {
			Stack<String> s = alist.get(name);
			if(!s.isEmpty()) {
				return Integer.parseInt(s.peek());
			}
				
		}
		throw new MyException("alist doesnt contain value for "+name);
	}


	SExpr plus(SExpr a, SExpr b) throws MyException {
		int av = getVal(a);
		int bv = getVal(b);
		return new SExpr(Integer.toString(av+bv), Utility.INT_ATOM);
	}
	
	SExpr minus(SExpr a, SExpr b) throws MyException {
		int av = getVal(a);
		int bv = getVal(b);
		return new SExpr(Integer.toString(av-bv), Utility.INT_ATOM);
	}
	
	SExpr atom(SExpr s) {
		if((s.type == Utility.INT_ATOM) ||(s.type == Utility.SYM_ATOM))
			return new SExpr("T", Utility.SYM_ATOM);
		return new SExpr("NIL", Utility.SYM_ATOM);
	}
	
	SExpr eq(SExpr a, SExpr b) throws MyException {
		int av = getVal(a);
		int bv = getVal(b);
		if(av==bv)
			return new SExpr("T", Utility.SYM_ATOM);
		return new SExpr("NIL", Utility.SYM_ATOM);
	}
	
	SExpr cons(SExpr a, SExpr b) {
		return new SExpr(a, b);
	}
	
	SExpr car(SExpr a) throws MyException {
		if(a.type != Utility.NON_ATOM)
			throw new MyException(" expected non atom, car called on a atom");
		return a.left;
	}
	SExpr cdr(SExpr a) throws MyException {
		if(a.type != Utility.NON_ATOM)
			throw new MyException(" expedted non atom, cdr called on a atom");
		return a.right;
	}
	
	SExpr _null(SExpr a) {
		if(a.type == Utility.SYM_ATOM && a.name.equals("NIL"))
			return new SExpr("T", Utility.SYM_ATOM);
	    return new SExpr("NIL", Utility.SYM_ATOM);
	}
	
	boolean isT(SExpr a) {
		if(a.type == Utility.SYM_ATOM)
			return a.name.equals("T");
		return false;
	}
	
	boolean isNIL(SExpr a) {
		if(a.type == Utility.SYM_ATOM)
			return a.name.equals("NIL");
		return false;
	}
	
	SExpr eval(SExpr e) throws MyException {
		if(e.isAtom()) {
			if(isT(e)|| isNIL(e))
				return e;
			int x = getVal(e);
			return new SExpr(Integer.toString(x), Utility.INT_ATOM);
		}
		SExpr carEx = car(e);
		if(carEx.isAtom()) {
			if(carEx.type == Utility.SYM_ATOM) {
				String fn = carEx.name;
				if(fn.equals("QUOTE"))
					return car(cdr(e));
				if(fn.equals("COND"))
					return evcon(cdr(e));
				if(fn.equals("DEFUN"))
					return defun(cdr(e));
			}
			return apply(car(e),evlis(cdr(e)));
		}	
		throw new MyException("error in eval");
	}
	private SExpr apply(SExpr f, SExpr x) throws MyException {
		if(f.type == Utility.SYM_ATOM) {
			String fn = f.name;
			switch (fn) {
				case "CAR": return car(car(x));
				case "CDR": return cdr(car(x));
				case "CONS": return cons(car(x), car(cdr(x)));
				case "ATOM": return atom(x);
				case "NULL": return _null(x);
				case "EQ" : return eq(car(x),car(cdr(x)));
				case "PLUS" : return plus(car(x),car(cdr(x)));
				case "MINUS" : return minus(car(x),car(cdr(x)));
				default:
			}		
		}
		
		Func fnDef = getFnDef(f);
		addpairs(fnDef.parameters,getlist(x));
		SExpr res = eval(fnDef.body);
		removepairs(fnDef.parameters);
		return res;
	}
	private void removepairs(ArrayList<String> parlist) {
		for( String s : parlist) {
			alist.get(s).pop();
		}
		
	}
	private ArrayList<String> getlist(SExpr ex) throws MyException {
		ArrayList<String> res = new ArrayList<String>();
		if(ex.isAtom()) {
			if(!isNIL(ex))
				res.add(ex.name);
		} else {
			res.add(car(ex).name);
			res.addAll(getlist(cdr(ex)));			
		}
		return res;
	}
	private void addpairs(ArrayList<String> parlist, ArrayList<String> arglist) throws MyException {
		if(parlist.size() != arglist.size())
			throw new MyException("function expects "+parlist.size()+" arguments, received "+arglist.size()+" arguments");
		int i =0;
		for(String s : parlist) {
			Stack<String> stack = alist.getOrDefault(s, new Stack<String>());
			stack.add(arglist.get(i++));
			alist.put(s, stack);
		}
		
	}
	private Func getFnDef(SExpr f) throws MyException {
		if(f.type == Utility.SYM_ATOM) {
			if(dlist.containsKey(f.name)) {
				Stack<Func> stack = dlist.get(f.name);
				if(stack.size()>0)
					return stack.peek();
			}
		}
		throw new MyException("Could not find a function named "+f.name);
	}
	
	private SExpr evlis(SExpr list) throws MyException {
		if(isNIL(list))
			return list;
		return cons(eval(car(list)),evlis(cdr(list)));
	}
	private SExpr defun(SExpr exp) throws MyException {
		Func fn = new Func();
		fn.process(exp);
		Stack<Func> stack = dlist.getOrDefault(fn.name, new Stack<Func>());
		stack.add(fn);
		dlist.put(fn.name,stack);
		return new SExpr(fn.name, Utility.SYM_ATOM);
	}
	private SExpr evcon(SExpr be) throws MyException {
		if(isNIL(be))
			throw new MyException("Error in boolean condition");
		if(isT(eval(car(car(be)))))
			return eval (car(cdr(car(be))));
		return evcon(cdr(be));
	}
	
}
