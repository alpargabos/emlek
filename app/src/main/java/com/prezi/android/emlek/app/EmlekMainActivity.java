package com.prezi.android.emlek.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EmlekMainActivity extends Activity {

	private static final int TAKE_PICTURE = 101;
	private static final int CAPTURE_VIDEO = 102;
	private Uri fileUri;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emlek_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emlek_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
		if(id == R.id.action_camera){
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
			fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

			startActivityForResult(intent, TAKE_PICTURE);
			return true;
		}
		if(id == R.id.action_video){
			//create new Intent
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

			fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name

			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

			// start the Video Capture Intent
			startActivityForResult(intent, CAPTURE_VIDEO);
		}
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case RESULT_CANCELED:
				break;
			case TAKE_PICTURE:
				if (resultCode == Activity.RESULT_OK) {
					File f = new File(fileUri.getPath());
					Bitmap b = decodeFile(f);
					ImageView v = (ImageView) findViewById(R.id.captured_image);
					v.setImageBitmap(b);

				}
				break;
			case CAPTURE_VIDEO:
				if (resultCode == Activity.RESULT_OK) {
					getWindow().setFormat(PixelFormat.TRANSLUCENT);
					VideoView videoHolder = (VideoView) findViewById(R.id.captured_video);
					videoHolder.setMediaController(new MediaController(this));
					videoHolder.setVideoURI(fileUri);
					videoHolder.requestFocus();
					videoHolder.start();
				}
				break;
			default:
				Toast.makeText(this, "Image capture failed! Try again!", Toast.LENGTH_LONG).show();

		}
	}



	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	private Bitmap decodeFile(File f) {
		try {
			FileInputStream is = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			is.close();
			return bitmap;
		} catch (FileNotFoundException ex) {
			Log.e("File not found: {}", f.getAbsolutePath());
		} catch (IOException ex) {
			Log.e("Decode failed: {}", ex.getMessage());
		}

		return null;
	}

	/**
				 * A placeholder fragment containing a simple view.
				 */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_emlek_main, container, false);
            return rootView;
        }
    }
}
