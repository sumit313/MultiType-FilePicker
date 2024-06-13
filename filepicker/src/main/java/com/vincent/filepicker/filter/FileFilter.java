package com.vincent.filepicker.filter;

import android.os.Environment;
import android.webkit.MimeTypeMap;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;

import com.vincent.filepicker.Util;
import com.vincent.filepicker.filter.callback.FileLoaderCallbacks;
import com.vincent.filepicker.filter.callback.FilterResultCallback;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.Directory;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.vincent.filepicker.filter.callback.FileLoaderCallbacks.TYPE_AUDIO;
import static com.vincent.filepicker.filter.callback.FileLoaderCallbacks.TYPE_FILE;
import static com.vincent.filepicker.filter.callback.FileLoaderCallbacks.TYPE_IMAGE;
import static com.vincent.filepicker.filter.callback.FileLoaderCallbacks.TYPE_VIDEO;

/**
 * Created by Vincent Woo
 * Date: 2016/10/11
 * Time: 10:19
 */

public class FileFilter {
    public static void getImages(FragmentActivity activity, FilterResultCallback<ImageFile> callback) {
        try{
            LoaderManager.getInstance(activity).destroyLoader(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        LoaderManager.getInstance(activity).initLoader(0, null,
                new FileLoaderCallbacks(activity, callback, TYPE_IMAGE));
    }

    public static void getVideos(FragmentActivity activity, FilterResultCallback<VideoFile> callback) {
        try{
            LoaderManager.getInstance(activity).destroyLoader(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        LoaderManager.getInstance(activity).restartLoader(1, null,
                new FileLoaderCallbacks(activity, callback, TYPE_VIDEO));
    }

    public static void getAudios(FragmentActivity activity, FilterResultCallback<AudioFile> callback) {
        try{
            LoaderManager.getInstance(activity).destroyLoader(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        LoaderManager.getInstance(activity).initLoader(2, null,
                new FileLoaderCallbacks(activity, callback, TYPE_AUDIO));
    }

    public static void getFiles(FragmentActivity activity,
                                FilterResultCallback<NormalFile> callback, String[] suffix) {
        try{
            LoaderManager.getInstance(activity).destroyLoader(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        LoaderManager.getInstance(activity).initLoader(3, null,
                new FileLoaderCallbacks(activity, callback, TYPE_FILE, suffix));
    }

    public static void getNonMediaFiles(FragmentActivity activity,
                                        FilterResultCallback<NormalFile> callback, String[] suffixArgs) {
        String mSuffixRegex = "";
        if (suffixArgs != null && suffixArgs.length > 0) {
            mSuffixRegex = Util.obtainSuffixRegex(suffixArgs);
        }

        Search_Dir(Environment.getExternalStorageDirectory(), mSuffixRegex, callback);
    }

    public static void Search_Dir(File dir, String mSuffixRegex, FilterResultCallback<NormalFile> callback) {
        File[] FileList = dir.listFiles();
        List<Directory<NormalFile>> directories = new ArrayList<>();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory()) {
                    Search_Sub_Dir(FileList[i], mSuffixRegex, directories);
                } else {
                    if (FileList[i].getPath() != null && Util.contains(FileList[i].getPath(), mSuffixRegex)) {
                        attachFileItem(FileList[i], directories);
                    }
                }
            }
            if (callback != null) {
                callback.onResult(directories);
            }
        }
    }

    public static void Search_Sub_Dir(File dir, String mSuffixRegex, List<Directory<NormalFile>> directories) {
        File[] FileList = dir.listFiles();
        for (int i = 0; i < (FileList != null ? FileList.length : 0); i++) {
            if (FileList[i].getPath() != null && Util.contains(FileList[i].getPath(), mSuffixRegex)) {
                attachFileItem(FileList[i], directories);
            }
        }
    }

    public static void attachFileItem(File rootFile, List<Directory<NormalFile>> directories) {
        NormalFile file = new NormalFile();
        file.setPath(rootFile.getPath());
        file.setSize(getFileSizeInMB(rootFile));
        file.setMimeType(getMimeType(rootFile.getAbsolutePath()));
        file.setName(rootFile.getName());
        file.setDate(rootFile.lastModified());

        //Create a Directory
        Directory<NormalFile> directory = new Directory<>();
        directory.setName(Util.extractFileNameWithSuffix(Util.extractPathWithoutSeparator(file.getPath())));
        directory.setPath(Util.extractPathWithoutSeparator(file.getPath()));

        if (!directories.contains(directory)) {
            directory.addFile(file);
            directories.add(directory);
        } else {
            directories.get(directories.indexOf(directory)).addFile(file);
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static long getFileSizeInMB(File file) {
        return file.length() / (1024 * 1024);
    }
}
