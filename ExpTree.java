import java.util.Queue;
import java.util.Scanner;
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

		class Position {
			public int val;
			public int nextval() {
				return val + 1;
			}
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
		}
		
		private ExprNode fill(String prefixExpr, Position i) {
			
			//loop through until c is an operator or number
			char c = ' ';
			while (i.val < prefixExpr.length() && c != '+' && c != '*' && !(c >= '0' && c <= '9')) {
				c = prefixExpr.charAt(i.val);
				i.val++;
			}
			
			//end method once expression has been iterated through
			if (i.val > prefixExpr.length()) return null;

			//Create operator node connected to next two nodes
			if (c == '+' || c == '*') {
				return new ExprNode(c, fill(prefixExpr, i), fill(prefixExpr, i));
				
			//c is a number
			} else {
				String value = "";	//Place characters into a string for multi-digit numbers
				value += c;
				
				//Look for more digits
				while (i.val < prefixExpr.length()) {
					c = prefixExpr.charAt(i.val);
					
					//if (next_c == '.'){
						//do something
					//}
					
					if (c >= '0' && c <= '9') {
						value += c;
						i.val++;
					} else {
						break;
					}
				}
				return new ExprNode(Integer.parseInt(value));
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
				result = result + e;
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
			//https://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string-in-java
			eval(); //Make sure that the tree is filled out.
			int tree_height = getTreeHeight(root);
			System.out.println("Height: " + tree_height);
			
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
		

			//Print each level of the tree (each level prints three lines except final level)
			for (int i = 0; i < tree_height; i++) {
				
				//Line 1/3 of the set
				String line = "";			//Progressively assembled before being printed
				int operator_counter = 0;	//Records the number of nodes on line that are operators
				int target_col;				//Significant slots in the line string
				ExprNode curr_node;			//Current node being formatted in the line
				
				int potent_nodes = (int) (Math.pow(2, i)); 	//Possible amount of nodes that may exist at current depth
				for (int j = 0; j < potent_nodes; j++) {
					
					//Get the next node, skip iteration if null
					curr_node = node_queue.remove();
					if (curr_node == null) {
						continue;
					}
					target_col = col_queue.remove();

					//Prepare correct number of spaces for positioning
					int num_of_spaces = (target_col-1) - line.length();
					String spaces = new String(new char[num_of_spaces]).replace("\0", " ");
				
					//For nodes with no operator, just display their value
					if (curr_node.op == ' ') {
						line += spaces + curr_node.val;
						
					//For operator nodes, display their operator followed by their value
					} else {
						line += spaces + curr_node.op + " (" + curr_node.val + ")";
						
						//Prepare for next line
						int step =  (int) (starting_column / Math.pow(2, i+1));	//Horizontal distance branches to children must go
						col_queue.add(target_col - step);						//column where left child will be
						col_queue.add(target_col);								//column where current operator is
						col_queue.add(target_col + step);						//column where right child will be
						
						operator_counter += 1;									//Used to iterate correct number of times for line 2/3
						
					}
					//Add child nodes to queue for next loop
					node_queue.add(curr_node.left);
					node_queue.add(curr_node.right);
				}
				System.out.println(line);
				
				//Line 2/3 of the set
				line = "";
				for (int j = 0; j < operator_counter; j++) {
					
					//Fill spaces leading up to next parent/child connection
					target_col = col_queue.remove();										//Grab next target column
					int num_of_cols = target_col - line.length(); 							//include target column as a space
					String columns = new String(new char[num_of_cols]).replace("\0", " ");	//Make spaces up to and including target column
					line += columns;														//Add the spaces to the string
					col_queue.add(target_col);												//Put target column back in queue for next line
					
					//Draw left side of connection between parent/child node
					target_col = col_queue.remove();										//Grab next target column
					num_of_cols = (target_col-1) - line.length();							//Count columns up to target
					columns = new String(new char[num_of_cols]).replace("\0", "_");			//Prepare correct number of underscores
					line += columns + "|";													//Add underscores and the line up to parent
					
					//Draw right side of connection between parent/child node
					target_col = col_queue.remove();
					num_of_cols = (target_col-1) - line.length();
					columns = new String(new char[num_of_cols]).replace("\0", "_");
					line += columns;
					col_queue.add(target_col);
				}
				System.out.println(line);
				
				//Line 3/3 of the set
				//Place vertical lines above where the nodes will be in next line
				line = "";
				int num_of_cols;
				for (int j = 0; j < operator_counter*2; j++) {
					target_col = col_queue.remove();
					num_of_cols = (target_col-1) - line.length();
					String columns = new String(new char[num_of_cols]).replace("\0", " ");
					line += columns + "|";
					col_queue.add(target_col);
				}
				System.out.println(line);
			}
		}
	
	
	public static void main(String[] args) {
		
		Scanner console_input = new Scanner(System.in);
		System.out.println("Please enter a prefix notation with integers");
		String user_expression = console_input.nextLine();
		
		ExpTree tree = new ExpTree();
		//tree.fill("+ * + * 8 7 4 5 * + 2 2 * 3 7");
		tree.fill(user_expression);
		
		System.out.println(tree);
		tree.drawTree();
		
		String prefix = tree.toPrefix();
		String postfix = tree.toPostfix();
		String infix = tree.toInfix();
		
		System.out.println(prefix);
		System.out.println(postfix);
		System.out.println(infix);
	}

}