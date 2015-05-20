package com.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.dao.DeptDao;
import com.po.Dept;

public class DeptDaoImpl extends HibernateDaoSupport implements DeptDao{
	public List<Dept> selAll() {
		return this.getHibernateTemplate().find("from Dept");
	}

}
