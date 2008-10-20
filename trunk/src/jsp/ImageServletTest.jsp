<%@page import="javax.servlet.http.*,javax.servlet.*,java.io.*, org.projectpossibility.*"%>
<%@page import="java.awt.*,java.awt.image.*,java.io.*,javax.imageio.*,java.net.*,com.google.zxing.*, com.google.zxing.common.*, java.net.*, java.util.*,redstone.xmlrpc.XmlRpcClient,redstone.xmlrpc.XmlRpcStruct,java.util.*"%>

<%@page import="javax.sound.sampled.AudioFileFormat,javax.sound.sampled.AudioFileFormat.Type,javax.sound.sampled.AudioSystem"%>
<%@page import="java.io.File,java.io.IOException,com.sun.speech.freetts.Voice,com.sun.speech.freetts.VoiceManager"%>
<%@page import="com.sun.speech.freetts.audio.AudioPlayer,com.sun.speech.freetts.audio.SingleFileAudioPlayer"%>


<%!

	String compressionPath = "/home/scf-22/csci402/crowley/lame-398-2/frontend/";
	
	/* Define Audio Functions */
	
/************* CREATE AUDIO FILE **************/	

	public String createAudioFile(String audioScript){
	
	// Access FreeTTS voices
	//System.setProperty("freetts.voices","com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory");
	System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
	
	// Select desired voice for audio output
	VoiceManager voiceManager = VoiceManager.getInstance(); 
    	Voice voice = voiceManager.getVoice("kevin16");
    	voice.allocate(); 
    
    	// Set output file type for audio Recorder
	AudioFileFormat.Type audioType = AudioFileFormat.Type.WAVE; 
	
	// Single File Player Records audio file
	AudioPlayer recorder = new SingleFileAudioPlayer("test", AudioFileFormat.Type.WAVE); 
	
		try{
			// Write audio script to the file by playing in recorder
			voice.setAudioPlayer(recorder);
			voice.speak(audioScript);
		}
	
		catch(Exception ex){
			//out.print("Error, audio write failed!");
			//System.exit(0);
		}
	
		finally{
			// Close voice and recorder connections
			recorder.close();
			voice.deallocate();
		}

	File f = null;
	FileInputStream fis = null;
	String stringAudio = null;

	try{
		f = new File("test.wav");
		fis = new FileInputStream(f);
		int len = fis.available();
		byte[] data = new byte[len];
		fis.read(data);
		fis.close();
		stringAudio = Base64.encode(data);
	}
	catch (IOException ioe) {
		ioe.printStackTrace();
	}
	//out.print(stringAudio);		
	return stringAudio;

	} // end createAudioFile
		
/************* COMPRESS AUDIO FILE ****************/	

	public void compressAudioFileToMp3(){
	
			try{
				// Try to compress output.wav to output.mp3
				//out.print("MP3 Compression Starting...");
				
				Process compression = Runtime.getRuntime().exec(compressionPath+"lame -h images/output.wav images/output.mp3");
				
				//out.print("MP3 Compression SUCCESS - output.wav -->> output.mp3 !!");
		
				// Clean up Old Wave File
				//Process deleteWave = Runtime.getRuntime().exec("rm output.wav");
				//System.out.println("Wave File Removed - output.wav");
				//System.out.println("TTS complete!!\n");
			
			}
	
			catch(Exception e){
				//out.print("MP3 Compression Fail...");
			}

} // end compression

%>


<%

/***********HERE'S OUR MAIN SEQUENCE *******/

    InputStream in = request.getInputStream();
    BufferedReader r = new BufferedReader(new InputStreamReader(in));
    StringBuffer buf = new StringBuffer();
    String line;
		while ((line = r.readLine()) != null) {
			buf.append(line);
		}
		String s = buf.toString();
		// Write the image of the bill from the cell phone to a file
		FileOutputStream f1;

	   	String imageName = "images/image.png";
		byte[] data = Base64.decode(s);
		try {
			f1 = new FileOutputStream(imageName);
			f1.write(data);
			f1.close();
		} catch (IOException e) {
			out.print("Problems creating the file:" + e.getMessage() );
		}
PrintWriter pw = new PrintWriter("images/results.txt");

Process p = Runtime.getRuntime().exec( "java  com.google.zxing.client.j2se.CommandLineRunner " + imageName );

BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
String pout = br.readLine();

//Retrieve the upc
String values[] = pout.split(":");
String upc = values[2];

//Now split on the space
values = upc.split(" ");
String text = values[1];
pw.println( upc );
br.close();

//Now get the upc info for this upc code

	// String to store output for audio TextToSpeech file
	
	String audioScript = "";
	
	try
	    {
		XmlRpcClient client  = new XmlRpcClient( "http://www.upcdatabase.com/rpc", false);
		XmlRpcStruct result  = (XmlRpcStruct)client.invoke( "lookupUPC", new Object[] { text } );
		HashMap      results = (HashMap)result;
		

		if (
		    results.size()>0 &&
		    results.get("message").toString().equalsIgnoreCase("Database entry found"))
		    {
			pw.println( results.get("description").toString()+"\n"+results.get("size").toString());
			audioScript = results.get("description").toString()+"."+results.get("size").toString();
		    }
		
	    }
	catch (Exception e)
	    {
	    }


/*  Image Analysis
File inputFile = new File(imageName);
    BufferedImage image = ImageIO.read( inputFile);
    if (image == null) {
      pw.println("Could not load image. Image is null.");
      return;
    } else {

	try {
	    MonochromeBitmapSource source = new BufferedImageMonochromeBitmapSource(image);
	    Result result = new MultiFormatReader().decode(source, null );
	    pw.println("UPC: " + result.getText() + " format: " +
		      result.getBarcodeFormat());
	} catch (ReaderException e) {
	    pw.println( "No barcode found" + e.getMessage() );
	}

    }
*/



/**** TEXT TO SPEECH MP3 CREATION ****/

	// Grab Item Data - Convert to Audio, and Compress for Streaming
	String waveFile = createAudioFile(audioScript);
	out.print(waveFile);
	//compressAudioFileToMp3();
	//pw.print(waveFile);
	pw.print("LEN: " + waveFile.length());
	pw.close();
%>
