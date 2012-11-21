package com.hu.iJogging.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtils {
  /** 
   * zip���������� 
   * bylijinnan 
   */  

  /** 
   * ���ָ��Ŀ¼�������ļ����������ļ��� 
   * @param sourceFolder  Ҫ������ļ�Ŀ¼ 
   * @param outputFolder  ���ɵ�ѹ���ļ���ŵ�Ŀ¼ 
   * @param zipFileName   ���ɵ�ѹ���ļ��� 
   * @throws IOException 
   */  
  public static void zipFolder(String sourceFolder, String outputFolder,String zipFileName, String encoding) throws IOException {  
      if (isEmptyStr(sourceFolder) || isEmptyStr(outputFolder) || isEmptyStr(zipFileName)) {   
          return;  
      }  
      sourceFolder = formatFilePath(sourceFolder);  
      outputFolder = formatFilePath(outputFolder);  
      List<String> filelist = generateFileList(sourceFolder);  
      zipFileList(filelist, sourceFolder, outputFolder, zipFileName, encoding);  
  }  
    
    
  /** 
   * ���ָ�����ļ���Ҫ������ļ����ļ��б���ָ�� 
   * @param filelist  Ҫ������ļ��б���Щ�ļ��Ǿ���·�� 
   * @param outputFullFileName    ���ɵ�ѹ���ļ�ȫ����Ŀ¼+�ļ��� 
   * @throws IOException 
   */  
  public static void zipFileList(List<String> filelist, String sourceFolder, String outputFolder, String zipFileName, String encoding) throws IOException {  
      if (filelist == null || filelist.isEmpty()) {  
          return;  
      }  
      if (isEmptyStr(outputFolder) || isEmptyStr(zipFileName)) {  
          return;  
      }  
        
      sourceFolder = formatFilePath(sourceFolder);  
      outputFolder = formatFilePath(outputFolder);  
      if (isEmptyStr(encoding)) {  
          encoding = "UTF-8";  
      }  
        
      File outputDir = new File(outputFolder);  
      if (!outputDir.exists()) {  
          outputDir.mkdirs();  
      }  
        
      byte[] buffer = new byte[1024];  

      String outputFullFileName = (outputFolder + "/" + zipFileName);  
      FileOutputStream fos = new FileOutputStream(outputFullFileName);  
      ZipOutputStream zos = new ZipOutputStream(fos);  
      zos.setEncoding(encoding);  

      for (String file : filelist) {  
          if (isEmptyStr(file)) {  
              continue;  
          }  
          ZipEntry ze = new ZipEntry(file);   //�����õ������·��  
          zos.putNextEntry(ze);  

          FileInputStream in = new FileInputStream(sourceFolder + "/" + file);        //�����Ǿ���·��  

          int len;  
          while ((len = in.read(buffer)) > 0) {  
              zos.write(buffer, 0, len);  
          }  

          in.close();  
      }  

      zos.closeEntry();  
      // remember close it  
      zos.close();  

  }  

  /** 
   * Traverse a directory and get all files(include the files in sub directory), add the file into fileList and return it. 
   *  
   * @param sourceFolder 
   *            file or directory 
   * @return filelist 
   */  
  public static List<String> generateFileList(String sourceFolder) {  
      List<String> filelist = null;  
      if (!isEmptyStr(sourceFolder)) {  
          sourceFolder = formatFilePath(sourceFolder);  
          filelist = new ArrayList<String>();  
          File node = new File(sourceFolder);  
          generateFileListHelper(sourceFolder, node, filelist);  
      }  
      return filelist;  
  }  
    
  private static void generateFileListHelper(String sourceFolder, File node, List<String> filelist) {  

      // add file only  
      if (node.isFile()) {  
          String absoluteFile = node.getAbsoluteFile().toString();  
          String filepath = generateZipEntry(sourceFolder, absoluteFile);  
          filelist.add(filepath);  
      }  

      if (node.isDirectory()) {  
          String[] subNote = node.list();  
          for (String filename : subNote) {  
              File subFile = new File(node, filename);  
              generateFileListHelper(sourceFolder, subFile, filelist);  
          }  
      }  

  }  

  /** 
   * Format the file path for zip. 
   * Delete the "SOURCE_FOLDER" directory info,e.g. 
   * d:\ziptest\tmpty.txt         --> tmpty.txt  
   * d:\ziptest\sub\t.xls     -->  sub\t.xls 
   *  
   * @param file 
   *            file path 
   * @return Formatted file path. 
   */  
  private static String generateZipEntry(String sourceFolder, String file) {  
      String formattedPath = file.substring(sourceFolder.length() + 1);  
      formattedPath = formatFilePath(formattedPath);  
      return formattedPath;  
  }  
    
  /** 
   * ���ļ�·���еķָ���ת����"/"����ȥ�����ķָ���������У� 
   * @param str   �ļ�·��  
   * @return ��ʽ������ļ�·�� 
   */  
  public static String formatFilePath(String str) {  
      if (str != null && str.length() !=0) {  
           str = str .replaceAll("\\\\", "/");  
      }  
      if (str.endsWith("/")) {  
          str = str.substring(0, str.length()-1);  
      }  
      return str;  
  }  
    
  private static boolean isEmptyStr(String str) {  
      return str == null || str.length() == 0;  
  }  
    
  /** 
   * ��ѹzip�ļ���֧�����ļ��к����� 
   * @param zipFileFullName .zip�ļ����������֣������ļ���·�� 
   * @param outputFolder  ��ѹ��ָ�����ļ��С�����·���������ָ������Ϊnull����Ĭ�Ͻ�ѹ��ѹ���ļ����ڵĵ�ǰ�ļ��� 
   * @param encoding �����ʽ 
   */  
  @SuppressWarnings("rawtypes")  
  public static void unzip(String zipFileFullName, String outputFolder, String encoding) {  
      if (zipFileFullName == null || zipFileFullName.length() == 0) {  
          return;  
      }  
      if (!zipFileFullName.endsWith(".zip")) {  
          return;  
      }  
      //change file separator to "/"  
      zipFileFullName = zipFileFullName.replaceAll("\\\\", "/");  
      //find outputFolder  
      String inputFolder = zipFileFullName.replaceAll("/[^/]+\\.zip", "");  
      if (outputFolder == null || outputFolder.length() == 0) {  
          outputFolder = inputFolder;  
      }  
      outputFolder = outputFolder.replaceAll("\\\\", "/");  
        
      File outputFolderFile = new File(outputFolder);  
      if (!outputFolderFile.exists()) {  
          outputFolderFile.mkdirs();  
      }  
      try {  
          ZipFile zip = new ZipFile(zipFileFullName, encoding);  
          Enumeration zipFileEntries = zip.getEntries();  

          while (zipFileEntries.hasMoreElements()) {  
              ZipEntry entry =  (ZipEntry) zipFileEntries.nextElement();  
              String entryName = entry.getName(); 
                
              /*�ñ�������ZipUtil.zipFolder����ZipUtil.zipFileList���ɵ�zip�ļ�����������ļ��У�entry.getName()��ֱ�ӵõ��ļ����Թ������ļ��У� 
               * �������ɣ���ѹʱ����� 
               * Extracting,entryName=sub/subsub/test.txt 
               * ѹ�����7-Zip���ɣ���ѹʱ����� 
               * Extracting,entryName=sub/subsub/  
               * Extracting,entryName=sub/subsub/test.txt 
               * ���Ҫ����Դ� 
               */  
                
              int lastSlashPos = entryName.lastIndexOf("/");  
              if (lastSlashPos != -1 ){  
                  String folderStr = outputFolder + "/" + entryName.substring(0, lastSlashPos);  
                  File folder = new File(folderStr);  
                  if (!folder.exists()) {  
                      folder.mkdirs();  
                  }  
              }  
              if (!entryName.endsWith("/")) {     //this entry is not a directory.  
                  File outFile = new File(outputFolder + "/" + entryName);  
                  FileOutputStream fos = new FileOutputStream(outFile);  
                  Writer bw = new BufferedWriter(new OutputStreamWriter(fos, encoding));  
                    
                  InputStream in = zip.getInputStream(entry);  
                  Reader reader = new InputStreamReader(in, encoding);   
                  BufferedReader br =  new BufferedReader(reader);  
                    
                  String line;  
                  while ((line = br.readLine()) != null) {  
                      bw.write(line);  
                  }  
                  bw.close();  
              }  
          }  

      } catch (Exception e) {  
          e.printStackTrace();  
      }  
  }  
  
  
  /** 
   * ��ѹzip�ļ���֧�����ļ��к�����,���������������ѹ���ߵ�ͼ���е�MapData�ļ��е�BaiduMapSdkĿ¼��
   * @param zipFileFullName .zip�ļ����������֣������ļ���·�� 
   * @param outputFolder  ��ѹ��ָ�����ļ��С�����·���������ָ������Ϊnull����Ĭ�Ͻ�ѹ��ѹ���ļ����ڵĵ�ǰ�ļ��У�����Ҫ��"/"��β 
   * @param prefix ֱ��ѹzip�������·���µ��ļ�������Ҫ��"/"��β
   * @param encoding �����ʽ 
   */  
  public static void unZipOneFolder(String zipFileFullName, String outputFolder, String prefix , String encoding){
    if (zipFileFullName == null || zipFileFullName.length() == 0) { return; }
    if (!zipFileFullName.endsWith(".zip")) { return; }
    // change file separator to "/"
    zipFileFullName = zipFileFullName.replaceAll("\\\\", "/");
    // find outputFolder
    String inputFolder = zipFileFullName.replaceAll("/[^/]+\\.zip", "");
    if (outputFolder == null || outputFolder.length() == 0) {
      outputFolder = inputFolder;
    }
    outputFolder = outputFolder.replaceAll("\\\\", "/");

    File outputFolderFile = new File(outputFolder);
    if (!outputFolderFile.exists()) {
      outputFolderFile.mkdirs();
    }
    try {
      ZipFile zip = new ZipFile(zipFileFullName, encoding);
      Enumeration zipFileEntries = zip.getEntries();

      while (zipFileEntries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
        String entryName = entry.getName();
        int position = entryName.indexOf(prefix);
        if (position == -1) {
          continue;
        } else {
          String tmp = entryName.substring(position + prefix.length());
          if (!tmp.endsWith("/")) { // this entry is not a directory.
            File outFile = new File(outputFolder + "/" + tmp);
            if(outFile.exists()){
              outFile.delete();
            }
            FileOutputStream output = new FileOutputStream(outFile);

            InputStream input = zip.getInputStream(entry);
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = input.read(buffer)) > 0){  
                output.write(buffer, 0, len);  
            }  
            output.flush();  
            output.close();
          }else{
            String folderStr = outputFolder + "/" + tmp;  
            File folder = new File(folderStr);  
            if (!folder.exists()) {  
                folder.mkdirs();  
            }  
          }
        }
      }
    } catch (Exception e) {

    }
  }
  
  public static void GetFileList(String path){
    try{
      ZipFile zip = new ZipFile(path, "utf-8");  
      Enumeration zipFileEntries = zip.getEntries();  

      while (zipFileEntries.hasMoreElements()) {  
          ZipEntry entry =  (ZipEntry) zipFileEntries.nextElement();  
          String entryName = entry.getName(); 
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
