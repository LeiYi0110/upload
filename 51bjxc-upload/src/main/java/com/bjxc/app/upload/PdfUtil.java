package com.bjxc.app.upload;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import sun.misc.BASE64Decoder;


/**
 * 在pdf上边加盖公章的工具类
 *
 */
public class PdfUtil {
	
	/**
	 * 生成学员阶段培训结果PDF文件
	 * @param pdfFile，PDF模版文件（首次为空模版，其他培训阶段为省平台返回PDF）
	 * @param outPdfFile，重命名盖章后上传的pdf文件
	 * @param student，学员基本信息
	 * @param tr，学员阶段培训结果
	 * @param imgStr，签章base64编码串
	 */
	public void createResultPDF(Map map) {
		try {
			InputStream pdfData = new FileInputStream((File)map.get("pdfFile"));
			PdfReader reader = new PdfReader(pdfData);
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(map.get("outPdfFile").toString()));
			
			//只有一页
			int pageNum = reader.getNumberOfPages();
			PdfDictionary p = reader.getPageN(pageNum);
			PdfContentByte over = stamper.getOverContent(pageNum);
			Rectangle rct=reader.getPageSize(p);
			//从左上角开始计算(595.32,841.92)
			float pdfHeight = rct.getHeight();
			
			//这个字体是itext-asian.jar中自带的，所以不用考虑操作系统环境问题，支持中文
			BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
			Font font = new Font(bf, 10);
			font.getBaseFont();
			if (!map.get("biye").toString().equals("1")) {
				
				//填写姓名
				print(over,font,155f, pdfHeight-100f,map.get("name").toString());
				
				//填写性别
				print(over,font,255f, pdfHeight-100f,map.get("sex").toString());
		
				//填写证件类别
				print(over,font,360f, pdfHeight-100f,map.get("cardType").toString()); 
				
				//填写身份证号码
				print(over,font,530f, pdfHeight-100f,map.get("idCard").toString()); 
				
				//填写家庭住址
				print(over,font,155f, pdfHeight-135f,map.get("address").toString()); 
				
				//填写联系方式
				print(over,font,465f, pdfHeight-135f,map.get("mobile").toString()); 
				
				//申请车型
				print(over,font,155f, pdfHeight-170f,map.get("carType").toString()); 
				
				//入学时间
				print(over,font,465f, pdfHeight-170f,map.get("time").toString()); 
				
				//学员编号
				print(over,font,660f, pdfHeight-80f,map.get("stunum").toString()); 
				
				//填写个人照片
				if(null!=map.get("photo").toString()){
					Image img = Image.getInstance(map.get("photo").toString());
					img.scaleAbsolute(80, 100);
					img.setAbsolutePosition(675f, pdfHeight-185f);
					over.addImage(img);
				}
				
				//盖章图片
				Image signImg = createEsign(map.get("imgStr").toString());
				//填写学时 
				if (map.get("subject").toString().equals("1")) {
					printSubjectInfo(over,font,map,signImg,pdfHeight-260f,527f, pdfHeight-279f,525f,pdfHeight-291f);
				} else if(map.get("subject").toString().equals("2")){
					printSubjectInfo(over,font,map,signImg,pdfHeight-330f,530f, pdfHeight-354f,525f, pdfHeight-362f);
				} else if(map.get("subject").toString().equals("3")){
					printSubjectInfo(over,font,map,signImg,pdfHeight-400f,530f, pdfHeight-425f,525f, pdfHeight-433f);
				} else if(map.get("subject").toString().equals("4")){
					printSubjectInfo(over,font,map,signImg,pdfHeight-470f,530f, pdfHeight-496f,525f, pdfHeight-503f);
				}
			}else{
				//填写个人照片
				if(null!=map.get("photo").toString()){
					Image img = Image.getInstance(map.get("photo").toString());
					img.scaleAbsolute(80, 100);
					img.setAbsolutePosition(400f, pdfHeight-250f);
					over.addImage(img);
				}
				
				//填写姓名
				print(over,font,170f, pdfHeight-260f,map.get("name").toString());
				
				//填写性别
				print(over,font,270f, pdfHeight-260f,map.get("sex").toString());
				
				//开始日期
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = simpleDateFormat.parse(map.get("startTime").toString());
				print(over,font,340f, pdfHeight-260f,(date.getYear()+1900)+"                    "+(date.getMonth()+1)+"                   "+date.getDate());
				
				//结束日期
				SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyyMMdd");
				Date date2 = simpleDateFormat2.parse(map.get("endTime").toString());
				print(over,font,100f, pdfHeight-283f,(date2.getYear()+1900)+"                   "+(date2.getMonth()+1)+"                    "+date2.getDate());

				//填写培训信息
				print(over,font,280f, pdfHeight-284f,map.get("subjectName").toString());

				//填写培训机构
				print(over,font,315f, pdfHeight-349f,map.get("insName").toString());
				
				//盖章日期
				Date date3 = new Date();
				print(over,font,315f, pdfHeight-369f,(date3.getYear()+1900)+"                    "+(date3.getMonth()+1)+"                    "+date3.getDate());
				
				map.put("provice", "广东");
				//填写省份
				print(over,font,120f, pdfHeight-385f,map.get("provice").toString());
		
				//盖章
				Image signImg = createEsign(map.get("imgStr").toString());
				if(signImg!=null){
					signImg.scaleAbsolute(70, 70);
					signImg.setAbsolutePosition(340f, pdfHeight-280f);
					over.addImage(signImg);
				}
			}
			
			stamper.close();
			reader.close();
			pdfData.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (DocumentException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据不同科目打印不同学时数据
	 */
	private void printSubjectInfo(PdfContentByte over, Font font,Map map, Image signImg, float top, float timeLeft, float timeTop,float photoLeft,float photoTop) throws DocumentException {
		Integer mins=Integer.parseInt(map.get("hours").toString());
		print(over,font,175f, top,String.format("%dhh%dmin", mins/60,mins%60));	//培训学时
		print(over,font,255f, top,String.valueOf(map.get("mileages")));	//培训里程
		print(over,font,335f, top,map.get("result").toString());	//考核结果
		print(over,font,415f, top,map.get("judge").toString());	//考核员
		Date date = new Date();
		print(over,font,timeLeft,timeTop ,(date.getYear()+1900)+"        "+(date.getMonth()+1)+"        "+date.getDate());	//盖章日期

		//盖章
		if(signImg!=null){
			signImg.scaleAbsolute(70, 70);
			signImg.setAbsolutePosition(photoLeft, photoTop);
			over.addImage(signImg);
		}
	}

	/**
	 * 在pdf中打印文字
	 */
	private void print(PdfContentByte over,Font font, float marginLeft,float marginTop,String text) {
		over.beginText();
		over.setFontAndSize(font.getBaseFont(), 10);
		over.setTextMatrix(marginLeft,marginTop);
		over.showText(text);
		over.endText();
	}
	
	/*
	 * 将公章的base64编码转换成公章图片
	 */
	private Image createEsign(String imgStr){
		Image img = null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			img = Image.getInstance(b);
		} catch (Exception e) {
			//日志，do nothing
			e.printStackTrace();
		}
		return img;
	}
}
