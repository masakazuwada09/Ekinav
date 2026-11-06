package com.example.ui.map

import android.content.Context
import android.graphics.*
import com.example.model.RailOperator
import com.example.model.Station
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory

object StationMarkerIconHelper {

    private val iconCache = mutableMapOf<String, Icon>()

    fun getStationMarkerIcon(context: Context, station: Station): Icon {
        val primaryOp = station.operators.firstOrNull() ?: RailOperator.JR_EAST
        val primaryLine = station.lines.firstOrNull()
        val cacheKey = "${primaryOp.name}_${primaryLine?.code ?: "ALL"}"

        iconCache[cacheKey]?.let { return it }

        val iconFactory = IconFactory.getInstance(context)
        val bitmap = createOperatorMarkerBitmap(primaryOp, primaryLine?.code ?: "EKI", primaryLine?.nameEn ?: "")
        val icon = iconFactory.fromBitmap(bitmap)
        iconCache[cacheKey] = icon
        return icon
    }

    private fun createOperatorMarkerBitmap(
        operator: RailOperator,
        lineCode: String,
        lineName: String
    ): Bitmap {
        val width = 100
        val height = 114
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val brandColorInt = Color.rgb(
            ((operator.brandColorHex shr 16) and 0xFF).toInt(),
            ((operator.brandColorHex shr 8) and 0xFF).toInt(),
            (operator.brandColorHex and 0xFF).toInt()
        )

        // 1. Draw outer shadow & pin shape
        val pinPath = Path().apply {
            // Rounded rectangle body
            val rect = RectF(6f, 6f, 94f, 86f)
            addRoundRect(rect, 18f, 18f, Path.Direction.CW)
            // Triangular pointer
            moveTo(40f, 84f)
            lineTo(50f, 108f)
            lineTo(60f, 84f)
            close()
        }

        // Draw shadow
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(90, 0, 0, 0)
            maskFilter = BlurMaskFilter(4f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawPath(pinPath, shadowPaint)

        // Draw white border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 5f
            strokeJoin = Paint.Join.ROUND
        }
        canvas.drawPath(pinPath, borderPaint)

        // Draw operator background fill
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = brandColorInt
            style = Paint.Style.FILL
        }
        canvas.drawPath(pinPath, fillPaint)

        // 2. Draw Operator Logo Graphics
        when (operator) {
            RailOperator.JR_EAST -> {
                // Official JR East logo graphic: Stylized bold italic "JR" emblem
                val jrPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 28f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("JR", 40f, 44f, jrPaint)

                // "EAST" subtext badge
                val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 12f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.LEFT
                }
                canvas.drawText("EAST", 58f, 38f, subPaint)
                val jpPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.LEFT
                }
                canvas.drawText("東日本", 58f, 50f, jpPaint)
            }
            RailOperator.TOKYO_METRO -> {
                // Tokyo Metro stylized "M" logo
                val mPath = Path().apply {
                    moveTo(24f, 50f)
                    cubicTo(24f, 28f, 38f, 26f, 46f, 36f)
                    cubicTo(48f, 38f, 52f, 38f, 54f, 36f)
                    cubicTo(62f, 26f, 76f, 28f, 76f, 50f)
                }
                val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    style = Paint.Style.STROKE
                    strokeWidth = 6.5f
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawPath(mPath, mPaint)

                val metroTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("METRO", 50f, 56f, metroTextPaint)
            }
            RailOperator.TOEI_SUBWAY -> {
                // Toei Ginkgo Leaf Logo
                val leafPath = Path().apply {
                    moveTo(50f, 24f)
                    cubicTo(32f, 26f, 26f, 42f, 44f, 50f)
                    lineTo(48f, 54f)
                    lineTo(52f, 54f)
                    lineTo(56f, 50f)
                    cubicTo(74f, 42f, 68f, 26f, 50f, 24f)
                    close()
                }
                val leafPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    style = Paint.Style.FILL
                }
                canvas.drawPath(leafPath, leafPaint)

                val toeiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 12f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("TOEI 都営", 50f, 56f, toeiPaint)
            }
            RailOperator.ODAKYU -> {
                val opPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 16f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("ODAKYU", 50f, 42f, opPaint)
                val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(220, 255, 255, 255)
                    textSize = 11f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("小田急", 50f, 56f, subPaint)
            }
            RailOperator.KEIO -> {
                val opPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 18f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("KEIO", 50f, 42f, opPaint)
                val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(220, 255, 255, 255)
                    textSize = 11f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("京王電鉄", 50f, 56f, subPaint)
            }
            RailOperator.TOKYU -> {
                val opPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 17f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("TOKYU", 50f, 42f, opPaint)
                val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(220, 255, 255, 255)
                    textSize = 11f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("東急電鉄", 50f, 56f, subPaint)
            }
            RailOperator.SHINKANSEN -> {
                val opPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 18f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("新幹線", 50f, 42f, opPaint)
                val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(220, 255, 255, 255)
                    textSize = 10f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("BULLET TRAIN", 50f, 56f, subPaint)
            }
            else -> {
                val opPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 18f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(operator.shortLogo, 50f, 42f, opPaint)
                val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(220, 255, 255, 255)
                    textSize = 10f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(operator.displayNameJa.take(4), 50f, 56f, subPaint)
            }
        }

        // 3. Lower sub-pill for Line Code badge (e.g. JY, M, G, E)
        val pillRect = RectF(20f, 62f, 80f, 80f)
        val pillBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(pillRect, 9f, 9f, pillBgPaint)

        val lineTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = brandColorInt
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val displayLine = if (lineCode.length > 5) lineCode.take(4) else lineCode
        canvas.drawText(displayLine, 50f, 76f, lineTextPaint)

        return bitmap
    }
}
