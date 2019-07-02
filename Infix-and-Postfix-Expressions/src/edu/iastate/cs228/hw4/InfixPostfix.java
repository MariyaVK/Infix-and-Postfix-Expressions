package edu.iastate.cs228.hw4;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *  
 * @author Mariya Karasseva mariyak
 *
 */

/**
 * 
 * This class evaluates input infix and postfix expressions. 
 *
 */

import java.util.HashMap;
import java.util.Scanner;

public class InfixPostfix {

	/**
	 * Repeatedly evaluates input infix and postfix expressions. See the project
	 * description for the input description. It constructs a HashMap object for
	 * each expression and passes it to the created InfixExpression or
	 * PostfixExpression object.
	 * 
	 * @param args
	 * @throws FileNotFoundException 
	 **/
	public static void main(String[] args) throws FileNotFoundException {
		Expression exp = null;
		System.out.println("Evaluation of Infix and Postfix Expressions");
		System.out.println("keys: 1 (standard input)   2 (file input)  3 (exit)");
		System.out.println("(Enter \"I\" before an infix expression, \"P\" before a postfix expression)");
		int trial = 1;
		System.out.print("Trial " + trial + ": ");
		Scanner sc = new Scanner(System.in);
		String key = sc.next();
		HashMap<Character, Integer> tbl = null;
		while (key.equals("1") || key.equals("2")) {
			if (key.equals("1")) {
				System.out.print("Expression: ");
				String IorP = sc.next();
				if (IorP.equals("I")) {
					exp = new InfixExpression(sc.nextLine());
					System.out.println("Infix form: " + exp.toString());
					System.out.println("Postfix form: " + ((InfixExpression) exp).postfixString());
				} else if (IorP.equals("P")) {
					exp = new PostfixExpression(sc.nextLine());
					System.out.println("Postfix form: " + exp.toString());
				} else
					System.out.println("(Enter \"I\" before an infix expression, \"P\" before a postfix expression)");

				Scanner sc1 = new Scanner(exp.toString());

				while (sc1.hasNext()) {
					char ch = sc1.next().charAt(0);
					if (exp.isVariable(ch)) {
						System.out.println("where");
						tbl = new HashMap<Character, Integer>();
						System.out.print(ch + " = ");
						int x = sc.nextInt();
						tbl.put(ch, x);
						break;
					}
				}
				while (sc1.hasNext()) {
					char ch = sc1.next().charAt(0);
					if (exp.isVariable(ch)) {
						System.out.print(ch + " = ");
						int x = sc.nextInt();
						tbl.put(ch, x);
					}
				}
				if (tbl != null) {
					exp.setVarTable(tbl);
				}
				try {
					System.out.print("Expression value: " + exp.evaluate());
				} catch (ExpressionFormatException | UnassignedVariableException e) {
					e.printStackTrace();
				}
			} else if (key.equals("2")) {
				System.out.println("Input from a file");
				System.out.print("Enter file name: ");
				String fileName = sc.next();
				File file = new File(fileName);
					Scanner fsc = new Scanner(file);

					Scanner scVar = new Scanner(file); // Scanner to scan through the variables later
					while (fsc.hasNextLine()) {
						String IorP = fsc.next();
						scVar.next();
						while (!IorP.equals("I") || !IorP.equals("P"))
							IorP = fsc.next();
						String expression = fsc.nextLine();
						if (IorP.equals("I")) {
							exp = new InfixExpression(expression);
							System.out.println("Infix form: " + exp.toString());
							System.out.println("Postfix form: " + ((InfixExpression) exp).postfixString());
						} else if (IorP.equals("P")) {
							exp = new PostfixExpression(expression);
							System.out.println("Postfix form: " + exp.toString());
						}

						while (scVar.hasNextLine()) {
							String s = scVar.nextLine();
							if (s.contains("I") || s.contains("P")) {
								exp.setVarTable(tbl);
								tbl.clear();
								break;
							}
							if (s.contains("=")) {
								Scanner var = new Scanner(s);
								if (var.hasNext()) {
									String st = var.next();
									if (exp.isVariable(st.charAt(0)) && var.hasNextInt()) {
										tbl.put(st.charAt(0), var.nextInt());
									}
								}
							}
						}

						Scanner sc1 = new Scanner(exp.toString());

						while (sc1.hasNext()) {
							char ch = sc1.next().charAt(0);
							if (exp.isVariable(ch)) {
								System.out.println("where");
								if (exp.varTable.containsKey(ch)) {
									System.out.print(ch + " = " + exp.varTable.get(ch));
								}
								break;
							}
						}
						while (sc1.hasNext()) {
							char ch = sc1.next().charAt(0);
							if (exp.isVariable(ch)) {
								if (exp.varTable.containsKey(ch)) {
									System.out.println("where");
									System.out.print(ch + " = " + exp.varTable.get(ch));
								}
							}
						}
						sc1.close();
						try {
							System.out.print("Expression value: " + exp.evaluate());
						} catch (ExpressionFormatException | UnassignedVariableException e) {
							e.printStackTrace();
						}
					}
					fsc.close();
					scVar.close();
			}
			System.out.println();
			System.out.println();
			System.out.println();
			trial++;
			System.out.print("Trial " + trial + ": ");
			key = sc.next();
		}
	}

	// helper methods if needed
}
