package batchquery;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
public class mainUI extends JFrame {
	
	JTextField pathTextField;
	JButton openButton;
	JButton startqueryButton;
	JButton getqueryresultButton;
	JButton json2csv;
	JRadioButton ICPradiobutton;
	JRadioButton tdkButton;
	JPanel radioPanel;
	JPanel radioPanel2;
	JRadioButton whoisButton;
	JRadioButton ipinfoButton;
	JRadioButton zhubanButton;
	ButtonGroup group;
	String selectedfilepath;
	String selectstatuString;
	static String iplexString = "ips";
	static String domainlexString = "domainNames";
	static String companyString = "companyName";
	static List<String> weblist; 
	static ArrayList<String> paramString;
	static String TaskID;
	static String resultString;
	static ArrayList<String>taskidArrayList;
	static String[][] arrjsonArrayList;//表格数据二维数组
	private static String ICPURL="http://apidata.chinaz.com/BatchAPI/NewDomain";
	private static String TDKURL="http://apidata.chinaz.com/BatchAPI/SiteData";
	private static String WHOISURL="http://apidata.chinaz.com/BatchAPI/Whois";
	private static String ipnifoURL="http://apidata.chinaz.com/BatchAPI/IP";
	private static String zhubanURL="http://apidata.chinaz.com/BatchAPI/NewSponsorUnit";
	JMenuBar jBar;
	JMenu jMenu;
	JMenuItem jItem;
	
	
	static String[][] queryresultdata;
	public mainUI() {
	// TODO Auto-generated constructor stub
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		jMenu  = new JMenu("帮助");
		jBar = new JMenuBar();
		jBar.add(jMenu);
		Container c = getContentPane();
		ICPradiobutton = new JRadioButton("查询ICP备案信息（实时）");
		ICPradiobutton.setSelected(true);
		tdkButton = new JRadioButton("查询网站相关信息");
		whoisButton = new JRadioButton("whois查询");
	    ipinfoButton = new JRadioButton("查询ip归属");
	    zhubanButton = new JRadioButton("查询主办单位信息(实时)");
		group = new ButtonGroup();
		group.add(ICPradiobutton);
		group.add(tdkButton);
		group.add(whoisButton);
		group.add(ipinfoButton);
		group.add(zhubanButton);
		radioPanel = new JPanel();
		radioPanel2 = new JPanel();
		radioPanel.add(ICPradiobutton);
		radioPanel.add(tdkButton);
		radioPanel.add(whoisButton);
		radioPanel2.add(ipinfoButton);
		radioPanel2.add(zhubanButton);
		pathTextField = new JTextField();
		pathTextField.setEditable(false);
		openButton = new JButton("打开文件");
		startqueryButton =new JButton("提交查询任务");
		getqueryresultButton =new JButton("获取查询结果");
		json2csv = new JButton("导出csv");
		startqueryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				// TODO Auto-generated method stub
				
				try {
					
					String urlString = null;
					if (ICPradiobutton.isSelected()) {
						
						 urlString = ICPURL;
						
						
					}else if (tdkButton.isSelected()) {
						
						 urlString = TDKURL;
						
					}else if (whoisButton.isSelected()) {
						
					  	 urlString=WHOISURL;
						
					}
					else if (ipinfoButton.isSelected()) {
						 urlString=ipnifoURL;
					}
					else if (zhubanButton.isSelected()) {
						urlString=zhubanURL;
						
					}
						
					
					arrjsonArrayList=new String[paramString.size()][4];
					for (int j = 0; j < paramString.size(); j++) {
					String str;
					if (ipinfoButton.isSelected()) {
						
						str=HttpClientPost(urlString,paramString.get(j),iplexString);
						
					} else if (zhubanButton.isSelected()) {
						str=HttpClientPost(urlString,paramString.get(j),companyString);
					} 

					else {
						str=HttpClientPost(urlString,paramString.get(j),domainlexString);
						
					}
                     						
					
    					JSONObject jsonObj = new JSONObject(str);
    					String statuscode = jsonObj.getString("StateCode");
    				    String taskString = jsonObj.getString("TaskID");
    				    String reasonString = jsonObj.getString("Reason");
    				    String total=String.valueOf(jsonObj.getInt("Total"));
    				    
    				    arrjsonArrayList[j][0]=statuscode;
    				    arrjsonArrayList[j][1]=taskString;
    				    arrjsonArrayList[j][2]=reasonString;
    				    arrjsonArrayList[j][3]=total;
    				   //JOptionPane.showMessageDialog(null,reasonString+"\n"+"任务ID:"+taskString+"\n"+"成功总条数："+String.valueOf(total)+"\n"+"请稍后查询结果",null, 1);
    				    
    				 
    					
						
				
					}
					
					JOptionPane.showMessageDialog(null,"提交成功,请稍后查看结果",null, 1);
                    new Taskidlist(arrjsonArrayList);
                    	
                    	
					
				    
				    
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null,"没有数据可提交",null, 1);
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
		       //     System.out.println(paramString);
		            
		        	
		        }
		         
				
			}
		});
		getqueryresultButton.addActionListener(new ActionListener()  {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					taskidArrayList=  new ArrayList<String>();
					for (int i = 0; i <paramString.size() ; i++) {
						
						
						String str=HttpClientPost2(arrjsonArrayList[i][1]);
						taskidArrayList.add(str);
						
					}
					
					queryresultdata = new String[taskidArrayList.size()][3];
					for (int i = 0; i < taskidArrayList.size(); i++) {
						
						JSONObject jsonObj = new JSONObject(taskidArrayList.get(i));
					    String reasonString = jsonObj.getString("Reason");
					    String statuscode = String.valueOf(jsonObj.getInt("StateCode"));
					 /*   JSONObject result=jsonObj.getJSONObject("Result");
					    String submittime = result.getString("SubmitTime");
					    String finishtime = result.getString("FinishedTime");
					    String submitcountString = String.valueOf(result.getInt("SubmitCount"));
					    String successedcount =String.valueOf(result.getInt("SuccessCount")) ;*/
					    
					    queryresultdata[i][1]=arrjsonArrayList[i][1];
					    queryresultdata[i][0]=statuscode;
					    queryresultdata[i][2]=reasonString;
					  /*  queryresultdata[i][3]=submittime;
					    queryresultdata[i][4]=finishtime;;
					    queryresultdata[i][5]=submitcountString;
					    queryresultdata[i][6]=successedcount;*/
					}
					
					new Taskidlist2(queryresultdata);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null,"没有结果可查询",null, 1);
				}
				
			}
		});
		json2csv.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				json2csv();
			}
		});
		c.setLayout(new GridLayout(8,1));
		c.add(jBar);
		c.add(pathTextField);
		c.add(openButton);
		c.add(startqueryButton);
		c.add(getqueryresultButton);
		c.add(json2csv);
		c.add(radioPanel);
		c.add(radioPanel2);
		this.setSize(500, 300);
		this.setResizable(false);
		this.setTitle("网络炒汇平台筛查工具");
		this.setVisible(true);
		
	}
	
	
	
	public static  List<String> readtxt(String path) {
		
		
		  String fileName = path;
	        //读取文件
	        List<String> lineLists = null;
	        try {
	        //   System.out.println(fileName);
	            lineLists = Files
	                    .lines(Paths.get(fileName), Charset.defaultCharset())
	                    .flatMap(line -> Arrays.stream(line.split("\n")))
	                    .collect(Collectors.toList());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	   
	        return  lineLists;
	    }

	public ArrayList<String> processlisttoparam(List<String> list) {
		String param = "" ;
		ArrayList<String> processedparamArrayList = new ArrayList<String>();
		String[][] paramarrStrings;
		int i=0;
		int y=0;
		boolean outpoint=false;
		ArrayList<String> arr2 = new ArrayList<String>();
		while (true) {
			
			
			
			for (y=1; y <= 50; y++) {
				
        		
				if (i==list.size()) {
					
					if (arr2.isEmpty()) {
						
						return processedparamArrayList;
					}
					
					outpoint = true; 
					break;
					
				}
				arr2.add(list.get(i++));
			
			}
		
	       
			for (String s:arr2) {
				param = param +s+"|";
			}
			System.out.println(param.length());
			param = param.substring(0, param.length() - 1);
			processedparamArrayList.add(param);
			
			param="";
			arr2.clear();
	
			if (outpoint) {
				
			}
			
			//System.out.println("...");
			
		}
		
		
	
		
	}
	
	
	public String HttpClientPost(String URL,String paramString,String lexString) throws Exception {
		String reasonString;
		String taskidString;
		String total;
	    
		// 获取默认的请求客户端
		CloseableHttpClient client = HttpClients.createDefault();
		// 通过HttpPost来发送post请求
		HttpPost httpPost = new HttpPost(URL);
		/*
		 * post带参数开始
		 */
		// 第三步：构造list集合，往里面丢数据
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		BasicNameValuePair basicNameValuePair = new BasicNameValuePair("key", "36b4918f04ac41108cbf680fac51ad59");
		BasicNameValuePair basicNameValuePair2 = new BasicNameValuePair(lexString, paramString);
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
	//	System.out.println(str);
		  JSONObject jsonObj = new JSONObject(str);
	        TaskID = jsonObj.getString("TaskID");
	        taskidString = jsonObj.getString("TaskID");
	        reasonString = jsonObj.getString("Reason");
	        total = String.valueOf(jsonObj.getInt("Total"));
	  //      System.out.println(TaskID);
	        @SuppressWarnings("unused")
			String[] arr = {reasonString,taskidString,total};
		
		// 关闭
		response.close();
		
		return str;

	}
		
	public String HttpClientPost2(String taskid) throws Exception {
		// 获取默认的请求客户端
		CloseableHttpClient client = HttpClients.createDefault();
		// 通过HttpPost来发送post请求
		HttpPost httpPost = new HttpPost("http://apidata.chinaz.com/batchapi/GetApiData");
		/*
		 * post带参数开始
		 */
		// 第三步：构造list集合，往里面丢数据
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		BasicNameValuePair basicNameValuePair = new BasicNameValuePair("taskid", taskid);
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
	//	System.out.println(str);
	
		// 关闭
		response.close();
		return str;
		
	}
	
	
	@SuppressWarnings("deprecation")
	public void json2csv() {
		
        JSONObject output;
        JSONObject output2;
        
        try {
        	for (int i = 0; i < taskidArrayList.size(); i++) {
        		
        		 output = new JSONObject(taskidArrayList.get(i));
                 String str2 = output.getString("Result");
                 output2= new JSONObject(str2);
                 JSONArray docs = output2.getJSONArray("Data");
                 File file=new File("c:\\result"+i+".csv");
                 String csv = CDL.toString(docs);
                 FileUtils.writeStringToFile(file, csv);
        		
			}
           
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
	
	
	
	


