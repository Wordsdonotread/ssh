package util;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;


public class FZParse extends Thread{
	static HttpURLConnection httpConn = null;
	static URL thisurl = null;
	public static String regexStr(String str,String regex)
	{
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while(m.find()){
			return m.group(1);
		}
		return null;
	}
	public static List<String> regexStrList(String str,String regex)
	{
		List<String> result = new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while(m.find()){
			result.add(m.group(1));
		}
		return result;
	}
	
	public static List<List<String>> regexStrListAll(String str,String regex)
	{
		List<List<String>> groupList = new ArrayList<List<String>>();
		List<String> innerList = new ArrayList<String>();
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while(m.find()){
			for(int i=1;i<m.groupCount();i++)
			{
				innerList.add(m.group(i));
			}
			groupList.add(innerList);

		}
		return groupList;
	}
	
	public static List<String> getLinks(String url, String rule) throws ParserException
	{
		Parser parser = new Parser();  
		parser.setURL(url);
		parser.setEncoding(CharsetAutoSwitch.dectedEncode(url)); 
		NodeFilter filter = new NodeClassFilter(LinkTag.class);
		NodeList list = parser.extractAllNodesThatMatch(filter);
		
		List<String> links=new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {  
			LinkTag node = (LinkTag) list.elementAt(i);
			String link=node.extractLink();
			link = regexStr(link, rule);
			if(link!=null && !link.trim().equals("") && !link.equals("#"))
			{
				links.add(link);
			}
		}
		
		return links;
	}
	
	public static String getTitle(String url) throws IOException, ParserException 
	{
		Parser parser=new Parser();
		parser.setURL(url);
		parser.setEncoding(CharsetAutoSwitch.dectedEncode(url)); 
		NodeFilter filter = new NodeClassFilter(TitleTag.class);
		NodeList  list = parser.extractAllNodesThatMatch(filter);
        String text = "";
        if(list.size() > 0)
        {
        	Node article = list.elementAt(0);
        	text = article.toPlainTextString();
		}
		return text.trim();
	}
	
	private static void processNodeList(NodeList list, String keyword) {
		//迭代开始
		SimpleNodeIterator iterator = list.elements();
		while (iterator.hasMoreNodes()) {
			Node node = iterator.nextNode();
			//得到该节点的子节点列表
			NodeList childList = node.getChildren();
			//孩子节点为空，说明是值节点
			if (null == childList)
			{
				//得到值节点的值
				String result = node.toPlainTextString();
				//若包含关键字，则简单打印出来文本
				if (result.indexOf(keyword) != -1)
					System.out.println(result);
			} //end if
			//孩子节点不为空，继续迭代该孩子节点
			else 
			{
				processNodeList(childList, keyword);
			}//end else
		}//end wile
	}
	
	public static String getContent(String url, String tag, String attrkey, String attrvalue) throws IOException, ParserException 
	{
		Parser parser=new Parser();
		parser.setURL(url);
		//设置目标网页的编码方式
		parser.setEncoding(CharsetAutoSwitch.dectedEncode(url)); 
		//创建正文节点的过滤器
		NodeFilter filter = new AndFilter(new TagNameFilter(tag), new HasAttributeFilter(attrkey, attrvalue));
		if(attrkey == null ||attrvalue == null ||attrkey.equals("")||attrvalue.equals(""))
		{
			filter = new TagNameFilter(tag);
		}
		NodeList  list = parser.extractAllNodesThatMatch(filter);

		//获取网页BODY内容
		String content = "";
        for(int i=0;i<list.size();i++)
        {
        	content += list.elementAt(i).toHtml();
        }
		return content;
	}
	
    public static String getPostResponse(List<HashMap<String, Object>> params) throws IOException {     
    	//ҳ����������
    	String urlstr = "http://www.huangye88.com/my/qiyeku_dwload/download.do";
    	
        URL url = new URL(urlstr);     
        URLConnection connection = url.openConnection();     
        connection.setDoOutput(true);    
        System.out.println( params.get(0).keySet().toString().substring(params.get(0).keySet().toString().indexOf("[")+1,params.get(0).keySet().toString().lastIndexOf("]")));
        String key = params.get(0).keySet().toString().substring(params.get(0).keySet().toString().indexOf("[")+1,params.get(0).keySet().toString().lastIndexOf("]"));
        String value = params.get(0).get(key).toString();
        String body = key+"="+value;
        for(int i = 1 ;i<params.size();i++)
        {
        	//key = params.get(i).keySet().toString().replace("[", "").replace("]", "");
        	key=params.get(i).keySet().toString().substring(params.get(i).keySet().toString().indexOf("[")+1,params.get(i).keySet().toString().lastIndexOf("]"));
        	value = params.get(i).get(key).toString();
            body += "&"+key+"="+value;
        }	
         
        
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "8859_1");
        out.write(body); 
        // remember to clean up     
        out.flush();
        out.close();
        
        String sCurrentLine;     
        String sTotalString;     
        sCurrentLine = "";     
        sTotalString = "";     
        InputStream l_urlStream;     
        l_urlStream = connection.getInputStream();     
        
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(     
                l_urlStream));     
        while ((sCurrentLine = l_reader.readLine()) != null) {     
            sTotalString += sCurrentLine + "/r/n";     
    
        }     
        sTotalString=sTotalString.replace('\\', ' ');
        sTotalString=sTotalString.replace(" ", "");
        //System.out.println(sTotalString);     
        return sTotalString;
        
    }   
    public static String getPostResponse(List<HashMap<String, Object>> params,String u) throws IOException {     
    	//ҳ����������
    	String urlstr = u;
        URL url = new URL(urlstr);     
        URLConnection connection = url.openConnection();     
        connection.setDoOutput(true);     
        String key = params.get(0).keySet().toString().replace("[", "").replace("]", "");
        String value = params.get(0).get(key).toString();
        String body = key+"="+value;
        for(int i = 1 ;i<params.size();i++)
        {
        	key = params.get(i).keySet().toString().replace("[", "").replace("]", "");
        	value = params.get(i).get(key).toString();
            body += "&"+key+"="+value;
        }	
         
        
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "8859_1");
        out.write(body); 
        // remember to clean up     
        out.flush();
        out.close();
        
        String sCurrentLine;     
        String sTotalString;     
        sCurrentLine = "";     
        sTotalString = "";     
        InputStream l_urlStream;     
        l_urlStream = connection.getInputStream();     
        
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(     
                l_urlStream));     
        while ((sCurrentLine = l_reader.readLine()) != null) {     
            sTotalString += sCurrentLine + "/r/n";     
    
        }     
        sTotalString=sTotalString.replace('\\', ' ');
        sTotalString=sTotalString.replace(" ", "");
        //System.out.println(sTotalString);     
        return sTotalString;
        
    }   
    public static String getPostResponse(List<HashMap<String, Object>> params,Double on,String oid) throws IOException {     
    	//ҳ����������
    	String urlstr = "http://data.foundationcenter.org.cn/DataForBD.aspx?no-cache="+on+"&OID="+oid+"&type=newgetinfo";
    	
        URL url = new URL(urlstr);     
        URLConnection connection = url.openConnection();     
        connection.setDoOutput(true);     
        String key = params.get(0).keySet().toString().replace("[", "").replace("]", "");
        String value = params.get(0).get(key).toString();
        String body = key+"="+value;
        for(int i = 1 ;i<params.size();i++)
        {
        	key = params.get(i).keySet().toString().replace("[", "").replace("]", "");
        	value = params.get(i).get(key).toString();
            body += "&"+key+"="+value;
        }	
         
        
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "8859_1");
        out.write(body); 
        // remember to clean up     
        out.flush();
        out.close();
        
        String sCurrentLine;     
        String sTotalString;     
        sCurrentLine = "";     
        sTotalString = "";     
        InputStream l_urlStream;     
        l_urlStream = connection.getInputStream();     
        
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(     
                l_urlStream));     
        while ((sCurrentLine = l_reader.readLine()) != null) {     
            sTotalString += sCurrentLine + "/r/n";     
    
        }     
        sTotalString=sTotalString.replace('\\', ' ');
        sTotalString=sTotalString.replace(" ", "");
        //System.out.println(sTotalString);     
        return sTotalString;
        
    }
    public static List<String> getLinks(String tag,String attrkey,String attrvalue, String url, String rule) throws ParserException, IOException
	{
		List<String> links=new ArrayList<String>();
		if (StringUtils.isNotBlank(tag)) {//固定区域抓取url
			String areaContent = getContent(url, tag, attrkey, attrvalue);
			//得到content后，经过regexStrList(content, rule)方法返回url链接，可能重复，要做处理
			List<String> originalList = new ArrayList<String>();
			originalList = regexStrList(areaContent, rule);
			//去掉重复url
			int size = originalList.size();
			if (size>0) {
				for (int i = 0; i < size; i++) {
					if (!links.contains(originalList.get(i))) {
						links.add(originalList.get(i));
					}
				}
			}
		}else {//整个网页全部抓取url
			Parser parser = new Parser();  
			parser.setURL(url);
			//设置目标网页的编码方式
			parser.setEncoding(CharsetAutoSwitch.dectedEncode(url)); 
			//创建链接节点的过滤器
			NodeFilter filter = new NodeClassFilter(LinkTag.class);
			NodeList list = parser.extractAllNodesThatMatch(filter);
			
			for (int i = 0; i < list.size(); i++) {  
				LinkTag node = (LinkTag) list.elementAt(i);
				//获取链接的目标网址
				String link=node.extractLink();
				//抓取到的连接过滤
				link = regexStr(link, rule);
				//排除非空
				if(link!=null && !link.trim().equals("") && !link.equals("#"))
				{
					//将目标网址加入到该页面的所有网址列表中
					links.add(link);
				}
			}
		}
		return links;
	}
    
    public static int hrefSize(String url, String tag, String attrkey, String attrvalue){
    	String size="";
    	try {
			String con=getContent(url, tag, attrkey, attrvalue);
			System.out.println(con);
			size=con.substring(con.indexOf("共")+1,con.indexOf("页"));
    	} catch (ParserException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println(size);
    	return Integer.parseInt(size);
    }
    
    public static List<String> ListHref(String leftHref,String rightHref,int size,String regex) throws ParserException, IOException{
    	List<String> href=new ArrayList<String>();
    	String tag="ul";
    	String attrkey="class";
    	String attrvalue="center_list_contlist";
    	
    	String con="";
    	for (int i = 1; i <= size; i++) {
    		if(i==1){
    			con=getContent(leftHref.substring(0,leftHref.indexOf("_"))+rightHref, tag, attrkey, attrvalue);
    		}else{
    			con=getContent(leftHref+(size-1)+rightHref, tag, attrkey, attrvalue);
    		}
			List<String> dangqian=regexStrList(con, regex);
			for (int j = 0; j < dangqian.size(); j++) {
				href.add(dangqian.get(j));
			}
		}
//    	HashSet<String> hs= new HashSet<String>();
//		for (int j = 0; j < href.size(); j++) {
//			hs.add(href.get(j));
//		}
//		href=new ArrayList<String>();
//		Iterator it=hs.iterator();
//	       while(it.hasNext())
//	       {
//	           String o=it.next().toString();
//	           href.add(o);
//	       }
    	return href;
    }
    
    public static List<String> allContent(List<String> href,String tag,String attrkey,String attrvalue) throws ParserException, IOException{
    	List<String> content = new ArrayList<String>();
    	for (int i = 0; i < href.size(); i++) {
			content.add(getContent(href.get(i), tag, attrkey, attrvalue));
		}
    	return content;
    }
    
    public static void main(String[] args) {
//    	try {
//    		quhuadisanceng();
//		} catch (ParserException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    	try {
//    		String re="(/beijing/[\\s\\S]{1,60}?.html)";
//			System.out.println(new FZParse().getLinks("table", "width", "728", "http://www.tcmap.com.cn/beijing/", re));
//		} catch (ParserException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    	//List<String> all=listAllHref("http://itjuzi.com/investevents?page=","", "(http://itjuzi.com/investevents/[\\s\\S]{1,60}?)<", 2,"table", "id", "company-member-list");
    	try {
			String con =getContent("http://itjuzi.com/company/20782", "div", "class", "page-wrapper");
			System.out.println(con);
			//(http://itjuzi.com/investfirm/(?![^<>]*?\\?[^<>]*?>).*?)\")
			System.out.println(regexStrList(con, "<li>行业:([\\s\\S]{0,600}?)</li>"));
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    
    public static void chuangyerongzi()throws org.htmlparser.util.ParserException{
    	List<String> all=listAllHref("http://itjuzi.com/investevents?page=","", "(http://itjuzi.com/investevents/[\\s\\S]{1,60}?)<", 753,"table", "id", "company-member-list");
    	System.out.println(all.size());
    	try {
    		HSSFWorkbook wb = new HSSFWorkbook();
	    	FileOutputStream fileOut = new FileOutputStream("d:/创业投资/rongzi1.xls");
			HSSFSheet sheet = wb.createSheet("new sheet");
    		for (int x = 0; x < all.size(); x++) {
    			System.out.println(x);
    			HSSFRow row = sheet.createRow((short) x);
    			String con = getContent(all.get(x), "table", "id", "company-member-list");
    			List<String> tr =regexStrList(con, "(<tr>[\\s\\S]{1,6000}?</tr>)");
    			List<String> conAll=new ArrayList<String>();
    			for (int i = 0; i < tr.size(); i++) {
    				List<String> td=regexStrList(tr.get(i),"(<td[\\s\\S]{1,6000}?</td>)");
    				for (int j = 1; j < td.size(); j++) {
						if(i==0||i==3||i==7){
							conAll.add(td.get(j).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("<td width='78%'>", ""));
						}else{
							List<String> gongsi = regexStrList(td.get(j), "(<a[\\s\\S]{1,6000}?</a>)");
							String mingcheng="";
							String lianjie="";
							for (int k = 0; k < gongsi.size(); k++) {
								String ziduan=gongsi.get(k);
								mingcheng=mingcheng+ziduan.substring(ziduan.indexOf(">")+1, ziduan.lastIndexOf("<"))+",";
								String a=regexStr(ziduan, "(http:[\\s\\S]{1,6000}?\")");
								lianjie=lianjie+a.substring(0,a.length()-1)+",";
							}
							try {
								conAll.add(mingcheng.substring(0,mingcheng.length()-1));
								conAll.add(lianjie.substring(0,lianjie.length()-1));
							} catch (Exception e) {
								conAll.add("");
								conAll.add("");
							}
							
						}
    				}
    			}
    			for (int i = 0; i < conAll.size(); i++) {
					HSSFCell cell1 = row.createCell(i);
					cell1.setCellValue(conAll.get(i));
					cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
				}
			}
    		wb.write(fileOut);
    		fileOut.close();
    	} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static List<String> ListHrefCon(String leftHref,String rightHref,int size,String regex) throws ParserException, IOException{
    	List<String> href=new ArrayList<String>();
    	String tag="ul";
    	String attrkey="class";
    	String attrvalue="center_list_contlist";
    	
    	String con="";
    	for (int i = 1; i <= size; i++) {
    		if(i==1){
    			con=getContent(leftHref.substring(0,leftHref.indexOf("_"))+rightHref, tag, attrkey, attrvalue);
    		}else{
    			con=getContent(leftHref+(size-1)+rightHref, tag, attrkey, attrvalue);
    		}
			List<String> dangqian=regexStrList(con, regex);
			for (int j = 0; j < dangqian.size(); j++) {
				href.add(dangqian.get(j).substring(dangqian.get(j).indexOf("-")+1,dangqian.get(j).indexOf("<")));
			}
		}
//    	HashSet<String> hs= new HashSet<String>();
//		for (int j = 0; j < href.size(); j++) {
//			hs.add(href.get(j));
//		}
//		href=new ArrayList<String>();
//		Iterator it=hs.iterator();
//	       while(it.hasNext())
//	       {
//	           String o=it.next().toString();
//	           href.add(o);
//	       }
    	return href;
    }
    
    public static List<String> ListHrefNo(String leftHref,String rightHref,int size,String regex) throws ParserException, IOException{
    	List<String> href=new ArrayList<String>();
    	String tag="ul";
    	String attrkey="class";
    	String attrvalue="center_list_contlist";
    	
    	String con="";
    	for (int i = 1; i <= size; i++) {
    		if(i==1){
    			con=getContent(leftHref.substring(0,leftHref.indexOf("_"))+rightHref, tag, attrkey, attrvalue);
    		}else{
    			con=getContent(leftHref+(size-1)+rightHref, tag, attrkey, attrvalue);
    		}
			List<String> dangqian=regexStrList(con, regex);
			for (int j = 0; j < dangqian.size(); j++) {
				href.add(dangqian.get(j).substring(dangqian.get(j).indexOf(">")+1,dangqian.get(j).indexOf("-")));
			}
		}
//    	HashSet<String> hs= new HashSet<String>();
//		for (int j = 0; j < href.size(); j++) {
//			hs.add(href.get(j));
//		}
//		href=new ArrayList<String>();
//		Iterator it=hs.iterator();
//	       while(it.hasNext())
//	       {
//	           String o=it.next().toString();
//	           href.add(o);
//	       }
    	return href;
    }
    
    /**
     * 第一层
     * @param href
     * @param regex
     * @return
     * @throws ParserException
     * @throws IOException
     */
    public static List<String> ListSonCon(String href,String regex) throws ParserException, IOException{
    	List<String> listCon=new ArrayList<String>();
    	String tag="table";
    	String attrkey="class";
    	String attrvalue="citytable";
    	String con=getContent(href, tag, attrkey, attrvalue);
    	listCon=regexStrList(con, regex);
    	return listCon;
    }
    /**
     * 第二层
     * @param href
     * @param regex
     * @return
     * @throws ParserException
     * @throws IOException
     */
    public static List<String> ListDiCon(String href,String regex) throws ParserException, IOException{
    	List<String> listCon=new ArrayList<String>();
    	String tag="table";
    	String attrkey="class";
    	String attrvalue="villagetable";
    	String con=getContent(href, tag, attrkey, attrvalue);
    	listCon=regexStrList(con, regex);
    	return listCon;
    }
    
    public void dierceng(){
    	List<String> href = null;
    	List<String> content = null;
    	List<String> no= null;
    	try {
    		href=ListHref("http://www.stats.gov.cn/tjsj/tjbz/tjypflml/index_",".html",5,"(/zjtj[\\s\\S]{1,60}?.html)");
    		content=ListHrefCon("http://www.stats.gov.cn/tjsj/tjbz/tjypflml/index_",".html",5,"(cont_tit03\">[\\s\\S]{1,30}?</)");
    		no = ListHrefNo("http://www.stats.gov.cn/tjsj/tjbz/tjypflml/index_",".html",5,"(cont_tit03\">[\\s\\S]{1,30}?</)");
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(href.get(0));
		System.out.println(href.size());
		System.out.println(content.size());
		System.out.println(no.size());
		
		int size=1;
		HSSFWorkbook wb = new HSSFWorkbook();// 建立新HSSFWorkbook对象
		try {
			FileOutputStream fileOut = new FileOutputStream("d:/chanpin/disanceng.xls");
			HSSFSheet sheet = wb.createSheet("new sheet");
			for (int i = 0; i < content.size(); i++) {
			
			List<String> son=new ArrayList<String>();
			try {
				son=ListSonCon("http://www.stats.gov.cn/"+href.get(i), "" +
						"(citytr\'>[\\s\\S]{1,1000}?</tr)");
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<List<String>> sonCon = new ArrayList<List<String>>();
			for (int j = 0; j < son.size(); j++) {
				sonCon.add(regexStrList(son.get(j), "(<td>[\\s\\S]{1,1000}?</td>)"));
			}
			for (int z = 0; z < sonCon.size(); z++) {
				size++;
				HSSFRow row = sheet.createRow((short) size);
				for (int j = 0; j < sonCon.get(z).size(); j++) {
					String neirong=sonCon.get(z).get(j).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</a>", "");
					System.out.println(neirong.substring(neirong.indexOf(">")+1));
					try {
						HSSFCell cell = row.createCell((short) j);
						cell.setCellValue(neirong.substring(neirong.indexOf(">")+1));
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//						HSSFCell cell3 = row.createCell((short) 3);
//						cell3.setCellValue(suoshudazhou2);
//						cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				HSSFCell cell = row.createCell((short) 3);
				cell.setCellValue(content.get(i));
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			}
			
			}
			wb.write(fileOut);// 把Workbook对象输出到文件workbook.xls中
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
    }
    
    public void disanceng(){
    	List<String> href = null;
    	List<String> content = null;
    	List<String> no= null;
    	try {
    		href=ListHref("http://www.stats.gov.cn/tjsj/tjbz/tjypflml/index_",".html",5,"(/zjtj[\\s\\S]{1,60}?.html)");
    		content=ListHrefCon("http://www.stats.gov.cn/tjsj/tjbz/tjypflml/index_",".html",5,"(cont_tit03\">[\\s\\S]{1,30}?</)");
    		no = ListHrefNo("http://www.stats.gov.cn/tjsj/tjbz/tjypflml/index_",".html",5,"(cont_tit03\">[\\s\\S]{1,30}?</)");
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(href.get(0));
		System.out.println(href.size());
		System.out.println(content.size());
		System.out.println(no.size());
		List<String> sonCon = new ArrayList<String>();
		for (int i = 0; i < content.size(); i++) {
			List<String> son=new ArrayList<String>();
			try {
				son=ListSonCon("http://www.stats.gov.cn/"+href.get(i), "(citytr\'>[\\s\\S]{1,1000}?</tr)");
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (int j = 0; j < son.size(); j++) {
				String a=regexStr(son.get(j), "(='[\\s\\S]{1,1000}?.html)");
				sonCon.add(a.substring(a.indexOf("'")+1));
			}
		}		
		System.out.println(sonCon.size());
		int size=1;
		HSSFWorkbook wb = new HSSFWorkbook();// 建立新HSSFWorkbook对象
		try {
			FileOutputStream fileOut = new FileOutputStream("d:/chanpin/disanceng.xls");
			HSSFSheet sheet = wb.createSheet("new sheet");
			try {
				for (int x = 0; x < sonCon.size(); x++) {
					List<String> son=new ArrayList<String>();
					try {
						son=ListDiCon("http://www.stats.gov.cn/zjtj/tjbz/tjypflml/2010/"+sonCon.get(x), "(tr\'>[\\s\\S]{1,1000}?</tr)");
					} catch (ParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					List<List<String>> sonCon1 = new ArrayList<List<String>>();
					for (int j = 0; j < son.size(); j++) {
						sonCon1.add(regexStrList(son.get(j), "(<td>[\\s\\S]{1,1000}?</td>)"));
					}
					for (int z = 0; z < sonCon1.size(); z++) {
						System.out.println(size);
						size++;
						HSSFRow row = sheet.createRow((short) size);
						
						for (int j = 0; j < sonCon1.get(z).size(); j++) {
							String neirong=sonCon1.get(z).get(j).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</a>", "");
							System.out.println(neirong.substring(neirong.indexOf(">")+1));
							try {
								HSSFCell cell = row.createCell((short) j);
								cell.setCellValue(neirong.substring(neirong.indexOf(">")+1));
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//								HSSFCell cell3 = row.createCell((short) 3);
//								cell3.setCellValue(suoshudazhou2);
//								cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
							}catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
				}
			wb.write(fileOut);// 把Workbook对象输出到文件workbook.xls中
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    public static void test58(){
    	String con ="";
    	try {
			con=getContent("http://qy.58.com/2906373156359/?PGTID=14268220548430.8307558104861528&ClickID=1", "div", "class", "basicMsg");
			
			System.out.println(con);
    	} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void ma(){
//    	List<String> sonCon = new ArrayList<String>();
//		HSSFWorkbook workbook;
//		try {
//			workbook = new HSSFWorkbook(new FileInputStream("d:/chanpin/disiceng.xls"));
//			HSSFSheet sheet = workbook.getSheetAt(0);
//			int rows = sheet.getPhysicalNumberOfRows();// 行
//			System.out.println(rows);
//			for (int i = 0; i < rows; i++) {
//				HSSFRow row = sheet.getRow(i);
//				String id=row.getCell(0).getStringCellValue();
//				String a=id.substring(0,2);
//				String b=id.substring(2,4);
//				String c=id.substring(4,6);
//				String d=id.substring(0,8);
//				String href=a+"/"+b+"/"+c+"/"+d+".html";
//				System.out.println(href);
//				sonCon.add(href);
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 
//		System.out.println(sonCon.size());
//		int size=1;
//		HSSFWorkbook wb = new HSSFWorkbook();// 建立新HSSFWorkbook对象
//		try {
//			FileOutputStream fileOut = new FileOutputStream("d:/chanpin/diwuceng.xls");
//			HSSFSheet sheet = wb.createSheet("new sheet");
//			try {
//				for (int x = 0; x < sonCon.size(); x++) {
//					List<String> son=new ArrayList<String>();
//					try {
//						son=ListDiCon("http://www.stats.gov.cn/zjtj/tjbz/tjypflml/2010/"+sonCon.get(x), "(tr\'>[\\s\\S]{1,1000}?</tr)");
//					} catch (ParserException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}catch (Exception e) {
//						e.printStackTrace();
//						continue;
//					}
//					List<List<String>> sonCon1 = new ArrayList<List<String>>();
//					for (int j = 0; j < son.size(); j++) {
//						sonCon1.add(regexStrList(son.get(j), "(<td>[\\s\\S]{1,1000}?</td>)"));
//					}
//					for (int z = 0; z < sonCon1.size(); z++) {
//						System.out.println(size);
//						size++;
//						HSSFRow row = sheet.createRow((short) size);
//						
//						for (int j = 0; j < sonCon1.get(z).size(); j++) {
//							String neirong=sonCon1.get(z).get(j).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</a>", "");
//							System.out.println(neirong.substring(neirong.indexOf(">")+1));
//							try {
//								HSSFCell cell = row.createCell((short) j);
//								cell.setCellValue(neirong.substring(neirong.indexOf(">")+1));
//								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
////								HSSFCell cell3 = row.createCell((short) 3);
////								cell3.setCellValue(suoshudazhou2);
////								cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
//							}catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}
//					
//				}
//			wb.write(fileOut);// 把Workbook对象输出到文件workbook.xls中
//			fileOut.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
    	//test58();
    	
    }
    public static List<String> listAllHref(String beforurl,String afterurl,String rule,int size,String tag,String attrkey,String attrvalue)throws org.htmlparser.util.ParserException{
    	List<String> all=new ArrayList<String>();
    	try {
			for (int i = 1; i <= size; i++) {
				System.out.println(i);
				List<String> href =getLinks(tag, attrkey, attrvalue, beforurl+i+afterurl, rule);
				for (int j = 0; j < href.size(); j++) {
					all.add(href.get(j));
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return all;
    }
    
    public static void quhuadiyiceng() throws ParserException, IOException{
    	String con =getContent("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/index.html", "table", "class", "provincetable");
    	List<String> all=regexStrList(con, "(ovincetr\'>[\\s\\S]{1,1000}?</tr)");
    	System.out.println(all);
    	List<String> con1=regexStrList(all.toString(), "(a[\\s\\S]{1,1000}?</a>)");
    	List<String> allHref=regexStrList(con1.toString(), "('[\\s\\S]{1,1000}?')");
    	List<String> allCon=regexStrList(con1.toString(), "(>[\\s\\S]{1,8}?<)");
    	System.out.println(allHref.size());
    	System.out.println(allCon.size());
    	HSSFWorkbook wb = new HSSFWorkbook();// 建立新HSSFWorkbook对象
		FileOutputStream fileOut = new FileOutputStream("d:/统计局行政区划/省份.xls");
		HSSFSheet sheet = wb.createSheet("new sheet");
		for (int i = 0; i < allHref.size(); i++) {
			HSSFRow row = sheet.createRow((short) i);
			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue(allCon.get(i).replaceAll(">", "").replaceAll("<", ""));
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		wb.write(fileOut);// 把Workbook对象输出到文件workbook.xls中
		fileOut.close();
		
    }
    
    public static void quhuadierceng() throws ParserException, IOException{
    	String con =getContent("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/index.html", "table", "class", "provincetable");
    	List<String> all=regexStrList(con, "(ovincetr\'>[\\s\\S]{1,1000}?</tr)");
    	List<String> con1=regexStrList(all.toString(), "(a[\\s\\S]{1,1000}?</a>)");
    	List<String> allHref=regexStrList(con1.toString(), "('[\\s\\S]{1,1000}?')");
    	List<String> allCon=regexStrList(con1.toString(), "(>[\\s\\S]{1,8}?<)");
    	HSSFWorkbook wb = new HSSFWorkbook();// 建立新HSSFWorkbook对象
		FileOutputStream fileOut = new FileOutputStream("d:/统计局行政区划/第二层.xls");
		HSSFSheet sheet = wb.createSheet("new sheet");
		int size=0;
    	for (int i = 0; i < allHref.size(); i++) {
    		String twoCon=getContent("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/"+allHref.get(i).replaceAll("'",""),"table", "class", "citytable");
    		List<String> son=regexStrList(twoCon, "(citytr\'>[\\s\\S]{1,1000}?</tr)");
    		List<List<String>> sonCon = new ArrayList<List<String>>();
			for (int j = 0; j < son.size(); j++) {
				sonCon.add(regexStrList(son.get(j), "(<td>[\\s\\S]{1,1000}?</td>)"));
			}
			for (int z = 0; z < sonCon.size(); z++) {
				size++;
				HSSFRow row = sheet.createRow((short) size);
				for (int j = 0; j < sonCon.get(z).size(); j++) {
					String neirong=sonCon.get(z).get(j).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</a>", "");
					System.out.println(neirong.substring(neirong.indexOf(">")+1));
					HSSFCell cell = row.createCell((short) j);
					cell.setCellValue(neirong.substring(neirong.indexOf(">")+1));
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//					HSSFCell cell3 = row.createCell((short) 3);
//					cell3.setCellValue(suoshudazhou2);
//					cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
					// TODO Auto-generated catch block
			}
			HSSFCell cell = row.createCell((short) 2);
			cell.setCellValue(allCon.get(i).replaceAll(">", "").replaceAll("<", ""));
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				
			}
    	}
    	wb.write(fileOut);
		fileOut.close();
    }
    
    public static void quhuadisanceng() throws ParserException, IOException{
    	String con =getContent("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/index.html", "table", "class", "provincetable");
    	List<String> all=regexStrList(con, "(ovincetr\'>[\\s\\S]{1,1000}?</tr)");
    	List<String> con1=regexStrList(all.toString(), "(a[\\s\\S]{1,1000}?</a>)");
    	List<String> allHref=regexStrList(con1.toString(), "('[\\s\\S]{1,1000}?')");
    	List<String> allCon=regexStrList(con1.toString(), "(>[\\s\\S]{1,8}?<)");
    	HSSFWorkbook wb = new HSSFWorkbook();// 建立新HSSFWorkbook对象
		FileOutputStream fileOut = new FileOutputStream("d:/统计局行政区划/第五层.xls");
		HSSFSheet sheet = wb.createSheet("new sheet");
		int size=0;
		int count=1;
    	for (int i = 0; i < allHref.size(); i++) {
    		String twoCon=getContent("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/"+allHref.get(i).replaceAll("'",""),"table", "class", "citytable");
    		List<String> son=regexStrList(twoCon, "(citytr\'>[\\s\\S]{1,1000}?</tr)");
    		List<List<String>> sonCon = new ArrayList<List<String>>();
			for (int j = 0; j < son.size(); j++) {
				sonCon.add(regexStrList(son.get(j), "(<td>[\\s\\S]{1,1000}?</td>)"));
			}
			for (int z = 0; z < sonCon.size(); z++) {
				for (int j = 0; j < 1; j++) {
//					String neirong=sonCon.get(z).get(j).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</a>", "");
//					System.out.println(neirong.substring(neirong.indexOf(">")+1));
					String treeHref=regexStr(sonCon.get(z).get(j),"('[\\s\\S]{1,1000}?')");
					String treeCon=getContent("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/"+treeHref.replaceAll("'",""),"table", "class", "countytable");
					List<List<String>> TreeCon = new ArrayList<List<String>>();
					TreeCon.add(regexStrList(treeCon, "(countytr'>[\\s\\S]{1,1000}?</tr>)"));
					for (int k = 0; k < TreeCon.size(); k++) {
						for (int x = 0; x < TreeCon.get(k).size(); x++) {
							String neirong=TreeCon.get(k).get(x);
							List<String> Neirong =regexStrList(neirong, "(<td>[\\s\\S]{1,1000}?</td>)");
							for (int l = 0; l < 1; l++) {
								String tiaomu=Neirong.get(l).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</a>", "");
								String id=tiaomu.substring(tiaomu.indexOf(">")+1);
								String a=id.substring(0,2);
								String b=id.substring(2,4);
								String c=id.substring(0,6);
								String href=a+"/"+b+"/"+c+".html";
								String foreCon="";
								try {
									foreCon = getContent("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/"+href,"table", "class", "towntable");
								} catch (Exception e) {
									continue;
								}
								List<List<String>> ForeCon = new ArrayList<List<String>>();
								ForeCon.add(regexStrList(foreCon, "(towntr'>[\\s\\S]{1,1000}?</tr>)"));
								for (int m = 0; m < ForeCon.size(); m++) {
									for (int m2 = 0; m2 < ForeCon.get(m).size(); m2++) {
										String Foreneirong=ForeCon.get(m).get(m2);
										List<String> ForeNeirong =regexStrList(Foreneirong, "(<td>[\\s\\S]{1,1000}?</td>)");
										for (int n = 0; n < 1; n++) {
											String Foretiaomu=ForeNeirong.get(n).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</a>", "");
											String Fiveid=Foretiaomu.substring(Foretiaomu.indexOf(">")+1);
											String a1=Fiveid.substring(0,2);
											String b1=Fiveid.substring(2,4);
											String d1=Fiveid.substring(4,6);
											String c1=Fiveid.substring(0,9);
											String Fivehref=a1+"/"+b1+"/"+d1+"/"+c1+".html";
											String fiveCon="";
											try {
												fiveCon = getContent("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/"+Fivehref,"table", "class", "villagetable");
											} catch (Exception e) {
												continue;
											}
											List<List<String>> FiveCon = new ArrayList<List<String>>();
											FiveCon.add(regexStrList(fiveCon, "(villagetr'>[\\s\\S]{1,1000}?</tr>)"));
											for (int o = 0; o < FiveCon.size(); o++) {
												for (int o2 = 0; o2 < FiveCon.get(o).size(); o2++) {
													String Fiveneirong=FiveCon.get(o).get(o2);
													List<String> FiveNeirong =regexStrList(Fiveneirong, "(<td>[\\s\\S]{1,1000}?</td>)");
													size++;
													if(size==65530){
														count++;
														sheet=wb.createSheet("new sheet"+count);
														size=1;
													}
													HSSFRow row = sheet.createRow(size);
													for (int p = 0; p < FiveNeirong.size(); p++) {
														String Fivetiaomu=FiveNeirong.get(p).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</a>", "");
														HSSFCell cell = row.createCell(p);
														if(p==2){
															System.out.println(Fivetiaomu.substring(Fivetiaomu.indexOf(">")+1));
														}
														cell.setCellValue(Fivetiaomu.substring(Fivetiaomu.indexOf(">")+1));
														cell.setCellType(HSSFCell.CELL_TYPE_STRING);
													}
													HSSFCell cell3 = row.createCell( 3);
													String fa=ForeNeirong.get(1).replaceAll("<td>", "").replaceAll("</td>", "").replaceAll("</a>", "");
													cell3.setCellValue(fa.substring(fa.indexOf(">")+1));
													cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
												}
											}
										}
									}
								}
							}
						}
					}
					
					
			}

			}
    	}
    	wb.write(fileOut);
		fileOut.close();
    }
    
    public static void wancheng(List<String> hrefAll,String tag,String attrkey,String attrvalue,String[] regex) throws ParserException, IOException{
    	HSSFWorkbook wb = new HSSFWorkbook();// 建立新HSSFWorkbook对象
		FileOutputStream fileOut = new FileOutputStream("d:/测试/test1.xls");
		HSSFSheet sheet = wb.createSheet("new sheet");
		System.out.println(hrefAll.size());
    	for (int i = 0; i < hrefAll.size(); i++) {
    		System.out.println(i);
    		String con =getContent(hrefAll.get(i), tag, attrkey, attrvalue);
			System.out.println("getContent");
    		List<String> content = new ArrayList<String>();
			for (int j = 0; j < regex.length; j++) {
				List<String> arr=regexStrList(con, regex[j]);
				if(arr.size()==0){
					content.add("");
				}else{
					for (int k = 0; k < arr.size(); k++) {
						try {
							content.add(arr.get(k).replaceAll("(<[\\s\\S]{1,1000}?>)", ""));
						} catch (Exception e) {
							content.add("");
						}
					}
				}
			}
			HSSFRow row = sheet.createRow(i);
			for (int j = 0; j < content.size(); j++) {
				HSSFCell cell=row.createCell(j);
				try {
					cell.setCellValue(content.get(j));
				} catch (Exception e) {
					cell.setCellValue("");
				}
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			}
		}
    	wb.write(fileOut);
		fileOut.close();
    }
   
}
