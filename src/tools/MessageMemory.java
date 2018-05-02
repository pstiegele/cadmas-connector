package tools;

import java.util.ArrayList;

import com.sun.javafx.collections.ObservableListWrapper;

public class MessageMemory<T> extends ObservableListWrapper<T> {
	
	
	
	public MessageMemory(){
		super(new ArrayList<>());
	}
	
	
	
	/**
	 * 
	 */
	T newestElement;
	@Override
	public boolean add(T element) {
		super.add(element);
		newestElement=element;
		return true;
	}
	public T getNewestElement() {
		return newestElement;
	}

	@Override
	public T get(int index) {
		// TODO Auto-generated method stub
		return super.get(index);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return super.size();
	}


}
