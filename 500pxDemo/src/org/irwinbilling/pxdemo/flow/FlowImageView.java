package org.irwinbilling.pxdemo.flow;

import org.irwinbilling.pxdemo.MainActivity;
import org.irwinbilling.pxdemo.R;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FlowImageView extends RelativeLayout {

	public static ImageLoader imageLoader = ImageLoader.getInstance();
	
	FlowImage flowImage;
	ImageView imageView;
	TextView title;
	
	public FlowImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		flowImage = null;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		imageView = (ImageView) findViewById( R.id.flowImage );
		title = (TextView) findViewById( R.id.flowText );
	}
	
	/**
	 * Set an image.  Can be called from outside UI thread.
	 * 
	 * @param flowImage
	 */
	public void setFlowImage( FlowImage flowImage ) {
		this.flowImage = flowImage;
		
		// Our own may be yet be null.
		MainActivity.handler.post( new Runnable() {
			
			public void run() {
//				if ( imageView.getWidth() > MainActivity.winSize.x * 0.5
//						&& imageView.getHeight() > MainActivity.winSize.x * 0.5 )
//					imageView.setScaleType( ScaleType.CENTER_CROP );
//				else 
//					imageView.setScaleType( ScaleType.CENTER );
				title.setText( FlowImageView.this.flowImage.title );
				imageLoader.displayImage( FlowImageView.this.flowImage.url, 
						imageView );
			}
		} );
	}
	
	public boolean initialized() {
		return flowImage != null;
	}
	
	public static class FlowImage {
		public String url;
		public String title;
		
		public FlowImage( String url, String title ) {
			this.url = url;
			this.title = title;
		}
	}
}
