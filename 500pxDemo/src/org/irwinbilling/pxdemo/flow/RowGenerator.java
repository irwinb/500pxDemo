package org.irwinbilling.pxdemo.flow;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.util.Log;

/**
 * Helper class to generate boxes.
 * 
 * 1.0 x 1.0
 * 1.0 x 0.5
 * 0.5 x 1.0
 * 0.5 x 0.5 
 * 
 * @author Irwin
 */
public class RowGenerator {
	
	private List< Rect > currentRect;
	
	public RowGenerator() {
		resetCurrentRect();
	}
	
	public void resetCurrentRect() {
		currentRect = new ArrayList< Rect >();
		
		currentRect.add( new Rect( "A" ) );
		currentRect.add( new Rect( "B" ) );
		currentRect.add( new Rect( "C" ) );
		currentRect.add( new Rect( "D" ) );
	}
	
	/**
	 * Give me a rect given the available space.
	 * 
	 * @param width
	 * @return
	 */
	public List< String > nextRect() {
		List< String > rects = new ArrayList< String >();
		resetCurrentRect();
		
		List< String > possibleRects;
		while( ( possibleRects = getPossibleRects() ).size() > 0 ) {
			int index = (int)(Math.random() * possibleRects.size());
			String rect = possibleRects.get( index );
			rects.add( rect );
			setFilled( rect );
			Log.d( "Generator", "New Rect: " + rect );
		}
		Log.d( "Generator", "----------------------" );
		return rects;
	}
	
	/**
	 * Given a rect, set all mentioned zones to filled.
	 * 
	 * @param rect
	 */
	private void setFilled( String rect ) {
		for ( char c : rect.toCharArray() ) {
			if ( c == 'A' )
				currentRect.get( 0 ).filled = true;
			else if ( c == 'B' )
				currentRect.get( 1 ).filled = true;
			else if ( c == 'C' )
				currentRect.get( 2 ).filled = true;
			else if ( c == 'D' )
				currentRect.get( 3 ).filled = true;
		}
	}
	
	/**
	 * Calculate all possible remaining rects.
	 * 
	 * @return
	 */
	private List< String > getPossibleRects() {
		List< String > rects = new ArrayList< String >();
		Rect prev = null;
		int totalFree = 0;
		for ( Rect r : currentRect ) {
			if ( !r.filled ) {
				totalFree++;
				rects.add( r.id );
				if ( prev != null && !prev.filled )
					rects.add( prev.id + r.id );
				
				if ( r.id.equals( "D" ) && !currentRect.get(0).filled )
					rects.add( "DA" );
			}
			
			prev = r;
		}
		
		if ( totalFree == 4 )
			rects.add( "ABCD" );
		
		return rects;
	}
	
	/**
	 * A representation of a rect.
	 * 
	 *  The following are id labels.
	 *  _____ _____
	 * |     |     |
	 * |  A  |  B  |
	 * |_____|_____|
	 * |     |     |
	 * |  D  |  C  |
	 * |_____|_____|
	 *
	 * @author Irwin
	 */
	public class Rect {
		public String id;
		public boolean filled;
		
		public Rect( String id ) {
			this.id = id;
			filled = false;
		}
	}
}
