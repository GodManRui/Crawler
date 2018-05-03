package com.bonree.mobile.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
	
	public static void setMod(File file, boolean readable, boolean writable, boolean executable) {
		if(file != null && file.exists()) {
			file.setReadable(readable, false);
			file.setWritable(writable, false);
			file.setExecutable(executable, false);
		}
	}
	
	public static void setMod(String path, boolean readable, boolean writable, boolean executable) {
		setMod(new File(path), readable, writable, executable);
	}
	
	public static void copy(InputStream input, File dst) {
		copy(input, dst.getAbsolutePath());
	}
	
	public static void copy(InputStream input, String dstPath) {
		FileOutputStream output = null;
		
		try {
			output = new FileOutputStream(dstPath);
			byte[] buf = new byte[4096];
			
			int readCount = 0;
			while((readCount = input.read(buf)) != -1) {
				output.write(buf, 0, readCount);
			}
			
			output.close();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void mkdir(String dirPath) {
		File dir = new File(dirPath);
		if(!dir.exists()) {
			dir.mkdirs();
			dir.setExecutable(true);
			dir.setReadable(true);
			dir.setWritable(true);
		}
	}
}
