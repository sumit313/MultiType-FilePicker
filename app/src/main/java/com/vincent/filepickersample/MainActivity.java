package com.vincent.filepickersample;

import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.VideoPickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.TITLE;

//import static com.vincent.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;
//import static com.vincent.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST;
//import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvResult = (TextView) findViewById(R.id.tv_result);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_pick_image:
                Intent intent1 = new Intent(this, ImagePickActivity.class);
                intent1.putExtra("IsNeedCamera", false);
                intent1.putExtra(Constant.MAX_NUMBER, 10);
                intent1.putExtra("isNeedFolderList", false);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.btn_pick_video:
                Intent intent2 = new Intent(this, VideoPickActivity.class);
                intent2.putExtra("IsNeedCamera", false);
                intent2.putExtra(Constant.MAX_NUMBER, 10);
                intent2.putExtra("isNeedFolderList", false);
                startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
                break;
            case R.id.btn_pick_audio:
                Intent intent3 = new Intent(this, AudioPickActivity.class);
                intent3.putExtra("IsNeedRecorder", false);
                intent3.putExtra(Constant.MAX_NUMBER, 10);
                intent3.putExtra("isNeedFolderList", false);
                startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
                break;
            case R.id.btn_pick_file:
                /*Intent intent4 = new Intent(this, NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 10);
                intent4.putExtra("isNeedFolderList", false);
                intent4.putExtra(NormalFilePickActivity.SUFFIX,
                        new String[] {"xlsx", "xls", "doc", "dOcX", "ppt", "pptx", "pdf"});*/

                Intent intent4 = new Intent(Intent.ACTION_GET_CONTENT);
                intent4.addCategory(Intent.CATEGORY_OPENABLE);
                intent4.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent4.setType("*/*");
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);

                /*NewNormalFilePickerUtil filePickerUtil = new NewNormalFilePickerUtil(MainActivity.this);
                filePickerUtil.loadFilePicker();*/
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    StringBuilder builder = new StringBuilder();
                    for (ImageFile file : list) {
                        String path = file.getPath();
                        builder.append(path + "\n");
                    }
                    mTvResult.setText(builder.toString());
                }
                break;
            case Constant.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                    StringBuilder builder = new StringBuilder();
                    for (VideoFile file : list) {
                        String path = file.getPath();
                        builder.append(path + "\n");
                    }
                    mTvResult.setText(builder.toString());
                }
                break;
            case Constant.REQUEST_CODE_PICK_AUDIO:
                if (resultCode == RESULT_OK) {
                    ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                    StringBuilder builder = new StringBuilder();
                    for (AudioFile file : list) {
                        String path = file.getPath();
                        builder.append(path + "\n");
                    }
                    mTvResult.setText(builder.toString());
                }
                break;
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    /*ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    StringBuilder builder = new StringBuilder();
                    for (NormalFile file : list) {
                        String path = file.getPath();
                        builder.append(path + "\n");
                    }*/
                    StringBuilder builder = new StringBuilder();
                    if(data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        int currentItem = 0;
                        while(currentItem < count) {
                            Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                            Cursor cursor = getContentResolver().query(imageUri,null,null,null);
                            try {
                                if (cursor != null && cursor.moveToFirst()) {
                                    String id = cursor.getString(cursor.getColumnIndex(_ID));
                                    String name = cursor.getString(cursor.getColumnIndex(TITLE));
                                }
                            } finally {
                                 cursor.close();
                            }
                            //do something with the image (save it to some directory or whatever you need to do with it here)
                            currentItem = currentItem + 1;
                            builder.append(imageUri.toString());
                        }
                    } else if(data.getData() != null) {
                        //String imagePath = data.getData().getPath();
                        Uri imageUri = data.getData();

                        builder.append(imageUri.toString());
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    }
                    mTvResult.setText(builder.toString());
                }
                break;
        }
    }
}
