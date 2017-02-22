/**
 ***********************************************************************
 * $Id: Expression.java 329 2015-01-06 07:43:12Z mkwayisi $
 * ---------------------------------------------------------------------
 * Authored by Michael Kwayisi. Copyright (c) 2014. See license below.
 * Comments are appreciated - mic at kwayisi dot org | www.kwayisi.org
 * ---------------------------------------------------------------------
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are stringently met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions, and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions, and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *  3. The end-user documentation included with the redistribution,
 *     if any, must include the following acknowledgment:
 *       "This product includes software written by Michael Kwayisi."
 *     Alternately, this acknowledgment may appear in the software
 *     itself, if and wherever such 3rd-party acknowledgments appear.
 *  4. Neither the name of the software nor the name of its author
 *     and/or contributors may be used to endorse or promote products
 *     derived from this software without specific prior permission.
 ***********************************************************************
 */

import java.util.ArrayList;
import java.math.BigDecimal;

public class Expression {
//======================================================================
// [数据] 空格, 根, 指数 和 阶乘.
public static final byte BRO = 0x00, BRACKET_OPEN = BRO;
public static final byte BRC = 0x01, BRACKET_CLOSE = BRC;
public static final byte SRT = 0x02, SQUARE_ROOT = SRT;
public static final byte CRT = 0x03, CUBE_ROOT = CRT;
public static final byte REC = 0x04, RECIPROCAL = REC;
public static final byte SQR = 0x05, SQUARED = SQR;
public static final byte CUB = 0x06, CUBED = CUB;
public static final byte POW = 0x07, POWER = POW;
public static final byte FCT = 0x08, FACTORIAL = FCT;

//======================================================================
// [数据] 通常的数学函数.
public static final byte SIN = 0x10, SINE = SIN;
public static final byte COS = 0x11, COSINE = COS;
public static final byte TAN = 0x12, TANGENT = TAN;
public static final byte LOG = 0x13, LOGARITHM = LOG;
public static final byte NLG = 0x14, NATURAL_LOG = NLG;
public static final byte INT = 0x15, INTEGRAL = INT;
public static final byte NEG = 0x16, NEGATE = NEG;

//======================================================================
// [数据] 常见的二元操作符.
public static final byte MUL = 0x20, MULTIPLY = MUL;
public static final byte DIV = 0x21, DIVIDE = DIV;
public static final byte MOD = 0x22, MODULO = MOD;
public static final byte ADD = 0x23, ADDITION = ADD;
public static final byte SUB = 0x24, SUBTRACTION = SUB;

//======================================================================
// [数据] 类实例数据字段.
private ArrayList list = null;
private Expression parent = null;

//======================================================================
// [方法] 类的构造方法.
public Expression() {
	this(null);
}

//======================================================================
// [方法] 拿Expression对象自身作为参数的私有构造函数.
private Expression(Expression parent) {
	this.list = new ArrayList();
	this.parent = parent;
}

//======================================================================
// [方法] 返回一个布尔值，表示此表达式是否为
//嵌入在另一个表达式中.
private boolean hasParent() {
	return this.parent != null;
}

//======================================================================
// [方法] 返回此表达式的父表达式.
private Expression getParent() {
	return this.parent;
}

//======================================================================
// [方法] 返回一个布尔值，指示是否传递
//参数是一个运算符.
private static boolean isOperator(Object obj) {
	byte opr = obj instanceof Byte ? (byte) obj : -1;
	return (opr >= BRO && opr <= FCT) || (opr >= SIN && opr <= NEG) ||
		(opr >= MUL && opr <= SUB);
}

//======================================================================
// [方法] 返回一个布尔值，指示是否传递
//参数是一个操作数（现在只是BigDecimal.
private static boolean isOperand(Object obj) {
	return obj instanceof BigDecimal;
}

//======================================================================
// [方法] 返回一个布尔值，指示是否传递
//参数是一个表达式.
private static boolean isExpression(Object obj) {
	return obj instanceof Expression;
}

//======================================================================
// [方法] 计算并返回参数的阶乘.
private static BigDecimal factorial(BigDecimal n) {
	BigDecimal r = BigDecimal.ONE;
	while (n.compareTo(BigDecimal.ONE) > 0) {
		r = r.multiply(n);
		n = n.subtract(BigDecimal.ONE);
	}
	return r;
	//return n.compareTo(BigDecimal.ONE) > 0 ?
	//	factorial(n.subtract(BigDecimal.ONE)).multiply(n) : n;
}

//======================================================================
// [方法] 返回一个布尔值，表示是否有项目在
//内部栈.
public boolean hasItems() {
	return list.size() > 0;
}

//======================================================================
// [方法] 将新项目添加到内部列表。
//把它认为是一个堆栈 ;)
public Expression push(Object ... args) {
	for (Object obj : args)
		this.list.add(obj);
	return this;
}

//======================================================================
// [方法] 从内部堆栈中删除最后一个项目（如果有）.
public Object pop() {
	int index = list.size() - 1;
	return (index >= 0) ? list.remove(index) : null;
}

//======================================================================
// [方法] 评估并返回此表达式的结果.
public BigDecimal eval()
	throws SyntaxErrorException, InvalidInputException, UnknownOperatorException {
	Object obj = null;
	Expression curr = this;
	BigDecimal lhs = null, rhs = null;
	
	// STEP 0: Evaluate brackets to determine sub-expressions
	for (int i = 0; i < list.size(); i++) {
		obj = list.get(i);
		if (obj.equals(BRO)) {
			if (this.equals(curr)) {
				curr = new Expression(curr);
				list.set(i, curr);
				continue;
			} else {
				curr = new Expression(curr);
				curr.getParent().push(curr);
			}
		} else if (obj.equals(BRC)) {
			curr = curr.getParent();
			if (curr == null) break;
		} else if (this.equals(curr)) {
			if (!(isOperator(obj) || isExpression(obj)))
				list.set(i, new BigDecimal(obj.toString()));
			continue;
		} else curr.push(obj);
		list.remove(i--);
	}
	
	if (!this.equals(curr))
		throw new SyntaxErrorException("Unmatched brackets");
	
	// STEP 1: Translate SQR & CUB into POWs
	for (int i = 0; i < list.size(); i++) {
		obj = list.get(i);
		if (obj.equals(SQR) || obj.equals(CUB)) {
			list.set(i, new BigDecimal(obj.equals(SQR) ? 2 : 3));
			list.add(i, POW);
			i++;
		}
	}

	// STEP 2: Roots, powers, reciprocal and factorial.
	for (int i = 0; i < list.size(); i++) {
		obj = list.get(i);
		if (isOperator(obj)) switch ((byte) obj) {
			case SQUARE_ROOT:
			case CUBE_ROOT:
				obj = i + 1 < list.size() ? list.get(i + 1) : -1;
				if (obj.equals(SRT) || obj.equals(CRT)) continue;
				else if (isOperand(obj)) rhs = (BigDecimal) obj;
				else if (isExpression(obj)) rhs = ((Expression) obj).eval();
				else throw new SyntaxErrorException("Missing operand");
				if (rhs.compareTo(BigDecimal.ZERO) < 0)
					throw new ArithmeticException("Root of negative no.");
				rhs = new BigDecimal(list.get(i).equals(SRT) ?
					Math.sqrt(rhs.doubleValue()) : Math.cbrt(rhs.doubleValue()));
				list.set(i, rhs);
				list.remove(i + 1);
				i = Math.max(i - 2, -1);
				break;
				
			case POWER:
				obj = i + 2 < list.size() ? list.get(i + 2) : -1;
				if (obj.equals(POW)) continue;
				obj = i > 0 ? list.get(i - 1) : -1;
				if (isOperand(obj)) lhs = (BigDecimal) obj;
				else if (isExpression(obj)) lhs = ((Expression) obj).eval();
				else throw new SyntaxErrorException("Missing operand");
				obj = i + 1 < list.size() ? list.get(i + 1) : -1;
				if (isOperand(obj)) rhs = (BigDecimal) obj;
				else if (isExpression(obj)) rhs = ((Expression) obj).eval();
				else throw new SyntaxErrorException("Missing operand");
				if (rhs.compareTo(BigDecimal.ZERO) < 0)
					lhs = BigDecimal.ONE.divide(lhs.pow(rhs.abs().intValue()));
				else lhs = lhs.pow(rhs.intValue());
				list.set(i - 1, lhs);
				list.remove(i);
				list.remove(i);
				i = Math.max(i - 3, -1);
				break;
			
			case RECIPROCAL:
				obj = i > 0 ? list.get(i - 1) : -1;
				if (isOperand(obj)) lhs = (BigDecimal) obj;
				else if (isExpression(obj)) lhs = ((Expression) obj).eval();
				else throw new SyntaxErrorException("Missing operand");
				list.set(i - 1, BigDecimal.ONE.divide(lhs, 30, BigDecimal.ROUND_DOWN));
				list.remove(i);
				i -= 1;
				break;
				
			case FACTORIAL:
				obj = i > 0 ? list.get(i - 1) : -1;
				if (isOperand(obj)) lhs = (BigDecimal) obj;
				else if (isExpression(obj)) lhs = ((Expression) obj).eval();
				else throw new SyntaxErrorException("Missing operand");
				//if (lhs.compareTo(BigDecimal.ZERO) < 0)
				//	throw new InvalidInputException("Factorial input less than zero");
				//else if (lhs.compareTo(new BigDecimal(5000)) > 0)
				//	throw new InvalidInputException("Factorial input too large (>5000)");
				list.set(i - 1, factorial(lhs.setScale(0, BigDecimal.ROUND_DOWN)));
				list.remove(i);
				i -= 1;
				break;
		}
	}
	
	// STEP 3: Common mathematical functions.
	for (int i = list.size() - 1; i >= 0; i--) {
		obj = list.get(i);
		if (obj.equals(SIN) || obj.equals(COS) || obj.equals(TAN) ||
			obj.equals(LOG) || obj.equals(NLG) || obj.equals(INT) ||
			obj.equals(NEG))
		{
			obj = i + 1 < list.size() ? list.get(i + 1) : -1;
			if (isOperand(obj)) rhs = (BigDecimal) obj;
			else if (isExpression(obj)) rhs = ((Expression) obj).eval();
			else throw new SyntaxErrorException("Missing operand");
			switch ((byte) list.get(i)) {
				case SIN:
					rhs = new BigDecimal(Math.sin(Math.toRadians(rhs.doubleValue())));
					break;
				case COS:
					rhs = new BigDecimal(Math.cos(Math.toRadians(rhs.doubleValue())));
					break;
				case TAN:
					if (rhs.compareTo(new BigDecimal(90)) == 0)
						throw new ArithmeticException("Tangent 90");
					rhs = new BigDecimal(Math.tan(Math.toRadians(rhs.doubleValue())));
					break;
				case LOG: rhs = new BigDecimal(Math.log10(rhs.doubleValue())); break;
				case NLG: rhs = new BigDecimal(Math.log(rhs.doubleValue())); break;
				case INT: rhs = rhs.setScale(0, BigDecimal.ROUND_DOWN); break;
				case NEG: rhs = rhs.negate(); break;
				default: continue;
			}
			if (rhs.scale() > 15) rhs = rhs.setScale(15, BigDecimal.ROUND_HALF_EVEN);
			list.set(i, rhs);
			list.remove(i + 1);
		}
	}
	
	// STEP 4: Multiplicative and additive operations.
	for (int s = 0; s < 2; s++)
	for (int i = 0; i < list.size(); i++) {
		obj = list.get(i);
		if (s == 0 && (obj.equals(MUL) || obj.equals(DIV) || obj.equals(MOD)) ||
			s == 1 && (obj.equals(ADD) || obj.equals(SUB)))
		{
			obj = i > 0 ? list.get(i - 1) : -1;
			if (isOperand(obj)) lhs = (BigDecimal) obj;
			else if (isExpression(obj)) lhs = ((Expression) obj).eval();
			else throw new SyntaxErrorException("Missing operand");
			obj = i + 1 < list.size() ? list.get(i + 1) : -1;
			if (isOperand(obj)) rhs = (BigDecimal) obj;
			else if (isExpression(obj)) rhs = ((Expression) obj).eval();
			else throw new SyntaxErrorException("Missing operand");
			switch ((byte) list.get(i)) {
				case MUL: lhs = lhs.multiply(rhs); break;
				case DIV:
					if (rhs.compareTo(BigDecimal.ZERO) == 0)
						throw new ArithmeticException("Division by zero");
					lhs = lhs.divide(rhs, 30, BigDecimal.ROUND_DOWN);
					break;
				case MOD: lhs = lhs.remainder(rhs); break;
				case ADD: lhs = lhs.add(rhs); break;
				case SUB: lhs = lhs.subtract(rhs); break;
			}
			list.set(i - 1, lhs);
			list.remove(i);
			list.remove(i);
			i -= 1;
		} else if (isExpression(obj)) {
			list.set(i, rhs = ((Expression) obj).eval());
			obj = i > 0 ? list.get(i - 1) : -1;
			if (isOperand(obj)) {
				list.set(i - 1, rhs = rhs.multiply((BigDecimal) obj));
				list.remove(i);
				i -= 1;
			}
			obj = i + 1 < list.size() ? list.get(i + 1) : -1;
			if (isOperand(obj)) {
				list.set(i, rhs.multiply((BigDecimal) obj));
				list.remove(i + 1);
			}	
		}
	}

	// STEP 4: Multiply any remaining items. A cheap way to get my math right :)
	// For example, 2 sin 30 == 2 * sin 30
	while (list.size() > 1) {
		obj = list.get(0);
		if (isExpression(obj))
			lhs = ((Expression) obj).eval();
		else if (isOperand(obj))
			lhs = (BigDecimal) obj;
		else throw new UnknownOperatorException();
		obj = list.get(1);
		if (isExpression(obj))
			rhs = ((Expression) obj).eval();
		else if (isOperand(obj))
			rhs = (BigDecimal) obj;
		else throw new UnknownOperatorException();
		
		list.set(0, lhs.multiply(rhs));
		list.remove(1);
	}
	
	if (list.size() == 0)
		throw new SyntaxErrorException("Empty "
			+ (this.hasParent() ? "brackets" : "expression"));
	else if (isExpression(list.get(0)))
		list.set(0, ((Expression) list.get(0)).eval());
	
	lhs = (BigDecimal) list.get(0);
	if (lhs.scale() > 30) lhs = lhs.setScale(30, BigDecimal.ROUND_HALF_EVEN);
	return lhs.stripTrailingZeros();
}

//======================================================================
// [方法] 返回此表达式的字符串表示形式.
public String toString() {
	String ret = "";
	Object obj = null;
	
	for (int i = 0; i < list.size(); i++) {
		obj = list.get(i);
		if (obj.equals(BRO)) ret += "(";
		else if (obj.equals(BRC)) ret += ")";
		else if (isExpression(obj)) ret += "[" + obj + "]";
		
		else if (obj.equals(SRT)) ret += "\u221A";
		else if (obj.equals(CRT)) ret += "\u221B";
		else if (obj.equals(REC)) ret += "\u02C9\u00B9";
		else if (obj.equals(SQR)) ret += "\u00B2";
		else if (obj.equals(CUB)) ret += "\u00B3";
		else if (obj.equals(POW)) ret += " ^ ";
		else if (obj.equals(FCT)) ret += "!";
		
		else if (obj.equals(SIN)) ret += " sin";
		else if (obj.equals(COS)) ret += " cos";
		else if (obj.equals(TAN)) ret += " tan";
		else if (obj.equals(LOG)) ret += " log";
		else if (obj.equals(NLG)) ret += " ln";
		else if (obj.equals(INT)) ret += "\u222B";
		else if (obj.equals(NEG)) ret += "-";
		
		else if (obj.equals(MUL)) ret += " \u00D7 ";
		else if (obj.equals(DIV)) ret += " \u00F7 ";
		else if (obj.equals(MOD)) ret += " mod ";
		else if (obj.equals(ADD)) ret += " \u002B ";
		else if (obj.equals(SUB)) ret += " - ";
		else if (i > 0 && (list.get(i - 1).equals(SRT) ||
			list.get(i - 1).equals(CRT) || list.get(i - 1).equals(NEG)))
				ret += obj;
		else ret += " " + obj;
	}
	
	ret = ret.replaceAll("\\s\\s+", " ");
	ret = ret.replaceAll("\\(\\s+", "(");
	return ret.trim();
}
}
