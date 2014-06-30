package com.zdawn.commons.jdbc;

import java.util.ArrayList;


public class PageSerialization {
	private int total = 0;
	private ArrayList<?> rows = null;
	public PageSerialization(PageDataSet<?> dataSet){
		this.total = dataSet.getTotal();
		this.rows = dataSet;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public ArrayList<?> getRows() {
		return rows;
	}
	public void setRows(ArrayList<?> rows) {
		this.rows = rows;
	}
	
}
