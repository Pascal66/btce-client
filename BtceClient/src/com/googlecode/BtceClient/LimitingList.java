package com.googlecode.BtceClient;

import java.util.LinkedList;

public class LimitingList<T> extends LinkedList {

	private final int maxSize;

	public LimitingList(int maxSize) {
		this.maxSize = maxSize < 0 ? 0 : maxSize;
	}

	@Override
	public boolean add(Object object) {
		// TODO Auto-generated method stub
		if (this.size() >= maxSize)
			this.remove();
		return super.add(object);
	}

}