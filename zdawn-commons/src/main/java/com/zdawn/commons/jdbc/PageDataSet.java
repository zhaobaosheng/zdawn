package com.zdawn.commons.jdbc;

import java.util.ArrayList;
import java.util.Collection;

public class PageDataSet<E> extends ArrayList<E>{
	private static final long serialVersionUID = 3412996726906619070L;
	
	public PageDataSet(){
	}
	
	public PageDataSet(int initialCapacity){
		super(initialCapacity);
	}
	
	public PageDataSet(Collection<? extends E> c){
		super(c);
	}
	private int total = 0;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
}
