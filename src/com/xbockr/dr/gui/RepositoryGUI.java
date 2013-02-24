package com.xbockr.dr.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.xbockr.dr.repository.Repository;
import com.xbockr.dr.repository.meta.MetaInfo;
import com.xbockr.dr.util.MetaInfoListPrinter;
import com.xbockr.dr.util.OutHelper;

public class RepositoryGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Repository repository = null;

	private JTable table;

	public RepositoryGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final RepositoryGUI pthis = this;
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.setFillsViewportHeight(true);
		table.setColumnSelectionAllowed(false);
		scrollPane.setViewportView(table);

		JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);

		JButton btnChangeRepository = new JButton("Change Repository");
		btnChangeRepository.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = chooseFile("Choose Repository",
						JFileChooser.DIRECTORIES_ONLY);
				if (f != null) {
					pthis.setRepository(f);
				}
			}
		});
		toolBar.add(btnChangeRepository);

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int retVal = fc.showDialog(pthis, "Add");
				if (retVal == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					JTextField nameField = new JTextField();
					String suggestedName = pthis.repository.getUniqueName(f
							.getName());
					nameField.setText(suggestedName);
					JTextArea descriptionField = new JTextArea();
					JCheckBox checkBox = new JCheckBox("Move");
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
					panel.add(new JLabel("Enter name here..."));
					panel.add(nameField);
					panel.add(new JLabel("Enter description here..."));
					panel.add(descriptionField);
					panel.add(checkBox);
					int rv = JOptionPane.showConfirmDialog(pthis, panel,
							"Enter name", JOptionPane.OK_CANCEL_OPTION);
					if (rv == 0) {
						String name = nameField.getText();
						String description = descriptionField.getText();
						boolean move = checkBox.isSelected();
						MetaInfo metaInfo = MetaInfo.createFromAdd(name,
								description);
						try {
							repository.add(metaInfo, f, move);
							// JOptionPane.showMessageDialog(pthis,
							// "Successfully added " + f.getName());
						} catch (RuntimeException ex) {
							// TODO
						}
					}
				}
				refreshTable();
			}
		});
		toolBar.add(btnAdd);

		JButton btnReplace = new JButton("Replace");
		btnReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int sr = pthis.table.getSelectedRow();
				if (sr != -1) {
					String name = (String) pthis.table.getModel().getValueAt(
							sr, 0);
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					int retVal = fc.showDialog(pthis, "Replace");
					if (retVal == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						JTextArea descriptionField = new JTextArea();
						JCheckBox checkBox = new JCheckBox("Move");
						JPanel panel = new JPanel();
						panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
						panel.add(new JLabel("Enter description here..."));
						panel.add(descriptionField);
						panel.add(checkBox);
						int rv = JOptionPane.showConfirmDialog(pthis, panel,
								"Enter description",
								JOptionPane.OK_CANCEL_OPTION);
						if (rv == 0) {
							String description = descriptionField.getText();
							boolean move = checkBox.isSelected();
							try {
								repository.replace(name, f, description, move);
								// JOptionPane.showMessageDialog(pthis,
								// "Successfully added " + f.getName());
							} catch (RuntimeException ex) {
								// TODO
							}
						}
					}
				}
				refreshTable();
			}
		});
		toolBar.add(btnReplace);

		JButton btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int sr = pthis.table.getSelectedRow();
				if (sr != -1) {
					String name = (String) pthis.table.getModel().getValueAt(
							sr, 0);
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int retVal = fc.showDialog(pthis, "Export");
					if (retVal == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						repository.export(name, f);
					}
				}
			}
		});
		toolBar.add(btnExport);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sr = pthis.table.getSelectedRow();
				if (sr != -1) {
					String name = (String) pthis.table.getModel().getValueAt(
							sr, 0);
					repository.delete(name);
				}
				refreshTable();
			}
		});
		toolBar.add(btnDelete);

		pack();

		OutHelper.toStdOut(false);
		OutHelper.toGUI(true);

		File f = null;
		while (f == null) {
			f = chooseFile("Choose Repository", JFileChooser.DIRECTORIES_ONLY);
		}
		pthis.setRepository(f);
	}

	private void refreshTable() {
		if (repository != null) {
			List<MetaInfo> metaInfos = repository.list();
			table.setModel(createTableModel(metaInfos));
		}
	}

	private void setRepository(File repositoryPath) {
		if (repositoryPath.exists()
				&& repositoryPath.isDirectory()
				&& (repository == null || repository.getPath() != repositoryPath)) {
			try {
				Repository rep = new Repository(repositoryPath, true);
				this.repository = rep;
			} catch (RuntimeException ex) {
				// TODO display error to user
			}
		}
		refreshTable();
	}

	private File chooseFile(String message, int mode) {
		File f = null;
		String[] buttons = { "OK", "Cancel", "Broswe" };
		JComboBox<File> comboBox = new JComboBox<File>();
		if (repository != null) {
			comboBox.addItem(repository.getPath());
		}
		Properties props = new Properties();
		File pf = new File(".drrc");
		String lastReps = "";
		if (pf.exists() && pf.isFile()) {
			try {
				props.load(new FileReader(pf));
				lastReps = props.getProperty("lastRepositories", "");
				if (!lastReps.equals("")) {
					String[] lr = lastReps.split(";");
					for (String s : lr) {
						comboBox.addItem(new File(s));
					}
				}
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}

		comboBox.setEditable(true);
		int choice = JOptionPane.showOptionDialog(null, comboBox, message,
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				buttons, buttons[0]);
		switch (choice) {
		case -1:
			break;
		case 0: {
			f = comboBox.getItemAt(0);
		}
			break;
		case 1:
			break;
		case 2: {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(mode);
			int retVal = fc.showDialog(this, "Select");
			if (retVal == JFileChooser.APPROVE_OPTION) {
				f = fc.getSelectedFile();
			}
		}
			break;
		}
		if (f != null) {
			boolean already = false;
			for (String s : lastReps.split(";")) {
				if (s.equals(f.getAbsolutePath())) {
					already = true;
					break;
				}
			}
			if (!already) {
				if (!lastReps.equals("")) {
					lastReps += ";";
				}
				lastReps += f.getAbsolutePath();
				try {
					props.setProperty("lastRepositories", lastReps);
					props.store(new FileWriter(".drrc"), null);
				} catch (IOException e) {
				}
			}
		}
		return f;
	}

	private TableModel createTableModel(List<MetaInfo> metaInfos) {
		int numberOfRows = metaInfos.size();
		int numberOfCols = MetaInfoListPrinter.HEADERS.length;
		Object[][] data = new Object[numberOfRows][numberOfCols];
		int rowIndex = 0;
		MetaInfoListPrinter listGetter = new MetaInfoListPrinter(metaInfos);
		for (MetaInfo mi : metaInfos) {
			List<String> list = listGetter.getStringList(mi);
			for (int colIndex = 0; colIndex < 6; colIndex++) {
				data[rowIndex][colIndex] = list.get(colIndex);
			}
			rowIndex++;
		}
		return new DefaultTableModel(data, MetaInfoListPrinter.HEADERS);
	}

}
