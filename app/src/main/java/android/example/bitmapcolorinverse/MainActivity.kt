package android.example.bitmapcolorinverse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import android.widget.ProgressBar
import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.View



class MainActivity : AppCompatActivity() {

    var bitmap:Bitmap? = null
    private var inversionTask:MyAsyncTask = MyAsyncTask()

    // extension function to get a bitmap from assets
    @SuppressLint("ResourceType")
    fun Context.assetsToBitmap(fileName:String):Bitmap?{
        return try {
            val stream:InputStream = resources.openRawResource(R.drawable.vibrant_avatar)
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
            inversionTask=MyAsyncTask()
            inversionTask?.execute(bitmap)
        }
    }

    private inner class MyAsyncTask : AsyncTask<Bitmap?, Integer?, Bitmap>() {
        override fun onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE)
        }

        override fun doInBackground(vararg bitmaps: Bitmap?): Bitmap? {
            bitmap?.apply {
                invertColors()?.apply {
                    imageView2.setImageBitmap(this)
                }
            }
            return bitmap
        }

        // extension function to invert bitmap colors
        fun Bitmap.invertColors(): Bitmap? {
            val tempBitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )
            publishProgress(Integer(20))
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
            publishProgress(Integer(50))

            bitmap?.let { Canvas(tempBitmap).drawBitmap(it, 0f, 0f, paint) }
            return tempBitmap
        }

        override fun onProgressUpdate(vararg values: Integer?) {
        }

        override fun onPostExecute(result: Bitmap) {
            progressBar.visibility = View.GONE
            progressBar.setVisibility(ProgressBar.INVISIBLE)
        }

    }
}