package com.angrynerds.ev3.lejos.sensors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers.newThread
import lejos.hardware.sensor.SensorMode
import lejos.robotics.SampleProvider
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep

internal class Sampler(sampleProvider: SampleProvider) {

    private val logger = LoggerFactory.getLogger(Sampler::class.java)
    val sample: Observable<Sample>

    init {
        this.sample = createSampleObservable(sampleProvider)
                .subscribeOn(newThread())
                .share()
    }

    private fun createSampleObservable(sampleProvider: SampleProvider): Observable<Sample> {
        return Observable.create { emitter ->
            try {
                logger.info("Start sampling, sample size: " + sampleProvider.sampleSize())
                val sample = Sample(FloatArray(sampleProvider.sampleSize()), 0)
                while (!emitter.isDisposed) {
                    sampleProvider.fetchSample(sample.values, sample.offset)
                    emitter.onNext(sample)
                    sleep(200)
                }
                logger.info("Stop sampling")
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }
}
