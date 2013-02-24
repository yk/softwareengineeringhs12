package com.xbockr.dr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;

public class HelpMsg {
	public static String getHelpMsg() {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			try {
				JarFile jar = null;
				try {
					jar = new JarFile(new File("data-repository.jar"));
					JarEntry jarEntry = jar.getJarEntry("help.txt");
					InputStream resourceAsStream = jar.getInputStream(jarEntry);
					br = new BufferedReader(new InputStreamReader(
							resourceAsStream));
				} finally {
					IOUtils.closeQuietly(jar);
				}
			} catch (FileNotFoundException e) {
				File f = new File("./files/help.txt");
				br = new BufferedReader(new FileReader(f));
			}

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading help message");
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				// silent
			}
		}
		return sb.toString();

	}
}
