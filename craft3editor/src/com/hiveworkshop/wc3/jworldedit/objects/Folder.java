package com.hiveworkshop.wc3.jworldedit.objects;

import java.util.ArrayList;
import java.util.List;

public class Folder {
	String name;
	int count;
	List<Object> children = new ArrayList<Object>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void add(Object item) {
		
	}
}
