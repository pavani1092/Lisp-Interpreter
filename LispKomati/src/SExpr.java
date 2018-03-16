

public class SExpr {

	int type = 0;
	String name = null;
	SExpr left = null;
	SExpr right = null;

	public SExpr(String str, int type) {
		this.type = type;
		name = str;
	}

	public SExpr(SExpr l, SExpr r) {
		this.type = Utility.NON_ATOM;
		this.left = l;
		this.right = r;
	}
	
	boolean isAtom() {
		return (type == Utility.INT_ATOM || type == Utility.SYM_ATOM);
	}

	@Override
	public String toString() {

		switch (type) {
			case Utility.INT_ATOM:
				return name;
			case Utility.SYM_ATOM:
				return name;
			case Utility.NON_ATOM:
				StringBuffer s = new StringBuffer();
				return s.append("(").append(left.toString()).append(".").append(right.toString()).append(")").toString();
			default: return "";
		}
	}

}
