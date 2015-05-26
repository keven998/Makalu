package com.aizou.core.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


/** 
* 转码工具，全面支持文件、字符串的转码 
* 
* @author Administrator 2009-11-29 16:14:21 
*/ 
public class EncodingToolkit { 

        public static void main(String[] args) {

        } 

        /** 
         * 对字符串重新编码 
         * 
         * @param text                字符串 
         * @param resEncoding 源编码 
         * @param newEncoding 新编码 
         * @return 重新编码后的字符串 
         */ 
        public static String reEncoding(String text, String resEncoding, String newEncoding) {
                String rs = null;
                try { 
                        rs = new String(text.getBytes(resEncoding), newEncoding);
                } catch (UnsupportedEncodingException e) {
                        Log.d("info", "读取文件为一个内存字符串失败，失败原因是使用了不支持的字符编码");
                        throw new RuntimeException(e);
                } 
                return rs; 
        } 

        /** 
         * 重新编码Unicode字符串 
         * 
         * @param text                源字符串 
         * @param newEncoding 新的编码 
         * @return 指定编码的字符串 
         */ 
        public static String reEncoding(String text, String newEncoding) {
                String rs = null;
                try { 
                        rs = new String(text.getBytes(), newEncoding);
                } catch (UnsupportedEncodingException e) {
                        Log.d("info", "读取文件为一个内存字符串失败，失败原因是使用了不支持的字符编码" + newEncoding);
                        throw new RuntimeException(e);
                } 
                return rs; 
        } 

        /** 
         * 文本文件重新编码 
         * 
         * @param resFile         源文件 
         * @param resEncoding 源文件编码 
         * @param distFile        目标文件 
         * @param newEncoding 目标文件编码 
         * @return 转码成功时候返回ture，否则false 
         */ 
        public static boolean reEncoding(File resFile, String resEncoding, File distFile, String newEncoding) {
                boolean flag = true; 
                InputStreamReader reader = null;
                OutputStreamWriter writer = null;
                try { 
                        reader = new InputStreamReader(new FileInputStream(resFile), resEncoding);
                        writer = new OutputStreamWriter(new FileOutputStream(distFile), newEncoding);
                        char buf[] = new char[1024 * 64];         //字符缓冲区 
                        int len; 
                        while ((len = reader.read(buf)) != -1) { 
                                writer.write(buf, 0, len); 
                        } 
                        writer.flush(); 
                        writer.close(); 
                        reader.close(); 
                } catch (FileNotFoundException e) {
                        flag = false; 
                        Log.d("info", "没有找到文件，转码发生异常！");
                        throw new RuntimeException(e);
                } catch (IOException e) {
                        flag = false; 
                        Log.d("info", "读取文件为一个内存字符串失败，失败原因是读取文件异常！");
                        throw new RuntimeException(e);
                } finally { 
                        if (reader != null) try { 
                                reader.close(); 
                        } catch (IOException e) {
                                flag = false; 
                                throw new RuntimeException(e);
                        } finally { 
                                if (writer != null) try { 
                                        writer.close(); 
                                } catch (IOException e) {
                                        flag = false; 
                                        throw new RuntimeException(e);
                                } 
                        } 
                } 
                return flag; 
        } 

        /** 
         * 读取文件为一个Unicode编码的内存字符串,保持文件原有的换行格式 
         * 
         * @param resFile    源文件对象 
         * @param encoding 文件字符集编码 
         * @return 文件内容的Unicode字符串 
         */ 
        public static String file2String(File resFile, String encoding) {
                StringBuffer sb = new StringBuffer();
                try { 
                        LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(new FileInputStream(resFile), encoding)));
                        String line;
                        while ((line = reader.readLine()) != null) { 
                                sb.append(line).append(System.getProperty("line.separator"));
                        } 
                        reader.close(); 
                } catch (UnsupportedEncodingException e) {
                        Log.d("info", "读取文件为一个内存字符串失败，失败原因是使用了不支持的字符编码" + encoding);
                        throw new RuntimeException(e);
                } catch (FileNotFoundException e) {
                        Log.d("info", "读取文件为一个内存字符串失败，失败原因所给的文件" + resFile + "不存在！");
                        throw new RuntimeException(e);
                } catch (IOException e) {
                        Log.d("info", "读取文件为一个内存字符串失败，失败原因是读取文件异常！");
                        throw new RuntimeException(e);
                } 
                return sb.toString(); 
        } 

        /** 
         * 使用指定编码读取输入流为一个内存Unicode字符串,保持文件原有的换行格式 
         * 
         * @param in             输入流 
         * @param encoding 构建字符流时候使用的字符编码 
         * @return Unicode字符串 
         */ 
        public static String stream2String(InputStream in, String encoding) {
                StringBuffer sb = new StringBuffer();
                LineNumberReader reader = null;
                try { 
                        reader = new LineNumberReader(new BufferedReader(new InputStreamReader(in, encoding)));
                        String line;
                        while ((line = reader.readLine()) != null) { 
                                sb.append(line).append(System.getProperty("line.separator"));
                        } 
                        reader.close(); 
                        in.close(); 
                } catch (UnsupportedEncodingException e) {
                        Log.d("info", "读取文件为一个内存字符串失败，失败原因是使用了不支持的字符编码" + encoding);
                        throw new RuntimeException(e);
                } catch (IOException e) {
                        Log.d("info", "读取文件为一个内存字符串失败，失败原因是读取文件异常！");
                        throw new RuntimeException(e);
                } finally { 
                        if (in != null) try { 
                                in.close(); 
                        } catch (IOException e) {
                                Log.d("info", "关闭输入流发生异常！", e);
                                throw new RuntimeException(e);
                        } 
                } 
                return sb.toString(); 
        } 

        /** 
         * 字符串保存为制定编码的文本文件 
         * 
         * @param text         字符串 
         * @param distFile 目标文件 
         * @param encoding 目标文件的编码 
         * @return 转换成功时候返回ture，否则false 
         */ 
        public static boolean string2TextFile(String text, File distFile, String encoding) {
                boolean flag = true; 
                if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs(); 
                OutputStreamWriter writer = null;
                try { 
                        writer = new OutputStreamWriter(new FileOutputStream(distFile), encoding);
                        writer.write(text); 
                        writer.close(); 
                } catch (IOException e) {
                        flag = false; 
                        Log.d("info", "将字符串写入文件发生异常！");
                        throw new RuntimeException(e);
                } finally { 
                        if (writer != null) try { 
                                writer.close(); 
                        } catch (IOException e) {
                                Log.d("info", "关闭输出流发生异常！", e);
                                throw new RuntimeException(e);
                        } 
                } 
                return flag; 
        } 
}