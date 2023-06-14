package com.xiaoxun.xun.camerax

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Camera
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.camera.core.*
import androidx.camera.core.VideoCapture.OnVideoSavedCallback
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.ListenableFuture
import com.xiaoxun.xun.ImibabyApp
import com.xiaoxun.xun.R
import com.xiaoxun.xun.base.BaseLifeCycleActivity
import com.xiaoxun.xun.utils.CloudBridgeUtil
import com.xiaoxun.xun.utils.VideoUitls
import com.xiaoxun.xun.views.RecordView
import com.xiaoxun.xun.views.RecordView.OnRecordListener
import java.io.File
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class VideoRecordNewActivity : BaseLifeCycleActivity(), View.OnClickListener {
    private val captureSuccess = false
    private var myApp: ImibabyApp? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    var viewFinder: PreviewView? = null
    var recordBack: ImageView? = null
    var recordCancel: ImageView? = null
    var recordConfirm: ImageView? = null
    var imagePhoto: ImageView? = null
    var imageFlash: ImageView? = null
    var imageSwitch: ImageView? = null
    var tvCameraPrompt: TextView? = null
    var mRecordView: RecordView? = null
    var mVideoView: VideoView? = null
    var mFocusView: FocusImageView? = null

    /**
     * 摄像头朝向 默认向后
     */
    private var mLensFacing = CameraSelector.LENS_FACING_BACK
    private var iPhotoPath // 图片路径
            : String? = null
    private var outputFilePath //录像路径
            : String? = null

    /**
     * 可以将一个camera跟任意的LifecycleOwner绑定的一个单例类
     */
    private var mCameraProvider: ProcessCameraProvider? = null

    //拍照的实体
    var mImageCapture: ImageCapture? = null

    //视频的实体
    var mVideoCapture: VideoCapture? = null
    private var mCameraControl: CameraControl? = null
    private var mCameraInfo: CameraInfo? = null

    // 是否正在拍照（处理按钮多次点击）
    private var isTakePhotoing = false
    private var lastTime: Long = 0

    // 屏幕比例
    private var mRational: Int = 0

    // 拍照完成状态 0/1拍照完成/2录制完成
    private var takeStatus = 0
    var cameraSelector: CameraSelector? = null
    private var type_flash = TYPE_FLASH_AUTO

    private var mCaptureMode = CaptureMode.IMAGE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_record_new)
        myApp = this.application as ImibabyApp
        initView()
        checkAudioPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_RESULT -> if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ToastUtils.showShort("缺少相机权限，请重试")
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ToastUtils.showShort("缺少录音权限，请重试")
            } else {
                setUpCamera()
            }
            else -> {}
        }
    }


    @SuppressLint("MissingPermission")
    private fun setUpCamera() {
        tvCameraPrompt!!.visibility = View.VISIBLE
        Handler().postDelayed({
            if (tvCameraPrompt != null) tvCameraPrompt!!.visibility = View.GONE
        }, 3000)
        LogUtils.e("----setUpCamera----")
        //Future表示一个异步的任务，ListenableFuture可以监听这个任务，当任务完成的时候执行回调
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            try {
                mCameraProvider = cameraProviderFuture.get()
                //选择摄像头的朝向
                mLensFacing = lensFacing
                if (mLensFacing == -1) {
                    Toast.makeText(
                        applicationContext,
                        "无可用的设备cameraId!,请检查设备的相机是否被占用",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Runnable
                }
                startCamera()
                setRecordListener()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private val lensFacing: Int
        get() {
            if (hasBackCamera()) {
                return CameraSelector.LENS_FACING_BACK
            }
            return if (hasFrontCamera()) {
                CameraSelector.LENS_FACING_FRONT
            } else -1
        }

    private fun getRatio(): Int {
        val screenRatio: Float = viewFinder?.height!! / (viewFinder?.width!! * 1.0f)
        return if (Math.abs(screenRatio - 4.0 / 3.0) <= Math.abs(screenRatio - 16.0 / 9.0)) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }

    private fun initStartRecordingPath(context: Context): File {
        return File(context.externalMediaDirs[0], System.currentTimeMillis().toString() + ".mp4")
    }

    /**
     * 当确认保存此文件时才去扫描相册更新并显示视频和图片
     *
     * @param dataFile
     */
    private fun scanPhotoAlbum(dataFile: File?) {
        if (dataFile == null) {
            return
        }
        Utils.getApp()
            .sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dataFile)))
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            dataFile.absolutePath.substring(
                dataFile.absolutePath.lastIndexOf(".") + 1
            )
        )
        MediaScannerConnection.scanFile(
            this, arrayOf(dataFile.absolutePath), arrayOf(mimeType)
        ) { path, uri -> ToastUtils.showLong("视频录制完成，请在本地相册查看") }
    }

    /**
     * 启动相机
     *
     * @param
     */
    @SuppressLint("RestrictedApi", "MissingPermission")
    @RequiresPermission(Manifest.permission.CAMERA)
    private fun startCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LogUtils.e("----startCamera----")
        cameraSelector = CameraSelector.Builder().requireLensFacing(mLensFacing).build()
        mRational = getRatio()
        // Preview
        val preview = Preview.Builder()
            .setTargetAspectRatio(mRational)
            .build()
        val builder = ImageCapture.Builder()
        mImageCapture = builder.setFlashMode(ImageCapture.FLASH_MODE_AUTO) //设置闪光灯
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) // 设置照片质量
            .setTargetAspectRatio(mRational)
            .build()
        mCameraProvider!!.unbindAll()
        val camera = mCameraProvider!!.bindToLifecycle(
            (context as LifecycleOwner),
            cameraSelector!!, preview, mImageCapture
        )
        // 相机控制，如点击
        mCameraControl = camera.cameraControl
        mCameraInfo = camera.cameraInfo
        initCameraListener()
        preview.setSurfaceProvider(viewFinder!!.surfaceProvider)
        setFlashRes()// 设置闪光灯
        imageFlash!!.setOnClickListener {
            type_flash++
            if (type_flash > 0x023) type_flash =
                TYPE_FLASH_AUTO
            setFlashRes()
        }
    }

    /**
     * 启动视频
     *
     * @param
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @SuppressLint("RestrictedApi")
    private fun startVideo() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(mLensFacing).build()
        val preview = Preview.Builder()
            .build()
        val builder = VideoCapture.Builder()
//            .setAudioBitRate(1024)//设置音频的码率
            .setVideoFrameRate(30)//视频帧率  越高视频体积越大
            .setBitRate(1024 * 1024)//设置视频的比特率  越大视频体积越大
            .setTargetResolution(Size(viewFinder!!.width, viewFinder!!.height))
        mVideoCapture = builder.build()
        mCameraProvider!!.unbindAll()
        mCameraProvider!!.bindToLifecycle(
            (context as LifecycleOwner),
            cameraSelector!!,
            preview,
            mVideoCapture
        )

        preview.setSurfaceProvider(viewFinder!!.surfaceProvider)
    }

    // 相机点击等相关操作监听
    private fun initCameraListener() {
        val zoomState: LiveData<ZoomState> = mCameraInfo!!.zoomState
        val cameraXPreviewViewTouchListener = CameraXPreviewViewTouchListener(this)

        cameraXPreviewViewTouchListener.setCustomTouchListener(object :
            CameraXPreviewViewTouchListener.CustomTouchListener {
            // 放大缩小操作
            override fun zoom(delta: Float) {
                Log.d(TAG, "缩放")
                zoomState.value?.let {
                    val currentZoomRatio = it.zoomRatio
                    mCameraControl!!.setZoomRatio(currentZoomRatio * delta)
                }
            }

            // 点击操作
            override fun click(x: Float, y: Float) {
                Log.d(TAG, "单击")
                val factory = viewFinder?.meteringPointFactory
                // 设置对焦位置
                val point = factory?.createPoint(x, y)
                val action = FocusMeteringAction.Builder(point!!, FocusMeteringAction.FLAG_AF)
                    // 3秒内自动调用取消对焦
                    .setAutoCancelDuration(3, TimeUnit.SECONDS)
                    .build()
                // 执行对焦
                mFocusView!!.startFocus(Point(x.toInt(), y.toInt()))
                val future: ListenableFuture<*> = mCameraControl!!.startFocusAndMetering(action)
                future.addListener({
                    try {
                        // 获取对焦结果
                        val result = future.get() as FocusMeteringResult
                        if (result.isFocusSuccessful) {
                            mFocusView!!.onFocusSuccess()
                        } else {
                            mFocusView!!.onFocusFailed()
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e(TAG, e.toString())
                    }
                }, ContextCompat.getMainExecutor(this@VideoRecordNewActivity))
            }

            // 双击操作
            override fun doubleClick(x: Float, y: Float) {
                Log.d(TAG, "双击")
                // 双击放大缩小
                val currentZoomRatio = zoomState.value!!.zoomRatio
                if (currentZoomRatio > zoomState.value!!.minZoomRatio) {
                    mCameraControl!!.setLinearZoom(0f)
                } else {
                    mCameraControl!!.setLinearZoom(0.5f)
                }
            }

            override fun longPress(x: Float, y: Float) {
                Log.d(TAG, "长按")
            }
        })
        // 添加监听事件
        viewFinder?.setOnTouchListener(cameraXPreviewViewTouchListener)
    }

    private fun setFlashRes() {
        when (type_flash) {
            TYPE_FLASH_AUTO -> {
                imageFlash!!.setImageResource(R.drawable.ic_flash_auto)
                mImageCapture!!.flashMode = ImageCapture.FLASH_MODE_AUTO
            }
            TYPE_FLASH_ON -> {
                imageFlash!!.setImageResource(R.drawable.ic_flash_on)
                mImageCapture!!.flashMode = ImageCapture.FLASH_MODE_ON
            }
            TYPE_FLASH_OFF -> {
                imageFlash!!.setImageResource(R.drawable.ic_flash_off)
                mImageCapture!!.flashMode = ImageCapture.FLASH_MODE_OFF
            }
        }
    }

    /**
     * 是否有后摄像头
     */
    private fun hasBackCamera(): Boolean {
        if (mCameraProvider == null) {
            return false
        }
        try {
            return mCameraProvider!!.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        } catch (e: CameraInfoUnavailableException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 是否有前摄像头
     */
    private fun hasFrontCamera(): Boolean {
        if (mCameraProvider == null) {
            return false
        }
        try {
            return mCameraProvider!!.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        } catch (e: CameraInfoUnavailableException) {
            e.printStackTrace()
        }
        return false
    }

    private fun setRecordListener() {
        mRecordView!!.setOnRecordListener(object : OnRecordListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTackPicture() {
                if (imagePhoto!!.isShown) {
                    mRecordView!!.resetRecord()
                    return
                }
                if (isTakePhotoing) {
                    return
                }
                //拍照
                isTakePhotoing = true
                if (captureMode != CaptureMode.IMAGE) captureMode =
                    CaptureMode.IMAGE
                //创建图片保存的文件地址
                val file = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath,
                    System.currentTimeMillis().toString() + ".jpeg"
                )
                val metadata = ImageCapture.Metadata()
                metadata.isReversedHorizontal = false
                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file)
                    .setMetadata(metadata)
                    .build()
                mImageCapture!!.takePicture(
                    outputFileOptions,
                    cameraExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            isTakePhotoing = false
                            var savedUri = outputFileResults.savedUri
                            if (savedUri == null) {
                                savedUri = Uri.fromFile(file)
                            }
                            iPhotoPath = file.absolutePath
                            onFileSaved(savedUri)
                            imagePhoto!!.post {
                                Glide.with(imagePhoto!!).load(iPhotoPath).into(imagePhoto!!)
                                showRemakeView()
                            }
                            takeStatus = 1
                            Log.e("TAG", "Photo capture success: ")
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("TAG", "Photo capture failed: " + exception.message, exception)
                            isTakePhotoing = false
                            val intent = Intent()
                            setResult(103, intent)
                            finish()
                        }
                    })
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("RestrictedApi", "MissingPermission")
            override fun onRecordVideo() {
                showVideoMakeView()
                isTakePhotoing = false
                if (captureMode != CaptureMode.VIDEO) {
                    captureMode = CaptureMode.VIDEO
                }
                //创建视频保存的文件地址
                val file = initStartRecordingPath(this@VideoRecordNewActivity)
                val outputFileOptions = VideoCapture.OutputFileOptions.Builder(file).build()
                mVideoCapture!!.startRecording(
                    outputFileOptions,
                    cameraExecutor,
                    object : OnVideoSavedCallback {
                        override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                            outputFilePath = file.absolutePath
                            val duration = VideoUitls.getDuration(outputFilePath)
                            if (duration / 1000 < 500) {
                                ToastUtils.showShort("录制时间过短")
                                return
                            }
                            mVideoView!!.post {
                                if (file.exists()) {
                                    //如果文件存在,则指定要播放的视频
                                    mVideoView!!.setVideoPath(outputFilePath)
                                } else {
                                    ToastUtils.showShort("视频预览失败，请重新拍摄")
                                }
                                playVideo()
                                showRemakeVideoView()
                            }
                            takeStatus = 2
                            Log.e("TAG", "Video capture success: ")
                            onFileSaved(Uri.fromFile(file))
                        }

                        override fun onError(
                            videoCaptureError: Int,
                            message: String,
                            cause: Throwable?
                        ) {
                            ToastUtils.showShort(message)
                            val intent = Intent()
                            setResult(103, intent)
                            finish()
                        }
                    })
            }

            @SuppressLint("RestrictedApi")
            override fun onFinish() {
                //录制完成
                stopRecording()
            }
        })
    }

    private fun playVideo() {
        /**
         * 控制视频的播放 主要通过MediaController控制视频的播放
         */
        //创建MediaController对象
        val mediaController = MediaController(this@VideoRecordNewActivity)
        mediaController.visibility = View.GONE
        mVideoView!!.setMediaController(mediaController) //让videoView 和 MediaController相关联
        //利用   MediaMetadataRetriever 解决缓冲黑屏问题
        val media = MediaMetadataRetriever()
        media.setDataSource(outputFilePath)
        //获取视频中的第一帧照片，设置为封面
        val bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        val drawable: Drawable = BitmapDrawable(
            resources, bitmap
        )
        mVideoView!!.background = drawable
        media.release()
        mVideoView!!.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setOnInfoListener { mediaPlayer, i, i1 ->
                if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    mVideoView!!.setBackgroundColor(Color.TRANSPARENT)
                }
                true
            }
        }
        mVideoView!!.isFocusable = true //让VideoView获得焦点
        mVideoView!!.start() //开始播放视频
        //给videoView添加完成事件监听器，监听视频是否播放完毕
        mVideoView!!.setOnCompletionListener { ToastUtils.showShort("视频预览完成，请确认发送") }
    }

    private fun onFileSaved(savedUri: Uri?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            sendBroadcast(Intent(Camera.ACTION_NEW_PICTURE, savedUri))
        }
        val mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            MimeTypeMap
                .getFileExtensionFromUrl(savedUri!!.path)
        )
        MediaScannerConnection.scanFile(
            applicationContext, arrayOf(
                File(
                    savedUri.path
                ).absolutePath
            ), arrayOf(mimeTypeFromExtension)
        ) { path, _ ->
            LogUtils.d(
                "TAG",
                "Image capture scanned into media store: \$path$path"
            )
        }
    }

    /**
     * 显示拍照视图（重拍）
     */
    private fun showMakeView() {
        if (FileUtils.isFileExists(iPhotoPath)) {
            FileUtils.delete(iPhotoPath)
        }
        tvCameraPrompt!!.visibility = View.VISIBLE
        Handler().postDelayed({
            if (tvCameraPrompt != null) tvCameraPrompt!!.visibility = View.GONE
        }, 3000)
        iPhotoPath = ""
        outputFilePath = ""
        takeStatus = 0
        viewFinder!!.visibility = View.VISIBLE
        mRecordView!!.visibility = View.VISIBLE
        imagePhoto!!.visibility = View.GONE
        imageFlash!!.visibility = View.VISIBLE
        mVideoView!!.visibility = View.GONE
        if (mVideoView!!.isPlaying) {
            mVideoView!!.pause()
        }
        imageSwitch!!.visibility = View.VISIBLE
        recordBack!!.visibility = View.VISIBLE
        recordConfirm!!.visibility = View.GONE
        recordCancel!!.visibility = View.GONE
    }

    /**
     * 显示录像视图
     */
    private fun showVideoMakeView() {
        viewFinder!!.visibility = View.VISIBLE
        mRecordView!!.visibility = View.VISIBLE
        imagePhoto!!.visibility = View.GONE
        mVideoView!!.visibility = View.GONE
        tvCameraPrompt!!.visibility = View.GONE
        if (mVideoView!!.isPlaying) {
            mVideoView!!.pause()
        }
        imageSwitch!!.visibility = View.GONE
        imageFlash!!.visibility = View.GONE
        recordBack!!.visibility = View.VISIBLE
        recordConfirm!!.visibility = View.GONE
        recordCancel!!.visibility = View.GONE
    }

    /**
     * 拍照完成视图
     */
    private fun showRemakeView() {
        viewFinder!!.visibility = View.GONE
        imagePhoto!!.visibility = View.VISIBLE
        mVideoView!!.visibility = View.GONE
        tvCameraPrompt!!.visibility = View.GONE
        mRecordView!!.visibility = View.GONE
        imageFlash!!.visibility = View.GONE
        if (mVideoView!!.isPlaying) {
            mVideoView!!.pause()
        }
        recordBack!!.visibility = View.GONE
        recordConfirm!!.visibility = View.VISIBLE
        recordCancel!!.visibility = View.VISIBLE
    }

    /**
     * 视频完成视图
     */
    private fun showRemakeVideoView() {
        viewFinder!!.visibility = View.GONE
        imagePhoto!!.visibility = View.GONE
        imageFlash!!.visibility = View.GONE
        mRecordView!!.visibility = View.GONE
        tvCameraPrompt!!.visibility = View.GONE
        mVideoView!!.visibility = View.VISIBLE
        recordBack!!.visibility = View.GONE
        imageSwitch!!.visibility = View.GONE
        recordConfirm!!.visibility = View.VISIBLE
        recordCancel!!.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (captureSuccess) {
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭相机
        cameraExecutor.shutdown()
    }

    private fun checkAudioPermission() {
        val audioPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        val videoPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (!audioPermission && !videoPermission) {
            ActivityCompat.requestPermissions(
                this@VideoRecordNewActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA),
                PERMISSION_RESULT
            )
        } else if (!audioPermission) {
            ActivityCompat.requestPermissions(
                this@VideoRecordNewActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_RESULT
            )
        } else if (!videoPermission) {
            ActivityCompat.requestPermissions(
                this@VideoRecordNewActivity,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_RESULT
            )
        } else {
            setUpCamera()
        }
    }

    override fun getContext(): Context {
        return this@VideoRecordNewActivity
    }

    override fun getActivity(): Activity? {
        return this@VideoRecordNewActivity
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}

    @SuppressLint("MissingPermission")
    override fun onClick(v: View) {
        if (System.currentTimeMillis() - lastTime < 500) {
            return
        }
        lastTime = System.currentTimeMillis()
        when (v.id) {
            R.id.record_back -> finish()
            R.id.record_confirm -> if (takeStatus == 2) {
                val intent = Intent()
                intent.putExtra("path", outputFilePath)
                intent.putExtra("type", CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)
                setResult(RESULT_OK, intent)
                finish()
            } else if (takeStatus == 1) {
                val intent = Intent()
                intent.putExtra("path", iPhotoPath)
                intent.putExtra("type", CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)
                setResult(RESULT_OK, intent)
                finish()
            }
            R.id.record_cancel -> showMakeView()
            R.id.image_switch -> if (mLensFacing == 0) {
                if (hasFrontCamera()) {
                    mLensFacing = 1
                    startCamera()
                } else {
                    ToastUtils.showShort("不支持切换后摄像头")
                }
            } else {
                if (hasBackCamera()) {
                    mLensFacing = 0
                    startCamera()
                } else {
                    ToastUtils.showShort("不支持切换前摄像头")
                }
            }
        }
    }

    private fun initView() {
        viewFinder = findViewById(R.id.camera_view)
        mVideoView = findViewById(R.id.video_preview)
        recordBack = findViewById(R.id.record_back)
        recordCancel = findViewById(R.id.record_cancel)
        recordConfirm = findViewById(R.id.record_confirm)
        imagePhoto = findViewById(R.id.image_photo)
        imageFlash = findViewById(R.id.image_flash)
        imageSwitch = findViewById(R.id.image_switch)
        mFocusView = findViewById(R.id.focus_view)
        //        fouceView = findViewById(R.id.fouce_view);
        tvCameraPrompt = findViewById(R.id.tv_camera_prompt)
        mRecordView = findViewById(R.id.capture_layout)
        recordBack?.setOnClickListener(this)
        recordCancel?.setOnClickListener(this)
        recordConfirm?.setOnClickListener(this)
        imageFlash?.setOnClickListener(this)
        imageSwitch?.setOnClickListener(this)
        imageFlash?.setVisibility(View.VISIBLE)
    }

    @set:SuppressLint("MissingPermission")
    var captureMode: CaptureMode
        get() = mCaptureMode
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        set(captureMode) {
            if (mCaptureMode != captureMode) {
                if (captureMode == CaptureMode.IMAGE) {
                    startCamera()
                } else {
                    startVideo()
                }
            }
            mCaptureMode = captureMode
        }

    @SuppressLint("RestrictedApi")
    fun stopRecording() {
        if (mVideoCapture == null) {
            return
        }
        mVideoCapture!!.stopRecording()
    }

    enum class CaptureMode(val id: Int) {
        /**
         * A mode where image capture is enabled.
         */
        IMAGE(0),

        /**
         * A mode where video capture is enabled.
         */
        VIDEO(1),

        /**
         * A mode where both image capture and video capture are simultaneously enabled. Note that
         * this mode may not be available on every device.
         */
        MIXED(2);

    }

    companion object {
        private val TAG = VideoRecordNewActivity::class.java.simpleName
        private const val FILE_TYPE_IMAGE = 0
        private const val FILE_TYPE_VIDEO = 1
        private const val PERMISSION_RESULT_CAMERA = 1
        private const val PERMISSION_RESULT_RECORD = 2

        //闪关灯状态
        private const val TYPE_FLASH_AUTO = 0x021
        private const val TYPE_FLASH_ON = 0x022
        private const val TYPE_FLASH_OFF = 0x023
        private val PERMISSION_RESULT = 0x11
    }
}