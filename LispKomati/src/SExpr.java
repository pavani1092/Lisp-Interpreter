import java.util.HashMap;

public class SExpr {

	public static HashMap<String, SExpr> lookup = new HashMap<String, SExpr>();
	int type = 0;
	int val = 0;
	String name = null;
	SExpr left = null;
	SExpr right = null;

	public SExpr(String str, int type) {
		this.type = type;
		if (type == Utility.INT_ATOM)
			val = Integer.parseInt(str);
		else {
			name = str;
			lookup.put(name, this);
		}
	}

	public SExpr(SExpr l, SExpr r) {
		this.type = Utility.NON_ATOM;
		this.left = l;
		this.right = r;
	}


	@Override
	public String toString() {

		switch (type) {
			case Utility.INT_ATOM:
				return Integer.toString(val);
			case Utility.SYM_ATOM:
				return name;
			case Utility.NON_ATOM:
				StringBuffer s = new StringBuffer();
				return s.append("(").append(left.toString()).append(".").append(right.toString()).append(")").toString();
			default: return "";
		}
	}

}
