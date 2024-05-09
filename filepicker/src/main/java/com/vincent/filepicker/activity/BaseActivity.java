package com.vincent.filepicker.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.vincent.filepicker.FolderListHelper;
import com.vincent.filepicker.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Vincent Woo
 * Date: 2016/10/12
 * Time: 16:21
 */

public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int RC_READ_EXTERNAL_STORAGE = 123;
    private static final String TAG = BaseActivity.class.getName();

    protected FolderListHelper mFolderHelper;
    protected boolean isNeedFolderList;
    public static final String IS_NEED_FOLDER_LIST = "isNeedFolderList";

    abstract void permissionGranted();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isNeedFolderList = getIntent().getBooleanExtra(IS_NEED_FOLDER_LIST, false);
        if (isNeedFolderList) {
            mFolderHelper = new FolderListHelper();
            mFolderHelper.initFolderListView(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * Read external storage files
     *
     * Added the Manage External Storage Permission for Android 11 as Android 11 don't permit you to access
     * the non media files and downloads directly through MediaStore API. You can only access the media files
     * through MediaStore API.
     * Google suggest that to access non media file you can use system picker, but you can't get the whole list of non media file
     * to get the whole list of media files to create this multi picker we have added the all files permission.
     */
    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    void readExternalStorageForFiles() {
        //boolean isReadPermissionGranted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R || EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean isReadPermissionGranted = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            isReadPermissionGranted = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            isReadPermissionGranted = (EasyPermissions.hasPermissions(this, Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO) || EasyPermissions.hasPermissions(this,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED));
        }else{
            isReadPermissionGranted = EasyPermissions.hasPermissions(this, Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO);
        }

        /*boolean isAllFileAccessGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isAllFileAccessGranted = Environment.isExternalStorageManager();
        }
        if (isReadPermissionGranted && isAllFileAccessGranted) {
            permissionGranted();
        } else {
            String[] PERMISSIONS;

            if (isAllFileAccessGranted){
                PERMISSIONS = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };
            }else if (!isReadPermissionGranted){
                PERMISSIONS = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                };
            }else {
                PERMISSIONS = new String[]{
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                };
            }
            EasyPermissions.requestPermissions(this, getString(R.string.vw_rationale_storage),
                    RC_READ_EXTERNAL_STORAGE, PERMISSIONS);
        }*/

        //boolean isAllFileAccessGranted = true;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isAllFileAccessGranted = Environment.isExternalStorageManager();
        }*/
        if (isReadPermissionGranted) {
            permissionGranted();
        } else {
            String[] PERMISSIONS;

           /* if (isAllFileAccessGranted){
                PERMISSIONS = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };
            }else if (!isReadPermissionGranted){*/
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                PERMISSIONS = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };
            }else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                PERMISSIONS = new String[]{Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED};
            }else{
                PERMISSIONS = new String[]{
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                };
            }
            /*}else {
                PERMISSIONS = new String[]{
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                };
            }*/
            EasyPermissions.requestPermissions(this, getString(R.string.vw_rationale_storage),
                    RC_READ_EXTERNAL_STORAGE, PERMISSIONS);
        }
    }

    /**
     * Read external storage file
     */
    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    void readExternalStorage() {
        String[] PERMISSIONS;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            };
        }else{
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
        }
            boolean isGranted = false;
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    isGranted=/*EasyPermissions.hasPermissions(this, PERMISSIONS) ||*/ EasyPermissions.hasPermissions(this,Manifest.permission.READ_MEDIA_AUDIO , Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
                else
                    isGranted=EasyPermissions.hasPermissions(this, PERMISSIONS);
        if (isGranted) {
            permissionGranted();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.vw_rationale_storage),
                    RC_READ_EXTERNAL_STORAGE, PERMISSIONS);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        permissionGranted();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE && EasyPermissions.hasPermissions(this,Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)){
            //isNeedFolderList=true;
            permissionGranted();
        }else{
            // If Permission permanently denied, ask user again
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            if (EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE")) {
                permissionGranted();
            } else {
                finish();
            }
        }
    }

    public void onBackClick(View view) {
        finish();
    }
}
