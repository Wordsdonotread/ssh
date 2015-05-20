package com.action;

import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.po.Dept;
import com.service.DeptService;

public class DeptAction {
	private List<Dept> list;
	private DeptService ds;
	private Dept dept;
	

	public List<Dept> getList() {
		return list;
	}


	public void setList(List<Dept> list) {
		this.list = list;
	}

	public Dept getDept() {
		return dept;
	}

	public void setDept(Dept dept) {
		this.dept = dept;
	}

	public void setDs(DeptService ds) {
		this.ds = ds;
	}
	
	public String selAll(){
		list=ds.selAll();
		ServletActionContext.getRequest().getSession().setAttribute("list", list);
		return "success";
	}
	
	
}
