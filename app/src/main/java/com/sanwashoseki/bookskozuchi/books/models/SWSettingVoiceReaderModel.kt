package com.sanwashoseki.bookskozuchi.books.models

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWSettingVoiceReaderModel : Entity(), Serializable {

    companion object {
        val voiceSpeeds = Array(16) { i -> i / 2f + 0.5f } //0.5f -> 8f
        val voiceTones = Array(10) { i -> i/5f + 0.2f}     //0.2f -> 2f

        fun speedFromIndex(index: Int): Float {
            return voiceSpeeds[index]
        }

        fun pitchFromIndex(index: Int): Float {
            return voiceTones[index]
        }
    }

    var voiceSpeed: Float = 1.0f
    var isMale: Boolean = true
    var voicePitch: Float = 1.0f

    var voiceSpeedIndex: Int = 0
        get() {
            for (i in voiceSpeeds.indices) {
                if (voiceSpeed == voiceSpeeds[i])
                    return  i
            }
            return 2
        }

    var voicePitchIndex: Int = 0
        get() {
            for (i in voiceTones.indices) {
                if (voicePitch == voiceTones[i])
                    return  i
            }
            return 2
        }
}