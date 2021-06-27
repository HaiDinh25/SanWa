package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils

import android.content.Context
import android.util.DisplayMetrics
import java.io.BufferedReader
import java.io.InputStreamReader

object Utils {

    fun getTextSize(context: Context, size: Int): Float {
        val sizes = arrayListOf<Float>()
        return sizes[size - 1]
    }

    fun getTextFuriganaSize(context: Context, size: Int): Float {
        val sizes = arrayListOf<Float>()
        return sizes[size - 1]
    }

    fun calculateNoOfColumns(context: Context, columnWidthDp: Float): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }

    fun getBook(context: Context): String {
        val br: BufferedReader
        try {
            br = BufferedReader(InputStreamReader(context.assets.open("bb.txt"), "Shift_JIS"))
            val sb = StringBuilder()
            var line = br.readLine()
            while (line != null) {
                sb.append(line)
                sb.append(System.lineSeparator())
                line = br.readLine()
            }
            val data = sb.toString()
            br.close()
            return data
        } catch (ignored: Exception) {
        }
        return ""
    }

    fun test(context: Context): String? {
        val br: BufferedReader
        try {
            br = BufferedReader(InputStreamReader(context.assets.open("demo.txt"), Charsets.UTF_8))
            val sb = StringBuilder()
            var line = br.readLine()
            while (line != null) {
                sb.append(line)
                sb.append(System.lineSeparator())
                line = br.readLine()
            }
            val data = sb.toString()
            br.close()
            return data
        } catch (ignored: Exception) {
        }
        return ""
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun formatRubyText(text: String): String {
        val output = text.replace("[一-龠亜-腕弌-熙0-9A-Za-z々仝〆〇ヶ']+《(.+?)》".toRegex(), "｜\\$&")
        return output
    }

    private fun removeSpace(s: String): String {
        return s.replace("\\s".toRegex(), "")
    }

    private val hiragana = listOf(
        "あ", "い", "う", "え", "お",
        "か", "き", "く", "け", "こ",
        "さ", "し", "す", "せ", "そ",
        "た", "ち", "つ", "て", "と",
        "な", "に", "ぬ", "ね", "の",
        "は", "ひ", "ふ", "へ", "ほ",
        "ま", "み", "む", "め", "も",
        "や", "ゆ", "よ",
        "ら", "り", "る", "れ", "ろ",
        "わ", "を", "ん",

        "が", "ぎ", "ぐ", "げ", "ご",
        "ざ", "じ", "ず", "ぜ", "ぞ",
        "だ", "ぢ", "づ", "で", "ど",
        "ば", "び", "ぶ", "べ", "ぼ",
        "ぱ", "ぴ", "ぷ", "ぺ", "ぽ",

        "きゃ", "きゅ", "きょ",
        "ぎゃ", "ぎゅ", "ぎょ",
        "しゃ", "しゅ", "しょ",
        "じゃ", "じゅ", "じょ",

        "ちゃ", "ちゅ", "ちょ",
        "にゃ", "にゅ", "にょ",
        "ひゃ", "ひゅ", "ひょ",
        "みゃ", "みゅ", "みょ",
        "りゃ", "りゅ", "りょ",

        "びゃ", "びゅ", "びょ",
        "ぴゃ", "ぴゅ", "ぴょ"
    )

    private val katakana = listOf(
        "ア", "イ", "ウ", "エ", "オ",
        "カ", "キ", "ク", "ケ", "コ",
        "サ", "シ", "ス", "セ", "ソ",
        "タ", "チ", "ツ", "テ", "ト",
        "ナ", "ニ", "ヌ", "ネ", "ノ",
        "ハ", "ヒ", "フ", "ヘ", "ホ",
        "マ", "ミ", "ム", "メ", "モ",
        "ヤ", "ユ", "ヨ",
        "ラ", "リ", "ル", "レ", "ロ",
        "ワ", "ヲ", "ン",

        "ガ", "ギ", "グ", "ゲ", "ゴ",
        "ザ", "ジ", "ズ", "ゼ", "ゾ",
        "ダ", "ヂ", "ヅ", "デ", "ド",
        "バ", "ビ", "ブ", "ベ", "ボ",
        "パ", "ピ", "プ", "ペ", "ポ",

        "キャ", "キュ", "キョ",
        "ギャ", "ギュ", "ギョ",
        "シャ", "シュ", "ショ",
        "ジャ", "ジュ", "ジョ",

        "チャ", "チュ", "チョ",
        "ニャ", "ニュ", "ニョ",
        "ヒャ", "ヒュ", "ヒョ",
        "ミャ", "ミュ", "ミョ",
        "リャ", "リュ", "リョ",

        "ビャ", "ビュ", "ビョ",
        "ピャ", "ピュ", "ピョ"
    )
}