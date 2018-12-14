package com.qhjf.cfm.excel.adapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.RichTextString;

public class POIWorkbookVersionAdapter {
	public final static String HSSF_WORK_BOOK_KEY ="org.apache.poi.hssf.usermodel.HSSFWorkbook";
	public final static String XSSF_WORK_BOOK_KEY ="org.apache.poi.xssf.usermodel.XSSFWorkbook";
	private final static Map<String, POIWorkbookVersionAdapter> WORKBOOK_VERSION_MAP = new HashMap<>();
	private final static Map<String, Class<?>> CLIENT_ANCHOR_MAP = new HashMap<>();
	private final static Map<String, Class<?>> RICH_TEXT_STRING_MAP = new HashMap<>();
	private final static Map<String, String> VERSION_TEXT = new HashMap<>();
	private String workBookClassName;
	private ClientAnchor clientAnchor;
	private RichTextString richTextString;

	static {
		WORKBOOK_VERSION_MAP.put(HSSF_WORK_BOOK_KEY, new POIWorkbookVersionAdapter(HSSFWorkbook.class));
		WORKBOOK_VERSION_MAP.put(XSSF_WORK_BOOK_KEY, new POIWorkbookVersionAdapter(XSSFWorkbook.class));

		CLIENT_ANCHOR_MAP.put(HSSF_WORK_BOOK_KEY, HSSFClientAnchor.class);
		CLIENT_ANCHOR_MAP.put(XSSF_WORK_BOOK_KEY, XSSFClientAnchor.class);

		RICH_TEXT_STRING_MAP.put(HSSF_WORK_BOOK_KEY, HSSFRichTextString.class);
		RICH_TEXT_STRING_MAP.put(XSSF_WORK_BOOK_KEY, XSSFRichTextString.class);

		VERSION_TEXT.put(HSSF_WORK_BOOK_KEY, "EXCEL97");
		VERSION_TEXT.put(XSSF_WORK_BOOK_KEY, "EXCEL2007");
	}

	public static POIWorkbookVersionAdapter adapt(Workbook workbook) {
		return new POIWorkbookVersionAdapter(workbook.getClass());
	}

	private POIWorkbookVersionAdapter(Class<?> workbookClass) {
		this.workBookClassName = workbookClass.getName();
	}

	public ClientAnchor createClientAnchor() {
		this.clientAnchor = (ClientAnchor) reflect(CLIENT_ANCHOR_MAP.get(this.workBookClassName));
		return this.clientAnchor;
	}

	public RichTextString createRichTextString(String value) {
		this.richTextString = (RichTextString) reflect(RICH_TEXT_STRING_MAP.get(this.workBookClassName), value);
		return this.richTextString;
	}

	public String getVersionText() {
		return VERSION_TEXT.get(this.workBookClassName);
	}
	public String getVersionClass() {
		return this.workBookClassName;
	}

	private Object reflect(Class<?> clazz, Object... values) {
		Object result = null;
		try {
			Constructor<?> ctr = null;
			if(values.length>0) {
				Class<?>[] classes = new Class[values.length];
				for(int i=0;i<values.length;i++)
					classes[i] = values[i].getClass();

				ctr = clazz.getConstructor(classes);
				result = ctr.newInstance(values);
			} else {
				ctr = clazz.getConstructor();
				result = ctr.newInstance();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	public static void main(String[] args) {
		POIWorkbookVersionAdapter adapt = POIWorkbookVersionAdapter.adapt(new HSSFWorkbook());
		System.out.println(adapt.createClientAnchor().getClass());
		System.out.println(adapt.createRichTextString("aaa").getString());
	}

}