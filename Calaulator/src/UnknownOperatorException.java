/**
 ***********************************************************************
 * $Id: UnknownOperatorException.java 2017-01-02 09:43:12Z 张涛 $
 ***********************************************************************
 */

public class UnknownOperatorException extends Exception {
//======================================================================
// [方法] 没有参数的主类构造函数.
public UnknownOperatorException() {
	super();
}

//======================================================================
// [方法] 构造函数接受异常消息作为参数.
public UnknownOperatorException(String msg) {
	super(msg);
}
}
