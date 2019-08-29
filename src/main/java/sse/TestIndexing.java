/** * Copyright (C) 2016 Tarik Moataz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This Class tests the text processing functionality
 * It outputs two multi-maps: the first associates keywords to the documents identifiers while the second associates the doc identifiers to keywords
 */

package sse;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
//import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class TestIndexing {

	public static void main(String[] args) throws Exception {
		
		Printer.addPrinter(new Printer(Printer.LEVEL.EXTRA));

		//BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));

		//System.out.println("Enter the relative path name of the folder that contains the files to make searchable:");
		//String pathName = keyRead.readLine();
		//System.out.println("Enter the relative path name of the input file:");
		//String fileName = keyRead.readLine();
		//String fileName = "data/2files/1.txt";
		
		String ftpServer = "jobserver2.hopto.org";
		String ftpUser = "repast";
		String ftpPwd = "repast_test_123258";
		//Download file from FTP
		//String ftpFile = "sse/data/2files/2.txt";
		String ftpFile = "sse/data/10files/" + args[0]; 
		FTPClient ftpClient = new FTPClient();
		
		String fileName = args[0];
		
		InputStream fis = null;
		OutputStream outputStream1 = null;
		try {
			// Download file from ftp
			System.out.println("Download file from FTP server");
			

			ftpClient.connect(ftpServer,21);
			showServerReply(ftpClient);
			ftpClient.enterLocalPassiveMode();
			showServerReply(ftpClient);
			
			int reply = ftpClient.getReplyCode();
			System.out.println("Reply code from FTP: " + reply);
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				throw new Exception("Exception in connecting to FTP Server");
			}

			System.out.println("Log into FTP server");
			boolean success = ftpClient.login(ftpUser, ftpPwd);
			showServerReply(ftpClient);
			if (!success) {
                System.out.println("Could not login to the server");
                return;
            } else {
                System.out.println("LOGGED IN SERVER");
            }
			File downloadFile = new File(fileName);
			outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile));
			System.out.println("Dowload file from FTP server: " + ftpFile);
			ftpClient.retrieveFile(ftpFile, outputStream1);
			showServerReply(ftpClient);
			
			// Run the search and measure time
			long startTimeMs = System.currentTimeMillis( );
			//ArrayList<File> listOfFile = new ArrayList<File>();
			//TextProc.listf(pathName, listOfFile);
			//TextProc.TextProc(false, pathName);
			TextProc.TextProc(false, fileName);

			long taskTimeMs  = System.currentTimeMillis( ) - startTimeMs;
			System.out.println("\n Indexing processing time: " + taskTimeMs + " ms");
			
			// Create an InputStream of the file to be uploaded	 		 	
			String resultFile = "sse/output/" + "res_"+ fileName;	
			fis = new ByteArrayInputStream(Long.toString(taskTimeMs).getBytes(StandardCharsets.UTF_8));
			// Store file on server and logout		 
			ftpClient.storeFile(resultFile, fis);
			showServerReply(ftpClient);
			ftpClient.logout();	
			showServerReply(ftpClient);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        try {
	        	if(outputStream1!=null) {
	        		outputStream1.close();
	        	}
	        	if (fis != null) {         		 
	        		  fis.close(); 
	        	}
	        	if(ftpClient.isConnected()) {
		            ftpClient.disconnect();	        		
	        	}
	        } catch (IOException e) {
	        	showServerReply(ftpClient);
	            e.printStackTrace();
	        }
		}
		//System.out.println("\nFirst mult-map " + TextExtractPar.lp1);
		//System.out.println("Second multi-map " + TextExtractPar.lp2);*/

	}
	
	private static void showServerReply(FTPClient ftpClient) {
	    String[] replies = ftpClient.getReplyStrings();
	    if (replies != null && replies.length > 0) {
	        for (String aReply : replies) {
	            System.out.println("SERVER: " + aReply);
	        }
	    }
	}
}
