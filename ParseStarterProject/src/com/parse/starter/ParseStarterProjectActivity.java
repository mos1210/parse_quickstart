package com.parse.starter;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

public class ParseStarterProjectActivity extends Activity implements OnClickListener {

    private static final String TAG = ParseStarterProjectActivity.class.getSimpleName();

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button ins = (Button) findViewById(R.id.btnInsert);
        ins.setOnClickListener(this);

        Button del = (Button) findViewById(R.id.btnDelete);
        del.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    /**
     * init
     */
    private void init() {

        ParseQuery query = new ParseQuery("test");
        query.whereEqualTo("foo", "bar");
        query.findInBackground(new FindCallback() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    final ImageView iv = (ImageView) findViewById(R.id.imageView1);
                    if (list.size() > 0) {
                        ParseObject parseObject = list.get(0);

                        ParseFile file = (ParseFile) parseObject.get("img");
                        file.getDataInBackground(new GetDataCallback() {

                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                                            data.length);
                                    iv.setImageBitmap(bitmap);
                                } else {
                                    Log.e(TAG, "Error: ", e);
                                }
                            }
                        });
                    } else {
                        iv.setImageDrawable(null);
                    }
                } else {
                    Log.e(TAG, "Error: ", e);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnInsert:
            insert();
            break;
        case R.id.btnDelete:
            delete();
            break;
        default:
            break;
        }
    }

    /**
     * INSERT
     */
    private void insert() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        byte[] data = out.toByteArray();
        final ParseFile file = new ParseFile("hoge.png", data);

        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {

                if (e == null) {
                    // Handle success or failure here ...
                    ParseObject testObject = new ParseObject("test");
                    testObject.put("foo", "bar");
                    testObject.put("img", file);
                    testObject.saveInBackground(new SaveCallback() {

                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                init();
                            } else {
                                Log.e(TAG, "Error: ", e);
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "Error: ", e);
                }
            }
        }, new ProgressCallback() {
            public void done(Integer percentDone) {
            }
        });
    }

    private void delete() {

        ParseQuery query = new ParseQuery("test");
        query.whereEqualTo("foo", "bar");
        query.findInBackground(new FindCallback() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    if (list.size() > 0) {
                        for (ParseObject parseObject : list) {
                            parseObject.deleteInBackground();
                        }
                        init();
                    }
                } else {
                    Log.e(TAG, "Error: ", e);
                }
            }
        });
    }
}