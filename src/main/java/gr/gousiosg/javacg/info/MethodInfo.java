package gr.gousiosg.javacg.info;

import java.util.ArrayList;
import java.util.List;

public class MethodInfo {

	private String signature; 
	private String name; 
	
	private List<MethodCall> methodcalls = new ArrayList<>();

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MethodCall> getMethodcalls() {
		return methodcalls;
	}

	public void setMethodcalls(List<MethodCall> methodcalls) {
		this.methodcalls = methodcalls;
	}

	public void addCall(MethodCall mc) {
		methodcalls.add(mc);
	}
	
}
