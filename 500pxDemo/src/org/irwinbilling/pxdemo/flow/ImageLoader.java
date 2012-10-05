package org.irwinbilling.pxdemo.flow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.irwinbilling.pxdemo.flow.FlowImageView.FlowImage;
import org.irwinbilling.pxdemo.remote.FlowApi;

import android.util.Log;

public class ImageLoader implements Runnable {
	
	private static final String TAG = "ImageLoader";
	private static final int IMAGES_BUFFER = 4;
	
	class ImageAndView {
		FlowImageView view;
		FlowImage image;
		
		public ImageAndView( FlowImageView v, FlowImage i ) {
			view = v;
			image = i;
		}
	}
	
	List< ImageAndView > imageViewsBuffer;
	List< FlowImage > imageUrls;
	FlowApi api;
	Object newViewLock;
	
	public ImageLoader() {
		imageViewsBuffer = new ArrayList< ImageAndView >();
		imageUrls = new ArrayList< FlowImage >();
		api = new FlowApi();
		newViewLock = new Object();
		
		Thread t = new Thread( this );
		t.start();
	}
	
	public void addView( FlowImageView view, FlowImage image )  {
		imageViewsBuffer.add( new ImageAndView( view, image ) );
		
		synchronized ( newViewLock ) {
			newViewLock.notifyAll();
		}
	}
	
	/**
	 * Load the next page of images.
	 */
	private void getMoreImages() {
		imageUrls.addAll( api.getNextPage() );
	}

	public void run() {
		while ( true ) {
			synchronized ( newViewLock ) {
				try {
					newViewLock.wait();
				} catch (InterruptedException e) {
					Log.e( TAG, "", e );
				}
			}
			
			if ( imageUrls.size() < IMAGES_BUFFER )
				getMoreImages();
			
			synchronized ( imageViewsBuffer ) {
				Iterator< ImageAndView > it = imageViewsBuffer.iterator();
				while ( it.hasNext() ) {
					ImageAndView iv = it.next();
					
					// Just in case the user scrolls fast..
					if ( imageUrls.size() == 0 )
						getMoreImages();
					
					FlowImage t = imageUrls.remove( 0 );
					iv.image.title = t.title;
					iv.image.url = t.url;
					iv.view.setFlowImage( iv.image );
					
					it.remove();
				}
			}
		}
	}
}
