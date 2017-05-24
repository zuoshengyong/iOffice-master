package com.wxiwei.office.officereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.shockwave.pdfium.PdfDocument;
import com.wxiwei.office.constant.MainConstant;
import com.wxiwei.office.system.IMainFrame;
import com.wxiwei.office.system.MainControl;

import java.io.File;
import java.util.List;

public class PDFViewActivity extends Activity implements OnPageChangeListener, OnLoadCompleteListener  {
    private static final String TAG = PDFViewActivity.class.getSimpleName();

    Integer pageNumber = 0;

    String FilePath;

    PDFView pdfView;
    TextView mTVpageNumber;

    File file;


    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_pdfview);

        pdfView = (PDFView)findViewById(R.id.pdfView);
        mTVpageNumber= (TextView) findViewById(R.id.tv_pageNum);
        Intent intent= getIntent();
        if(intent!=null){
             FilePath = intent.getStringExtra(MainConstant.INTENT_FILED_FILE_PATH);
            file = new File(FilePath);
            setTitle(file.getName());
            displayFromFile(file);
        }
    }


    private void displayFromFile(File file) {

        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .load();
    }



    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;


        mTVpageNumber.setText(String.format(" %s / %s", page + 1, pageCount));
    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }





}
