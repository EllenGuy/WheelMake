package com.eyes.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;


public class FileOperation {
	private static final String BLACKSLASH_X4 = "\\\\";
	private static final String SPACE_X0 = "";
	private String path;
	private List<String> allAbsolutePaths;
	private List<String> allSavePaths;
	public FileOperation(String path) throws Exception {
		if (path == null || path.equals("")) {
			throw new IllegalArgumentException("path参数内容不正确！");
		}
		this.path = path;
		allAbsolutePaths = new ArrayList<>();
		allSavePaths = new ArrayList<>();
		getAllPaths(path);
	}

	
	
	private void getAllPaths(String path) throws Exception{
		File file = new File(path);
		if (file.isDirectory()) {//是文件夹
			File[] files = file.listFiles();
			if (files == null) {
				throw new Exception("读取文件路径错误");
			}else if(files.length == 0) {
				throw new IllegalArgumentException("当前文件夹空的");
			}else{
				//递归获取子path
				recursionPaths(files);
			}
		} else {				//是文件
			//保存该文件目录
			String p = file.getAbsolutePath();
			allAbsolutePaths.add(p);
		}
	}

	private void recursionPaths(File[] files) throws Exception{
		for (File file : files) {
			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				if (childFiles == null) {
					//throw new Exception("读取文件路径错误");
					System.out.println("读取文件夹"+file.getAbsolutePath()+"下面文件出错");
					//continue;
				}else if(childFiles.length == 0) {
					//保存该文件夹目录
					String p = file.getAbsolutePath();
					allAbsolutePaths.add(p);
				}else{
					//递归获取子path
					recursionPaths(childFiles);
				}
			}else{
				//保存该文件目录
				String p = file.getAbsolutePath();
				allAbsolutePaths.add(p);
			}
		}
	}

	//获取子路径——给定路径下面的文件
	public List<String> getAllChildPaths(){
		List<String> list = new ArrayList<String>();
		if (allAbsolutePaths == null) {
			return null;
		}else if(allAbsolutePaths.size() == 1){
			String filename = new File(path).getName();
			list.add(filename);
			return list;
		}else{
			String newPath = moreBlackSlash(path);
			for (String string : allAbsolutePaths) {
				String tmpstr = string.replaceFirst(newPath, SPACE_X0);
				//				System.out.println(tmpstr);
				list.add(tmpstr);
			}
			return list;
		}
	}

	//获取子路径——给定路径下面的文件
	public void setAllSavePaths(String parentPath){
		//没文件
		if (allAbsolutePaths == null) {
			return;
			//是一个文件
		}else if(allAbsolutePaths.size() == 1){
			String filename = parentPath + new File(path).getName();
			allSavePaths.add(filename);
			return;
			//是一个文件夹
		}else{
			String newPath = moreBlackSlash(path);
			String newPPath = moreBlackSlash(parentPath);
			for (String string : allAbsolutePaths) {
				String tmpstr = string.replaceFirst(newPath, newPPath);
				System.out.println(tmpstr);
				allSavePaths.add(tmpstr);
			}
		}
	}

	private String moreBlackSlash(String path){
		String[] paths = path.split(BLACKSLASH_X4);
		StringBuilder sb = new StringBuilder();
		for (String string : paths) {
			sb.append(string);
			sb.append(BLACKSLASH_X4);
		}
		return sb.toString();
	}

	private String getSingleSavePath(String absolutePath, String targetParentPath){
		String p = moreBlackSlash(path);
		String pP = moreBlackSlash(targetParentPath);
		return absolutePath.replaceFirst(p, pP);
	}

	public void copyDirectoriesToLocal(String targetPath, WriteFileTo wft){
		long begin = System.currentTimeMillis();
		for (String  readStr: allAbsolutePaths) {
			File readFile = new File(readStr);
			String savePath = getSingleSavePath(readStr, targetPath);
			File writeFile = new File(savePath);
			if (readFile.isFile()) {
				//读取文件
				wft.writeFile(readFile, writeFile);
			}else{
				//创建文件夹
				writeFile.mkdir();
			}
		}

		long end = System.currentTimeMillis();
		printTime(begin, end);

	}
	
	public void copyDirectoriesToSocket(){
		
	}
	
	private void printTime(long before, long after){
		if (before <=0 || after <= 0 || after < before) {
			throw new IllegalArgumentException("时间参数不合法");
		}

		double time = ((double)after - (double)before)/1000;

		String second = String.format("%.4f", time%60);
		int minute = (int) (time/60)%60;
		int hour = (int)(time/3600)%24;
		int day = (int)time/(3600*24);
		if (day == 0) {
			if (hour == 0) {
				if (minute == 0) {
					System.out.println("复制成功，共花费"+second+"秒");
				}else {
					System.out.println("复制成功，共花费"+minute+"分钟"+time+"秒");
				}
			}else {
				System.out.println("复制成功，共花费"+hour+"小时"+minute+"分钟"+time+"秒");
			}
		}else{
			System.out.println("复制成功，共花费"+day+"天"+hour+"小时"+minute+"分钟"+time+"秒");
		}
	}

	public static class WriteFileToDisk implements WriteFileTo{

		@Override
		public void writeFile(File sourceFile, File targetFile) {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				if (!targetFile.exists()) {
					File parentFile = targetFile.getParentFile();
					if (parentFile != null && !parentFile.exists()) {
						//不存在同名的文件，否则创建文件夹会失败
						if (parentFile != null && parentFile.isFile()) {
							if (!parentFile.delete()) {
								System.out.println("存在文件与给定目录名称相同，删除失败");
								return;
							}
						}
						if (!parentFile.mkdirs()) {
							System.out.println("创建"+targetFile.getParent()+"文件夹失败");
							return;
						}
						if (!targetFile.createNewFile()) {
							System.out.println("创建"+targetFile+"文件失败");
							return;
						}
					}
				}
				fis = new FileInputStream(sourceFile);
				fos = new FileOutputStream(targetFile);
				byte[] bytes = new byte[102400];
				int len = 0;
				while ((len = fis.read(bytes)) >= 0) {
					fos.write(bytes, 0, len);
					fos.flush();
				}
				System.out.println(
						"复制"+sourceFile.getAbsolutePath()+
						"到"+targetFile.getAbsolutePath()+"成功");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					fis = null;
					fis = null;
				}
			}
		}

	}

}
