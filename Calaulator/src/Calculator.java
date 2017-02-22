/**
 ***********************************************************************
 * $Id: Calculator.java 2017-01-02 09:43:12Z 张涛 $
 ***********************************************************************
 */

import java.math.BigDecimal;
import java.util.ArrayList;

public class Calculator {
//======================================================================
// [数据] 静态的成员常量，用于显示窗体内容
public static final String SCI = "Scientific";	// 科学计算型
public static final String STD = "Standard";	// 标准型
public static final String GRP = "Digit Grouping"; //数字用逗号分隔

public static final String HEX = "Hex";			// 十六进制
public static final String DEC = "Dec";			// 十进制
public static final String OCT = "Oct";			// 八进制
public static final String BIN = "Bin";			// 二进制

public static final String MCX = "MC";			// Memory Clear
public static final String MRX = "MR";			// Memory Recall
public static final String MSX = "MS";			// Memory Store
public static final String MPX = "M+";			// Memory Add
public static final String MMX = "M-";			// Memory Subtract

public static final String XXX = null;			// Reserved
public static final String NLG = "ln";			// 自然对数
public static final String BR1 = "(";			// 开括号
public static final String BR2 = ")";			// 闭括号
public static final String BSP = "\u2190";		// 空格
public static final String CEX = "CE";			// 清除输入
public static final String CXX = " C ";			// 清屏

public static final String SIN = "sin";			// Sin函数
public static final String COS = "cos";			// Cos函数
public static final String TAN = "tan";			// Tan函数
public static final String NEG = "\u00b1";		// 输入±的特殊字符
public static final String REC = "1/x";			// 倒数
public static final String INT = "int";			// 取整
public static final String LOG = "log";			// 对数
public static final String PWT = "10\u02E3";	// 10的X次幂
public static final String F2E = "F-E";			// 精细到指数
public static final String EXP = "Exp";			// 指数

public static final String SRT = "\u221a";		// 平方根
public static final String CRT = "\u221bx";		// 立方根
public static final String SQR = "x\u00b2";		// 平方
public static final String CUB = "x\u00b3";		// 立方
public static final String FCT = "n!";			// 阶乘
public static final String PER = "%";			// 百分比
public static final String DMS = "dms";			// DMS
public static final String POW = "x\u02B8";		// x的y次幂

public static final String DG0 = "0";			// 数字 0
public static final String DG1 = "1";			// 数字 1
public static final String DG2 = "2";			// 数字 2
public static final String DG3 = "3";			// 数字 3
public static final String DG4 = "4";			// 数字 4
public static final String DG5 = "5";			// 数字 5
public static final String DG6 = "6";			// 数字 6
public static final String DG7 = "7";			// 数字 7
public static final String DG8 = "8";			// 数字 8
public static final String DG9 = "9";			// 数字 9
public static final String DOT = ".";			// 小数点

public static final String DGA = "A";			// 十六进制数 A
public static final String DGB = "B";			// 十六进制数 B
public static final String DGC = "C";			// 十六进制数 C
public static final String DGD = "D";			// 十六进制数 D
public static final String DGE = "E";			// 十六进制数 E
public static final String DGF = "F";			// 十六进制数 F

public static final String MUL = "\u00D7";		// 乘
public static final String DIV = "\u00F7";		// 除
public static final String MOD = "mod";			// 取余
public static final String ADD = "\u002B";		// 加
public static final String SUB = "\u2212";		// 减
public static final String EQU = "\u003D";		// 等于

public static final String PIX = "\u03C0";		// 常量π
public static final String AVG = "\u03BC";		// 统计平均值
public static final String SUM = "\u2211";		// 统计求和
public static final String LST = "lst";			// 统计累加
public static final String CLS = "clr";			// 清楚列表

//======================================================================
// [数据] 类实例的数据域.
private Expression[] expr = null;
private String memValue = null;
private String prmScreenText = null;
private boolean clearPrmScreen = false;
private boolean groupDigits = false;
private boolean hasError = false;
private String numMode = null;
private String lastKey = null;
private ArrayList nset = null;

//======================================================================
// [方法] 主类的构造方法.
public Calculator() {
	expr = new Expression[2];
	nset = new ArrayList();
	for (int i = 0; i < expr.length; i++)
		expr[i] = new Expression();
	numMode = DEC;
	memValue = "0";
	initFields();
}

//======================================================================
// [方法] 重新初始化各种数据域.
private void initFields() {
	clear();
	prmScreenText = "0";
	clearPrmScreen = true;
	hasError = false;
	lastKey = null;
}

//======================================================================
// [方法] 返回一个boolean表明这个参数是否为小数.
private boolean isDigit(String val) {
	return val == DG0 || val == DG1 || val == DG2 || val == DG3
		|| val == DG4 || val == DG5 || val == DG6 || val == DG7
		|| val == DG8 || val == DG9 || val == DGA || val == DGB
		|| val == DGC || val == DGD || val == DGE || val == DGF;
}

//======================================================================
// [方法] 返回一个boolean表明这个计算器是否已经有一个存储了的值
public boolean hasMemValue() {
	return memValue != "0";
}

//======================================================================
// [方法] 返回数字集中的项目数.
public int getSetSize() {
	return nset.size();
}

//======================================================================
// [方法] 返回辅助屏幕的文本.
public String getSecScreenText() {
	return expr[1] + (lastKey == EQU ? " =" : "");
}

//======================================================================
// [F方法UNC] 返回主屏幕的文本.
public String getPrmScreenText() {
	if (!hasError && groupDigits) {
		if (numMode == BIN || numMode == HEX)
			return groupText(prmScreenText, 4, " ");
		else if (numMode == OCT)
			return groupText(prmScreenText, 3, " ");
		else return groupText(prmScreenText, 3, ",");
	}
	return prmScreenText;
}

//======================================================================
// [方法] 将BigDecimal转换为一个基数
private static String dec2rad(String str, BigDecimal rad) {
	BigDecimal bd = new BigDecimal(str);
	String ret = "";
	int rem = 0;
	bd = bd.setScale(0, BigDecimal.ROUND_DOWN);
	while (bd.compareTo(BigDecimal.ZERO) > 0) {
		rem = bd.remainder(rad).intValue();
		if (rem >= 0 && rem <= 9)
			ret = (char) (rem + '0') + ret;
		else if (rem >= 10)
			ret = (char) (rem + 'A' - 10) + ret;
		bd = bd.divide(rad, 0, BigDecimal.ROUND_DOWN);
	}
	return ret == "" ? "0" : ret;
}

//======================================================================
// [方法] 将基数值转换为BigDecimal.
private static String rad2dec(String str, BigDecimal rad) {
	BigDecimal bd = new BigDecimal(0);
	int chr = 0;
	
	for (int i = str.length() - 1, p = 0; i >= 0; i--, p++) {
		chr = Character.toUpperCase(str.charAt(i));
		if (chr >= '0' && chr <= '9')
			bd = bd.add(rad.pow(p).multiply(new BigDecimal(chr - '0')));
		else if (chr >= 'A' && chr <= 'Z')
			bd = bd.add(rad.pow(p).multiply(new BigDecimal(chr - 'A' + 10)));
		else p--;	// Ignore other characters as if they weren't there ;)
	}
	
	return bd.toPlainString();
}

//======================================================================
// [方法] 将十进制值转换为基数值.
private static String dec2rad(String str, String mode) {
	if (mode == BIN)
		return dec2rad(str, new BigDecimal(2));
	else if (mode == OCT)
		return dec2rad(str, new BigDecimal(8));
	else if (mode == HEX)
		return dec2rad(str, new BigDecimal(16));
	return str;
}

//======================================================================
// [方法] 将基数值转换为十进制.
private static String rad2dec(String str, String mode) {
	if (mode == BIN)
		return rad2dec(str, new BigDecimal(2));
	else if (mode == OCT)
		return rad2dec(str, new BigDecimal(8));
	else if (mode == HEX)
		return rad2dec(str, new BigDecimal(16));
	return str;
}

//======================================================================
// [方法] 返回分组的文本.
private static String groupText(String str, int cnt, String sep) {
	String ret = "";
	int i = 0, a = 0, z = str.lastIndexOf(".");
	if (z <= 0) z = str.length();
	else ret = str.substring(z);
	if (str.length() > 0 && str.charAt(0) == '-') a++;
	
	for (i = z - cnt; i > a; i -= cnt)
		ret = sep + str.substring(i, i + cnt) + ret;
	
	return str.substring(0, i + cnt) + ret;
}

//======================================================================
// [方法] 弹出窗口!
private static void beep() {
	java.awt.Toolkit.getDefaultToolkit().beep();
}

//======================================================================
// [方法] 清除表达式堆栈中的所有项目.
private void clear() {
	for (int i = 0; i < expr.length; i++)
		while (expr[i].hasItems())
			expr[i].pop();
}

//======================================================================
// [方法] 将新项目推送到表达式堆栈.
private void push(Object obj) {
	expr[1].push(obj);
	if (obj instanceof String) {
		expr[0].push(rad2dec((String) obj, numMode));
	} else expr[0].push(obj);
}

//======================================================================
// [方法] 从表达式堆栈中弹出一个项目.
private void pop() {
	expr[0].pop();
	expr[1].pop();
}

//======================================================================
// [方法] 从十进制字符串中剥离尾随的零.
private String stripZeros(String s) {
	if (s.indexOf(".") >= 0)
	while (s.length() > 1 && (s.endsWith("0") || s.endsWith(".")))
		s = s.substring(0, s.length() - 1);
	return s;
}

//======================================================================
// [方法] 向用户抛出错误消息.
private void throwError(String msg) {
	prmScreenText = msg;
	hasError = true;
	beep();
}

//======================================================================
// [方法] 接受用户的输入键.
public void inputKey(String key) {
	if (hasError) {
		if (key == CXX || key == GRP) {}
		else if (key == HEX || key == DEC || key == OCT || key == BIN)
			initFields();
		else { beep(); return; }
	} else if (lastKey == EQU && key != BSP) {
		clear();
	}
	
	switch (key) {
		case GRP:
			groupDigits = !groupDigits;
			if (hasError) return;
			break;
		case BIN:
		case OCT:
		case DEC:
		case HEX:
			prmScreenText = dec2rad(rad2dec(prmScreenText, numMode), key);
			numMode = key;
			clearPrmScreen = true;
			break;
		case MCX:
			memValue = "0";
			clearPrmScreen = true;
			break;
		case MRX:
			if (memValue != "0")
				prmScreenText = dec2rad(memValue, numMode);
			else { prmScreenText = "0"; lastKey = DG0; return; }
			clearPrmScreen = true;
			break;
		case MSX:
			memValue = rad2dec(prmScreenText, numMode);
			clearPrmScreen = true;
			break;
		case MPX:
			memValue = new BigDecimal(rad2dec(prmScreenText, numMode)).add(
				new BigDecimal(memValue)).toPlainString();
			clearPrmScreen = true;
			break;
		case MMX:
			memValue = new BigDecimal(rad2dec(memValue, numMode)).subtract(
				new BigDecimal(prmScreenText)).toPlainString();
			clearPrmScreen = true;
			break;
		case BSP:
			if (clearPrmScreen) {
				beep(); return;
			} else if (prmScreenText.length() > 1)
				prmScreenText = prmScreenText.substring(0, prmScreenText.length() - 1);
			else if (prmScreenText != "0")
				prmScreenText = "0";
			else if (expr[0].hasItems())
				pop();
			else beep();
			break;
		case CEX:
			prmScreenText = "0";
			break;
		case CXX:
			this.initFields();
			break;
		case AVG:
			if (nset.size() > 0) {
				BigDecimal bd = BigDecimal.ZERO;
				for (int i = 0; i < nset.size(); i++)
					bd = bd.add((BigDecimal) nset.get(i));
				prmScreenText = dec2rad(bd.divide(new BigDecimal(nset.size()), 32,
					BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros().toPlainString(), numMode);
			} else throwError("Invalid Operation: Empty set");
			clearPrmScreen = true;
			break;
		case SUM:
			BigDecimal bd = BigDecimal.ZERO;
			for (int i = 0; i < nset.size(); i++)
				bd = bd.add((BigDecimal) nset.get(i));
			prmScreenText = dec2rad(bd.toPlainString(), numMode);
			clearPrmScreen = true;
			if (prmScreenText == "0") { lastKey = DG0; return; }
			break;
		case LST:
			if (prmScreenText != "0")
				nset.add(new BigDecimal(rad2dec(prmScreenText, numMode)));
			else beep();
			clearPrmScreen = true;
			break;
		case CLS:
			nset.clear();
			break;
		case DG0:
			if (clearPrmScreen) {
				prmScreenText = "0";
				clearPrmScreen = false;
			} else if (prmScreenText != "0")
				prmScreenText += key;
			else if (lastKey == DG0)
				beep();
			break;
		case DG1: case DG2: case DG3: case DG4: case DG5: case DG6: case DG7:
		case DG8: case DG9: case DGA: case DGB: case DGC: case DGD: case DGE:
		case DGF:
			if (clearPrmScreen || prmScreenText == DG0) {
				prmScreenText = key;
				clearPrmScreen = false;
			} else prmScreenText += key;
			break;
		case DOT:
			if (clearPrmScreen || prmScreenText == DG0) {
				prmScreenText = DG0 + DOT;
				clearPrmScreen = false;
			} else if (prmScreenText.indexOf(DOT) < 0)
				prmScreenText += key;
			else beep();
			break;
		case BR1: case BR2: case SRT: case CRT: case REC: case SQR: case CUB:
		case FCT: case SIN: case COS: case TAN: case LOG: case NLG: case INT:
		case NEG: case POW: case MUL: case DIV: case MOD: case ADD: case SUB:
			if (prmScreenText != "0" || isDigit(lastKey))
				push(prmScreenText);
			if (key.equals(BR1)) push(Expression.BRO);
			else if (key.equals(BR2)) push(Expression.BRC);
			else if (key.equals(SRT)) push(Expression.SRT);
			else if (key.equals(CRT)) push(Expression.CRT);
			else if (key.equals(REC)) push(Expression.REC);
			else if (key.equals(SQR)) push(Expression.SQR);
			else if (key.equals(CUB)) push(Expression.CUB);
			else if (key.equals(FCT)) push(Expression.FCT);
			else if (key.equals(SIN)) push(Expression.SIN);
			else if (key.equals(COS)) push(Expression.COS);
			else if (key.equals(TAN)) push(Expression.TAN);
			else if (key.equals(LOG)) push(Expression.LOG);
			else if (key.equals(NLG)) push(Expression.NLG);
			else if (key.equals(INT)) push(Expression.INT);
			else if (key.equals(NEG)) push(Expression.NEG);
			else if (key.equals(POW)) push(Expression.POW);
			else if (key.equals(MUL)) push(Expression.MUL);
			else if (key.equals(DIV)) push(Expression.DIV);
			else if (key.equals(MOD)) push(Expression.MOD);
			else if (key.equals(ADD)) push(Expression.ADD);
			else if (key.equals(SUB)) push(Expression.SUB);
			prmScreenText = "0";
			clearPrmScreen = false;
			break;
		case EQU:
			if (prmScreenText != "0" || isDigit(lastKey) || !expr[0].hasItems())
				push(prmScreenText);
			try {
				prmScreenText = stripZeros(expr[0].eval().toPlainString());
				if (numMode == BIN) prmScreenText = dec2rad(prmScreenText, BIN);
				else if (numMode == OCT) prmScreenText = dec2rad(prmScreenText, OCT);
				else if (numMode == HEX) prmScreenText = dec2rad(prmScreenText, HEX);
				clearPrmScreen = true;
			} catch (SyntaxErrorException e) {
				throwError("Syntax Error: " + e.getMessage());
			} catch (InvalidInputException e) {
				throwError("Input Error: " + e.getMessage());
			} catch (UnknownOperatorException e) {
				throwError("Unknown Operator: " + e.getMessage());
			} catch (ArithmeticException e) {
				throwError("Math Error: " + e.getMessage());
			} catch (Exception e) {
				throwError("Application Error: " + e.getMessage());
			}
			break;
	}
	
	lastKey = key;
}
}
