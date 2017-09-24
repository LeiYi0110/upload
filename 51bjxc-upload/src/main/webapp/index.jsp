<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
<head>
<base href="<%=basePath%>">
<title>My JSP 'index.jsp' starting page</title>
<script src="resources/jquery-1.10.2.min.js"></script>
<script type="text/javascript">
//十六进制转二进制字节数组
function Hex2Bytes(str) {
	var len = str.length;
	if (len % 2 != 0) {
		str = "0" + str;
		len = len + 1;
	}
	len /= 2;
	var Bytes = new Array();
	for (var i = 0; i < len; i++) {
		var byteString = str.substr(i * 2, 2);
		var value = parseInt(byteString, 16);
		Bytes.push(value);
	}
	return Bytes;
}

//二进制转base64  
function bytesToEncodedString(bytes) {
	return btoa(String.fromCharCode.apply(null, bytes));
}

function readseal()
{
	var obj = window.document.getElementById("ocx"); 
	var seal;
	var sealInfo = obj.ReadSeal();
	var sealInfoList = sealInfo.toArray();
	
	if (sealInfoList[0]){
		seal = sealInfoList[0];
		var obj=sealInfoList[1];
		var jinzhi2=Hex2Bytes(obj);
		var jinzhi64=bytesToEncodedString(jinzhi2);
		$("#imgStr").val(jinzhi64);
	}
}

$(document).ready(function(){ 
　　readseal();
}); 
</script>
</head>
<body>
	<h2>Hello World!</h2>

	<div>
		<form action="<%=basePath%>FileUpload" method="post" enctype="multipart/form-data">
			请选择上传的图片:<input type="file" name="fileName"/>
			<input type="submit" value="上传"/> 
		</form>
	</div>
	<div>
		<form action="<%=basePath%>docUpload" method="post" enctype="multipart/form-data">
			请选择上传的文件:<input type="file" name="fileName"/>
			<input type="submit" value="上传"/> 
		</form>
	</div>
	<div>
		<form action="<%=basePath%>PDFUpload" method="post" enctype="multipart/form-data">
			请选择上传的PDF:<input type="file" name="fileName"/>
			<br/>name:<input type="text" name="name" value="大帅哥"><br/>
			photo:<input type="text" name="photo" value="http://img.91ygxc.com/2016/12/14/591342c6-e68f-4f0a-92b3-788b6432f9fb_s.jpg"><br/>
			sex:<input type="text" name="sex" value="男"><br/>
			cardType:<input type="text" name="cardType" value="身份证"><br/>
			idCard:<input type="text" name="idCard" value="434325698741563982"><br/>
			address:<input type="text" name="address" value="南天门"><br/>
			mobile:<input type="text" name="mobile" value="12233336666"><br/>
			carType:<input type="text" name="carType" value="C1"><br/>
			time:<input type="text" name="time" value="2016-11-11"><br/>
			hours:<input type="text" name="hours" value="10"><br/>
			mileages:<input type="text" name="mileages" value="0"><br/>
			result:<input type="text" name="result" value="通过"><br/>
			judge:<input type="text" name="judge" value="本人"><br/>
			imgStr:<input type="text" id="imgStr" name="imgStr" value=""><br/>
			<input type="submit" value="上传"/> 
		</form>
	</div>
<object id="ocx" classid="CLSID:4E194A99-7F41-453E-914C-544BB186A59C"  codebase ="signocx.cab#version=1.0.0.3" style="display:none;"></object>
</body>
</html>
