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
	
	HashMap<String,Stack<SExpr>> alist = new HashMap<>();
	static HashMap<String,Stack<Func>> dlist = new HashMap<>();
	
	SExpr getVal(SExpr a) throws MyException {
		if(a.type == Utility.INT_ATOM )
			return a;
		else {
//			while(!isNIL(a) && !isT(a) && a.type == Utility.SYM_ATOM )
//				a =  getFrom_alist(a.name);
			return getFrom_alist(a.name);
		}
			
	}
	private SExpr getFrom_alist(String name) throws MyException {
		if(alist.containsKey(name)) {
			Stack<SExpr> s = alist.get(name);
			if(!s.isEmpty()) {
				return s.peek();
			}
				
		}
		throw new MyException("Unbound atom "+name);
	}

	SExpr times(SExpr a, SExpr b) throws MyException {
		int av = getIntVal(a);
		int bv = getIntVal(b);
		return new SExpr(Integer.toString(av*bv), Utility.INT_ATOM);
	}
	private int getIntVal(SExpr a) throws MyException {
		SExpr s = getVal(a);
		if(s.type != Utility.INT_ATOM)
			throw new MyException("expected int atom but received non atom");
		return Integer.parseInt(s.name);
	}
	SExpr quotient(SExpr a, SExpr b) throws MyException {
		int av = getIntVal(a);
		int bv = getIntVal(b);
		if(bv==0)
			throw new MyException("divisor cannot be zero!!");
		return new SExpr(Integer.toString(av/bv), Utility.INT_ATOM);
	}
	SExpr remainder(SExpr a, SExpr b) throws MyException {
		int av = getIntVal(a);
		int bv = getIntVal(b);
		if(bv==0)
			throw new MyException("divisor cannot be zero!!");
		return new SExpr(Integer.toString(av%bv), Utility.INT_ATOM);
	}
	SExpr less(SExpr a, SExpr b) throws MyException {
		int av = getIntVal(a);
		int bv = getIntVal(b);
		String s = "T";
		if(av>=bv)
			s= "NIL";
		return new SExpr(s, Utility.SYM_ATOM);
	}
	SExpr greater(SExpr a, SExpr b) throws MyException {
		int av = getIntVal(a);
		int bv = getIntVal(b);
		String s = "T";
		if(av<=bv)
			s= "NIL";
		return new SExpr(s, Utility.SYM_ATOM);
	}
	
	SExpr plus(SExpr a, SExpr b) throws MyException {
		int av = getIntVal(a);
		int bv = getIntVal(b);
		return new SExpr(Integer.toString(av+bv), Utility.INT_ATOM);
	}
	
	SExpr minus(SExpr a, SExpr b) throws MyException {
		int av = getIntVal(a);
		int bv = getIntVal(b);
		return new SExpr(Integer.toString(av-bv), Utility.INT_ATOM);
	}
	SExpr isInt(SExpr a){
		if(a.type == Utility.INT_ATOM)
			return new SExpr("T", Utility.SYM_ATOM);
		return new SExpr("NIL", Utility.SYM_ATOM);
	}
	
	
	SExpr atom(SExpr s) {
		if((s.type == Utility.INT_ATOM) ||(s.type == Utility.SYM_ATOM))
			return new SExpr("T", Utility.SYM_ATOM);
		return new SExpr("NIL", Utility.SYM_ATOM);
	}
	
	SExpr eq(SExpr a, SExpr b) throws MyException {
		if(!a.isAtom()|| !b.isAtom())
			throw new MyException("eq expects only atoms");
		if(a.name.equals(b.name))
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
		//System.out.println("in eval "+e.toString());
		if(e.isAtom()) {
			if(isT(e)|| isNIL(e))
				return e;
			return getVal(e);
		}
		SExpr carEx = car(e);
		if(carEx.isAtom()) {
			if(carEx.type == Utility.SYM_ATOM) {
				String fn = carEx.name;
				if(fn.equals("QUOTE")) {
					int l = length(cdr(e));
					if(l!=1)
						throw new MyException("quote expects 1 arg, received "+l);
					return car(cdr(e));
				}
				if(fn.equals("COND"))
					return evcon(cdr(e));
				if(fn.equals("DEFUN"))
					return defun(cdr(e));
			}
			return apply(car(e),evlis(cdr(e)));
		}	
		throw new MyException("error in eval");
	}
	private int length(SExpr e) throws MyException{
		if(isNIL(e))
			return 0;
		if(e.isAtom())
			return 1;
		return 1+length(cdr(e));
	}

	private SExpr apply(SExpr f, SExpr x) throws MyException {
		//System.out.println("in apply "+x.toString() +" of "+f.name);
		Func fnDef = getFnDef(f);
		if(fnDef!=null) {
			addpairs(fnDef.parameters,getlist(x));
			SExpr res = eval(fnDef.body);
			removepairs(fnDef.parameters);
			return res;
		}
		
		if(f.type == Utility.SYM_ATOM) {
			String fn = f.name;
			int l = length(x);
			switch (fn) {
				case "CAR": 
					if(l!=1)
						throw new MyException("car expects 1 arg, received "+l);
					return car(car(x));
				case "CDR": 
					if(l!=1)
						throw new MyException("cdr expects 1 arg, received "+l);
					return cdr(car(x));
				case "CONS":
					if(l!=2)
						throw new MyException("cons expects 2 arg, received "+l);
					return cons(car(x), car(cdr(x)));
				case "ATOM":
					if(l!=1)
						throw new MyException("atom expects 1 arg, received "+l);
					return atom(car(x));
				case "NULL": 
					if(l!=1)
						throw new MyException("null expects 1 arg, received "+l);
					return _null(car(x));
				case "EQ" : 
					if(l!=2)
						throw new MyException("eq expects 2 arg, received "+l);
					return eq(car(x),car(cdr(x)));
				case "PLUS" : 
					if(l!=2)
						throw new MyException("plus expects 2 arg, received "+l);
					return plus(car(x),car(cdr(x)));
				case "MINUS" :
					if(l!=2)
						throw new MyException("minus expects 2 arg, received "+l);
					return minus(car(x),car(cdr(x)));
				case "INT" : 
					if(l!=1)
						throw new MyException("int expects 1 arg, received "+l);
					return isInt(car(x));
				case "TIMES":
					if(l!=2)
						throw new MyException("times expects 2 arg, received "+l);
					return times(car(x),car(cdr(x)));
				case "QUOTIENT":
					if(l!=2)
						throw new MyException("quotient expects 2 arg, received "+l);
					return quotient(car(x),car(cdr(x)));
				case "REMAINDER":
					if(l!=2)
						throw new MyException("remainder expects 2 arg, received "+l);
					return remainder(car(x),car(cdr(x)));
				case "LESS":
					if(l!=2)
						throw new MyException("less expects 2 arg, received "+l);
					return less(car(x),car(cdr(x)));
				case "GREATER": 
					if(l!=2)
						throw new MyException("greater expects 2 arg, received "+l);
					return greater(car(x),car(cdr(x)));
				default:
			}		
		}
		
		throw new MyException("Could not find a function named "+f.name);

	}
	private void removepairs(ArrayList<String> parlist) {
		for( String s : parlist) {
			SExpr x = alist.get(s).pop();
			//System.out.println("removed "+s+" "+x.toString());
		}
		
	}
	private ArrayList<SExpr> getlist(SExpr ex) throws MyException {
		ArrayList<SExpr> res = new ArrayList<SExpr>();
		if(ex.isAtom()) {
			if(!isNIL(ex))
				res.add(ex);
		} else {
			res.add(car(ex));
			res.addAll(getlist(cdr(ex)));			
		}
		return res;
	}
	private void addpairs(ArrayList<String> parlist, ArrayList<SExpr> arglist) throws MyException {
		if(parlist.size() != arglist.size())
			throw new MyException("function expects "+parlist.size()+" arguments, received "+arglist.size()+" arguments");
		int i =0;
		for(String s : parlist) {
			//System.out.println("added "+s+" "+arglist.get(i).toString());
			Stack<SExpr> stack = alist.getOrDefault(s, new Stack<SExpr>());
			stack.add(arglist.get(i++));
			alist.put(s, stack);
		}
		
	}
	private Func getFnDef(SExpr f) {
		if(f.type == Utility.SYM_ATOM) {
			if(dlist.containsKey(f.name)) {
				Stack<Func> stack = dlist.get(f.name);
				if(stack.size()>0)
					return stack.peek();
			}
		}
		return null;
	}
	
	private SExpr evlis(SExpr list) throws MyException {
		//System.out.println("in evlis "+ list.toString());
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
		//System.out.println("in evcon "+ be.toString() );
		if(isNIL(be))
			throw new MyException("Error in boolean condition");
		SExpr be_car = car(be);
		int l = length(be_car);
		if(l!=2)
			throw new MyException("Cond case expects 2 args, received "+l);
		if(!isNIL(eval(car(be_car))))
			return eval (car(cdr(be_car)));
		return evcon(cdr(be));
	}
	
}
