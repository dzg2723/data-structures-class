import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
public class ExpTree {


		class ExprNode {
			public char op;
			public int val;
			public ExprNode left;
			public ExprNode right;

			public String toString() {
				if (op == ' ') 
					return Integer.toString(val);
				else
					return Character.toString(op);
			}

			ExprNode(int v) { val = v; op = ' '; left = null; right = null; }
			ExprNode(char c, ExprNode l, ExprNode r)  { val = 0; op = c; left = l; right = r; }
		}

		class TokenString {
			String data;
			
			public TokenString(String text) {
				data = text;
			}
			
			public char nextToken() {
				if (data.length() == 0) return ' ';
				char x = data.charAt(0);
				data = data.substring(1);
				if (x == ' ') return nextToken();
				return x;
			}
		}

		
		class IndexedString {
			public String data;
			public int i;

			IndexedString(String text) {
				data = text;
				i = 0;
			}
		}

		class Position {
			public int val;
		}

		ExprNode root;

		public String toString() {
			return stringify(root);
		}

		private String stringify(ExprNode node) {
			if (node == null) return "";
			return node.toString() + " " + stringify(node.left) + stringify(node.right);
		}

		public void fill(String prefixExpr) {
			root = fill(prefixExpr, new Position());
			//root = fill(new IndexedString(prefixExpr));
			//root = fill(new TokenString(prefixExpr));
		}

		ExprNode fill(String prefixExpr, Position i)
		{
			char c = ' ';
			while (i.val < prefixExpr.length() && c != '+' && c != '*' && !(c >= '0' && c <= '9')) {
				c = prefixExpr.charAt(i.val);
				i.val++;
			}
			if (i.val > prefixExpr.length()) return null;

			if (c == '+' || c == '*') {
				return new ExprNode(c, fill(prefixExpr, i), fill(prefixExpr, i));
			} else {
				return new ExprNode(c - '0');
			}
		}

		ExprNode fill(IndexedString prefixExpr)
		{
			char c = ' ';
			while (prefixExpr.i < prefixExpr.data.length() && c != '+' && c != '*' && !(c >= '0' && c <= '9')) {
				c = prefixExpr.data.charAt(prefixExpr.i);
				prefixExpr.i++;
			}
			if (prefixExpr.i > prefixExpr.data.length()) return null;

			if (c == '+' || c == '*') {
				return new ExprNode(c, fill(prefixExpr), fill(prefixExpr));
			} else {
				return new ExprNode(c - '0');
			}
		}

		ExprNode fill(TokenString prefixExpr) {
			char c = prefixExpr.nextToken();
			if (c == ' ') { 
				return null;
			} else if (c == '+' || c == '*') {
				return new ExprNode(c, fill(prefixExpr), fill(prefixExpr));
			} else {
				return new ExprNode(c - '0');
			}
		}

		public int eval() { return eval(root); }

		private int eval(ExprNode node)
		{
			if ( node == null ) return 0;
			int a = eval(node.left);
			int b = eval(node.right);
			if (node.op == '+')
				node.val = a+b;
			if (node.op == '*')
				node.val = a*b;
			if (node.op == '-')
				node.val = a-b;
			return node.val;
		}

		public String toInfix() {
			Queue<String> q = new LinkedList<>();
			toInfix(root, q);
			String result = "";
			String e = q.remove();
			while (true) {
				result = result + e + " ";
				try {
					e = q.remove();
				} catch (java.util.NoSuchElementException er) {
					break;
				}
			}
			return result;
		}
	
		private void toInfix(ExprNode node, Queue<String> queue) {
			if ( node == null ) return;
			if (node.left != null || node.right != null) {
				queue.add("(");
				toInfix(node.left, queue);
			}
			
			
			if (node.op == ' ') {
				queue.add(Integer.toString(node.val));
			} else {
				queue.add(Character.toString(node.op));
			}
			
			if (node.left != null || node.right != null) {
				toInfix(node.right, queue);
				queue.add(")");
			}
		}
		
		private int getTreeHeight(ExprNode root) {
			if (root == null) {
				return 0;
			}
			int h_left = getTreeHeight(root.left);
			int h_right = getTreeHeight(root.right);
			
			int height = Math.max(h_left, h_right) + 1;
			return height;
			
		}
		
		public void drawTree() {
			int tree_height = getTreeHeight(root);
			System.out.println(tree_height);
			//start by assuming tree height of 5
			
		}
	
	
	public static void main(String[] args) {
		ExpTree tree = new ExpTree();
		tree.fill("+ * + * 8 7 4 5 * + 2 2 * 3 7");
		System.out.println(tree);
		System.out.println(tree.eval());
		String a = tree.toInfix();
		System.out.println(a);
		tree.drawTree();
	}

}