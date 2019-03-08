package gr.gousiosg.javacg.output;

import java.io.OutputStream;
import java.util.List;

import gr.gousiosg.javacg.info.ClassInfo;

public interface Renderer {
	
	public void write(List<ClassInfo> cis, OutputStream os);
	

}
