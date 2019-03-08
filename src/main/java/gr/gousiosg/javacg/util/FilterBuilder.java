package gr.gousiosg.javacg.util;

import java.util.ArrayList;
import java.util.List;

public class FilterBuilder {

	private List<String> includePatterns = new ArrayList<String>();
	private List<String> excludePatterns = new ArrayList<String>();
	
	
	private FilterBuilder() {
	}

	public FilterBuilder include(String... includePattern) {
		for(String p : includePattern) {
			includePatterns.add(p);
		}
		return this;
	}
	
	public FilterBuilder exclude(String... excludePattern) {
		for(String p : excludePattern) {
			excludePatterns.add(p);
		}
		return this;
	}
	
	public Filter build() {
		return new RegexFilter(includePatterns, excludePatterns);
	}

	public static FilterBuilder get() {
		return new FilterBuilder();
	}
	
	
	
	
}
