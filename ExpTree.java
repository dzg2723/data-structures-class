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
			result = "Infix notation: " + result;
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
		
		public String toPostfix() {
			Queue<String> q = new LinkedList<>();
			toPostfix(root, q);
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
			result = "Postfix notation: " + result;
			return result;
		}
		
		private void toPostfix(ExprNode node, Queue<String> queue) {
			if ( node == null ) return;
			
			toPostfix(node.left, queue);
			
			toPostfix(node.right, queue);
			
			if (node.op == ' ') {
				queue.add(Integer.toString(node.val));
			} else {
				queue.add(Character.toString(node.op));
			}
			
			
		}
		
		public String toPrefix() {
			Queue<String> q = new LinkedList<>();
			toPrefix(root, q);
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
			result = "Prefix notation: " + result;
			return result;
		}
		
		private void toPrefix(ExprNode node, Queue<String> queue) {
			if ( node == null ) return;
			
			
			if (node.op == ' ') {
				queue.add(Integer.toString(node.val));
			} else {
				queue.add(Character.toString(node.op));
			}
			
			toPrefix(node.left, queue);
			
			toPrefix(node.right, queue);
			
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
			eval(); //Make sure that the tree is filled out.
			int tree_height = getTreeHeight(root);
			//System.out.print("Tree Height: ");
			//System.out.println(tree_height);
			
			//Handle exceptions
			if (tree_height == 0) {
				System.out.println();
				return;
			}
			if (tree_height == 1) {
				System.out.println(root.val);
				return;
			}
			
			//Create two queues
			Queue<ExprNode> node_queue = new LinkedList<>();
			node_queue.add(root);
			
			Queue<Integer> col_queue = new LinkedList<>();
			int starting_column = (int) (5 * Math.pow(2, tree_height-2));
			col_queue.add(starting_column);


			//https://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string-in-java
			//String repeated = new String(new char[100]).replace("\0", "abcde");
			//System.out.println(repeated);
			


			for (int i = 0; i < tree_height; i++) {
				
				//Line 1/3 of the set
				String line = "";			//Progressively assembled before being printed
				int operator_counter = 0;	//Records the number of nodes on line that are operators
				int curr_col = 0;
				int target_col;
				ExprNode curr_node;
				
				int potent_nodes = (int) (Math.pow(2, i));
				for (int j = 0; j < potent_nodes; j++) {
					curr_node = node_queue.remove();
					if (curr_node == null) {
						continue;
					}
					target_col = col_queue.remove();

					
					int num_of_spaces = (target_col-1) - curr_col;
					String spaces = new String(new char[num_of_spaces]).replace("\0", " ");
				
					if (curr_node.op == ' ') {
						line += spaces + curr_node.val;
						curr_col += num_of_spaces + 1; //+1 to account for value slot
						
					} else {
						line += spaces + curr_node.op + " (" + curr_node.val + ")";
						int val_digits = Integer.toString(curr_node.val).length();
						curr_col += num_of_spaces + 3 + val_digits + 1;
						
						int step =  (int) (starting_column / Math.pow(2, i+1)); //todo, make it ceil?
						col_queue.add(target_col - step);
						col_queue.add(target_col);
						col_queue.add(target_col + step);
						
						operator_counter += 1;
						
					}
					node_queue.add(curr_node.left);
					node_queue.add(curr_node.right);
				}
				
				System.out.println(line);
				
				//Line 2/3 of the set
				line = "";
				curr_col = 0;
				for (int j = 0; j < operator_counter; j++) {
					target_col = col_queue.remove();
					int num_of_cols = target_col - curr_col; 			//we include target column as a space
					String columns = new String(new char[num_of_cols]).replace("\0", " ");
					line += columns;
					curr_col += num_of_cols;
					col_queue.add(target_col);
					
					target_col = col_queue.remove();
					num_of_cols = (target_col-1) - curr_col;
					columns = new String(new char[num_of_cols]).replace("\0", "_");
					line += columns + "|";
					curr_col += num_of_cols + 1;
					
					target_col = col_queue.remove();
					num_of_cols = (target_col-1) - curr_col;
					//System.out.print("Number of columns: ");
					//System.out.println(num_of_cols);
					columns = new String(new char[num_of_cols]).replace("\0", "_");
					line += columns;
					curr_col += num_of_cols;
					col_queue.add(target_col);
				}
				System.out.println(line);
				
				//Line 3/3 of the set
				line = "";
				curr_col = 0;
				int num_of_cols;
				for (int j = 0; j < operator_counter*2; j++) {
					target_col = col_queue.remove();
					num_of_cols = (target_col-1) - curr_col;
					//System.out.print("Target: ");
					//System.out.println(target_col);
					//System.out.print("Number of columns: ");
					//System.out.println(num_of_cols);
					String columns = new String(new char[num_of_cols]).replace("\0", " ");
					line += columns + "|";
					curr_col += num_of_cols + 1;
					col_queue.add(target_col);
				}
				System.out.println(line);
			}
		}
	
	
	public static void main(String[] args) {
		ExpTree tree = new ExpTree();
		tree.fill("+ * + * 8 7 4 5 * + 2 2 * 3 7");
		
		System.out.println(tree);
		//System.out.println(tree.eval());
		
		//String prefix = tree.toPrefix();
		//String postfix = tree.toPostfix();
		//String infix = tree.toInfix();
		
		//System.out.println(prefix);
		//System.out.println(postfix);
		//System.out.println(infix);
		tree.drawTree();
	}

}