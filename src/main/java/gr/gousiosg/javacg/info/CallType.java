package gr.gousiosg.javacg.info;

public enum CallType {

	invokevirtual("M"), invokeinterface("I"), invokespecial("O"), invokestatic("S"), invokedynamic("D");

	private String code;
	
	private CallType(String code) {
	}
	
	public String code() {
		return code;
	}
	
}
