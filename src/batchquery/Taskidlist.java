package batchquery;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Taskidlist extends JFrame {
          JTable table;
          JScrollPane scrollPane;
    Taskidlist(String[][] paramStrings){
    	
    	JPanel panel = new JPanel(new BorderLayout());

        // 表头（列名）
        Object[] columnNames = {"状态码", "任务ID", "结果", "提交成功数"};

        // 表格所有行数据
        Object[][] rowData =paramStrings;

        // 创建一个表格，指定 所有行数据 和 表头
        table = new JTable(rowData, columnNames);

        // 把 表头 添加到容器顶部（使用普通的中间容器添加表格时，表头 和 内容 需要分开添加）
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        // 把 表格内容 添加到容器中心
        panel.add(table, BorderLayout.CENTER);

        this.setContentPane(panel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setSize(500,200);
        
    }
	
	
	
	
}
