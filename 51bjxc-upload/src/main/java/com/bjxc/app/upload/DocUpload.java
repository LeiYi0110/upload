package com.bjxc.app.upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjxc.Result;
import com.bjxc.exception.BusinessException;
import com.bjxc.json.JacksonBinder;

public class DocUpload extends HttpServlet {
	private static JacksonBinder binder = JacksonBinder.buildNormalBinder();
	private static final Logger logger=LoggerFactory.getLogger(DocUpload.class);
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		Result result = new Result();
		Properties pps = new Properties();     //读取配置文件
		String path=null;
		String url=null;
		try {
			String filepath=FileUpload.class.getResource("/path.properties").getPath();
			InputStream in = new BufferedInputStream (new FileInputStream(filepath));
			pps.load(in);
			path=pps.getProperty("documents");
			url=pps.getProperty("url");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.info(e.toString());
		}  
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String timePath=format.format(new Date());
		path+=timePath;        //文件夹名
		File folder=new File(path);
		if(!folder.exists()){                     //创建文件夹
			folder.mkdirs();
		}
		int maxSize=1024*1024*100; //100m ,设置文件
		UUID uuid = UUID.randomUUID();//生成UUID
		//获得磁盘文件条目工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//创建解析类的实例
		ServletFileUpload sfu = new ServletFileUpload(factory); 
		try {
			List<FileItem> items = sfu.parseRequest(request); 
			for(FileItem item : items)
			{
				if(item.getSize()>maxSize){
					throw new UploadException("文件大小不能超过100M");
				}
				//isFormField为true，表示这不是文件上传表单域
				if(!item.isFormField()){ 
					//获得文件名
					String fileName = item.getName();
					//获得文件后缀名
					String postfixName=fileName.substring(fileName.lastIndexOf("."),fileName.length());
					//原图名
					String newFileName=uuid+"_s"+postfixName;
					File file = new File(path+"/"+newFileName); 
					logger.info(file.getPath());
					if(!file.exists()){
						item.write(file); 
						Map<String, String> map=new HashMap<String, String>();
				        map.put("url", url+timePath+"/"+newFileName);
				        result.success(map);
						out.print(binder.toJson(result));
					}else {
						throw new UploadException("文件已存在！");
					}
				}
			}
		}catch (BusinessException ex){
			result.error(ex);
			logger.error("img api  ",ex);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
