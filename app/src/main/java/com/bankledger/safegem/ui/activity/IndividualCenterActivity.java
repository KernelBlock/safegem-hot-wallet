package com.bankledger.safegem.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bankledger.safegem.R;
import com.bankledger.safegem.greendaodb.UserUtil;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.ModifyPhotoRequest;
import com.bankledger.safegem.net.model.request.UpdateNickNameRequest;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.ModifyPhotoResponse;
import com.bankledger.safegem.ui.view.CommonPopupWindow;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.BitmapUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.PhotoUtils;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.io.File;
import butterknife.BindView;
import butterknife.OnClick;

public class IndividualCenterActivity extends BaseActivity {

    @BindView(R.id.header_img)
    ImageView headerImg;
    @BindView(R.id.nick_tv)
    TextView nickTv;
    @BindView(R.id.id_tv)
    TextView idTv;
    @BindView(R.id.phone)
    TextView phone;
    private CommonPopupWindow popupWindow;

    /**
     * 相册
     * @param savedInstanceState
     */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private final String provider=".provider";
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_center);
        setTitle(getString(R.string.center_user_str));
    }

    @Override
    public void initView() {
        super.initView();
        if (safeGemApplication.getUserInfo().getNickName() != null) {
            nickTv.setText(safeGemApplication.getUserInfo().getNickName());
        }
        if (safeGemApplication.getUserInfo().getIncrementId() != null) {
            idTv.setText(safeGemApplication.getUserInfo().getIncrementId() + "");
        }
        if (safeGemApplication.getUserInfo().getPhone() != null) {
            if (safeGemApplication.getUserInfo().getPhone().indexOf(Constants.RIGHT_BRACKET) != -1) {
                phone.setText(safeGemApplication.getUserInfo().getPhone().substring(safeGemApplication.getUserInfo().getPhone().indexOf(Constants.RIGHT_BRACKET) + 1));
            }
        }
        if (safeGemApplication.getUserInfo().getPicUrl() != null) {
            BitmapUtil.loadImage(safeGemApplication.getUserInfo().getPicUrl(),headerImg, R.drawable.head_default);
        }
    }

    @OnClick({R.id.user_head_img, R.id.nick_view, R.id.exit_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_head_img:
                showPopWindow();
                break;
            case R.id.nick_view:
                DialogUtil.showEditDialog(this, nickTv.getText().toString(), getString(R.string.update_nick), getString(R.string.input_nick_name), new DialogUtil.OnClickConfirmListener() {
                    @Override
                    public void onClickConfirm(String content) {
                        updateNickName(content);
                    }
                });
                break;
            case R.id.exit_btn:
                DialogUtil.showTextDialog(this, R.string.confirm_exit_login, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            DialogUtil.showProgressDialog(IndividualCenterActivity.this);
                            Utils.clearTableData();
                            DialogUtil.dismissProgressDialog();
                            ActivitySkipUtil.skipAnotherActivityFinish(IndividualCenterActivity.this, LoginRegGuideActivity.class);
                    }
                });
                break;
        }
    }

    private void showPopWindow() {
        popupWindow = new CommonPopupWindow.Builder(this)
                //设置PopupWindow布局
                .setView(R.layout.img_select_layout)
                //设置宽高
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                //设置背景颜色，取值范围0.0f-1.0f 值越小越暗 1.0f为透明
                .setBackGroundLevel(0.5f)
                //设置外部是否可点击 默认是true
                .setOutsideTouchable(true)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        view.findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                takePhoto();
                            }
                        });
                        view.findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getPhoto();
                            }
                        });
                        view.findViewById(R.id.tv3).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupWindow.dismiss();
                            }
                        });
                    }
                })
                //开始构建
                .create();
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
    }

    private void takePhoto() {
        getActivity().verifyPermissions(Manifest.permission.CAMERA, new PermissionCallBack() {
            @Override
            public void onGranted() {
                if (Utils.hasSdcard()) {
                    imageUri = Uri.fromFile(fileUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        //通过FileProvider创建一个content类型的Uri
                        imageUri = FileProvider.getUriForFile(IndividualCenterActivity.this, getPackageName()+provider, fileUri);
                    PhotoUtils.takePicture(IndividualCenterActivity.this, imageUri, CODE_CAMERA_REQUEST);
                } else {
                    ToastUtil.showShort(getActivity(), getString(R.string.no_sdcard_tip));
                }
            }

            @Override
            public void onDenied() {
            }
        });

    }

    /**
     * 调用系统相册
     */
    public void getPhoto() {
        PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
    }


    private void updateNickName(String nickName) {
        if (nickName.trim().length() == 0) {
            ToastUtil.showShort(this, R.string.input_nick_name);
            return;
        }
        DialogUtil.showProgressDialog(this);
        RetrofitManager.getInstance().mNetService.modifyNickName(new UpdateNickNameRequest(nickName))
                .compose(RxSchedulers.<BaseResponse<String>>compose())
                .compose(bindLifecycle())
                .subscribe(new ResponseObserver<String>() {
                    @Override
                    protected void onHandleSuccess(String datas) {
                        ToastUtil.showShort(getActivity(), getString(R.string.nick_name_success));
                        nickTv.setText(nickName);
                        safeGemApplication.getUserInfo().setNickName(nickName);
                        new UserUtil(IndividualCenterActivity.this).updateUserTb(safeGemApplication.getUserInfo());
                    }
                });
    }

    public LifecycleTransformer bindLifecycle() {
        LifecycleTransformer objectLifecycleTransformer = bindToLifecycle();
        return objectLifecycleTransformer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int output_X = 480, output_Y = 480;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                    cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    break;
                case CODE_GALLERY_REQUEST://访问相册完成回调
                    if (Utils.hasSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            newUri = FileProvider.getUriForFile(this, getPackageName()+provider, new File(newUri.getPath()));
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    } else {
                        ToastUtil.showShort(getActivity(), getString(R.string.no_sdcard_tip));
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    Bitmap bitmap = BitmapUtil.comp(PhotoUtils.getBitmapFromUri(cropImageUri, this));
                    if (bitmap != null) {
                        showImages(bitmap);
                    }
                    break;
            }
        }
    }

    /**
     * 展示图片
     * @param bitmap
     */
    private void showImages(Bitmap bitmap) {
        DialogUtil.showProgressDialog(this);
        RetrofitManager.getInstance().mNetService.modifyPhoto(new ModifyPhotoRequest(BitmapUtil.bitmapToBase64(bitmap)))
                .compose(RxSchedulers.<BaseResponse<ModifyPhotoResponse>>compose())
                .compose(bindLifecycle())
                .subscribe(new ResponseObserver<ModifyPhotoResponse>() {
                    @Override
                    protected void onHandleSuccess(ModifyPhotoResponse datas) {
                        popupWindow.dismiss();
                        headerImg.setImageBitmap(bitmap);
                        ToastUtil.showShort(getActivity(), getString(R.string.header_success));
                        safeGemApplication.getUserInfo().setPicUrl(datas.picUrl);
                        new UserUtil(IndividualCenterActivity.this).updateUserTb(safeGemApplication.getUserInfo());
                    }
                });
    }


}

