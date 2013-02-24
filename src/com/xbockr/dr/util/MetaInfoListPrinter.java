package com.xbockr.dr.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.xbockr.dr.repository.meta.MetaInfo;

public class MetaInfoListPrinter {
	protected List<MetaInfo> metaInfos;
	public static final String[] HEADERS = { "Name", "Original Name",
			"Timestamp", "Number of Files", "Size", "Description" };

	public MetaInfoListPrinter(List<MetaInfo> metaInfos) {
		this.metaInfos = new ArrayList<MetaInfo>(metaInfos);
		Collections.sort(this.metaInfos, new Comparator<MetaInfo>() {

			@Override
			public int compare(MetaInfo o1, MetaInfo o2) {
				return (int) (o2.getTimestamp() - o1.getTimestamp());
			}
		});
	}
	
	private int[] getColumnSizes(){
		int[] is = new int[HEADERS.length];
		for(int i=0;i<is.length;i++){
			is[i] = HEADERS[i].length();
		}
		for(MetaInfo mi : this.metaInfos){
			List<String> sl = getStringList(mi);
			for(int i=0;i<is.length;i++){
				int l = sl.get(i).length();
				if(l > is[i]){
					is[i] = l;
				}
			}
		}
		return is;
	}
	
	public List<String> getStringList(MetaInfo metaInfo){
		List<String> sl = new ArrayList<String>();
		sl.add(metaInfo.getName());
		sl.add(metaInfo.getOriginalName());
		sl.add(new MessageFormat("{0,date,yyyy-MM-dd HH:mm:ss}").format(new Object[]{metaInfo.getTimestamp()}));
		sl.add("" + metaInfo.getNumberOfFiles());
		sl.add("" + metaInfo.getSize());
		sl.add(metaInfo.getDescription());
		return sl;
	}

	public String tabPrint() {
		StringBuilder sb=  new StringBuilder();
		for(String hs : HEADERS){
			sb.append(hs);
			sb.append('\t');
		}
		int l = sb.length();
		sb.replace(l-1, l, "\n");
		for(MetaInfo mi : metaInfos){
			for(String s : getStringList(mi)){
				sb.append(s);
				sb.append('\t');
			}
			int sbl = sb.length();
			sb.replace(sbl-1, sbl, "\n");
		}
		return sb.toString();
	}
	
	private String getCell(String string, int size, boolean rightAlign){
		StringBuilder sb = new StringBuilder();
		sb.append(' ');
		if(!rightAlign){
		sb.append(string);
		while(sb.length() < size){
			sb.append(' ');
		}
		}else{
			while(sb.length() < size - string.length() - 1){
				sb.append(' ');
			}
			sb.append(string);
			sb.append(' ');
		}
		sb.append('|');
		return sb.toString();
	}
	
	private String getCell(String string, int size){
		return this.getCell(string, size, false);
	}

	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		int[] sizes = getColumnSizes();
		for(int i = 0;i<sizes.length;i++){
			sizes[i]+=2;
			sb.append(getCell(HEADERS[i],sizes[i]));
		}
		int sbl = sb.length();
		sb.replace(sbl-1, sbl, "\n");
		for(int i : sizes){
			for(int j=0;j<i;j++){
				sb.append('-');
			}
			sb.append('+');
		}
		sbl = sb.length();
		sb.replace(sbl-1, sbl, "\n");
		for(MetaInfo mi : this.metaInfos){
			List<String> sl = getStringList(mi);
			for(int i=0;i<sl.size();i++){
				sb.append(getCell(sl.get(i), sizes[i], i==3||i==4));
			}
			sbl = sb.length();
			sb.replace(sbl-1, sbl, "\n");
		}
		return sb.toString();
	}

	public void print(boolean pretty) {
		String s;
		if (pretty) {
			s = this.prettyPrint();
		} else {
			s = this.tabPrint();
		}
		System.out.print(s);
	}
}
