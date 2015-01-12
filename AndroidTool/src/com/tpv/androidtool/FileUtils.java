package com.tpv.androidtool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class FileUtils {

	public static String IsToString(InputStream is) throws IOException {
		int i = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}

	public static String IsToString2(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len;
		while ((len = is.read(buffer)) > 0) {
			baos.write(buffer, 0, len);
		}
		return baos.toString();
	}

	public static String IsToString3(InputStream is) throws IOException {
		int i = -1;
		byte[] b = new byte[4096];
		StringBuffer sb = new StringBuffer();
		while ((i = is.read(b)) != -1) {
			sb.append(new String(b, 0, i));
		}
		String content = sb.toString();
		return content;
	}

	public static SQLiteDatabase openAssetDatabase(Context context,
			String filename, int sourceId) {
		try {
			File dbFile = context.getDatabasePath(filename);
			String dbFielName = dbFile.toString();
			String dbDirName = dbFielName.substring(0, dbFielName.length()
					- filename.length() - 1);
			String databaseFilename = dbFielName;
			File dir = new File(dbDirName);
			if (!dir.exists())
				dir.mkdir();
			if (!(new File(databaseFilename)).exists()) {
				InputStream is = context.getResources().openRawResource(
						sourceId);
				FileOutputStream fos = new FileOutputStream(databaseFilename);

				byte[] buffer = new byte[is.available()];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}

				fos.close();
				is.close();
			}
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
					databaseFilename, null);
			return database;
		} catch (Exception e) {
		}
		return null;
	}

	public String unicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}

	public static boolean saveDocToFile(Document document, String path) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(path));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
			return true;
		} catch (TransformerException mye) {
			mye.printStackTrace();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
		return false;
	}

	public static boolean saveAsFileWriter(String content, String path) {
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(path);
			fwriter.write(content);
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static String saveToFile(InputStream is, String path, String filename) {
		OutputStream os = null;
		try {
			File folder = new File(path);
			if (!folder.exists() && !folder.mkdir()) {
				return null;
			}
			File file = new File(path, filename);
			if (file.exists()) {
				file.delete();
				file = new File(path);
			}
			os = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			while ((is.read(buffer)) != -1) {
				os.write(buffer);
			}
			os.flush();
			return file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	public static String saveToFile(String path, String filename, String data,
			boolean replace) throws IOException {
		BufferedOutputStream stream = null;
		try {
			File folder = new File(path);
			if (!folder.exists() && !folder.mkdir()) {
				return null;
			}
			File file = new File(path, filename);
			if (file.exists()) {
				if (replace) {
					file.delete();
					file = new File(path, filename);
				} else {
					return file.getAbsolutePath();
				}
			}
			byte[] b = data.getBytes();

			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
			stream.close();
			return file.getAbsolutePath();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

	}

	/**
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromFile1(File f) throws IOException {
		InputStream is = null;
		String ret = null;
		try {
			is = new BufferedInputStream(new FileInputStream(f));
			long contentLength = f.length();
			ByteArrayOutputStream outstream = new ByteArrayOutputStream(
					contentLength > 0 ? (int) contentLength : 1024);
			byte[] buffer = new byte[4096];
			int len;
			while ((len = is.read(buffer)) > 0) {
				outstream.write(buffer, 0, len);
			}
			outstream.close();
			ret = outstream.toString();
			// byte[] ba = outstream.toByteArray();
			// ret = new String(ba);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromFile2(File f) throws IOException {
		InputStream is = null;
		String ret = null;
		try {
			is = new FileInputStream(f);
			long contentLength = f.length();
			byte[] ba = new byte[(int) contentLength];
			is.read(ba);
			ret = new String(ba);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		return ret;
	}

	public static String readStringFromFile3(String path, String filename)
			throws IOException {
		BufferedReader bf = null;
		try {
			File file = new File(path, filename);
			bf = new BufferedReader(new FileReader(file));
			String content = "";
			StringBuilder sb = new StringBuilder();
			while (content != null) {
				content = bf.readLine();
				if (content == null) {
					break;
				}
				sb.append(content.trim());
			}
			return sb.toString();
		} finally {
			if (bf != null) {
				bf.close();
			}
		}
	}
}
