package com.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;

public class Cmd {
	
	private Process process;
	private BufferedReader bufferedReader;
	private BufferedReader errorBufferedReader;
	
	public String inputCommand(String cmd) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("cmd /c ");
//		buffer.append("cmd /c start /wait "); // 콘솔 실행
		buffer.append(cmd);
		return buffer.toString();
	}
	
	public String execCommand(String cmd) {
		return execCommand(cmd, null);
	}
	
	public String execCommand(String cmd, Function<BufferedReader, StringBuffer> printInfo) {
		System.out.println("command : " + cmd);
		try {
			process = Runtime.getRuntime().exec(cmd);
			
			String line = null;
			StringBuffer readBuffer = new StringBuffer();
			StringBuffer errorReadBuffer = new StringBuffer();
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));
			errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
			
			if(printInfo != null) {
				errorReadBuffer = printInfo.apply(errorBufferedReader);
			} else {
				while((line = errorBufferedReader.readLine()) != null) {
					errorReadBuffer.append(line);
					errorReadBuffer.append("\n");
				}
			}
			errorBufferedReader.close();
			
			while((line = bufferedReader.readLine()) != null) {
				readBuffer.append(line);
				readBuffer.append("\n");
			}
			bufferedReader.close();
			
			int waitFor = process.waitFor();
			System.out.println("Process exited with " + waitFor);
			
			int exitValue = process.exitValue();
			if(exitValue != 0) {
				throw new RuntimeException("exit code is not 0 [" + exitValue + "]\n" + errorReadBuffer.toString());
			}
			
			if(readBuffer.toString() != null && !readBuffer.toString().equals("")) {
				return readBuffer.toString();
			} else {
				return errorReadBuffer.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void destroyProcess() {
		if(process != null) {
			try {
				Runtime.getRuntime().exec("taskkill /F /IM ffmpeg.exe");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Cmd cmd = new Cmd();
		String command = cmd.inputCommand("ipconfig");
		String result = cmd.execCommand(command);
		
		System.out.println(result);
	}

}
