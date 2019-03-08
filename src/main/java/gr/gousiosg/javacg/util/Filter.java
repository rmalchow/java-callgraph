package gr.gousiosg.javacg.util;

public interface Filter {
	
	public boolean accept(Object o);
	public boolean include(Object o);

}
