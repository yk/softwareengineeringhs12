package com.xbockr.dr.gui;

import javax.swing.SwingUtilities;

public class RepositoryGUITester {

	public static void main(String args[]) {
		/* Create and display the form */
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new RepositoryGUI().setVisible(true);
			}
		});

	}

}
