package com.example.smprojectkotlin.ui.theme.components

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import kotlin.math.abs

fun extractAmplitudes(filePath: String): List<Int> {
    val extractor = MediaExtractor()
    extractor.setDataSource(filePath)
    val format = extractor.getTrackFormat(0)
    val mime = format.getString(MediaFormat.KEY_MIME) ?: return emptyList()

    extractor.selectTrack(0)

    val codec = MediaCodec.createDecoderByType(mime)
    codec.configure(format, null, null, 0)
    codec.start()

    val bufferInfo = MediaCodec.BufferInfo()
    val inputBuffers = codec.inputBuffers
    val outputBuffers = codec.outputBuffers

    val amplitudes = mutableListOf<Int>()
    var isEOS = false

    while (!isEOS) {
        val inputBufferIndex = codec.dequeueInputBuffer(10000)
        if (inputBufferIndex >= 0) {
            val inputBuffer = inputBuffers[inputBufferIndex]
            val sampleSize = extractor.readSampleData(inputBuffer, 0)

            if (sampleSize < 0) {
                codec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                isEOS = true
            } else {
                codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.sampleTime, 0)
                extractor.advance()
            }
        }

        val outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 10000)
        if (outputBufferIndex >= 0) {
            val outputBuffer = outputBuffers[outputBufferIndex]
            outputBuffer.position(bufferInfo.offset)
            outputBuffer.limit(bufferInfo.offset + bufferInfo.size)

            while (outputBuffer.hasRemaining()) {
                val amplitude = abs(outputBuffer.get().toInt())
                amplitudes.add(amplitude)
            }

            codec.releaseOutputBuffer(outputBufferIndex, false)
        }
    }

    codec.stop()
    codec.release()
    extractor.release()

    return amplitudes
}
