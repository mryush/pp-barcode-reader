/*
 * CameraForm.java
 *
 * Created on 9. August 2004, 10:34
 */
package org.projectpossibility;
import javax.microedition.media.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.control.*;
import java.io.IOException;
/**
 *
 * @author  Karin
 * @version

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
 */
class CameraForm extends Form implements CommandListener {
	private final CameraMIDlet midlet;
	private final Command exitCommand;
	private Command captureCommand = null;
	private Command showImageCommand = null;
	private Player player = null;
	private VideoControl videoControl = null;
	private boolean active = false;
	private StringItem messageItem;
	VideoRecordingThread videoRecordThread = null;

	public CameraForm(CameraMIDlet midlet) {
		super("Camera");
		this.midlet = midlet;
		messageItem = new StringItem("Message", "start");
		append(messageItem);
		exitCommand = new Command("EXIT", Command.EXIT, 1);
		addCommand(exitCommand);
		setCommandListener(this);
		try {
			//creates a new player and set it to realize
//			player = Manager.createPlayer("capture://video");
			player = Manager.createPlayer("file://BARCODE13_READER.MPG");
			player.realize();
			//Grap the Video control and set it to the current display
			videoControl = (VideoControl) (player.getControl("VideoControl"));
			if (videoControl != null) {
				append((Item) (videoControl.initDisplayMode(
						VideoControl.USE_GUI_PRIMITIVE, null)));
				captureCommand = new Command("CAPTURE", Command.SCREEN, 1);
				addCommand(captureCommand);
				messageItem.setText("OK");
			} else {
				messageItem.setText("No video control");
			}
		} catch (IOException ioe) {
			messageItem.setText("IOException: " + ioe.getMessage());
		} catch (MediaException me) {
			messageItem.setText("Media Exception: " + me.getMessage());
		} catch (SecurityException se) {
			messageItem.setText("Security Exception: " + se.getMessage());
		}
	}
	/**
	 *	the video should be visualized on the sreen
	 *	therefore you have to start the player and set the videoControl visible
	 **/
	synchronized void start() {
		if (!active) {
			try {
				if (player != null) {
					player.start();
				}
				if (videoControl != null) {
					videoControl.setVisible(true);
				}
			} catch (MediaException me) {
				messageItem.setText("Media Exception: " + me.getMessage());
			} catch (SecurityException se) {
				messageItem.setText("Security Exception: " + se.getMessage());
			}
			active = true;
		}
	}
	/**
	 *	to stop the player. First the videoControl has to be set invisible
	 *	than the player can be stopped
	 **/
	synchronized void stop() {
		if (active) {
			try {
				if (videoControl != null) {
					videoControl.setVisible(false);
				}
				if (player != null) {
					player.stop();
				}
			} catch (MediaException me) {
				messageItem.setText("Media Exception: " + me.getMessage());
			}
			active = false;
		}
	}
	/**
	 *	eventHandling
	 *	on the captureCommand a picture is taken and transmited to the server
	 *
	 **/
	public void commandAction(Command c, Displayable d) {
		if (c == exitCommand) {
			midlet.cameraFormExit();
		} else {
			if (c == captureCommand) {
					videoRecordThread = new VideoRecordingThread();
					videoRecordThread.start();

			}
		}
	}
	
	
	
	class VideoRecordingThread extends Thread {
		public VideoRecordingThread() {
		}
		public void run() {
			try {
				byte[] image = videoControl.getSnapshot(null);					
//				byte[] image = videoControl.getSnapshot("encoding=jpeg&width=640&height=480");					
//				byte[] image = videoControl.getSnapshot("width=320&height=240");					
//				byte[] image = videoControl.getSnapshot("width=640&height=480");					
				midlet.transmitImage(image);
				messageItem.setText("Ok");
			} catch (MediaException me) {
				messageItem.setText("Media Exception: " + me.getMessage());
			}			
		}
	}
	
	
}
