package android.example.bitmapcolorinverse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import android.graphics.Bitmap
import android.os.*


class MainActivity : AppCompatActivity() {

    var bitmap: Bitmap? = null
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val inversionTask: LooperThread = LooperThread().apply { start() }

    // extension function to get a bitmap from assets
    @SuppressLint("ResourceType")
    fun Context.assetsToBitmap(fileName: String): Bitmap? {
        return try {
            val stream: InputStream = resources.openRawResource(R.drawable.vibrant_avatar)
            BitmapFactory.decodeStream(stream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bitmap = assetsToBitmap("vibrant_avatar.png")
        bitmap?.apply {
            // set original bitmap to first image view
            imageView.setImageBitmap(this)
        }

        button.setOnClickListener {
            val message: Message = Message()
            inversionTask.handler?.handleMessage(message)
        }
    }

    inner class LooperThread : Thread() {
        var handler: Handler? = null

        override fun run() {
            Looper.prepare()
            //handler = Looper.myLooper()?.let { Handler(it) }
            handler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    bitmap?.apply {
                        invertColors()?.apply {
                            imageView2.setImageBitmap(this)
                        }
                    }
                }
            }
            Looper.loop()
        }
    }

    fun Bitmap.invertColors(): Bitmap? {
        val tempBitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        val matrixInvert = ColorMatrix().apply {
            set(
                floatArrayOf(
                    -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                    0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                    0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                )
            )
        }

        val paint = Paint()
        ColorMatrixColorFilter(matrixInvert).apply {
            paint.colorFilter = this
        }

        bitmap?.let { Canvas(tempBitmap).drawBitmap(it, 0f, 0f, paint) }
        return tempBitmap
    }
}