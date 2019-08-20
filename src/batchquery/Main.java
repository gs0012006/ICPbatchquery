package batchquery;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

String filepath ="https://pmmomo.xyz:456/2.txt";
try {
    URL pathUrl = new URL(filepath);
    HttpURLConnection urlcon = (HttpURLConnection) pathUrl.openConnection();
    if(urlcon.getResponseCode()>=400){ 
       System.out.println("文件不存在");
       JOptionPane.showMessageDialog(null,"连接失败",null, 1);
       System.exit(1);
    }else{
       System.out.println("文件存在");
       JFrame mainUIFrame;
		mainUIFrame = new mainUI();
   }
}catch (Exception e) {
    System.out.print("请求失败");
} 	

		
	}

}
