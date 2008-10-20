/*
 * Display.java
 *
 * Created on 9. August 2004, 10:19
 */
package org.projectpossibility;
import javax.microedition.lcdui.*;
/**
 *
 * @author  Karin
 * @version
 *
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

 * 
 */
class DisplayImage extends Canvas implements CommandListener {
	private final CameraMIDlet midlet;
	private Image image = null;
	private StringItem messageItem;
	private final Command exitCommand;
	private Command nextCommand = null;
	DisplayImage(CameraMIDlet midlet) {
		this.midlet = midlet;
		messageItem = new StringItem("Message", "start");
		exitCommand = new Command("EXIT", Command.EXIT, 1);
		addCommand(exitCommand);
		setCommandListener(this);
		nextCommand = new Command("CAMERA", Command.OK, 1);
		addCommand(nextCommand);
	}
	/**
	 *	creates an image from the given byte-Array
	 **/
	void setImage(byte[] pngImage) {
		image = image.createImage(pngImage, 0, pngImage.length);
		repaint();
	}
	public void commandAction(Command c, Displayable d) {
		if (c == exitCommand) {
			midlet.cameraFormExit();
		} else {
			midlet.displayCanvasBack();
		}
	}
	/**
	 *	visualize the image
	 **/
	public void paint(Graphics g) {
		g.setColor(0x0000ffff);//cyan
		g.fillRect(0, 0, getWidth(), getHeight());
		if (image != null) {
			g.drawImage(image, getWidth() / 2, getHeight() / 2,
					Graphics.VCENTER | Graphics.HCENTER);
		}
	}
}
