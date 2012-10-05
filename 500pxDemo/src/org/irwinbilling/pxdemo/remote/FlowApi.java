package org.irwinbilling.pxdemo.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.irwinbilling.pxdemo.flow.FlowImageView.FlowImage;

public class FlowApi {
	private static final String ROOT = "http://500px.com/?nolayout=true&page=";

	private static Pattern urlPattern = Pattern.compile( "http://.*\\.jpg" );
	private static Pattern titlePattern = Pattern.compile( "title=\"[^\"]*" );
	
	private int currentPage;
	
	public FlowApi() {
		currentPage = 1;
	}
	
	/**
	 * Retrieve the next page of images.
	 * 
	 * Will vary in the number of images returned.
	 * 
	 * @param handler
	 */
	public List< FlowImage > getNextPage() {
		BufferedReader bufferedReader = null;
		List< FlowImage > images = new ArrayList< FlowImage >();
		
		try {
			bufferedReader = new BufferedReader(
					new InputStreamReader(new URL(
							ROOT + currentPage).openStream()));

			StringBuilder body = new StringBuilder();
			String s = null;
			while( ( s = bufferedReader.readLine() ) != null )
				body.append( s + "\n" );
			
			String[] lines = body.toString().split( "\n" );
			for ( String line : lines ) {
				Matcher matches = urlPattern.matcher( line );
		        if ( matches.find() ) {
		        	String url = matches.group();
		        	String title = "None";
		        	matches = titlePattern.matcher( line );
		        	if ( matches.find() )
		        		title = matches.group().substring( 7 );
		        	
		        	images.add( new FlowImage( url, title ) );
		        }
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
			}
		}
		
		currentPage++;
		return images;
	}
}
