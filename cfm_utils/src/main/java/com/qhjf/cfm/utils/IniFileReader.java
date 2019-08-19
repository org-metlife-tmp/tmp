package com.qhjf.cfm.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniFileReader {

	protected HashMap<String, Properties> sections = new HashMap<String, Properties>();
	private transient String currentSecion;
	private transient Properties current;

	public IniFileReader(String filename) throws IOException {
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filename), "UTF-8");  
		BufferedReader reader = new BufferedReader(isr);  
		read(reader);
		reader.close();
	}

	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			parseLine(line);
		}
	}

	protected void parseLine(String line) {
		line = line.trim();		
		if (line.matches("\\[.*\\]")) {
			currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
			current = new Properties();
			sections.put(currentSecion, current);
		}else if (line.matches("^#[^#]*$")){
			
		} 
		else {
			Pattern line_pattern = Pattern.compile("([^#=]*)=([^#]*)(#)?.*");
			Matcher matcher =line_pattern.matcher(line);
			if(matcher.matches()){
				if (current != null) {
					String name = matcher.group(1).trim();
					String value = matcher.group(2).trim();
					current.setProperty(name, value);
				}
			}
		}
	}


	public boolean isContain(String section){
		return sections.containsKey(section);
	}


	public Properties getSection(String section){
		return sections.get(section);
	}


	public Map<String, Properties> getSectionByRegx(String regex){
		Map<String, Properties> result = new HashMap<>();

		for (String key : sections.keySet()) {
			if(key.matches(regex)){
				result.put(key,sections.get(key));
			}
		}
		return result;
	}


	public String getValue(String section, String name) {
		Properties p = (Properties) sections.get(section);

		if (p == null) {
			return null;
		}
		String value = p.getProperty(name);
		return value;
	}


	public List<String> getAllSectionNames(){
		List<String> result = null;
		if(sections != null && sections.size() > 0){
			result = new ArrayList<>();
			for (String name : sections.keySet()) {
				result.add(name);
			}
		}
		return result;
	}


}
