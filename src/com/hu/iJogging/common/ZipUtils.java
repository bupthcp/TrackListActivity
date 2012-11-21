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
   * zip操作帮助类 
   * bylijinnan 
   */  

  /** 
   * 打包指定目录下所有文件，包括子文件夹 
   * @param sourceFolder  要打包的文件目录 
   * @param outputFolder  生成的压缩文件存放的目录 
   * @param zipFileName   生成的压缩文件名 
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
   * 打包指定的文件。要打包的文件在文件列表中指定 
   * @param filelist  要打包的文件列表，这些文件是绝对路径 
   * @param outputFullFileName    生成的压缩文件全名，目录+文件名 
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
          ZipEntry ze = new ZipEntry(file);   //这里用的是相对路径  
          zos.putNextEntry(ze);  

          FileInputStream in = new FileInputStream(sourceFolder + "/" + file);        //这里是绝对路径  

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
   * 将文件路径中的分隔符转换成"/"，并去掉最后的分隔符（如果有） 
   * @param str   文件路径  
   * @return 格式化后的文件路径 
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
   * 解压zip文件，支持子文件夹和中文 
   * @param zipFileFullName .zip文件的完整名字，包括文件夹路径 
   * @param outputFolder  解压到指定的文件夹。完整路径，如果不指定或者为null，则默认解压到压缩文件所在的当前文件夹 
   * @param encoding 编码格式 
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
                
              /*用本程序中ZipUtil.zipFolder或者ZipUtil.zipFileList生成的zip文件，如果有子文件夹，entry.getName()会直接得到文件而略过了子文件夹： 
               * 程序生成，解压时输出： 
               * Extracting,entryName=sub/subsub/test.txt 
               * 压缩软件7-Zip生成，解压时输出： 
               * Extracting,entryName=sub/subsub/  
               * Extracting,entryName=sub/subsub/test.txt 
               * 因此要区别对待 
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
   * 解压zip文件，支持子文件夹和中文,这个方法是用来解压离线地图包中的MapData文件夹到BaiduMapSdk目录下
   * @param zipFileFullName .zip文件的完整名字，包括文件夹路径 
   * @param outputFolder  解压到指定的文件夹。完整路径，如果不指定或者为null，则默认解压到压缩文件所在的当前文件夹，不需要以"/"结尾 
   * @param prefix 直接压zip包中这个路径下的文件，不需要以"/"结尾
   * @param encoding 编码格式 
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
