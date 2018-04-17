package com.angrynerds.ev3.lejos.sensors

import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.EV3UltrasonicSensor

class RxEV3UltrasonicSensor(port: Port) {

    private val distance: Observable<Float>

    init {
        distance = Observable.using(
                { EV3UltrasonicSensor(port) },
                { sensor: EV3UltrasonicSensor -> Sampler(sensor.distanceMode).sample },
                { it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .distinctUntilChanged()
    }
}