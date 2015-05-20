package com.service.impl;

import java.util.List;

import com.dao.DeptDao;
import com.po.Dept;
import com.service.DeptService;

public class DeptServiceImpl implements DeptService{

	private DeptDao dd;
	public void setDd(DeptDao dd) {
		this.dd = dd;
	}
	public List<Dept> selAll() {
		return dd.selAll();
	}

}
