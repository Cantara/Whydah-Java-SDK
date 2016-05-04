/*
 * This file is part of Smooby project,  
 * 
 * Copyright (c) 2011-2011 Leif Auke <leif@auke.no> - All rights
 * reserved.
 * 
 */

package net.whydah.sso.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {

	public static boolean writeToFile(String fileName, String dataLine,
	        boolean isAppendMode, boolean isNewLine) {
		
		if (isNewLine) {
			dataLine = "\n" + dataLine;
		}
		
		DataOutputStream dos=null;
		try {
			File outFile = new File(fileName);
			if (isAppendMode) {
				dos = new DataOutputStream(new FileOutputStream(fileName, true));
			} else {
				dos = new DataOutputStream(new FileOutputStream(outFile));
			}
			dos.writeBytes(dataLine);
			dos.close();
		} catch (FileNotFoundException ex) {
			
			if(dos!=null) {
				
				try {
					dos.close();
				} catch (IOException e) {
				}

			}			
			return (false);
			
		} catch (IOException ex) {

			if(dos!=null) {
				
				try {
					dos.close();
				} catch (IOException e) {
				}

			}			
			
			return (false);
		}
		return (true);
	}

	public static boolean writeToFile(String fileName, byte[] bytes, boolean append) {

		FileOutputStream fStream=null;
		try {
			
			File f = new File(fileName);
			fStream = new FileOutputStream(f, append);
			fStream.write(bytes);
			fStream.flush();
			fStream.close();
			
		} catch (FileNotFoundException ex) {
			
			if(fStream!=null) {
		
				try {
					fStream.close();
				} catch (IOException e) {
				}

			}
			
			return (false);
		
		} catch (IOException ex) {

			if(fStream!=null) {
				
				try {
					fStream.close();
				} catch (IOException e) {
				}

			}
			
			return (false);
		}
		return (true);
	}

	public static String readFromFile(String fileName) throws IOException {
		
		File f = new File(fileName);
		FileInputStream str = new FileInputStream(f);
		int bufferSize = 4096;
		StringBuilder sb = new StringBuilder();
		long dataToRead = f.length();
		long length;
		while (dataToRead > 0) {
			long packSize = dataToRead > bufferSize ? bufferSize: dataToRead;
			byte[] buffer = new byte[(int) packSize];
			length = str.read(buffer, 0, (int) packSize);
			sb.append(StringConv.UTF8(buffer));
			dataToRead = dataToRead - length;
		}
		str.close();
		return sb.toString();
	}

	public static String getSize(long length) {
		double size = (length / 1024.0);
		if (size <= 1) {
			return "1 KB";
		}
		if (size < 1024) {

			return String.format("%1$.2f KB", size);
			
		} else {
			size /= 1024.0;
			if (size < 1024)
				return String.format("%1$.2f MB", size);
			else {
				size /= 1024;
				return String.format("%1$.2f GB", size);
			}
		}
	}

	public static boolean isFileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	public static boolean createFileIfNotExist(String fileName,boolean deleteIfExists) {
		
		boolean ok = true;
		File f = new File(fileName);
		if (f.exists() && deleteIfExists) {

			f.delete();
			
		} else {
			
			try {
			
				ok = f.createNewFile();
			
			} catch (IOException e) {
			
				ok = false;
				e.printStackTrace();
			}
		}
		return ok;
	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		
		if (file.exists()) {
			
		    return file.delete();
		
		} else {
			
		    return true;
		}
	}

	public static boolean createDirectory(String dir) {
		boolean ok = true;
		File f;
		try {
			if (!(f = new File(dir)).exists()) {
				ok = f.mkdirs();
			}
		} catch (SecurityException ex) {
			
			throw ex;

		}
		return ok;
	}

	public static String getFileNameWithoutExtension(String fileName) {
		File file = new File(fileName);
		if(file.exists()) {
			fileName = file.getName();
		}
		
		int index = fileName.lastIndexOf('.');
		if (index > 0 && index <= fileName.length() - 2) {
		
			return fileName.substring(0, index);
		
		} else {
		
			return fileName;
		}
		
	}

	public static String getLinuxFileName(String filePath) {
		/*
		 * File file = new File(fileName); int index =
		 * file.getName().lastIndexOf('.'); if (index > 0 && index <=
		 * file.getName().length() - 2) { return file.getName().substring(0,
		 * index); }
		 */
		String linuxPath = filePath.replace("\\", "/");
		return linuxPath.substring(linuxPath.lastIndexOf("/") + 1);
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i >= 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext.trim();
	}

	public static String[] getChunkFiles(String path, String FileId) {
		String[] filelist = FileUtil.getFiles(path, FileId + ".part*");
		String[] result = new String[filelist.length];
		for (int i = 0; i < filelist.length; i++) {
			for (int x = 0; x < filelist.length; x++) {
				int z = Integer.parseInt(filelist[x].replace(FileId + ".part",
				        ""));
				if (z == (i + 1)) {
					result[i] = filelist[x];
					break;
				}
			}
		}
		return result;
	}

	public static String[] getFiles(String path, final String[] extensions) {

		File dir = new File(path);
		if (dir.isDirectory()) {

			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					for (String ext : extensions) {
						return name.endsWith(ext);
					}
					return false;
				}
			};
			return dir.list(filter);
		}
		return new String[0];
	}
	
	public static File[] listFiles(String path, final String[] extensions) {

		File dir = new File(path);
		if (dir.isDirectory()) {

			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					boolean ok = false;
					for (String ext : extensions) {
						if(name.endsWith(ext)) {ok =true; break;}
					}
					return ok;
				}
			};
			return dir.listFiles(filter);
		}
		return new File[0];
	}

	public static String[] getFiles(String path, String patern) {

		File dir = new File(path);
		if (dir.isDirectory()) {

			Pattern regexp = Pattern.compile(patern);
			final Matcher matcher = regexp.matcher("");
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					matcher.reset(name);
					if (matcher.find())
						return true;
					return false;
				}
			};
			return dir.list(filter);
		}
		return new String[0];
	}

	public static File[] getSubDirectories(String path) {
		File dir = new File(path);
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		File[] files = dir.listFiles(fileFilter);
		return files==null?new File[0]:files;
	}

	public static File[] getFiles(String path) {
		File dir = new File(path);
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		};
		File[] files = dir.listFiles(fileFilter);
		return files==null?new File[0]:files;
	}

	public static void writeStream(String file, byte[] data, int ByteFrom, int ByteTo)
	        throws Exception {
		//write whole data block, better to write a block each 10K?
		//HUYDO: do later
		RandomAccessFile str = new RandomAccessFile(file, "rw");
		str.seek((long) ByteFrom);
		str.write(data, ByteFrom, ByteTo);
		str.close();

	}
	
	public static byte[] extractStream(String file, long ByteFrom, long ByteTo) throws Exception {
	
		int bufferReadLength = 1024 * 10; // read 10k
		long dataToRead = ByteTo - ByteFrom; // data to read in chunk
		int length;
		byte[] bs = new byte[(int) dataToRead];

		RandomAccessFile str = new RandomAccessFile(file, "rw");
		str.seek((long) ByteFrom);
		while (dataToRead > 0) {
			long packSize = dataToRead > bufferReadLength ? bufferReadLength : dataToRead;
			byte[] buffer = new byte[(int) packSize];
			length = str.read(buffer, 0, (int) packSize);
			System.arraycopy(buffer, 0, bs, (int) bs.length - (int) dataToRead,(int) packSize);
			dataToRead = dataToRead - length;
		}
		str.close();
		return bs;

	}
	
	public static byte[] getByteArrayFromFile(String file) throws Exception {
		
		File f = new File(file);
		if(f.exists()) {
			
			return extractStream(file, 0, f.length());
			
		} else {
			
			return new byte[0];
			
		}
	}

	public static String GetParentPath(String path) {
		path = path.replace("\\", "/");
		if (path.lastIndexOf("/") > 0) {
			if (path.endsWith("/")) {
				path = path.substring(0, path.lastIndexOf("/"));
				return path.substring(0, path.lastIndexOf("/"));
			} else {
				return path.substring(0, path.lastIndexOf("/"));
			}
		} else
			return "";
	}

	public static String combinePath(String path1, String path2) {
		path1 = path1.replace("\\", "/");
		path2 = path2.replace("\\", "/");
		if (path1.endsWith("/")) {
			path1 = path1.substring(0, path1.lastIndexOf("/"));
		}
		path2 = path2.substring(path2.lastIndexOf("/") + 1);
		return path1 + "/" + path2;
	}

	public static void deleteDir(File dir) throws IOException {
		
		if(!dir.exists()) {
			return;
		}
		
		if (!dir.isDirectory()) {
			throw new IOException("Not a directory " + dir);
		}

		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			
			File file = files[i];

			if (file.isDirectory()) {
			
				deleteDir(file);
			
			} else {
			
				boolean deleted = file.delete();
				if (!deleted) {
					throw new IOException("Unable to delete file" + file);
				}
			}
		}

		dir.delete();
		
	}



	public static String readFromFile(InputStream is) {
		
		try {
			
			if (is != null) {
			
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				StringBuilder sb = new StringBuilder();
				int c;
				while ((c = br.read()) != -1) {
					sb.append((char) c);
				}
				br.close();
				isr.close();
				is.close();
				return sb.toString();

			} else {
				
				return "";				
			}
		
		} catch (IOException ex) {
			
			return "";
		
		}
		
	}
	
	public static byte[] readBytesFromStream(InputStream is) throws IOException {

		if (is != null) {
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1) {
			  
				buffer.write(data, 0, nRead);
			
			}
			
			buffer.flush();
			return buffer.toByteArray();

		}  
		return new byte[0];
				
	}	
	
	public static void saveData(byte[] bytes, String path, String saveAsName) {
		String file = path + "/" + saveAsName;
		if (StringUtil.isNullOrEmpty(file))
			return;
		
		FileUtil.createDirectory(path);
		FileUtil.writeToFile(file, bytes, false);
	}
	
    
    public static Properties readPropertiesFromFile(Object context, String propertyFile) throws IOException {
        Properties properties = new Properties();
        InputStream is = context.getClass().getResourceAsStream("/" + propertyFile);
        if(is == null) {
            throw new IOException("Error reading " + propertyFile + " from classpath.");
        }
        properties.load(is);
        return properties;
    }

    public static File[] listSubFoldersByDate(String path)
    {
    	File[] files = getSubDirectories(path);
    	FilePair[] pairs = new FilePair[files.length];
    	for (int i = 0; i < files.length; i++)
    	    pairs[i] = new FilePair(files[i]);

    	Arrays.sort(pairs);

    	for (int i = 0; i < files.length; i++)
    	    files[i] = pairs[i].f;
    	return files;
    }
    
    public static File[] listFilesByDate(String path, final String[] extensions)
    {
    	File[] files = listFiles(path, extensions);
    	FilePair[] pairs = new FilePair[files.length];
    	for (int i = 0; i < files.length; i++)
    	    pairs[i] = new FilePair(files[i]);

    	Arrays.sort(pairs);

    	for (int i = 0; i < files.length; i++)
    	    files[i] = pairs[i].f;
    	return files;
    }
    
	public static String readFromJARFile(Object context, String resourceFile) throws IOException {
		InputStream is = context.getClass().getResourceAsStream("/" + resourceFile);
		return readFromFile(is);
	}    
    
	public static byte[] readBytesFromJAR(Object context, String resourceFile) throws IOException {
		InputStream is = context.getClass().getResourceAsStream("/" + resourceFile);
		return readBytesFromStream(is);
	}    
    
    public static String readFromJARFile(String fileName, String resource) throws IOException {
    	JarFile jarFile = new JarFile(fileName);
    	JarEntry entry = jarFile.getJarEntry(resource);
    	InputStream input = jarFile.getInputStream(entry);
    	return readFromFile(input);
	}
    
    public static byte[] readBytesJARFile(String fileName, String resource) throws IOException {
    	JarFile jarFile = new JarFile(fileName);
    	JarEntry entry = jarFile.getJarEntry(resource);
    	InputStream input = jarFile.getInputStream(entry);
    	return readBytesFromStream(input);
	}

    public static void loadPropertyFromFile(Object context, Properties properties, String configfilename) throws IOException {
        
    	File file = new File(configfilename);
        FileInputStream fis = null;
        
        try {
            if(file.exists()) {
                fis = new FileInputStream(file);
                properties.load(fis);
            } else {
                throw new IOException("Config file " + configfilename + " not found.");
            }
        } finally {
            if(fis != null) {
                fis.close();
            }
        }
    }
}