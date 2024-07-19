/*
 *
 *  * Created by https://github.com/braver-tool on 12/10/21, 08:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 23/03/22, 09:45 AM
 *
 */

package com.braver.tool.picker.view;

import static com.braver.tool.picker.view.AppUtils.START_POINT;
import static com.braver.tool.picker.view.PermissionUtils.PERMISSION_CAMERA;
import static com.braver.tool.picker.view.PermissionUtils.PERMISSION_GALLERY;
import static com.braver.tool.picker.view.PermissionUtils.PERMISSION_MIC;
import static com.braver.tool.picker.view.PermissionUtils.READ_EXTERNAL_STORAGE_PERMISSION;
import static com.braver.tool.picker.BraveFileUtils.IMAGE_PATH;
import static com.braver.tool.picker.BraveFileUtils.IS_IMAGE_FILE;
import static com.braver.tool.picker.BraveFileUtils.PREVIEW_IMAGE_FILE_PATH;
import static com.braver.tool.picker.BraveFileUtils.PREVIEW_VIDEO_FILE_PATH;
import static com.braver.tool.picker.BraveFileUtils.VIDEO_PATH;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.braver.tool.picker.BraveFilePath;
import com.braver.tool.picker.BraveFilePicker;
import com.braver.tool.picker.BraveFileType;
import com.braver.tool.picker.BraverDocPathUtils;
import com.braver.tool.picker.CameraActivity;
import com.braver.tool.picker.R;

import java.io.File;
import java.util.ArrayList;

public class PickerActivity extends AppCompatActivity implements View.OnClickListener {

    private String tempFileAbsolutePath = "";
    private int selectedOption = 0;
    private TextView sourceFilePathTextView;
    private ActivityResultLauncher<Intent> activityResultLauncherForGallery;
    private ActivityResultLauncher<Intent> activityResultLauncherForCamera;
    private ActivityResultLauncher<Intent> activityResultLauncherForDocs;
    private int resultOption = 0;
    private boolean isPreviewClicks = false;
    private TextView previewTextView;
    private boolean isNavigate = false;

    private boolean isMultiple = false;

    private boolean isVideo = false;

    private boolean isAudio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SplashScreen.installSplashScreen(this);
        new Handler().postDelayed(() -> isNavigate = true, 2000);
        setupOnPreDrawListenerToRootView();
        setContentView(R.layout.activity_picker);
        setTheme();
        initializeViews();

        if (getIntent().getStringExtra("openType").equals("CAMERA")) {
            selectedOption = 1;
            callFileAccessIntent();
        }

        if (getIntent().getStringExtra("openType").equals("CAMERA_VIDEO")) {
            selectedOption = 1;
            callFileAccessIntent();
        }

        if (getIntent().getStringExtra("openType").equals("GALLERY")) {
            if (getIntent().hasExtra("isMultiple"))
                isMultiple = getIntent().getBooleanExtra("isMultiple", false);
            if (getIntent().hasExtra("isVideo"))
                isVideo = getIntent().getBooleanExtra("isVideo", false);
            selectedOption = 2;
            callFileAccessIntent();
        }

        if (getIntent().getStringExtra("openType").equals("DOC")) {
            if (getIntent().hasExtra("isAudio"))
                isAudio = getIntent().getBooleanExtra("isAudio", false);
            selectedOption = 3;
            callFileAccessIntent();
        }

        /*if (getIntent().getStringExtra("openType").equals("GALLERY")) {
            showDialog();
        }*/
    }

    private void showDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Select Option !")
                //.setMessage("Please provide your email")
                .setPositiveButton(R.string.app_title_camera, (dialog, which) -> {
                    selectedOption = 1;
                    callFileAccessIntent();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.app_title_gallery, (dialog, which) -> {
                    selectedOption = 2;
                    callFileAccessIntent();
                    dialog.dismiss();
                })
                .setOnCancelListener(dialog -> {
                    dialog.dismiss();
                    onBackPressed();
                })
                .show();
    }

    @Override
    protected void onResume() {
        isPreviewClicks = false;
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.MANDATORY_GALLERY_PERMISSION_REQ_CODE) {
            boolean cameraAndStoragePermissionGranted = true;
            for (int grantResult : grantResults) {
                cameraAndStoragePermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }
            if (cameraAndStoragePermissionGranted) {
                callFileAccessIntent();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q &&
                    (!PermissionUtils.isAudioAccessed(PickerActivity.this)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                            READ_EXTERNAL_STORAGE_PERMISSION) ||
                            !PermissionUtils.isCameraAccessed(PickerActivity.this))) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE_PERMISSION)) {
                    PermissionUtils.showAlertForAppInfoScreen(this, PERMISSION_GALLERY);
                } else if (!PermissionUtils.isCameraAccessed(this)) {
                    PermissionUtils.showAlertForAppInfoScreen(this, PERMISSION_CAMERA);
                } else if (!PermissionUtils.isAudioAccessed(PickerActivity.this)) {
                    PermissionUtils.showAlertForAppInfoScreen(this, PERMISSION_MIC);
                }
            } else {
                AppUtils.printLogConsole("onRequestPermissionsResult", "-------->Permission Denied!!");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cameraLayout) {
            selectedOption = 1;
            callFileAccessIntent();
        } else if (v.getId() == R.id.galleryLayout) {
            selectedOption = 2;
            callFileAccessIntent();
        } else if (v.getId() == R.id.docLayout) {
            selectedOption = 3;
            callFileAccessIntent();
        } else if (v.getId() == R.id.previewButtonTextView) {
            if (!isPreviewClicks) {
                isPreviewClicks = true;
                if (resultOption == 1 || resultOption == 2) {
                    navigateToPreviewScreen();
                } else {
                    AppUtils.openDocument(PickerActivity.this, tempFileAbsolutePath);
                }
            }

        }
    }

    /**
     * Declaration setupOnPreDrawListenerToRootView()
     * Set to navigate
     */
    private void setupOnPreDrawListenerToRootView() {
        View mViewContent = findViewById(android.R.id.content);
        mViewContent.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (isNavigate) {
                            mViewContent.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> );
        }*/
    }

    /**
     * Declaration setSourcePathView()
     * Set file path to the view and show Preview Button
     */
    private void setSourcePathView(String path) {
        tempFileAbsolutePath = path;
        String filePath = AppUtils.getFileNameFromPath(path);
        sourceFilePathTextView.setText(START_POINT.concat(filePath));
        previewTextView.setVisibility(View.VISIBLE);

        Intent intent = new Intent();
        intent.putExtra("PATH", path);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setSourcePathViewMulti (ArrayList<String> fileList) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("PATH_MULTI", fileList);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Declaration navigateToPreviewScreen()
     * Navigation to Preview Screen
     */
    private void navigateToPreviewScreen() {
        Bundle bundle = new Bundle();
        bundle.putString(resultOption == 1 ? PREVIEW_IMAGE_FILE_PATH : PREVIEW_VIDEO_FILE_PATH, tempFileAbsolutePath);
        /*Intent navigateTo = new Intent(PickerActivity.this, PreviewActivity.class);
        navigateTo.putExtras(bundle);
        startActivity(navigateTo);*/
    }

    private ArrayList<String> fileList = new ArrayList<>();

    /**
     * Declaration initializeViews()
     * Initialize views with the XML
     */
    private void initializeViews() {
        LinearLayout cameraLayout = findViewById(R.id.cameraLayout);
        LinearLayout galleryLayout = findViewById(R.id.galleryLayout);
        LinearLayout docLayout = findViewById(R.id.docLayout);
        sourceFilePathTextView = findViewById(R.id.sourceFilePathTextView);
        previewTextView = findViewById(R.id.previewButtonTextView);
        cameraLayout.setOnClickListener(this);
        galleryLayout.setOnClickListener(this);
        docLayout.setOnClickListener(this);
        previewTextView.setOnClickListener(this);
        previewTextView.setVisibility(View.GONE);
        activityResultLauncherForGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                if (!isMultiple) {
                    if (result.getData() != null) {

                        try {
                            Uri selectedMediaUri = result.getData().getData();
                            if (selectedMediaUri.toString().contains("image") || selectedMediaUri.toString().contains("jpg") || selectedMediaUri.toString().contains("jpeg") || selectedMediaUri.toString().contains("png")) {
                                File imageFile = new BraveFilePicker().setActivity(PickerActivity.this).setIsCompressImage(false).setIsTrimVide(false).setFileType(BraveFileType.IMAGE).setDestinationFilePath(AppUtils.getRandomImageFileName(PickerActivity.this)).setContentUri(selectedMediaUri).getSourceFile();
                                resultOption = 1;
                                if (imageFile != null) {
                                    setSourcePathView(imageFile.getAbsolutePath());
                                }
                            } else {
                                String pth = BraverDocPathUtils.Companion.getSourceDocPath(PickerActivity.this, selectedMediaUri);
                                //For Video
                                resultOption = 2;
                                setSourcePathView(pth);
                            }
                        } catch (Exception e) {
                            AppUtils.printLogConsole("activityResultLauncherForGallery", "Exception-------->" + e.getMessage());
                        }
                    }
                } else {

                    if (result.getData().getClipData() != null) {
                        try {
                            ClipData mClipData = result.getData().getClipData();

                            if (mClipData.getItemCount() == 0) {


                                if (result.getData() != null) {

                                    try {
                                        Uri selectedMediaUri = result.getData().getData();
                                        if (selectedMediaUri.toString().contains("image") || selectedMediaUri.toString().contains("jpg") || selectedMediaUri.toString().contains("jpeg") || selectedMediaUri.toString().contains("png")) {
                                            File imageFile = new BraveFilePicker().setActivity(PickerActivity.this).setIsCompressImage(false).setIsTrimVide(false).setFileType(BraveFileType.IMAGE).setDestinationFilePath(AppUtils.getRandomImageFileName(PickerActivity.this)).setContentUri(selectedMediaUri).getSourceFile();
                                            resultOption = 1;
                                            if (imageFile != null) {
                                                setSourcePathView(imageFile.getAbsolutePath());
                                            }
                                        } else {
                                            //For Video
                                            String pth = BraverDocPathUtils.Companion.getSourceDocPath(PickerActivity.this, selectedMediaUri);
                                            //For Video
                                            resultOption = 2;
                                            setSourcePathView(pth);
                                        }
                                    } catch (Exception e) {
                                        AppUtils.printLogConsole("activityResultLauncherForGallery", "Exception-------->" + e.getMessage());
                                    }
                                }

                            } else {

                                fileList = new ArrayList<>();
                                for (int i = 0; i < mClipData.getItemCount(); i++) {

                                    ClipData.Item item = mClipData.getItemAt(i);
                                    Uri uri = item.getUri();

                                    if (uri.toString().contains("image") || uri.toString().contains("jpg") ||
                                            uri.toString().contains("jpeg") || uri.toString().contains("png")) {
                                        File imageFile = new BraveFilePicker()
                                                .setActivity(PickerActivity.this)
                                                .setIsCompressImage(false).setIsTrimVide(false)
                                                .setFileType(BraveFileType.IMAGE)
                                                .setDestinationFilePath(AppUtils.getRandomImageFileName(
                                                        PickerActivity.this)).setContentUri(uri).getSourceFile();
                                        resultOption = 1;
                                        if (imageFile != null) {
                                            fileList.add(imageFile.getAbsolutePath());
                                        }
                                    }
                                }
                                setSourcePathViewMulti(fileList);
                            }
                        } catch (Exception e) {
                            AppUtils.printLogConsole("activityResultLauncherForGallery", "Exception-------->" + e.getMessage());
                        }
                    } else if (result.getData() != null) {

                        try {
                            Uri selectedMediaUri = result.getData().getData();
                            if (selectedMediaUri.toString().contains("image") || selectedMediaUri.toString().contains("jpg") || selectedMediaUri.toString().contains("jpeg") || selectedMediaUri.toString().contains("png")) {
                                File imageFile = new BraveFilePicker().setActivity(PickerActivity.this).setIsCompressImage(false).setIsTrimVide(false).setFileType(BraveFileType.IMAGE).setDestinationFilePath(AppUtils.getRandomImageFileName(PickerActivity.this)).setContentUri(selectedMediaUri).getSourceFile();
                                resultOption = 1;
                                if (imageFile != null) {
                                    setSourcePathView(imageFile.getAbsolutePath());
                                }
                            } else {
                                //For Video
                                String pth = BraverDocPathUtils.Companion.getSourceDocPath(PickerActivity.this, selectedMediaUri);

                                //For Video
                                resultOption = 2;
                                setSourcePathView(pth);
                            }
                        } catch (Exception e) {
                            AppUtils.printLogConsole("activityResultLauncherForGallery", "Exception-------->" + e.getMessage());
                        }
                    } else
                        onBackPressed();
                }
            } else
                onBackPressed();
        });
        activityResultLauncherForCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                try {
                    boolean isImageFile = result.getData().getBooleanExtra(IS_IMAGE_FILE, false);
                    String filePath;
                    if (isImageFile) {
                        filePath = result.getData().getStringExtra(IMAGE_PATH);
                        resultOption = 1;
                    } else {
                        //For Video
                        filePath = result.getData().getStringExtra(VIDEO_PATH);
                        resultOption = 2;
                    }
                    setSourcePathView(filePath);
                } catch (Exception e) {
                    AppUtils.printLogConsole("activityResultLauncherForCamera", "Exception-------->" + e.getMessage());
                }
            } else
                onBackPressed();
        });
        activityResultLauncherForDocs = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                try {
                    //String tempFileAbsolutePath = "";
                    Uri selectedDocUri = result.getData().getData();
                    if (selectedDocUri.toString().contains("video") || selectedDocUri.toString().contains("mp4") || selectedDocUri.toString().contains("mkv") || selectedDocUri.toString().contains("mov")) {
                        //For Video

                        String pth = BraverDocPathUtils.Companion.getSourceDocPath(PickerActivity.this, selectedDocUri);

                        //For Video
                        resultOption = 2;
                        setSourcePathView(pth);
                    } else {
                        tempFileAbsolutePath = BraverDocPathUtils.Companion.getSourceDocPath(PickerActivity.this, selectedDocUri);
                        resultOption = 3;
                        setSourcePathView(tempFileAbsolutePath);
                    }
                } catch (Exception e) {
                    AppUtils.printLogConsole("activityResultLauncherForDocs", "Exception-------->" + e.getMessage());
                }
            } else
                onBackPressed();
        });
    }

    /**
     * Declaration callFileAccessIntent()
     * Handling click event of the picker icons
     */
    private void callFileAccessIntent() {
        if (PermissionUtils.isFileAccessPermissionGranted(this)) {
            if (selectedOption == 1) {
                openCamera();
            } else if (selectedOption == 2) {
                openGalleryForImageAndVideo();
            } else if (selectedOption == 3) {
                openAttachments();
            }
        } else {
            PermissionUtils.showFileAccessPermissionRequestDialog(this);
        }
    }

    /**
     * Declaration openGalleryForImageAndVideo()
     * Method is used to open the Gallery
     */
    private void openGalleryForImageAndVideo() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            if (isMultiple)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            if (isVideo)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
            else
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            activityResultLauncherForGallery.launch(intent);
        } catch (Exception exception) {
            AppUtils.printLogConsole("openGalleryForImageAndVideo", "Exception-------->" +
                    exception.getMessage());
        }
    }

    /**
     * Declaration openCamera()
     * Method is used to open the Camera
     */
    private void openCamera() {
        try {
            Intent intent = new Intent(PickerActivity.this, CameraActivity.class);
            intent.putExtra("openType", getIntent().getStringExtra("openType"));
            activityResultLauncherForCamera.launch(intent);
        } catch (Exception exception) {
            AppUtils.printLogConsole("openCamera", "Exception-------->" + exception.getMessage());
        }
    }

    /**
     * Handling Get doc,pdf,xlx,apk,et., from user to send throw Attachments
     */
    private void openAttachments() {
        try {
            Intent addAttachment = new Intent(Intent.ACTION_GET_CONTENT);
            /*if (isAudio)
                addAttachment.setType("audio/*");
            else
                addAttachment.setType("file/*");*/
            addAttachment.setType("*/*");
            addAttachment.setAction(Intent.ACTION_GET_CONTENT);
            addAttachment.setAction(Intent.ACTION_OPEN_DOCUMENT);
            activityResultLauncherForDocs.launch(addAttachment);
        } catch (Exception exception) {
            AppUtils.printLogConsole("openAttachments", "Exception-------->" + exception.getMessage());
        }
    }

    /**
     * Declaration setTheme()
     * Method used to set transparent theme for the current window
     */
    private void setTheme() {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.getWindow().setDecorFitsSystemWindows(false);
        } else {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}