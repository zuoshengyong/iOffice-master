package com.wxiwei.office.officereader;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.wxiwei.office.common.IOfficeToPicture;
import com.wxiwei.office.constant.EventConstant;
import com.wxiwei.office.constant.MainConstant;
import com.wxiwei.office.constant.wp.WPViewConstant;
import com.wxiwei.office.macro.DialogListener;
import com.wxiwei.office.officereader.beans.AImageButton;
import com.wxiwei.office.officereader.beans.AImageCheckButton;
import com.wxiwei.office.officereader.beans.AToolsbar;
import com.wxiwei.office.officereader.beans.CalloutToolsbar;
import com.wxiwei.office.res.ResKit;
import com.wxiwei.office.ss.sheetbar.SheetBar;
import com.wxiwei.office.system.FileKit;
import com.wxiwei.office.system.IControl;
import com.wxiwei.office.system.IMainFrame;
import com.wxiwei.office.system.MainControl;
import com.wxiwei.office.system.beans.pagelist.IPageListViewListener;
import com.wxiwei.office.system.dialog.ColorPickerDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件注释
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            梁金晶
 * <p>
 * 日期:            2011-10-27
 * <p>
 * 负责人:          梁金晶
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */

public class AppActivity extends Activity implements IMainFrame
{
    /**
     * 构造器
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        control = new MainControl(this);
        appFrame = new AppFrame(getApplicationContext());
        appFrame.post(new Runnable()
        {
            /**
             */
            public void run()
            {
                init();
            }
        });
        control.setOffictToPicture(new IOfficeToPicture()
        {
            public Bitmap getBitmap(int componentWidth, int componentHeight)
            {                
                if (componentWidth == 0 || componentHeight == 0)
                {
                    return null;
                }
                if (bitmap == null 
                    || bitmap.getWidth() != componentWidth
                    || bitmap.getHeight() != componentHeight)
                {
                    // custom picture size
                    if (bitmap != null)
                    {
                        bitmap.recycle();
                    }
                    //bitmap = Bitmap.createBitmap(800, 600,  Config.ARGB_8888);
                    // 
                    bitmap = Bitmap.createBitmap((int)(componentWidth), (int)(componentHeight),  Config.ARGB_8888);
                }
                return bitmap;
                //return null;
            }
            
            public void callBack(Bitmap bitmap)
            {
                saveBitmapToFile(bitmap);
            }
            //
            private Bitmap bitmap;
			@Override
			public void setModeType(byte modeType) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public byte getModeType() {
				// TODO Auto-generated method stub
				return VIEW_CHANGE_END;
			}

			@Override
			public boolean isZoom() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
        });
        setContentView(appFrame);
    }
    
    private void saveBitmapToFile(Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return;
        }
        if (tempFilePath == null)
        {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state))
            {
                tempFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            File file = new File(tempFilePath + File.separatorChar + "tempPic");
            if (!file.exists())
            {
                file.mkdir();
            }
            tempFilePath = file.getAbsolutePath();
        }
        
        File file = new File(tempFilePath + File.separatorChar +"export_image.jpg");
        try
        {
            if (file.exists())
            {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            
        }
        catch(IOException e)
        {
        }
        finally
        {
            //bitmap.recycle();
        }
    }
    
    public void setButtonEnabled(boolean enabled)
    {
    	if (fullscreen)
    	{
	    	pageUp.setEnabled(enabled);
	    	pageDown.setEnabled(enabled);
	    	penButton.setEnabled(enabled);
	    	eraserButton.setEnabled(enabled);
	    	settingsButton.setEnabled(enabled);
    	}
    }

    protected void onPause()
    {
        super.onPause();

        Object obj = control.getActionValue(EventConstant.PG_SLIDESHOW, null);
        if (obj != null && (Boolean)obj)
        {
            wm.removeView(pageUp);
            wm.removeView(pageDown);
            wm.removeView(penButton);
            wm.removeView(eraserButton);
            wm.removeView(settingsButton);
        }
    }

    protected void onResume()
    {
        super.onResume();
        Object obj = control.getActionValue(EventConstant.PG_SLIDESHOW, null);
        if (obj != null && (Boolean)obj)
        {
            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wm.addView(penButton, wmParams);
            
            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wmParams.y = wmParams.height;
            wm.addView(eraserButton, wmParams);
            
            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wmParams.y = wmParams.height * 2;
            wm.addView(settingsButton, wmParams);
            
            wmParams.gravity = Gravity.LEFT | Gravity.CENTER;
            wmParams.x = MainConstant.GAP;
            wmParams.y = 0;
            wm.addView(pageUp, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.CENTER;
            wm.addView(pageDown, wmParams);
        }
    }

    /**
     * 
     */
    public void onBackPressed()
    {


            Object obj = control.getActionValue(EventConstant.PG_SLIDESHOW, null);
            if (obj != null && (Boolean)obj)
            {
                fullScreen(false);
                //
                this.control.actionEvent(EventConstant.PG_SLIDESHOW_END, null);
            }
            else
            {
                if (control.getReader() != null)
                {
                    control.getReader().abortReader();
                }

                if (control != null && control.isAutoTest())
                {
                    System.exit(0);
                }
                else
                {
                    super.onBackPressed();
                }
        }
    }



    /**
     * 
     *
     */
    protected void onDestroy()
    {
        dispose();
        super.onDestroy();
    }

    /**
     * 
     *(non-Javadoc)
     * @see com.wxiwei.office.system.IMainFrame#showProgressBar(boolean)
     *
     */
    public void showProgressBar(boolean visible)
    {
        setProgressBarIndeterminateVisibility(visible);
    }

    /**
     * 
     */
    private void init()
    {
        //
        toast = Toast.makeText(getApplicationContext(), "", 0);
        //
        Intent intent = getIntent();

        filePath = intent.getStringExtra(MainConstant.INTENT_FILED_FILE_PATH);
        // 文件关联打开文件
        if (filePath == null)
        {
            this.filePath = intent.getDataString();
            int index = getFilePath().indexOf(":");
            if (index > 0)
            {
                filePath = filePath.substring(index + 3);
            }
            filePath = Uri.decode(filePath);
        }

        // 显示打开文件名称
        int index = filePath.lastIndexOf(File.separator);
        if (index > 0)
        {
            setTitle(filePath.substring(index + 1));
        }
        else
        {
            setTitle(filePath);
        }

        boolean isSupport = FileKit.instance().isSupport(filePath);


        // open file
        control.openFile(filePath);
        // initialization marked
        initMarked();
    }

    /**
     * true: show message when zooming
     * false: not show message when zooming
     * @return
     */
    public boolean isShowZoomingMsg()
    {
        return true;
    }

    /**
     * true: pop up dialog when throw err
     * false: not pop up dialog when throw err
     * @return
     */
    public boolean isPopUpErrorDlg()
    {
        return true;
    }



    /**
     * 
     * @return
     */
    private boolean isSearchbarActive()
    {
        if (appFrame == null || isDispose)
        {
            return false;
        }
        int count = appFrame.getChildCount();
        for (int i = 0; i < count; i++)
        {
            View v = appFrame.getChildAt(i);
            if (v instanceof FindToolBar)
            {
                return v.getVisibility() == View.VISIBLE;
            }
        }
        return false;
    }


    
    /**
     * show toolbar or search bar
     * @param show
     */
    public void showCalloutToolsBar(boolean show)
    {
        //show callout bar
        if (show)
        {
            if (calloutBar == null)
            {
                calloutBar = new CalloutToolsbar(getApplicationContext(), control);
                appFrame.addView(calloutBar, 0);
            }
            calloutBar.setCheckState(EventConstant.APP_PEN_ID, AImageCheckButton.CHECK);
            calloutBar.setCheckState(EventConstant.APP_ERASER_ID, AImageCheckButton.UNCHECK);
            calloutBar.setVisibility(View.VISIBLE);
        }
        // hide callout bar
        else
        {
            if (calloutBar != null)
            {
            	calloutBar.setVisibility(View.GONE);
            }
        }
    }
    
    public void setPenUnChecked()
    {
    	if (fullscreen)
    	{
    		penButton.setState(AImageCheckButton.UNCHECK);
    		penButton.postInvalidate();
    	}
    	else
    	{
	    	calloutBar.setCheckState(EventConstant.APP_PEN_ID, AImageCheckButton.UNCHECK);
	    	calloutBar.postInvalidate();
    	}
    }
    
    public void setEraserUnChecked()
    {
    	if (fullscreen)
    	{
    		eraserButton.setState(AImageCheckButton.UNCHECK);
    		eraserButton.postInvalidate();
    	}
    	else
    	{
	    	calloutBar.setCheckState(EventConstant.APP_ERASER_ID, AImageCheckButton.UNCHECK);
	    	calloutBar.postInvalidate();
    	}
    }

    /**
     * set the find back button and find forward button state
     * @param state
     */
    public void setFindBackForwardState(boolean state)
    {
        if (isSearchbarActive())
        {
        }
    }

    /**
     * 发送邮件
     */
    public void fileShare()
    {
        ArrayList<Uri> list = new ArrayList<Uri>();

        File file = new File(filePath);
        list.add(Uri.fromFile(file));

        Intent intent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_STREAM, list);
        intent.setType("application/octet-stream");
        startActivity(Intent
            .createChooser(intent, getResources().getText(R.string.sys_share_title)));
    }

    /**
     *
     * @return
     */
    public void initMarked()
    {

    }

    /**
     * 
     * @return
     */
    private void markFile()
    {
        marked = !marked;
    }

    public void resetTitle(String title)
    {
        if (title != null)
        {
            this.setTitle(title);
        }
    }


    /**
     * 
     */
    public Dialog onCreateDialog(int id)
    {
        return control.getDialog(this, id);
    }

    /**
     * 更新工具条的状态
     */
    public void updateToolsbarStatus()
    {
        if (appFrame == null || isDispose)
        {
            return;
        }
        int count = appFrame.getChildCount();
        for (int i = 0; i < count; i++)
        {
            View v = appFrame.getChildAt(i);
            if (v instanceof AToolsbar)
            {
                ((AToolsbar)v).updateStatus();
            }
        }
    }

    /**
     * 
     */
    public IControl getControl()
    {
        return this.control;
    }

    /**
     * 
     */
    public int getApplicationType()
    {
        return this.applicationType;
    }

    /**
     * @return Returns the filePath.
     */
    public String getFilePath()
    {
        return filePath;
    }

    /**
     * 
     *
     */
    public Activity getActivity()
    {
        return this;
    }

    /**
     * do action，this is method don't call <code>control.actionEvent</code> method, Easily lead to infinite loop 
     *
     * @param actionID action ID 
     * @see #
     * @param obj acValue
     * 
     * @return  True if the listener has consumed the event, false otherwise. 
     */
    public boolean doActionEvent(int actionID, Object obj)
    {
        try
        {
            switch (actionID)
            {
                case EventConstant.SYS_RESET_TITLE_ID:
                    setTitle((String)obj);
                    break;

                case EventConstant.SYS_ONBACK_ID:
                    onBackPressed();
                    break;

                case EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS: //update toolsbar state
                    updateToolsbarStatus();
                    break;

                case EventConstant.SYS_HELP_ID: //show help net
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources()
                        .getString(R.string.sys_url_wxiwei)));
                    startActivity(intent);
                    break;

                case EventConstant.APP_FIND_ID: //show search bar
                    break;

                case EventConstant.APP_SHARE_ID: //share file
                    fileShare();
                    break;

                case EventConstant.FILE_MARK_STAR_ID: //mark
                    markFile();
                    break;

                case EventConstant.APP_FINDING:
                    String content = ((String)obj).trim();
                    if (content.length() > 0 && control.getFind().find(content))
                    {
                        setFindBackForwardState(true);
                    }
                    else
                    {
                        setFindBackForwardState(false);
                        toast.setText(getLocalString("DIALOG_FIND_NOT_FOUND"));
                        toast.show();
                    }
                    break;

                case EventConstant.APP_FIND_BACKWARD:
                    if (!control.getFind().findBackward())
                    {
                        toast.setText(getLocalString("DIALOG_FIND_TO_BEGIN"));
                        toast.show();
                    }
                    else
                    {
                    }
                    break;

                case EventConstant.APP_FIND_FORWARD:
                    if (!control.getFind().findForward())
                    {
                        toast.setText(getLocalString("DIALOG_FIND_TO_END"));
                        toast.show();
                    }
                    else
                    {
                    }
                    break;

                case EventConstant.SS_CHANGE_SHEET:
                    bottomBar.setFocusSheetButton((Integer)obj);
                    break;
                    
                case EventConstant.APP_DRAW_ID:
                	showCalloutToolsBar(true);
                	control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_CALLOUTDRAW);
                	appFrame.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							control.actionEvent(EventConstant.APP_INIT_CALLOUTVIEW_ID, null);
							
						}
					});
                	
                	break;
                	
                case EventConstant.APP_BACK_ID:
                	showCalloutToolsBar(false);
                	control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                	break;
                	
                case EventConstant.APP_PEN_ID:
                	if ((Boolean)obj)
                	{
                		control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_CALLOUTDRAW);
                		setEraserUnChecked();
                    	appFrame.post(new Runnable() {
    						
    						@Override
    						public void run() {
    							// TODO Auto-generated method stub
    							control.actionEvent(EventConstant.APP_INIT_CALLOUTVIEW_ID, null);
    							
    						}
    					});
                	}
                	else
                	{
                		control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                	}
                	break;
                	
                case EventConstant.APP_ERASER_ID:
                	if ((Boolean)obj)
                	{
                		control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_CALLOUTERASE);
                		setPenUnChecked();
                	}
                	else
                	{
                		control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                	}
                	break;
                	
                case EventConstant.APP_COLOR_ID:
                	ColorPickerDialog dlg = new ColorPickerDialog(this, control);
                	dlg.show();
            		dlg.setOnDismissListener(new OnDismissListener() {
    					
    					@Override
    					public void onDismiss(DialogInterface dialog) {
    						setButtonEnabled(true);
    					}
    				});
                	setButtonEnabled(false);
                	break;

                default:
                    return false;
            }
        }
        catch(Exception e)
        {
            control.getSysKit().getErrorKit().writerLog(e);
        }
        return true;
    }



    /**
     * 
     */
    public void openFileFinish()
    {

        //
        View app = control.getView();
        appFrame.addView(app,
            new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    }

    /**
     * 
     */
    public int getBottomBarHeight()
    {
        if (bottomBar != null)
        {
            return bottomBar.getSheetbarHeight();
        }
        return 0;
    }

    /**
     * 
     */
    public int getTopBarHeight()
    {
        return 0;
    }

    /**
     * event method, office engine dispatch 
     * 
     * @param       v             event source
     * @param       e1            MotionEvent instance
     * @param       e2            MotionEvent instance
     * @param       xValue        eventNethodType is ON_SCROLL, this is value distanceX
     *                             eventNethodType is ON_FLING, this is value velocityY
     *                             eventNethodType is other type, this is value -1   
     * 
     * @param       yValue        eventNethodType is ON_SCROLL, this is value distanceY
     *                             eventNethodType is ON_FLING, this is value velocityY
     *                             eventNethodType is other type, this is value -1  
     * @param
     *              @see IMainFrame#ON_CLICK
     *              @see IMainFrame#ON_DOUBLE_TAP
     *              @see IMainFrame#ON_DOUBLE_TAP_EVENT
     *              @see IMainFrame#ON_DOWN
     *              @see IMainFrame#ON_FLING
     *              @see IMainFrame#ON_LONG_PRESS
     *              @see IMainFrame#ON_SCROLL
     *              @see IMainFrame#ON_SHOW_PRESS
     *              @see IMainFrame#ON_SINGLE_TAP_CONFIRMED
     *              @see IMainFrame#ON_SINGLE_TAP_UP
     *              @see IMainFrame#ON_TOUCH
     */
    public boolean onEventMethod(View v, MotionEvent e1, MotionEvent e2, float xValue,
        float yValue, byte eventMethodType)
    {
        return false;
    }

    
    public void changePage()
    {   
    }

    /**
     * 
     */
    public String getAppName()
    {
         return getString(R.string.sys_name);

    }

    /**
     * 是否绘制页码
     */
    public boolean isDrawPageNumber()
    {
        return true;
    }

    /**
     * 是否支持zoom in / zoom out
     */
    public boolean isTouchZoom()
    {
        return true;
    }

    /**
     * Word application 默认视图(Normal or Page)
     * 
     * @return  WPViewConstant.PAGE_ROOT or WPViewConstant.NORMAL_ROOT
     *           
     */
    public byte getWordDefaultView()
    {
        return WPViewConstant.PAGE_ROOT;
        //return WPViewConstant.NORMAL_ROOT;
    }

    /**
     * normal view, changed after zoom bend, you need to re-layout
     * 
     *  @return  true   re-layout
     *            false  don't re-layout   
     */
    public boolean isZoomAfterLayoutForWord()
    {
        return true;
    }



    /**
     * full screen, not show top tool bar
     */
    public void fullScreen(boolean fullscreen)
    {
    	this.fullscreen = fullscreen;
        if (fullscreen)
        {


            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wm.addView(penButton, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wmParams.y = wmParams.height;
            wm.addView(eraserButton, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wmParams.y = wmParams.height * 2;
            wm.addView(settingsButton, wmParams);

            wmParams.gravity = Gravity.LEFT | Gravity.CENTER;
            wmParams.x = MainConstant.GAP;
            wmParams.y = 0;
            wm.addView(pageUp, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.CENTER;
            wm.addView(pageDown, wmParams);

            //hide title and tool bar
            ((View)getWindow().findViewById(android.R.id.title).getParent())
                .setVisibility(View.GONE);
            //hide status bar
            //

            penButton.setState(AImageCheckButton.UNCHECK);
            eraserButton.setState(AImageCheckButton.UNCHECK);

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            //landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
        else
        {
            wm.removeView(pageUp);
            wm.removeView(pageDown);
            wm.removeView(penButton);
            wm.removeView(eraserButton);
            wm.removeView(settingsButton);
            //show title and tool bar
            ((View)getWindow().findViewById(android.R.id.title).getParent())
                .setVisibility(View.VISIBLE);

            //show status bar
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

    }

    /**
     * 
     */

    public void changeZoom()
    {
    }

    /**
     * 
     */
    public void error(int errorCode)
    {
    }

    /**
     * when need destroy the office engine instance callback this method 
     * 
     */
    public void destroyEngine()
    {
        super.onBackPressed();
    }

    /**
     * get Internationalization resource
     * 
     * @param resName Internationalization resource name
     */
    public String getLocalString(String resName)
    {
        return ResKit.instance().getLocalString(resName);
    }

    @ Override
    public boolean isShowPasswordDlg()
    {
        return true;
    }

    @ Override
    public boolean isShowProgressBar()
    {
        return true;
    }

    @ Override
    public boolean isShowFindDlg()
    {
        return true;
    }

    @ Override
    public boolean isShowTXTEncodeDlg()
    {
        return true;
    }

    /**
     * get txt default encode when not showing txt encode dialog
     * @return null if showing txt encode dialog
     */
    public String getTXTDefaultEncode()
    {
        return "GBK";
    }

    /**
     * 
     */
    public DialogListener getDialogListener()
    {
        return null;
    }


    @ Override
    public void completeLayout()
    {
        // TODO Auto-generated method stub
        
    }

    @ Override
    public boolean isChangePage()
    {
        // TODO Auto-generated method stub
        return true;
    }
    
    /**
     * 
     * @param saveLog
     */
    public void setWriteLog(boolean saveLog)
    {
        this.writeLog = saveLog;
    }
    
    /**
     * 
     * @return
     */
    public boolean isWriteLog()
    {
        return writeLog;            
    }
    
    /**
     * 
     * @param isThumbnail
     */
    public void setThumbnail(boolean isThumbnail)
    {
        this.isThumbnail = isThumbnail;
    }
    
    /**
     * get view backgrouond
     * @return
     */
    public Object getViewBackground()
    {
    	return bg;
    }
    
    /**
     * set flag whether fitzoom can be larger than 100% but smaller than the max zoom
     * @param ignoreOriginalSize
     */
    public void setIgnoreOriginalSize(boolean ignoreOriginalSize)
    {
    	
    }
    
    /**
     * 
     * @return
     * true fitzoom may be larger than 100% but smaller than the max zoom
     * false fitzoom can not larger than 100%
     */
    public boolean isIgnoreOriginalSize()
    {
    	return false;
    }
    
    public byte getPageListViewMovingPosition()
    {
    	return IPageListViewListener.Moving_Horizontal;
    }
    
    /**
     * 
     * @return
     */
    public boolean isThumbnail()
    {
        return isThumbnail;
    }
    
    /**
     * 
     * @param viewList
     */
    public void updateViewImages(List<Integer> viewList)
    {
    	
    }
    
    /**
     * 
     * @return
     */
    public File getTemporaryDirectory()
    {
    	// Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = getExternalFilesDir(null);
        if(file != null)
        {
        	return file;
        }
        else
        {
        	return getFilesDir();
        }
    }
    
    /**
     * 释放内存
     *
     */
    public void dispose()
    {
        isDispose = true;
        if (control != null)
        {
            control.dispose();
            control = null;
        }
        bottomBar = null;

        if (appFrame != null)
        {
            int count = appFrame.getChildCount();
            for (int i = 0; i < count; i++)
            {
                View v = appFrame.getChildAt(i);
                if (v instanceof AToolsbar)
                {
                    ((AToolsbar)v).dispose();
                }
            }
            appFrame = null;
        }

        if (wm != null)
        {
            wm = null;
            wmParams = null;
            pageUp.dispose();
            pageDown.dispose();
            penButton.dispose();
            eraserButton.dispose();
            settingsButton.dispose();
            pageUp = null;
            pageDown = null;
            penButton = null;
            eraserButton = null;
            settingsButton = null;
        }
    }

    //
    private boolean isDispose;
    // 当前标星状态
    private boolean marked;
    //
    private int applicationType = -1;
    //
    private String filePath;
    // application activity control
    private MainControl control;
    //
    private AppFrame appFrame;
    //
    //
    private SheetBar bottomBar;
    //
    private Toast toast;

    //float button: PageUp/PageDown
    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private AImageButton pageUp;
    private AImageButton pageDown;
    private AImageCheckButton penButton;
    private AImageCheckButton eraserButton;
    private AImageButton settingsButton;

  //whether write log to temporary file
    private boolean writeLog = true;
    //open file to get thumbnail, or not
    private boolean isThumbnail;
    //view background
    private Object bg = Color.GRAY;
    //
    private CalloutToolsbar calloutBar;
    //
    private boolean fullscreen;
    //
    private String tempFilePath;
}
