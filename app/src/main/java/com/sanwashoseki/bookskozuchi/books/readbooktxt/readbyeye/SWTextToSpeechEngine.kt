package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*


class SWTextToSpeechEngine(private val context: Activity) : TextToSpeech.OnInitListener {

    companion object {
        val TAG: String = SWTextToSpeechEngine::class.java.simpleName
        const val DATA_CHECK_CODE = 1000
        const val ENGINE = "android.speech.tts"
        val MALE_VOICES = arrayListOf(
            "ja-JP-SMTm00",
            "ja-jp-x-jad-local"
        )
        val FEMALE_VOICES = arrayListOf(
            "ja-JP-SMTf00",
            "ja-jp-x-jab-local"
        )
        val IGNORED_CHARATERS = "<>(){}[]＜＞（）『』「」"
        val SPACE_CHARATER = "　"
    }

    private var tts: TextToSpeech? = null

    private val handler = Handler()

    init {
        checkMissingVoiceData()
    }

    fun stop() {
        tts?.stop()
//        tts = null
    }

    fun update(
        isMale: Boolean = true,
        speed: Float,
        pitch: Float,
    ) {
        if (isMale) {
            tts?.voices?.first { voice ->
                MALE_VOICES.contains(voice.name)
            }.let {
                Log.i(TAG, "Setting male voice ${it?.name} ${it?.features?.joinToString(", ")}")
                tts?.setVoice(it)
            }
        } else {
            tts?.voices?.first { voice ->
                FEMALE_VOICES.contains(voice.name)
            }.let {
                Log.i(TAG, "Setting female voice ${it?.name} ${it?.features?.joinToString(", ")}")
                tts?.setVoice(it)
            }
        }
        tts?.setSpeechRate(speed)
        tts?.setPitch(pitch)
    }

    fun start(
        text: String,
        isMale: Boolean = true,
        speed: Float,
        pitch: Float,
        onStarted: (() -> Unit),
        onSpeaking: ((String) -> Unit),
        onFinished: (() -> Unit),
    ) {
        stop()

        if (isMale) {
            tts?.voices?.first { voice ->
                MALE_VOICES.contains(voice.name)
            }.let {
                Log.i(TAG, "Setting male voice ${it?.name} ${it?.features?.joinToString(", ")}")
                tts?.setVoice(it)
            }
        } else {
            tts?.voices?.first { voice ->
                FEMALE_VOICES.contains(voice.name)
            }.let {
                Log.i(TAG, "Setting female voice ${it?.name} ${it?.features?.joinToString(", ")}")
                tts?.setVoice(it)
            }
        }

        tts?.setSpeechRate(speed)
        tts?.setPitch(pitch)
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onDone(utteranceId: String?) {
                Log.i(TAG, "onDone $utteranceId")
                handler.post {
                    onFinished()
                }
            }

            override fun onError(utteranceId: String?) {
                Log.e(TAG, "onError $utteranceId")
            }

            override fun onStart(utteranceId: String?) {
                Log.v(TAG, "onStart $utteranceId")
                handler.post {
                    onStarted()
                }
            }

            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
                Log.e(TAG, "onStop $utteranceId")
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                super.onRangeStart(utteranceId, start, end, frame)
                Log.e(TAG,
                    "onRangeStart utteranceId = $utteranceId start = $start end = $end frame = $frame")
                val currentText = text.subSequence(start, end) as String
                Log.e(TAG, "onRangeStart $currentText")
                handler.post {
                    onSpeaking(currentText)
                }
            }

        })
        var inputText = text
        IGNORED_CHARATERS.forEach {
            inputText = inputText.replace(it.toString(), SPACE_CHARATER)
        }
        Log.e(TAG, "start inputText $inputText")

        tts?.speak(inputText,
            TextToSpeech.QUEUE_FLUSH,
            null,
            TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
    }

    fun checkMissingVoiceData() {
        val checkIntent = Intent()
        checkIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        context.startActivityForResult(checkIntent, DATA_CHECK_CODE)
    }

    fun installMissingVoiceData() {
        val installIntent = Intent()
        installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
        context.startActivity(installIntent)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DATA_CHECK_CODE) {
            Log.v(TAG, "* Checked text to speak engine")
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                Log.i(TAG, "\t + Checked voice data pass")
                tts = TextToSpeech(context, this, ENGINE)
            } else {
                Log.e(TAG, "\t + Checked voice data failure")
                installMissingVoiceData()
            }
        }
    }

    override fun onInit(status: Int) {
        when (status) {
            TextToSpeech.SUCCESS -> {
                Log.i(TAG, "Initialize text to speak successfully")
                val result = tts?.setLanguage(Locale.JAPAN)
                Log.v("TAG", "Setting japanese locale")
                when (result) {
                    TextToSpeech.LANG_MISSING_DATA -> {
                        Log.e(TAG, "Missing language data LANG_MISSING_DATA")
                    }
                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        Log.e(TAG, "Not supported language LANG_NOT_SUPPORTED")
                        installMissingVoiceData()
                    }
                    else -> {
//                        Log.v(TAG, "* Checking voices")
//                        val availableVoices = tts!!.voices
//                        Log.v(TAG, "\t+ availableVoices")
//                        availableVoices.forEach {
//                            Log.v(TAG, "\t\t- ${it.name} - ${it.locale.displayName}")
//                        }
//                        val availableLocales: List<Locale> = Locale.getAvailableLocales().toList()
//                        Log.v(TAG, "\t+ availableLocales")
//                        availableLocales.forEach {
//                            Log.v(TAG, "\t\t- ${it.displayLanguage}")
//                        }
//                        availableVoices.forEach { voice ->
//                            if (voice.locale == Locale.JAPAN) {
//                                Log.v(TAG,
//                                    "\t+ Ready voice: ${voice.name} ${voice.features.joinToString(", ")}")
//                            }
//                        }
                    }
                }
            }
            else -> {
                Log.e(TAG, "Can't init text to speak")
            }
        }
    }
}