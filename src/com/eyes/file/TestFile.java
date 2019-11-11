package com.eyes.file;

import java.util.List;
import java.util.Scanner;

public class TestFile {
	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		System.out.println("请输入读取的文件/文件夹路径：");
		String pathRead = scan.nextLine();
		System.out.println("请输入保存的文件/文件夹路径：");
		String pathWrite = scan.nextLine();
		
		
		
		FileOperation fr = new FileOperation(pathRead);
		List<String> lf = fr.getAllChildPaths();
		fr.setAllSavePaths(pathWrite);
		fr.copyDirectoriesToLocal(pathWrite, new FileOperation.WriteFileToDisk());
		
	}
}


