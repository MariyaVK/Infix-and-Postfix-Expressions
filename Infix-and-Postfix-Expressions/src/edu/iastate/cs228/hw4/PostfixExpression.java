package edu.iastate.cs228.hw4;

/**
 *  
 * @author Mariya Karasseva mariyak
 *
 */

/**
 * 
 * This class evaluates a postfix expression using one stack.    
 *
 */

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class PostfixExpression extends Expression {
	private int leftOperand; // left operand for the current evaluation step
	private int rightOperand; // right operand (or the only operand in the case of
								// a unary minus) for the current evaluation step

	private PureStack<Integer> operandStack; // stack of operands

	/**
	 * Constructor stores the input postfix string and initializes the operand
	 * stack.
	 * 
	 * @param st     input postfix string.
	 * @param varTbl hash map that stores variables from the postfix string and
	 *               their values.
	 */
	public PostfixExpression(String st, HashMap<Character, Integer> varTbl) {
		super(st, varTbl);
		operandStack = new ArrayBasedStack<Integer>();
	}

	/**
	 * Constructor supplies a default hash map.
	 * 
	 * @param s
	 */
	public PostfixExpression(String s) {
		super(s);
		operandStack = new ArrayBasedStack<Integer>();
	}

	/**
	 * Outputs the postfix expression according to the format in the project
	 * description.
	 */
	@Override
	public String toString() {
		return removeExtraSpaces(postfixExpression);
	}

	/**
	 * Resets the postfix expression.
	 * 
	 * @param st
	 */
	public void resetPostfix(String st) {
		postfixExpression = st;
	}

	/**
	 * Scan the postfixExpression and carry out the following:
	 * 
	 * 1. Whenever an integer is encountered, push it onto operandStack. 2. Whenever
	 * a binary (unary) operator is encountered, invoke it on the two (one) elements
	 * popped from operandStack, and push the result back onto the stack. 3. On
	 * encountering a character that is not a digit, an operator, or a blank space,
	 * stop the evaluation.
	 * 
	 * @return value of the postfix expression
	 * @throws ExpressionFormatException with one of the messages below:
	 * 
	 *                                   -- "Invalid character" if encountering a
	 *                                   character that is not a digit, an operator
	 *                                   or a whitespace (blank, tab); -- "Too many
	 *                                   operands" if operandStack is non-empty at
	 *                                   the end of evaluation; -- "Too many
	 *                                   operators" if getOperands() throws
	 *                                   NoSuchElementException; -- "Divide by zero"
	 *                                   if division or modulo is the current
	 *                                   operation and rightOperand == 0; -- "0^0"
	 *                                   if the current operation is "^" and
	 *                                   leftOperand == 0 and rightOperand == 0; --
	 *                                   self-defined message if the error is not
	 *                                   one of the above.
	 * 
	 *                                   UnassignedVariableException if the operand
	 *                                   as a variable does not have a value stored
	 *                                   in the hash map. In this case, the
	 *                                   exception is thrown with the message
	 * 
	 *                                   -- "Variable <name> was not assigned a
	 *                                   value", where <name> is the name of the
	 *                                   variable.
	 * 
	 */
	public int evaluate() throws ExpressionFormatException, UnassignedVariableException {
		Scanner sc = new Scanner(postfixExpression);
		while (sc.hasNext()) {
			String str = sc.next();
			if (isInt(str))
				operandStack.push(Integer.parseInt(str));
			else {
				char ch = str.charAt(0);
				if (isOperator(ch)) {
					try {
						getOperands(ch);
						operandStack.push(compute(ch));
					} catch (NoSuchElementException e) {
						throw new ExpressionFormatException("Too many operators");
					}
				} else if (isVariable(ch)) {
					if (varTable.containsKey(ch))
						operandStack.push((int)varTable.get(ch));
					else
						throw new UnassignedVariableException("Variable " + ch + " was not assigned a value");
				} else
					throw new ExpressionFormatException("Invalid character");
			}
		}
		int result = operandStack.pop();
		if (!operandStack.isEmpty()) throw new ExpressionFormatException("Too many operands");
		return result;
	}

	/**
	 * For unary operator, pops the right operand from operandStack, and assign it
	 * to rightOperand. The stack must have at least one entry. Otherwise, throws
	 * NoSuchElementException. For binary operator, pops the right and left operands
	 * from operandStack, and assign them to rightOperand and leftOperand,
	 * respectively. The stack must have at least two entries. Otherwise, throws
	 * NoSuchElementException.
	 * 
	 * @param op char operator for checking if it is binary or unary operator.
	 */
	private void getOperands(char op) throws NoSuchElementException {
		if (op == '~') {
			if (operandStack.isEmpty())
				throw new NoSuchElementException();
			else {
				rightOperand = operandStack.pop();
			}
		} else {
			if (operandStack.size() < 2)
				throw new NoSuchElementException();
			else {
				rightOperand = operandStack.pop();
				leftOperand = operandStack.pop();
			}
		}
	}

	/**
	 * Computes "leftOperand op rightOperand" or "op rightOperand" if a unary
	 * operator.
	 * 
	 * @param op operator that acts on leftOperand and rightOperand.
	 * @return returns the value obtained by computation.
	 * @throws ExpressionFormatException with one of the messages below: <br>
	 *                                   -- "Divide by zero" if division is the
	 *                                   current operation and rightOperand == 0;
	 *                                   <br>
	 *                                   -- "0^0" if the current operation is "^"
	 *                                   and leftOperand == 0 and rightOperand == 0.
	 */
	private int compute(char op) throws ExpressionFormatException {
		if (op == '~')
			return -rightOperand;
		if (op == '/' && rightOperand == 0)
			throw new ExpressionFormatException("Divide by zero");
		if (op == '^' && rightOperand == 0 && leftOperand == 0)
			throw new ExpressionFormatException("0^0");
		if (op == '+')
			return leftOperand + rightOperand;
		if (op == '-')
			return leftOperand - rightOperand;
		if (op == '*')
			return leftOperand * rightOperand;
		if (op == '/')
			return (int) (leftOperand / rightOperand);
		if (op == '%')
			return (int) (leftOperand % rightOperand);
		if (op == '^')
			return (int) Math.pow(leftOperand, rightOperand);
		// should never be executed
		return 0;
	}
}
