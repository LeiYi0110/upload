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
		Properties pps = new Properties();     //��ȡ�����ļ�
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
		path+=timePath;        //�ļ�����
		File folder=new File(path);
		if(!folder.exists()){                     //�����ļ���
			folder.mkdirs();
		}
		int maxSize=1024*1024*100; //100m ,�����ļ�
		UUID uuid = UUID.randomUUID();//����UUID
		//��ô����ļ���Ŀ����
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//�����������ʵ��
		ServletFileUpload sfu = new ServletFileUpload(factory); 
		try {
			List<FileItem> items = sfu.parseRequest(request); 
			for(FileItem item : items)
			{
				if(item.getSize()>maxSize){
					throw new UploadException("�ļ���С���ܳ���100M");
				}
				//isFormFieldΪtrue����ʾ�ⲻ���ļ��ϴ�����
				if(!item.isFormField()){ 
					//����ļ���
					String fileName = item.getName();
					//����ļ���׺��
					String postfixName=fileName.substring(fileName.lastIndexOf("."),fileName.length());
					//ԭͼ��
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
						throw new UploadException("�ļ��Ѵ��ڣ�");
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
