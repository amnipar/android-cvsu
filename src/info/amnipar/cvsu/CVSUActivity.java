package info.amnipar.cvsu;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class CVSUActivity extends Activity implements CvCameraViewListener {
	private static final String    TAG = "CVSU::CVSUActivity";

    private static final int       VIEW_MODE_RGBA     = 0;
    private static final int       VIEW_MODE_GRAY     = 1;
    private static final int       VIEW_MODE_CANNY    = 2;
    private static final int       VIEW_MODE_FEATURES = 5;

    private int                    mViewMode;
    private Mat                    mRgba;
    private Mat                    mIntermediateMat;
    private Mat                    mGrayMat;

    private MenuItem               mItemPreviewRGBA;
    private MenuItem               mItemPreviewGray;
    private MenuItem               mItemPreviewCanny;
    private MenuItem               mItemPreviewFeatures;
    
	private CameraBridgeViewBase mOpenCvCameraView;
	
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("cvsu");

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CVSUActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_cvsu);

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.cvsu_surface);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemPreviewRGBA = menu.add("Preview RGBA");
        mItemPreviewGray = menu.add("Preview GRAY");
        mItemPreviewCanny = menu.add("Canny");
        mItemPreviewFeatures = menu.add("Find features");
        return true;
    }
    

    @Override
    public void onPause()
    {
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    	Log.i(TAG, "width=" + width + " heigh=" +height); 
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGrayMat = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGrayMat.release();
        mIntermediateMat.release();
    }

    public Mat onCameraFrame(Mat inputFrame) {
        final int viewMode = mViewMode;

        switch (viewMode) {
        case VIEW_MODE_GRAY:
            // input frame has gray scale format
        	if (inputFrame.type() == CvType.CV_8UC1) {
        		inputFrame.copyTo(mGrayMat);
        	}
        	else {
        		Imgproc.cvtColor(inputFrame, mGrayMat, Imgproc.COLOR_RGBA2GRAY, 1);
        	}
        	ProcessGray(mGrayMat.getNativeObjAddr());
            Imgproc.cvtColor(mGrayMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            break;
        case VIEW_MODE_RGBA:
            // input frame has RBGA format
        	if (inputFrame.type() == CvType.CV_8UC4) {
        		inputFrame.copyTo(mRgba);
        	}
        	else {
        		Imgproc.cvtColor(inputFrame, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
        	}
            ProcessRgba(mRgba.getNativeObjAddr());
            break;
        case VIEW_MODE_CANNY:
            // input frame has gray scale format
        	if (inputFrame.type() == CvType.CV_8UC1) {
        		Imgproc.Canny(inputFrame, mIntermediateMat, 80, 100);
        	}
        	else {
        		Imgproc.cvtColor(inputFrame, mGrayMat, Imgproc.COLOR_RGBA2GRAY, 1);
        		Imgproc.Canny(mGrayMat, mIntermediateMat, 80, 100);
        	}
            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
            break;
        case VIEW_MODE_FEATURES:
            // input frame has RGBA format
            inputFrame.copyTo(mRgba);
            Imgproc.cvtColor(mRgba, mGrayMat, Imgproc.COLOR_RGBA2GRAY);
            DrawTrees(mGrayMat.getNativeObjAddr(), mRgba.getNativeObjAddr());
            break;
        }

        return mRgba;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        if (item == mItemPreviewRGBA) {
            mOpenCvCameraView.SetCaptureFormat(Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
            mViewMode = VIEW_MODE_RGBA;
            return true;
        } else if (item == mItemPreviewGray) {
            mOpenCvCameraView.SetCaptureFormat(Highgui.CV_CAP_ANDROID_GREY_FRAME);
            mViewMode = VIEW_MODE_GRAY;
            return true;
        } else if (item == mItemPreviewCanny) {
            mOpenCvCameraView.SetCaptureFormat(Highgui.CV_CAP_ANDROID_GREY_FRAME);
            mViewMode = VIEW_MODE_CANNY;
            return true;
        } else if (item == mItemPreviewFeatures) {
            mViewMode = VIEW_MODE_FEATURES;
            mOpenCvCameraView.SetCaptureFormat(Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
            return true;
        }
		return super.onOptionsItemSelected(item);
	}

	//public native String cvsuTest();
	//public native String cvsuOther(String message);
	public native void ProcessGray(long matAddrGr);
	public native void ProcessRgba(long matAddrRgba);
	public native void DrawTrees(long matAddrGr, long matAddrRgba);
}
