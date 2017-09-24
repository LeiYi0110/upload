<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'index2.jsp' starting page</title>
<script type="text/javascript">
function readseal()
{
	alert("readseal");
	var obj = window.document.getElementById("ocx"); 
	var seal;
	var sealInfo = obj.ReadSeal();
	var sealInfoList = sealInfo.toArray();
	
	if (sealInfoList[0])
	{
		seal = sealInfoList[0];
		alert(seal);
		document.getElementById("data").value = seal;
		var obj = window.document.getElementById("txt"); 
		obj.value = sealInfoList[1];
		alert(obj.value);
	}
}

function sign()
{
	alert("sign");
	var obj = window.document.getElementById("ocx"); 
	//var data = window.document.getElementById("data").value;
	var data = '{"simulatortime":null,"rectype":"2","stunum":"6161758136324302","subject":"1","examresult":"0","duration":"60","totaltime":"60","networktime":null,"inscode":"5851633716061642","recarray":[{"rnum":"616175813632430200001"}],"vehicletime":null,"classtime":"60","mileage":null}';
	var signInfo = obj.sign(data);
	var signInfoList = signInfo.toArray();
	var signature, signerCert,
	signature = signInfoList[0];
    signerCert = signInfoList[1];
    window.document.getElementById("signature").value = signature;
    window.document.getElementById("cert").value = signerCert;
    
	//alert(signature);
	//alert(signerCert);
}

function verify()
{
	alert("verify");
	var obj = window.document.getElementById( "ocx"); 
	var data = window.document.getElementById("data").value;
	var signature = window.document.getElementById("signature").value;
	var cert = window.document.getElementById("cert").value;
	var ok = obj.verify(data, signature, cert);
	alert(ok);
}

function getCertInfo()
{
	var obj = window.document.getElementById( "txt");
	obj.value = "acb";
	alert("getCertInfo");
	var obj = window.document.getElementById( "ocx"); 
	var cert = window.document.getElementById("cert").value;
	
	var certInfo = obj.GetCertInfo(cert);
	var certInfoList = certInfo.toArray();
	var sn,dn,fingersprint;
	sn = certInfoList[0];
    dn = certInfoList[1];
	fingersprint = certInfoList[2];
	alert(sn);
	alert(dn);
	alert(fingersprint);
}
</script>
</head>

<body>
<table border="0">	
  <tr>
    <td nowrap>原文</td>  
	<td width="10"><input type="text"  id="data" value="" ></td>  
  </tr>
  <tr>
    <td nowrap>签名值</td>  
	<td width="10"><input type="text"  id="signature" value="" ></td>  
  </tr>  
  <tr>
    <td nowrap>证书</td>  
	<td width="10"><input type="text"  id="cert" value="" ></td>  
  </tr>    
  <tr>
    <td width="10"><input type="button" value="读取签章" onclick="readseal()"></td>
  </tr>
  <tr>
    <td width="10"><input type="button" value="数字签名" onclick="sign()"></td>
  </tr>
  <tr>
	<td width="10"><input type="button" value="验证签名" onclick="verify()"></td>
  </tr>
  <tr>
	<td width="10"><input type="button" value="获取证书信息" onclick="getCertInfo()">
  </tr>
</table>
<object id="ocx" classid="CLSID:4E194A99-7F41-453E-914C-544BB186A59C"  codebase ="signocx.cab#version=1.0.0.3" width="100" height="50">
</object>
<textarea id="txt"></textarea>
</body>
</html>
