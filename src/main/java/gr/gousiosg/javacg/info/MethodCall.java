package gr.gousiosg.javacg.info;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.Type;

public class MethodCall {
	
	private String callerSignature;
	private String caller;
	private String callerMethod; 

	private String calleeSignature;
	private CallType callType;
	private String callee;
	private String calleeMethod;
	private Type[] calleeArgumentTypes;
	private String argumentSignature;
	
	private JavaClass[] implementers;

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getCallerMethod() {
		return callerMethod;
	}

	public void setCallerMethod(String callerMethod) {
		this.callerMethod = callerMethod;
	}

	public String getCallee() {
		return callee;
	}

	public void setCallee(String callee) {
		this.callee = callee;
	}

	public String getCalleeMethod() {
		return calleeMethod;
	}

	public void setCalleeMethod(String calleeMethod) {
		this.calleeMethod = calleeMethod;
	}

	public Type[] getCalleeArgumentTypes() {
		return calleeArgumentTypes;
	}

	public void setCalleeArgumentTypes(Type[] calleeArgumentTypes) {
		this.calleeArgumentTypes = calleeArgumentTypes;
	}

	public JavaClass[] getImplementers() {
		return implementers;
	}

	public void setImplementers(JavaClass[] implementers) {
		this.implementers = implementers;
	}

	public CallType getCallType() {
		return callType;
	}

	public void setCallType(CallType callType) {
		this.callType = callType;
	}

	public String getArgumentSignature() {
		return argumentSignature;
	}

	public void setArgumentSignature(String argumentSignature) {
		this.argumentSignature = argumentSignature;
	}


}
