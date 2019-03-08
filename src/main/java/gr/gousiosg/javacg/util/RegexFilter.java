package gr.gousiosg.javacg.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class RegexFilter implements Filter {
	
	private List<String> includePatterns = new ArrayList<String>();
	private List<String> excludePatterns = new ArrayList<String>();

	public RegexFilter(List<String> includePatterns, List<String> excludePatterns) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
	}

	@Override
	public boolean accept(Object o) {
		if(o instanceof String) return true;
		if(o instanceof JavaClass) return true;
		if(o instanceof Method) return true;
		return false;
	}

	@Override
	public boolean include(Object o) {
		if(o instanceof String) {
			for(String ex : excludePatterns) {
				if(((String)o).matches(ex)) return false;
			}
			for(String in : includePatterns) {
				if(((String)o).matches(in)) return true;
			}
		} else if (o instanceof JavaClass) {
			return include(((JavaClass)o).getClassName());
		} else if (o instanceof Method) {
			return include(((Method)o).getClass().getCanonicalName());
		}
		return false;
	}

}
