package com.xbockr.dr.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.xbockr.dr.repository.meta.MetaInfo;

public class MetaInfoListHtmlPrinter extends MetaInfoListPrinter {


	private static final String[] headers = { "Name", "Original Name",
			"Timestamp", "Number of Files", "Size", "Description" };

	private File htmlOverview;


	public MetaInfoListHtmlPrinter(File htmlOverview, List<MetaInfo> metaInfos) {
		super(metaInfos);

		this.htmlOverview = htmlOverview;

		
	}

	public List<String> getStringList(MetaInfo metaInfo) {
		List<String> sl = new ArrayList<String>();
		sl.add(metaInfo.getName());
		sl.add(metaInfo.getOriginalName());
		sl.add(new MessageFormat("{0,date,yyyy-MM-dd HH:mm:ss}")
				.format(new Object[] { metaInfo.getTimestamp() }));
		sl.add("" + metaInfo.getNumberOfFiles());
		sl.add("" + metaInfo.getSize());
		sl.add(metaInfo.getDescription());
		return sl;

	}

	// TODO : HTML table print
	public void printHtml() throws FileNotFoundException {
		PrintWriter htmlWriter = null;
		try {
			htmlWriter = new PrintWriter(htmlOverview);
			htmlWriter.write("<html>\n");
			htmlWriter.write("<body>\n");
			htmlWriter.write("<h1>html overview</h1>\n");
			htmlWriter.write("<table border=\"1\">\n");

			htmlWriter.write("<tr>");
			for (String hs : headers) {
				htmlWriter.write("<th>" + hs + "</th>");
			}
			htmlWriter.write("</tr>\n");

			for (MetaInfo mi : metaInfos) {
				htmlWriter.write("<tr>");
				for (String s : getStringList(mi)) {
					htmlWriter.write("<td>" + s + "</td>");
				}
				htmlWriter.write("</tr>\n");
			}

			htmlWriter.write("</table>\n");
			htmlWriter.write("</body>\n");
			htmlWriter.write("</html>\n");
		} finally {
			IOUtils.closeQuietly(htmlWriter);
		}
	}

	public void print() throws IOException {
		this.printHtml();
	}

}
