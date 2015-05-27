<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
  </head>
  
  <body>
  	
	<h1>封装爬虫</h1>
  	 <form action="parse/lianjie" method="post">
  	 	<table>
  	 		<thead><tr><td>无分页获取所有链接</td></tr></thead>
  	 		<tr><td>地址:</td><td><input name="url"></td></tr>
  	 		<tr><td>标签名:</td><td><input name="tag"></td><td>样式:</td><td><input name="attrkey"></td><td>值:</td><td><input name="attrvalue"></td></tr>
  	 		<tr><td>超链接前缀:</td><td><input name="qianzhui"></td></tr>
  	 		<tr><td>超链接正则表达式:</td><td><input name="regex"></td></tr>
  	 		<tr><td><input type="submit"></td></tr>
  	 	</table>
  	 </form>
  	 <form action="fenyelianjie" method="post">
  	 	<table>
  	 		<thead><tr><td>分页获取所有链接</td></tr></thead>
  	 		<tr><td>地址前缀:</td><td><input name="beforurl"></td><td>地址后缀:</td><td><input name="afterurl"></td><td>长度：</td><td><input name="size"></td></tr>
  	 		<tr><td>标签名:</td><td><input name="tag"></td><td>样式:</td><td><input name="attrkey"></td><td>值:</td><td><input name="attrvalue"></td></tr>
  	 		<tr><td>超链接前缀:</td><td><input name="qianzhui"></td></tr>
  	 		<tr><td>超链接正则表达式:</td><td><input name="regex"></td></tr>
  	 		<tr><td><input type="submit"></td></tr>
  	 	</table>
  	 </form>
  	 <span>${href }</span>
  	 <form action="parse/zhuaqu" method="post">
  	 	<table>
  	 		<tr><td>地址:</td><td><input name="url"></td></tr>
  	 		<tr><td>标签名:</td><td><input name="tag"></td><td>样式:</td><td><input name="attrkey"></td><td>值:</td><td><input name="attrvalue"></td></tr>
  	 		<tr><td><input type="submit"></td></tr>
  	 	</table>
  	 	
  	 </form>
  	 
  	 <form action="parse/tiqu" method="post">
  	 	<textarea name="con" rows="30" cols="100">${con }</textarea>
  	 	<br><br>
  	 	<table id="tiqu">
  	 		<tr><td valign="top">提取内容正则：</td><td><div class="form_listrl" id="sel" >
             <div class="sel">
	             <input name="regex">
	             <span class="del">X</span>
                 </div>
        </div>
        <span id="mm">+</span></td></tr>
  	 		<tr><td><input type="submit"></td></tr>
  	 	</table>
                              
  	 </form>
  	 <textarea name="tiqu" rows="10" cols="50">${tiqu }</textarea>
  	 <br><br>
  	 <form action="wancheng" method="post">
  	 	<table id="tiqu">
  	 	<span>下拉框的值表示：1.获取第一个符合正则表达式的内容 2.表示获取所有符合正则表达式的内容</span>
  	 	<tr><td></td><td><input name="href" value="${href }"></td></tr>
  	 	<tr><td>Excel:</td><td><input name="ename"></td></tr>
  	 	<tr><td>标签名:</td><td><input name="tag"></td><td>样式:</td><td><input name="attrkey"></td><td>值:</td><td><input name="attrvalue"></td></tr>
  	 	<tr><td valign="top">提取内容正则：</td><td><div class="form_listrl" id="sel1" >
             <div class="sel1">
	             <input name="regex"><select name="type"><option selected="selected">1</option><option>2</option></select>
	             <span class="del1">X</span>
                 </div>
        </div>
        <span id="nn">+</span></td></tr>
  	 	<tr><td><input type="submit" value="完成"></td></tr>
  	 	</table>
  	 </form>
  </body>
 	<script type="text/javascript">
 		$("#add").click(function(){
       				$("#tiqu").append($(".sel:eq(0)").clone().show());
       			});
       $("#mm").click(function(){
       				$("#sel").append($(".sel:eq(0)").clone().show());
       			});
        $("#nn").click(function(){
       				$("#sel1").append($(".sel1:eq(0)").clone().show());
       			});			
       $(document).on('click', '#sel .del',function(){
       				if($("#sel .sel").length==1){
       					alert("至少一个");
       					return;
       				}
       				$(this).closest(".sel").remove();
       				srot();
       			});
      	$(document).on('click', '#sel1 .del1',function(){
       				if($("#sel1 .sel1").length==1){
       					alert("至少一个");
       					return;
       				}
       				$(this).closest(".sel1").remove();
       				srot();
       			});
       	
 	</script>
</html>
