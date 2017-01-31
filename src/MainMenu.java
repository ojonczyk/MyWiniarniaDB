import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class MainMenu {

	public static DefaultTableModel buildTableModel(ResultSet rs)
			throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		// names of columns
		Vector<String> columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		for (int column = 1; column <= columnCount; column++) {
			columnNames.add(metaData.getColumnName(column));
		}

		// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				vector.add(rs.getObject(columnIndex));
			}
			data.add(vector);
		}

		return new DefaultTableModel(data, columnNames);

	}
	static JFrame frame;
	private static void createMenuButton(JPanel panel, String tableName){
		JButton button = new JButton(tableName);
		button.setPreferredSize(new Dimension(200, 45));
		button.addActionListener(new ActionListener() {  
			public void actionPerformed(ActionEvent e)
			{
				tableWindow(tableName);
			}});
		panel.add(button);

	}
	private static void createMainMenu(){

		JFrame frame = new JFrame("MyWiniarnia");
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		Connection con = TestDriver.getConnection();
		try {
			DatabaseMetaData md = con.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				createMenuButton(panel, rs.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setVisible(true);
		frame.pack();
	}
	private static void tableWindow(String tableName){
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Connection con = TestDriver.getConnection();
			stmt = con.createStatement(); 
			rs = stmt.executeQuery("SELECT * FROM "+tableName);

		} catch (SQLException e) {
			e.printStackTrace();
		}


		JTable table;
		try {
			table = new JTable(buildTableModel(rs));
			JScrollPane pane = new JScrollPane(table);
			JFrame frame = new JFrame(tableName);
			JPanel panel = new JPanel();
			JPanel buttons = new JPanel();
			JButton addRecord = new JButton("Add Record");
			buttons.add(addRecord);
			addRecord.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFrame frame2 = new JFrame("Add Record");
					JPanel panel2 = new JPanel();
					panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

					try {
						Connection con = TestDriver.getConnection();
						Statement stmt = con.createStatement();
						ResultSet rs = stmt.executeQuery("SELECT * FROM "+tableName+" limit 1");
						ResultSetMetaData rsmd = rs.getMetaData();
						int columnCount = rsmd.getColumnCount();
						Vector<JTextField> vec = new Vector<JTextField>();
						for (int i = 1; i <= columnCount; i++ ) {
							JPanel tmppanel= new JPanel();
							tmppanel.setLayout(new BorderLayout());
							JLabel tmp = new JLabel(rsmd.getColumnName(i)+":");
							JTextField tmptext = new JTextField(rsmd.getColumnTypeName(i),60);
							tmptext.addFocusListener(new FocusListener() {
								
								@Override
								public void focusLost(FocusEvent e) {
									// TODO Auto-generated method stub
									
								}
								
								@Override
								public void focusGained(FocusEvent e) {
									tmptext.selectAll();
									
								}
							});
							tmppanel.add(tmp, BorderLayout.WEST);
							tmppanel.add(tmptext, BorderLayout.EAST);
							vec.add(tmptext);
							panel2.add(tmppanel);
						}
						JButton tmpAddBut = new JButton("Add Record");
						buttons.add(tmpAddBut);
						tmpAddBut.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								String tmpQuery= "INSERT INTO "+tableName+" VALUES (";
								for ( JTextField tex : vec) {
									tmpQuery=tmpQuery+"\""+tex.getText()+"\""+",";
								}
								tmpQuery = tmpQuery.substring(0, tmpQuery.length()-1)+");";
								try {
									stmt.executeUpdate(tmpQuery);
									JOptionPane.showMessageDialog(frame, "Record added");
								} catch (SQLException e1) {
									JOptionPane.showMessageDialog(frame, e1.getMessage(),"DBError",JOptionPane.ERROR_MESSAGE);
									e1.printStackTrace();
								}
								frame2.dispose();
								frame.dispose();
								tableWindow(tableName);
							}
						});
						panel2.add(tmpAddBut);
					} catch (SQLException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					frame2.add(panel2);
					frame2.setVisible(true);
					frame2.pack();
					frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
			});
			JButton removeRecord = new JButton("Remove Record");
			removeRecord.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String sStmt = "DELETE FROM "+tableName+" WHERE ";
					for(int i=0;i<table.getColumnCount();++i){
						sStmt+=table.getColumnName(i)+"="+table.getModel().getValueAt(table.getSelectedRow(), i);
						if (i!=table.getColumnCount()-1) {
							sStmt+=" AND ";
						} else {
							sStmt+=";";
						}
					}
					JOptionPane.showMessageDialog(frame, "Record deleted");
					try {
						Connection con = TestDriver.getConnection();
						Statement stmt = con.createStatement();
						stmt.executeUpdate(sStmt);
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					frame.dispose();
					tableWindow(tableName);
				}
			});
			buttons.add(removeRecord);
			buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			//options
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			panel.add(pane);
			frame.add(panel);
			panel.add(buttons);
			frame.setVisible(true);
			frame.pack();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createMainMenu();

			}
		});

	}

}
