package ar.edu.unlam.mobile.scaffolding.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap

suspend fun loadMarkerDescriptorFromUrl(
    context: Context,
    url: String
): BitmapDescriptor? {
    val loader = ImageLoader(context)
    val req = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false)
        .build()

    val result = loader.execute(req).drawable ?: return null
    val bitmap = (result as BitmapDrawable).bitmap
    val circular = createCircularMarkerBitmap(bitmap)
    return BitmapDescriptorFactory.fromBitmap(circular)
}
fun createCircularMarkerBitmap(avatarBitmap: Bitmap): Bitmap {
    val size = 160            // tamaño total del marcador
    val border = 10f          // grosor del borde

    // bitmap final (el que va al marker)
    val out = createBitmap(size, size)
    val canvas = Canvas(out)

    // 1) dibujar borde fucsia
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.MAGENTA
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, borderPaint)

    // 2) preparar el bitmap circular interno
    val innerSize = (size - border * 2).toInt()

    // escalamos la foto al tamaño interno
    val scaledAvatar = avatarBitmap.scale(innerSize, innerSize)

    // bitmap donde vamos a recortar en círculo
    val clippedAvatar = createBitmap(innerSize, innerSize)
    val avatarCanvas = Canvas(clippedAvatar)

    // recorte en círculo
    val path = Path().apply {
        addCircle(
            innerSize / 2f,
            innerSize / 2f,
            innerSize / 2f,
            Path.Direction.CCW
        )
    }
    avatarCanvas.clipPath(path)
    // ahora sí dibujamos la foto dentro del recorte
    avatarCanvas.drawBitmap(scaledAvatar, 0f, 0f, null)

    // 3) dibujar la foto redondeada sobre el marcador con borde
    val left = border
    val top = border
    canvas.drawBitmap(clippedAvatar, left, top, null)

    return out
}