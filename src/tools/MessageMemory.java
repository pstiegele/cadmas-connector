package tools;

import java.util.ArrayList;

public class MessageMemory<T> extends ArrayList<T> {
	
	public MessageMemory(){
		super();
	}
	
	public MessageMemory(int size) {
		super(size);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2625617557391637807L;
	T newestElement;
	
	public boolean add(T element) {
		super.add(element);
		newestElement=element;
		return true;
	}
	public T getNewestElement() {
		return newestElement;
	}


}
