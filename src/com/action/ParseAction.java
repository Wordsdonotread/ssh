package com.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.htmlparser.util.ParserException;
import org.springframework.web.bind.annotation.RequestMapping;

import util.FZParse;



public class ParseAction {
	private String tag;
	private String attrkey;
	private String attrvalue;
	private String url;
	private String con;
	private List<String> href;
	private String qianzhui;
	private List<String> tiqu;
	private String beforurl;
	private String afterurl;
	private int size;
	private String[] regex;
	private String[] type;
	private String ename;
	
	
	public String getEname() {
		return ename;
	}


	public void setEname(String ename) {
		this.ename = ename;
	}


	public String[] getType() {
		return type;
	}


	public void setType(String[] type) {
		this.type = type;
	}


	public String getBeforurl() {
		return beforurl;
	}


	public void setBeforurl(String beforurl) {
		this.beforurl = beforurl;
	}




	public String getAfterurl() {
		return afterurl;
	}


	public void setAfterurl(String afterurl) {
		this.afterurl = afterurl;
	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	public List<String> getTiqu() {
		return tiqu;
	}


	public void setTiqu(List<String> tiqu) {
		this.tiqu = tiqu;
	}


	public String getQianzhui() {
		return qianzhui;
	}


	public void setQianzhui(String qianzhui) {
		this.qianzhui = qianzhui;
	}


	public List<String> getHref() {
		return href;
	}


	public void setHref(List<String> href) {
		this.href = href;
	}
	
	public String[] getRegex() {
		return regex;
	}


	public void setRegex(String[] regex) {
		this.regex = regex;
	}


	public String getCon() {
		return con;
	}


	public void setCon(String con) {
		this.con = con;
	}
	private FZParse fz;
	public void setFz(FZParse fz) {
		this.fz = fz;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}


	public void setAttrkey(String attrkey) {
		this.attrkey = attrkey;
	}


	public void setAttrvalue(String attrvalue) {
		this.attrvalue = attrvalue;
	}
	@RequestMapping("/parse/zhuaqu")
	public String zhuaqu(){
		try {
			con=fz.getContent(url, tag, attrkey, attrvalue);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "zhuaqu";
	}
	@RequestMapping("/parse/lianjie")
	public String lianjie(){
		try {
			href=fz.getLinks(tag, attrkey, attrvalue, url, regex[0]);
			System.out.println(href);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> arr=new ArrayList<String>();
		if(qianzhui!=""&&qianzhui!=null){
			for (int i = 0; i < href.size(); i++) {
				arr.add(qianzhui+href.get(i));
			}
		}
		href=arr;
		return "zhuaqu";
	}
	
	public String fenyelianjie()throws org.htmlparser.util.ParserException{
		href=fz.listAllHref(beforurl,afterurl, regex[0], size, tag, attrkey, attrvalue);
		List<String> arr=new ArrayList<String>();
		if(qianzhui!=""&&qianzhui!=null){
			for (int i = 0; i < href.size(); i++) {
				arr.add(qianzhui+href.get(i));
			}
		}
		href=arr;
		return "zhuaqu";
	}
	@RequestMapping("/parse/tiqu")
	public String tiqu(){
		tiqu=new ArrayList<String>();
		for (int i = 0; i < 1; i++) {
			tiqu.add(FZParse.regexStr(con, regex[i]));
		}
		
		return "zhuaqu";
	}
	public String wancheng(){
		String hrefAll=href.get(0).replace("]","").replace("[","");
		System.out.println(hrefAll);
		String all[]=hrefAll.split(", ");
		List<String> list=new ArrayList<String>();
		for (int i = 0; i < all.length; i++) {
			list.add(all[i]);
		}
		try {
			fz.wancheng(list, tag, attrkey, attrvalue, regex,type,ename);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "zhuaqu";
	}
}
