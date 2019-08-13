package batchquery;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ls.LSInput;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
public class mainUI extends JFrame {
	
	JTextField pathTextField;
	JButton openButton;
	JButton startqueryButton;
	JButton getqueryresultButton;
	JButton json2csv;
	String selectedfilepath;
	static List<String> weblist; 
	static String paramString;
	static String TaskID;
	static String resultString;
	private static String Drivde="org.sqlite.JDBC";
	
	public mainUI() {
	// TODO Auto-generated constructor stub
		
		Container c = getContentPane();
		pathTextField = new JTextField();
		openButton = new JButton("打开文件");
		startqueryButton =new JButton("提交查询任务");
		getqueryresultButton =new JButton("获取查询结果");
		json2csv = new JButton("导出csv");
		startqueryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String str=HttpClientPost();
					JSONObject jsonObj = new JSONObject(str);
				    String taskString = jsonObj.getString("TaskID");
				    String reasonString = jsonObj.getString("Reason");
				    int total=jsonObj.getInt("Total");
				    JOptionPane.showMessageDialog(null,reasonString+"\n"+"任务ID:"+taskString+"\n"+"成功总条数："+String.valueOf(total)+"\n"+"请稍后查询结果",null, 1);
				    System.out.println(TaskID);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		openButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();
		        FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "TXT文件", "txt");
		        chooser.setFileFilter(filter);
		        int returnVal = chooser.showOpenDialog(chooser);
		        if(returnVal == JFileChooser.APPROVE_OPTION) {
		      
		        	selectedfilepath =chooser.getSelectedFile().getAbsolutePath();//所选文件绝对路径
		        	pathTextField.setText(selectedfilepath);
		            weblist =readtxt(selectedfilepath);
		            paramString = processlisttoparam(weblist);
		            
		        	
		        }
		         
				
			}
		});
		getqueryresultButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String str=HttpClientPost2();
					JSONObject jsonObj = new JSONObject(str);
				    String reasonString = jsonObj.getString("Reason");
					JOptionPane.showMessageDialog(null,reasonString+"\n"+"请导出结果",null, 1);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		json2csv.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				json2csv(resultString);
			}
		});
		c.setLayout(new GridLayout(5,1));
		c.add(pathTextField);
		c.add(openButton);
		c.add(startqueryButton);
		c.add(getqueryresultButton);
		c.add(json2csv);
		this.setSize(500, 200);
		this.setVisible(true);
		
		
		
	}
	
	
	
	public static  List<String> readtxt(String path) {
		
		
		  String fileName = path;
	        //读取文件
	        List<String> lineLists = null;
	        try {
	            System.out.println(fileName);
	            lineLists = Files
	                    .lines(Paths.get(fileName), Charset.defaultCharset())
	                    .flatMap(line -> Arrays.stream(line.split("\n")))
	                    .collect(Collectors.toList());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	   
	        return  lineLists;
	    }

	public String processlisttoparam(List<String> list) {
		
		String param = "" ;
       
		for (String s:list) {
			param = param +s+"|";
		}
		
		
		return param;
		
	}
	
	
	public String HttpClientPost() throws Exception {
		String reasonString;
		String taskidString;
		String total;
	    
		// 获取默认的请求客户端
		CloseableHttpClient client = HttpClients.createDefault();
		// 通过HttpPost来发送post请求
		HttpPost httpPost = new HttpPost("http://apidata.chinaz.com/BatchAPI/NewDomain");
		/*
		 * post带参数开始
		 */
		// 第三步：构造list集合，往里面丢数据
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		BasicNameValuePair basicNameValuePair = new BasicNameValuePair("key", "a5d90fef0c70482fb3056953ab8aa25b");
		BasicNameValuePair basicNameValuePair2 = new BasicNameValuePair("domainNames", paramString);
		list.add(basicNameValuePair);
		list.add(basicNameValuePair2);
		// 第二步：我们发现Entity是一个接口，所以只能找实现类，发现实现类又需要一个集合，集合的泛型是NameValuePair类型
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(list);
		// 第一步：通过setEntity 将我们的entity对象传递过去
		httpPost.setEntity(formEntity);
		/*
		 * post带参数结束
		 */
		// HttpEntity
		// 是一个中间的桥梁，在httpClient里面，是连接我们的请求与响应的一个中间桥梁，所有的请求参数都是通过HttpEntity携带过去的
		// 通过client来执行请求，获取一个响应结果
		CloseableHttpResponse response = client.execute(httpPost);
		HttpEntity entity = response.getEntity();
		String str = EntityUtils.toString(entity, "UTF-8");
		System.out.println(str);
		  JSONObject jsonObj = new JSONObject(str);
	        TaskID = jsonObj.getString("TaskID");
	        taskidString = jsonObj.getString("TaskID");
	        reasonString = jsonObj.getString("Reason");
	        total = String.valueOf(jsonObj.getInt("Total"));
	        System.out.println(TaskID);
	        String[] arr = {reasonString,taskidString,total};
		
		// 关闭
		response.close();
		
		return str;

	}
		
	public String HttpClientPost2() throws Exception {
		// 获取默认的请求客户端
		CloseableHttpClient client = HttpClients.createDefault();
		// 通过HttpPost来发送post请求
		HttpPost httpPost = new HttpPost("http://apidata.chinaz.com/batchapi/GetApiData");
		/*
		 * post带参数开始
		 */
		// 第三步：构造list集合，往里面丢数据
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		BasicNameValuePair basicNameValuePair = new BasicNameValuePair("taskid", TaskID);
		list.add(basicNameValuePair);
		// 第二步：我们发现Entity是一个接口，所以只能找实现类，发现实现类又需要一个集合，集合的泛型是NameValuePair类型
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(list);
		// 第一步：通过setEntity 将我们的entity对象传递过去
		httpPost.setEntity(formEntity);
		/*
		 * post带参数结束
		 */
		// HttpEntity
		// 是一个中间的桥梁，在httpClient里面，是连接我们的请求与响应的一个中间桥梁，所有的请求参数都是通过HttpEntity携带过去的
		// 通过client来执行请求，获取一个响应结果
		CloseableHttpResponse response = client.execute(httpPost);
		HttpEntity entity = response.getEntity();
		String str = EntityUtils.toString(entity, "UTF-8");
		resultString = str;
		System.out.println(str);
	
		// 关闭
		response.close();
		return str;
		
	}
	
	
	public void json2csv(String json) {
		
        JSONObject output;
        JSONObject output2;
        try {
            output = new JSONObject(json);
            String str2 = output.getString("Result");
            output2= new JSONObject(str2);
            JSONArray docs = output2.getJSONArray("Data");
            File file=new File("c:\\fromJSON.csv");
            String csv = CDL.toString(docs);
            FileUtils.writeStringToFile(file, csv);
        } catch (JSONException e) {
        	 JOptionPane.showMessageDialog(null,"数据处理失败",null, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	JOptionPane.showMessageDialog(null,"数据处理失败",null, 1);
        } 
        catch (RuntimeException e) {
        	JOptionPane.showMessageDialog(null,"没有数据可导出",null, 1);
		}
		
	}
	
	
		
	}
	
	
	
	


