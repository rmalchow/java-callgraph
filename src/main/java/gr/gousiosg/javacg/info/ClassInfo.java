package gr.gousiosg.javacg.info;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {

	private String classname;
	private List<String> superclasses  = new ArrayList<>();
	
	private List<MethodInfo> methodInfos = new ArrayList<>();

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public List<MethodInfo> getMethodInfos() {
		return methodInfos;
	}

	public void setMethodInfos(List<MethodInfo> methodInfos) {
		this.methodInfos = methodInfos;
	}

	public List<String> getSuperclasses() {
		return superclasses;
	}

	public void setSuperclasses(List<String> superclasses) {
		this.superclasses = superclasses;
	}

	public void addMethod(MethodInfo mi) {
		methodInfos.add(mi);
	}
	
	
}
