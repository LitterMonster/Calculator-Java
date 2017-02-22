/**
 ***********************************************************************
 * $Id: Calculator.java 2017-01-02 09:43:12Z ���� $
 ***********************************************************************
 */

import java.math.BigDecimal;
import java.util.ArrayList;

public class Calculator {
//======================================================================
// [����] ��̬�ĳ�Ա������������ʾ��������
public static final String SCI = "Scientific";	// ��ѧ������
public static final String STD = "Standard";	// ��׼��
public static final String GRP = "Digit Grouping"; //�����ö��ŷָ�

public static final String HEX = "Hex";			// ʮ������
public static final String DEC = "Dec";			// ʮ����
public static final String OCT = "Oct";			// �˽���
public static final String BIN = "Bin";			// ������

public static final String MCX = "MC";			// Memory Clear
public static final String MRX = "MR";			// Memory Recall
public static final String MSX = "MS";			// Memory Store
public static final String MPX = "M+";			// Memory Add
public static final String MMX = "M-";			// Memory Subtract

public static final String XXX = null;			// Reserved
public static final String NLG = "ln";			// ��Ȼ����
public static final String BR1 = "(";			// ������
public static final String BR2 = ")";			// ������
public static final String BSP = "\u2190";		// �ո�
public static final String CEX = "CE";			// �������
public static final String CXX = " C ";			// ����

public static final String SIN = "sin";			// Sin����
public static final String COS = "cos";			// Cos����
public static final String TAN = "tan";			// Tan����
public static final String NEG = "\u00b1";		// ������������ַ�
public static final String REC = "1/x";			// ����
public static final String INT = "int";			// ȡ��
public static final String LOG = "log";			// ����
public static final String PWT = "10\u02E3";	// 10��X����
public static final String F2E = "F-E";			// ��ϸ��ָ��
public static final String EXP = "Exp";			// ָ��

public static final String SRT = "\u221a";		// ƽ����
public static final String CRT = "\u221bx";		// ������
public static final String SQR = "x\u00b2";		// ƽ��
public static final String CUB = "x\u00b3";		// ����
public static final String FCT = "n!";			// �׳�
public static final String PER = "%";			// �ٷֱ�
public static final String DMS = "dms";			// DMS
public static final String POW = "x\u02B8";		// x��y����

public static final String DG0 = "0";			// ���� 0
public static final String DG1 = "1";			// ���� 1
public static final String DG2 = "2";			// ���� 2
public static final String DG3 = "3";			// ���� 3
public static final String DG4 = "4";			// ���� 4
public static final String DG5 = "5";			// ���� 5
public static final String DG6 = "6";			// ���� 6
public static final String DG7 = "7";			// ���� 7
public static final String DG8 = "8";			// ���� 8
public static final String DG9 = "9";			// ���� 9
public static final String DOT = ".";			// С����

public static final String DGA = "A";			// ʮ�������� A
public static final String DGB = "B";			// ʮ�������� B
public static final String DGC = "C";			// ʮ�������� C
public static final String DGD = "D";			// ʮ�������� D
public static final String DGE = "E";			// ʮ�������� E
public static final String DGF = "F";			// ʮ�������� F

public static final String MUL = "\u00D7";		// ��
public static final String DIV = "\u00F7";		// ��
public static final String MOD = "mod";			// ȡ��
public static final String ADD = "\u002B";		// ��
public static final String SUB = "\u2212";		// ��
public static final String EQU = "\u003D";		// ����

public static final String PIX = "\u03C0";		// ������
public static final String AVG = "\u03BC";		// ͳ��ƽ��ֵ
public static final String SUM = "\u2211";		// ͳ�����
public static final String LST = "lst";			// ͳ���ۼ�
public static final String CLS = "clr";			// ����б�

//======================================================================
// [����] ��ʵ����������.
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
// [����] ����Ĺ��췽��.
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
// [����] ���³�ʼ������������.
private void initFields() {
	clear();
	prmScreenText = "0";
	clearPrmScreen = true;
	hasError = false;
	lastKey = null;
}

//======================================================================
// [����] ����һ��boolean������������Ƿ�ΪС��.
private boolean isDigit(String val) {
	return val == DG0 || val == DG1 || val == DG2 || val == DG3
		|| val == DG4 || val == DG5 || val == DG6 || val == DG7
		|| val == DG8 || val == DG9 || val == DGA || val == DGB
		|| val == DGC || val == DGD || val == DGE || val == DGF;
}

//======================================================================
// [����] ����һ��boolean��������������Ƿ��Ѿ���һ���洢�˵�ֵ
public boolean hasMemValue() {
	return memValue != "0";
}

//======================================================================
// [����] �������ּ��е���Ŀ��.
public int getSetSize() {
	return nset.size();
}

//======================================================================
// [����] ���ظ�����Ļ���ı�.
public String getSecScreenText() {
	return expr[1] + (lastKey == EQU ? " =" : "");
}

//======================================================================
// [F����UNC] ��������Ļ���ı�.
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
// [����] ��BigDecimalת��Ϊһ������
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
// [����] ������ֵת��ΪBigDecimal.
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
// [����] ��ʮ����ֵת��Ϊ����ֵ.
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
// [����] ������ֵת��Ϊʮ����.
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
// [����] ���ط�����ı�.
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
// [����] ��������!
private static void beep() {
	java.awt.Toolkit.getDefaultToolkit().beep();
}

//======================================================================
// [����] ������ʽ��ջ�е�������Ŀ.
private void clear() {
	for (int i = 0; i < expr.length; i++)
		while (expr[i].hasItems())
			expr[i].pop();
}

//======================================================================
// [����] ������Ŀ���͵����ʽ��ջ.
private void push(Object obj) {
	expr[1].push(obj);
	if (obj instanceof String) {
		expr[0].push(rad2dec((String) obj, numMode));
	} else expr[0].push(obj);
}

//======================================================================
// [����] �ӱ��ʽ��ջ�е���һ����Ŀ.
private void pop() {
	expr[0].pop();
	expr[1].pop();
}

//======================================================================
// [����] ��ʮ�����ַ����а���β�����.
private String stripZeros(String s) {
	if (s.indexOf(".") >= 0)
	while (s.length() > 1 && (s.endsWith("0") || s.endsWith(".")))
		s = s.substring(0, s.length() - 1);
	return s;
}

//======================================================================
// [����] ���û��׳�������Ϣ.
private void throwError(String msg) {
	prmScreenText = msg;
	hasError = true;
	beep();
}

//======================================================================
// [����] �����û��������.
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
