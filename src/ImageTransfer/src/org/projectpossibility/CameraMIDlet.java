/*
 * CamerMIDlet.java
 *
 * Created on 9. August 2004, 10:15
 *
 */
package org.projectpossibility;
import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.microedition.media.*;

/**
Copyright (C) 2006 Media Informatics Group (www.mimuc.de), 
University of Munich, Contact person: Enrico Rukzio
(Enrico.Rukzio@ifi.lmu.de)

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

2008, April 30.  This code has been adapted for use with the Mobile Currency Reader.
Changes are mostly found in the function
	void buildHTTPConnection(byte[] byteImage)

*/
public class CameraMIDlet extends MIDlet {
	private CameraForm cameraSave = null;
	private DisplayImage displayImage = null;
	public CameraMIDlet() {
	}
	/*
	 * startApp()
	 * starts the MIDlet and generates cameraSave, displayImage, database
	 *
	 **/
	public void startApp() {
		Displayable current = Display.getDisplay(this).getCurrent();
		if (current == null) {
			//first call
			cameraSave = new CameraForm(this);
			displayImage = new DisplayImage(this);
			Display.getDisplay(this).setCurrent(cameraSave);
			cameraSave.start();
		} else {
			//returning from pauseApp
			if (current == cameraSave) {
				cameraSave.start();
			}
			Display.getDisplay(this).setCurrent(current);
		}
	}
	public void pauseApp() {
		if (Display.getDisplay(this).getCurrent() == cameraSave) {
			cameraSave.stop();
		}
	}
	public void destroyApp(boolean unconditional) {
		if (Display.getDisplay(this).getCurrent() == cameraSave) {
			cameraSave.stop();
		}
	}
	private void exitRequested() {
		destroyApp(false);
		notifyDestroyed();
	}
	void cameraFormExit() {
		exitRequested();
	}
	/**
	 * restart the camera again
	 *
	 */
	void displayCanvasBack() {
		Display.getDisplay(this).setCurrent(cameraSave);
		cameraSave.start();
	}
	/**
	 *	the byte[] of the image should be transmitted to a server
	 *
	 **/
	void buildHTTPConnection(byte[] byteImage) {
		displayImage.setImage(byteImage);
		Display.getDisplay(this).setCurrent(displayImage);
		HttpConnection hc = null;
		OutputStream out = null;
		try {

			//enode the image data by the Base64 algorithm
			String stringImage = Base64.encode(byteImage);
			// URL of the Sevlet
			String url = new String(
					"http://csci571.usc.edu:8080/ImageServletTest.jsp");
//					"http://207.151.241.29:8080/examples/servlets/servlet/ImageServlet");
			// Obtain an HTTPConnection
			hc = (HttpConnection) Connector.open(url);
			// Modifying the headers of the request
			hc.setRequestMethod(HttpConnection.POST);
			// Obtain the output stream for the HttpConnection
            		out = hc.openOutputStream();
			out.write(stringImage.getBytes());			
//			out.write(byteImage);
	
			out.close();
			
		
			getReturnAudio(hc, "http://csci571.usc.edu:8080/ImageServletTest.jsp");
			
		 /*	
		  * The following code is designed to receive the value of the bill from the
		  * Servlet application.
		  * In its current instantiation, there are some concurrency issues, where
		  * the client or server does not close properly, needs to be investigated.
		  */
			/*
		    InputStream is;
		    SocketConnection sc;
		    String myValue;
			try {
			    sc = (SocketConnection) Connector.open("socket://207.151.241.29:4444");
			    is = sc.openInputStream();
				StringBuffer sb = new StringBuffer();
				int c = 0;
				c = is.read();
				sb.append((char) c);
				myValue = sb.toString();
			} catch (ConnectionNotFoundException cnfe) {
				System.out.println("connection not found");
			} catch (Exception e) {
			    e.printStackTrace();
			}
			 */
			
			/*
			 * Here, the return value in myValue
			 * would contain a 1,5,10,20 or 0, depending
			 * on if the bill was identified or not 
			 * JME has a media library, which can play wav files
			 * We did not have time to implement the audio playback,
			 * but this is where the playback of the value would occur
			 * (just a case statement depending on the value of myValue)
			 */			
			
			
		} catch (IOException ioe) {
			StringItem stringItem = new StringItem(null, ioe.toString());
			System.out.println("caught: "+stringItem);

		}
		
		 finally {
			try {
				if (out != null)
					out.close();
				if (hc != null)
					hc.close();
			} catch (IOException ioe) {
			}
		}
		// ** end network
	}
	/**
	 *	stop the camera, show the captured image and transmit the image to a server
	 **/
	void transmitImage(byte[] image) {
		cameraSave.stop();
		Display.getDisplay(this).setCurrent(displayImage);
		buildHTTPConnection(image);
	}

	void getReturnAudio(HttpConnection c, String url) {

		//HttpConnection c = null;
		//InputStream is = null;
		OutputStream os = null;
		byte[] stuff = null;
		int size = 0;
		try {

			//System.out.println("WHAT UP");
			//c = (HttpConnection)Connector.open(url, Connector.READ_WRITE, true);
			//c.setRequestMethod(HttpConnection.GET); //default
			InputStream in = c.openInputStream();
			byte[] data = new byte[1024 * 4];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			while (true) {
				int len = in.read(data);
				if (len == -1)
					break;
				baos.write(data, 0, len);
				
			}
			byte[] realdata = baos.toByteArray();
			String ss = new String(realdata);
			stuff = Base64.decode(ss);
			size = stuff.length;

		  /*
		  while(true){
			  int a = in.read(data);
			  if (a == -1)
				  break;
			  tot += a;
		  }
		  */
		  //size = tot;//data.length;


		  /*
		  String s = new String(data);
		  byte[] realdata = Base64.decode(s);
		  size = realdata.length;
		  */


		  /*
		  File f;
		  // Write the sound file
		  FileOutputStream f1;

		  String imageName = "images/image.png";
		  try {
			  f1 = new FileOutputStream(imageName);
			  f1.write(realdata);
			  f1.close();
		  } catch (IOException e) {
			  System.out.println("Problems creating the file");
		  }
		*/


		}
		catch (IOException x){
			x.printStackTrace();
		}
		finally{
		     try     {
		       //is.close();
		          c.close();
		     } catch (IOException x){
		          x.printStackTrace();
		     }
		}

		//System.out.println("Size of file: " + size + " bytes.");
		//display.setCurrent(t);;

		ByteArrayInputStream bs = new ByteArrayInputStream(stuff);
		
//		byte[] somebytes = bs.toByteArray();
		String temp = new String(stuff);
		//System.out.println("TEMP: " + temp + "'");
		
		try{
			Player player = Manager.createPlayer(bs, "audio/x-wav");
			player.realize();
			player.start();
			//Manager.playTone(440, 1000, 100);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	

	}
}
