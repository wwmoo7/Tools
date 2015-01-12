package com.tpv.androidtool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Execmd {
	
	public static String execCommand(String... command) {
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		String result = "";

		try {
			process = new ProcessBuilder().command(command).start();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			result = new String(baos.toByteArray());
			if (inIs != null)
				inIs.close();
			if (errIs != null)
				errIs.close();
			process.destroy();
		} catch (IOException e) {
			result = e.getMessage();
		}
		return result;
	}
}