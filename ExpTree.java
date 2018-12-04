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

		//prints a string of the nodes in the tree
		public String toString() {
			return stringify(root);
		}

		private String stringify(ExprNode node) {
			if (node == null) return "";
			return node.toString() + " " + stringify(node.left) + stringify(node.right);
		}

		//Fills the tree with operators and values given an expression in prefix notation
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
			}  
			
			//c should be a number at this point. If not, input is bad
			if (!(c >= '0' && c <= '9')) {
				System.out.println("Improper input given");
				System.exit(0);
			}
	
			//c is a number
			String value = "";	//Place characters into a string for multi-digit numbers
			value += c;
			
			//Look for more digits
			while (i.val < prefixExpr.length()) {
				c = prefixExpr.charAt(i.val);
				if (c >= '0' && c <= '9') {
					value += c;
					i.val++;
				} else { break; }
			}
			return new ExprNode(Integer.parseInt(value));
		}

		//Fills values of non-leaf nodes
		public int eval() {
			int result = eval(root);
			if (result < 0){ System.out.println("Result is negative, values may have overflowed.");}	//Notify user as to overflow
			return result; 
			}

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
		
		public String notate(String form) {
			
			//Create a queue and string to store the result in
			Queue<String> q = new LinkedList<>();
			String result;
			
			//Determine notation and fill queue with private method
			if (form.toLowerCase().startsWith("in")){
				result = "Infix notation: ";
				toInfix(root, q);
			} else if (form.toLowerCase().startsWith("pre")) {
				result = "Prefix notation: ";
				toPrefix(root, q);
			} else {
				result = "Postfix notation: ";
				toPostfix(root, q);
			}
			
			//take items from queue and add them to the string
			while (q.size() > 0) {
				if (form.toLowerCase().startsWith("in")){
					result += q.remove();					//No extra space included for infix notation
				} else {
					result += q.remove() + " ";				//Space out elements in prefix/postfix
				}
			}

			return result;
		}
	
		private void toInfix(ExprNode node, Queue<String> queue) {
			//For infix, first add left child to queue, then self, then right child
			
			//Handle base case
			if ( node == null ) return;
			
			//If node is not a leaf, add parenthesis and call recursive method
			if (node.left != null || node.right != null) {
				queue.add("(");
				toInfix(node.left, queue);
			}
			
			//Add self to the queue
			if (node.op == ' ') {
				queue.add(Integer.toString(node.val));
			} else {
				queue.add(Character.toString(node.op));
			}
			
			//If node is not a leaf, add parenthesis and call recursive method
			if (node.left != null || node.right != null) {
				toInfix(node.right, queue);
				queue.add(")");
			}
		}
		
		private void toPostfix(ExprNode node, Queue<String> queue) {
			//For postfix, first add left child to queue, then right child, then self
			
			//Handle base case
			if ( node == null ) return;
			
			//Recursive call to child nodes
			toPostfix(node.left, queue);
			toPostfix(node.right, queue);
			
			//Add self to queue
			if (node.op == ' ') {
				queue.add(Integer.toString(node.val));
			} else {
				queue.add(Character.toString(node.op));
			}
			
			
		}
		
		private void toPrefix(ExprNode node, Queue<String> queue) {
			//For prefix, first add self to queue, then left child, then right child
			
			//Handle base case
			if ( node == null ) return;
			
			//Add self to queue
			if (node.op == ' ') {
				queue.add(Integer.toString(node.val));
			} else {
				queue.add(Character.toString(node.op));
			}
			
			//Recursive call to child nodes
			toPrefix(node.left, queue);
			toPrefix(node.right, queue);
			
		}
		
		//Returns height of tree
		private int getTreeHeight(ExprNode root) {
			
			//Handle base case
			if (root == null) { return 0; }
			
			//Recursive call to child nodes
			int h_left = getTreeHeight(root.left);
			int h_right = getTreeHeight(root.right);
			
			//Choose larger height of child nodes and add one for self
			int height = Math.max(h_left, h_right) + 1;
			
			//return the value
			return height;
			
		}
		
		public void drawTree() {
			//https://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string-in-java
			eval(); //Make sure that the tree is filled out.
			
			//Get tree height for error prevention
			int tree_height = getTreeHeight(root);
			System.out.println("Height of Tree: " + tree_height);
			
			//Handle exceptions
			if (tree_height == 0) {
				System.out.println();
				return;
			}
			if (tree_height == 1) {
				System.out.println(root.val);
				return;
			}
			if (tree_height > 10) {
				System.out.println("Your expression tree is of height " + tree_height);
				System.out.println("Trees of over height 10 will not be displayed due to the large amount of space required.");
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
				
				int curr_queue_size = node_queue.size();
				for (int j = 0; j < curr_queue_size; j++) {
					
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
		
		//Get user input
		Scanner console_input = new Scanner(System.in);
		System.out.println("Please enter a prefix notation with integers");
		String user_expression = console_input.nextLine();
		
		//Create and fill tree
		ExpTree tree = new ExpTree();
		tree.fill(user_expression);
		
		//Draw the tree
		tree.drawTree();
		
		//Display expression in different notations
		System.out.println(tree.notate("Prefix"));
		System.out.println(tree.notate("Postfix"));
		System.out.println(tree.notate("Infix"));
		
		//Show answer to expression
		System.out.println("Solution: " + tree.eval());
	}

}