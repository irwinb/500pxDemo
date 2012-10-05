package org.irwinbilling.pxdemo;

import java.util.ArrayList;
import java.util.List;

import org.irwinbilling.pxdemo.flow.FlowImageView;
import org.irwinbilling.pxdemo.flow.ImageLoader;
import org.irwinbilling.pxdemo.flow.RowGenerator;
import org.irwinbilling.pxdemo.flow.FlowImageView.FlowImage;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

public class MainActivity extends ListActivity implements OnScrollListener {

	public static final int IMAGE_PADDING = 3;

	public static Point winSize = new Point();
	public static Handler handler;
	
	RowGenerator gen;
	ImageLoader imageLoader;
	ListView listView;
	FlowListAdapter adapter;
	
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
		
        handler = new Handler();
		winSize.set( getWindowManager().getDefaultDisplay().getWidth(),
				getWindowManager().getDefaultDisplay().getHeight() );
		
		gen = new RowGenerator();
		adapter = new FlowListAdapter( this );
		imageLoader = new ImageLoader();

		initImageLoader();
		
		listView = getListView();
		listView.setDividerHeight( IMAGE_PADDING );
		listView.setOnScrollListener( this );
		
		this.setListAdapter( adapter );
	}

	private void initImageLoader() {
		DisplayImageOptions options =
				new DisplayImageOptions.Builder()
					.cacheInMemory()
					.cacheOnDisc()
					.build();
		ImageLoaderConfiguration config =
				new ImageLoaderConfiguration.Builder( getApplicationContext() )
						.defaultDisplayImageOptions( options )
						.memoryCacheExtraOptions( winSize.x, winSize.x )
						.discCacheExtraOptions( winSize.x, winSize.x, 
								CompressFormat.JPEG, 100 )
						.build();
		FlowImageView.imageLoader.init(config);
	}
	
	/**
	 * Initialize the list with a few items.
	 */
	private void initializeList() {
		addNewRows( 3 );
	}
	
	/**
	 * Add new rows of images.
	 * 
	 * @param numRows
	 */
	private void addNewRows( int numRows ) {
		for ( int i = 0; i < numRows; i++ ) {
			adapter.addItem( gen.nextRect() );
		}
	}

	public void onScroll( AbsListView view, int firstVisibleItem, 
			int visibleItemCount, int totalItemCount ) {
		if ( firstVisibleItem + visibleItemCount == totalItemCount ) {
			addNewRows( 3 );
		}
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {}
	
	public static class FlowListAdapter extends BaseAdapter {

		private class FlowRect {
			public String rect;
			public FlowImage flowImage;
		}
		
		List< List< FlowRect > > items;
		MainActivity context;
		int width;
		LayoutInflater inflator;
		
		public FlowListAdapter( MainActivity c )  {
			items = new ArrayList< List< FlowRect > >();
			context = c;
			width = context.winSize.x;
			inflator = LayoutInflater.from( context );
		}
		
		public int getCount() {
			return items.size();
		}

		public Object getItem( int position ) {
			return position;
		}

		public long getItemId( int position ) {
			return position;
		}
		
		public boolean isEnabled( int position ) {
			return false;
		}

		public View getView( int position, View convertView, ViewGroup parent ) {
			RelativeLayout layout = new RelativeLayout( context );
			layout.setLayoutParams( new AbsListView.LayoutParams( width, width ) );
			
			List< FlowRect > rects = items.get( position );
			for ( FlowRect rect : rects ) {
				LayoutParams imgParams = getLayout( rect.rect );
				FlowImageView imageView = (FlowImageView) inflator.inflate( R.layout.flow_image_view, null );
				
				if ( rect.flowImage == null ) {
					FlowImage newImage = new FlowImage( "", "" );
					rect.flowImage = newImage;
					context.imageLoader.addView( imageView, newImage );
				}
				else
					imageView.setFlowImage( rect.flowImage );
				
				layout.addView( imageView, imgParams );
			}
			
			return layout;
		}
		
		public void addItem( List< String > rects ) {
			List< FlowRect > flowRects = new ArrayList< FlowRect >();
			for ( String s : rects ) {
				FlowRect r = new FlowRect();
				r.rect = s;
				flowRects.add( r );
			}
			
			items.add( flowRects );
			notifyDataSetChanged();
		}
		
		private LayoutParams getLayout( String rect ) {
			LayoutParams layout = new LayoutParams( 0, 0 );
			
			if ( rect.equals( "A" ) ) {
				layout = getSmallSquare();
				layout.leftMargin = MainActivity.IMAGE_PADDING;
				layout.rightMargin = (int) (MainActivity.IMAGE_PADDING * 0.5);
				layout.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
				layout.addRule( RelativeLayout.ALIGN_PARENT_TOP );
			} else if ( rect.equals( "B" ) ) {
				layout = getSmallSquare();
				layout.rightMargin = MainActivity.IMAGE_PADDING;
				layout.leftMargin = (int) (MainActivity.IMAGE_PADDING * 0.5);
				layout.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
				layout.addRule( RelativeLayout.ALIGN_PARENT_TOP );
			} else if ( rect.equals( "C" ) ) {
				layout = getSmallSquare();
				layout.rightMargin = MainActivity.IMAGE_PADDING;
				layout.leftMargin = (int) (MainActivity.IMAGE_PADDING * 0.5);
				layout.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
				layout.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
			} else if ( rect.equals( "D" ) ) {
				layout = getSmallSquare();
				layout.leftMargin = MainActivity.IMAGE_PADDING;
				layout.rightMargin = (int) (MainActivity.IMAGE_PADDING * 0.5);
				layout.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
				layout.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
			} else if ( rect.equals( "AB" ) ) {
				layout = getHorizontalRect();
				layout.rightMargin = MainActivity.IMAGE_PADDING;
				layout.leftMargin = layout.rightMargin;
				layout.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
				layout.addRule( RelativeLayout.ALIGN_PARENT_TOP );
			} else if ( rect.equals( "BC" ) ) {
				layout = getVerticleRect();
				layout.rightMargin = MainActivity.IMAGE_PADDING;
				layout.leftMargin = (int) (MainActivity.IMAGE_PADDING * 0.5);
				layout.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
				layout.addRule( RelativeLayout.ALIGN_PARENT_TOP );
			} else if ( rect.equals( "CD" ) ) {
				layout = getHorizontalRect();
				layout.rightMargin = MainActivity.IMAGE_PADDING;
				layout.leftMargin = layout.rightMargin;
				layout.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
				layout.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
			} else if ( rect.equals( "DA" ) ) {
				layout = getVerticleRect();
				layout.rightMargin = (int) (MainActivity.IMAGE_PADDING * 0.5);
				layout.leftMargin = MainActivity.IMAGE_PADDING;
				layout.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
				layout.addRule( RelativeLayout.ALIGN_PARENT_TOP );
			} else { // ABCD
				layout = new LayoutParams( width, width );
				layout.width -= MainActivity.IMAGE_PADDING * 2;
				layout.height = layout.height;
				layout.rightMargin = MainActivity.IMAGE_PADDING;
				layout.leftMargin = MainActivity.IMAGE_PADDING;
				layout.addRule( RelativeLayout.CENTER_HORIZONTAL );
			}
			
			return layout;
		}
		
		/**
		 * Return a small square (0.5, 0.5)
		 * @return
		 */
		private LayoutParams getSmallSquare() {
			LayoutParams layout = new LayoutParams( (int) (width * 0.5), 
					(int) (width * 0.5) );
			layout.width -= MainActivity.IMAGE_PADDING 
					+ (int) (MainActivity.IMAGE_PADDING * 0.5);
			layout.height = layout.width;
			layout.bottomMargin = IMAGE_PADDING;
			
			return layout;
		}
		
		/**
		 * Return a horizontal rectangle (1.0, 0.5)
		 * 
		 * @return
		 */
		private LayoutParams getHorizontalRect() {
			LayoutParams layout = new LayoutParams( width, 
					(int) (width * 0.5) );
			layout.width -= MainActivity.IMAGE_PADDING * 2;
			layout.height = (int) (layout.width * 0.5);
			layout.bottomMargin = IMAGE_PADDING;
			
			return layout;
		}
		
		/**
		 * Return a horizontal rectangle (1.0, 0.5)
		 * 
		 * @return
		 */
		private LayoutParams getVerticleRect() {
			LayoutParams layout = new LayoutParams( (int) (width * 0.5), 
					width );
			layout.width -= MainActivity.IMAGE_PADDING 
					+ (int) (MainActivity.IMAGE_PADDING * 0.5);
			// layout.height = layout.width * 2;
			layout.bottomMargin = IMAGE_PADDING;
			
			return layout;
		}
	}
}
