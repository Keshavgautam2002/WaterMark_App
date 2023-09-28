package com.example.watermarkpdf

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.NonNull
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.File
import java.io.FileOutputStream

class MainActivity: FlutterActivity() {
    private val filePlatform = "pdf_file";
    @SuppressLint("WrongThread")
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, filePlatform).setMethodCallHandler { call, result ->
            if(call.method == "createPdf")
            {
                print("Called")
                var outputPath = call.argument<String>("outputPath")
                outputPath = outputPath + System.currentTimeMillis().toString().replace(":", ".") + ".pdf"
                val images = call.argument<ArrayList<String>>("files")
                val tempPath = call.argument<String>("tempPath")
                var pdfDoc = PdfDocument(PdfWriter(FileOutputStream(outputPath)))
                val document = Document(pdfDoc)
                document.setMargins(0f,0f,0f,0f)
                if (images != null) {
                    for(imageFile in images) {
                        println("Before compression : ${File(imageFile).length()/(1024*1024)} MB}")
                        val bitmap = BitmapFactory.decodeFile(imageFile)
                        var watermark = bitmap.copy(Bitmap.Config.ARGB_8888,true)
                        val fontSize = (bitmap.width * 3) / 100f;
                        val canvas = Canvas(watermark)
                        val paint = Paint()
                        paint.alpha = 90
                        paint.color = Color.WHITE
                        paint.textSize = fontSize
                        paint.isAntiAlias = true

                        val x = (bitmap.width * 0.05).toFloat();
                        val y = (bitmap.height * 0.9).toFloat();

                        var intialHeight = bitmap.height - (3 * fontSize);
                        var x1 = bitmap.height - (6*fontSize)
                        var y1 = bitmap.width * 0.4f
                        var margin = 5;
                        canvas.drawRect(x-margin,intialHeight - fontSize,y1, (intialHeight+(1.5 * fontSize)).toFloat(),paint)
                        paint.color = Color.BLACK
                        canvas.drawText("Hello this is watermark", x, intialHeight, paint)
                        intialHeight += fontSize;
                        canvas.drawText("Not provided", x, intialHeight, paint)
                        val fileName: String = System.currentTimeMillis().toString().replace(":", ".") + ".jpg"
                        val file = File(tempPath,fileName)
                        val ops = FileOutputStream(file)
                        watermark.compress(Bitmap.CompressFormat.JPEG,20,ops)
                        println("After compression : ${file.length()/(1024*1024)} MB}")
                        ops.close()


                        val image = Image(ImageDataFactory.create(file.path))
                        val imageSize = image.imageHeight to image.imageWidth // Get image dimensions
                        val pageSize = PageSize(imageSize.second, imageSize.first)
                        pdfDoc.defaultPageSize = pageSize
                        pdfDoc.writer.compressionLevel = 9
                        document.add(image)

                    }
                    document.close()

                }
                result.success(outputPath)
            }
        }
    }
}
