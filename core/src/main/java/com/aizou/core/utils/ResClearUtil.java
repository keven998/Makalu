package com.aizou.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * 描述:Res资源清理类<br>
 * 
 * 清理项目中没有使用的多余垃圾资源<br>
 * 
 * 使用该类前，首先使用android Lint 对项目进行检测，并将结果导入到指定文件<br>
 *
 * @author xby
 *
 * 2013-11-15
 */
public class ResClearUtil {
	private static final String RES_LAYOUT = "res\\layout";
	
	//实例  lint --check UnusedResources D:\EclipseWorkspaceNewNew\DriveControl >d:\test.txt
	//lint –-check 检查项ID 工程目录 >d:/Temp/res.txt   未使用资源ID UnusedResources
	/** SourceLocation: 工程项目目录*/
	private static final String SourceLocation = "D:\\EclipseWorkspaceNew\\testsqliteimage\\";
	private static final String ENTER = "\n";
	private static final String WARNING = ": Warning:";
	private static final String RES_DRAWABLE = "res\\drawable";
	private static final String RES_DRAWABLEXXH = "res\\drawable-xxhdpi";
	private static final String RES_DRAWABLEXH = "res\\drawable-xhdpi";
	private static final String RES_DRAWABLEH = "res\\drawable-hdpi";
	private static final String RES_DRAWABLEM = "res\\drawable-mdpi";
	private static final String LINT_OUTPUT_FILE = "D:/test.txt";
	private static final String DEFAULT_ENCODE = "UTF-8";
	private static final String RES_VALUES_STRINGS_XML = "res\\values\\strings.xml";
	private static final String RES_VALUES_STRINGS = "res\\values\\strings";

	/**
	 * 描述:清理多余的xml布局文件
	 */
	public static void clearUnusedLayoutXml() {
		try {
			FileInputStream fis = new FileInputStream(LINT_OUTPUT_FILE);
			InputStreamReader isr = new InputStreamReader(fis, "GBK");
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (line.indexOf(RES_LAYOUT) != -1 && line.indexOf(WARNING) != -1) {
					String fileName = line.substring(line.indexOf(RES_LAYOUT), line.indexOf(WARNING));
					File file = new File(SourceLocation + fileName);
					System.out.println(SourceLocation + fileName + " 文件是否存在 " + file.exists());
					if (file.exists()) {
						file.delete();
						System.out.println("  删除文件     " + fileName + " 成功");
						i++;
					}
				}
			}
			System.out.println("  删除总个数     " + i);
			br.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 描述:清理无用的图片资源
	 */
	public static void clearUnusedDrawble() {
		try {
			FileInputStream fis = new FileInputStream(LINT_OUTPUT_FILE);
			InputStreamReader isr = new InputStreamReader(fis, "GBK");
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (line.indexOf(RES_DRAWABLE) != -1 && line.indexOf(WARNING) != -1) {
					String fileName = line.substring(line.indexOf(RES_DRAWABLE), line.indexOf(WARNING));
					File file = new File(SourceLocation + fileName);
					System.out.println(SourceLocation + fileName + " 文件是否存在 " + file.exists());
					if (file.exists()) {
						file.delete();
						System.out.println("  删除文件     " + fileName + " 成功");
						i++;
					}
				} else if (line.indexOf(RES_DRAWABLEXXH) != -1 && line.indexOf(WARNING) != -1) {
					String fileName = line.substring(line.indexOf(RES_DRAWABLEXXH), line.indexOf(WARNING));
					File file = new File(SourceLocation + fileName);
					System.out.println(SourceLocation + fileName + " 文件是否存在 " + file.exists());
					if (file.exists()) {
						file.delete();
						System.out.println("  删除文件     " + fileName + " 成功");
						i++;
					}
				} else if (line.indexOf(RES_DRAWABLEXH) != -1 && line.indexOf(WARNING) != -1) {
					String fileName = line.substring(line.indexOf(RES_DRAWABLEXH), line.indexOf(WARNING));
					File file = new File(SourceLocation + fileName);
					System.out.println(SourceLocation + fileName + " 文件是否存在 " + file.exists());
					if (file.exists()) {
						file.delete();
						System.out.println("  删除文件     " + fileName + " 成功");
						i++;
					}
				} else if (line.indexOf(RES_DRAWABLEH) != -1 && line.indexOf(WARNING) != -1) {
					String fileName = line.substring(line.indexOf(RES_DRAWABLEH), line.indexOf(WARNING));
					File file = new File(SourceLocation + fileName);
					System.out.println(SourceLocation + fileName + " 文件是否存在 " + file.exists());
					if (file.exists()) {
						file.delete();
						System.out.println("  删除文件     " + fileName + " 成功");
						i++;
					}
				} else if (line.indexOf(RES_DRAWABLEM) != -1 && line.indexOf(WARNING) != -1) {
					String fileName = line.substring(line.indexOf(RES_DRAWABLEM), line.indexOf(WARNING));
					File file = new File(SourceLocation + fileName);
					System.out.println(SourceLocation + fileName + " 文件是否存在 " + file.exists());
					if (file.exists()) {
						file.delete();
						System.out.println("  删除文件     " + fileName + " 成功");
						i++;
					}
				}
			}
			System.out.println("  删除总个数     " + i);
			br.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 描述:清理无用的字符串
	 */
	public static void clearUnusedString() {
		try {
			FileInputStream fis = new FileInputStream(LINT_OUTPUT_FILE);
			InputStreamReader isr = new InputStreamReader(fis, DEFAULT_ENCODE);
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			ArrayList<String> unUsedStrings = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				if (line.indexOf(RES_VALUES_STRINGS) != -1 && line.indexOf(WARNING) != -1) {
					String lineNum = line.substring(line.indexOf("res\\values\\strings.xml:") + 23,
							line.indexOf(WARNING));
					String stringName = line.substring(line.indexOf("R.string.") + 9, line.indexOf(" appears to"));
					unUsedStrings.add(stringName);
					System.out.println("line--  " + line + "   Num ==  " + lineNum + " stringName = " + stringName);
				}
			}
			FileInputStream fisR = new FileInputStream(SourceLocation + RES_VALUES_STRINGS_XML);
			System.out.println(SourceLocation + RES_VALUES_STRINGS);
			InputStreamReader isrR = new InputStreamReader(fisR);
			BufferedReader brR = new BufferedReader(isrR);
			StringBuffer sb = new StringBuffer();
			while ((line = brR.readLine()) != null) {
				System.out.println("read line = " + line);
				if (line.contains("name=")) {
					String itemName = line.substring(line.indexOf("name=") + 6, line.indexOf("\">"));
					System.out.println("itemName== " + itemName);
					if (!(unUsedStrings.contains(itemName))) {
						sb.append(line).append(ENTER);
					}
				} else {
					sb.append(line).append(ENTER);
				}
			}
			FileOutputStream fos = new FileOutputStream(SourceLocation + RES_VALUES_STRINGS_XML);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(sb.toString());
			System.out.println("sb==  " + sb.toString());
			brR.close();
			br.close();
			isr.close();
			fis.close();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
