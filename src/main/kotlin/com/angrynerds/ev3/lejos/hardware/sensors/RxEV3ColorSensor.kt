package com.angrynerds.ev3.lejos.hardware.sensors

import com.angrynerds.ev3.lejos.robotics.ColorId
import com.angrynerds.ev3.lejos.robotics.Sampler
import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.EV3ColorSensor

class RxEV3ColorSensor {

    private var sensor: EV3ColorSensor? = null
    var colorId: Observable<ColorId> private set

    constructor(port: Port, autoClose: Boolean = true, distinctUntilChanged: Boolean = false) {
        colorId = Observable.using(
                { EV3ColorSensor(port) },
                { sensor: EV3ColorSensor -> Sampler(sensor.colorIDMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .map { value -> ColorId.colorId(value) }

        if (distinctUntilChanged) {
            colorId = colorId.distinctUntilChanged()
        }
    }

    constructor(sensor: EV3ColorSensor, autoClose: Boolean = true, distinctUntilChanged: Boolean = false) {
        this.sensor = sensor
        colorId = Observable.using(
                { sensor },
                { colorSensor: EV3ColorSensor -> Sampler(colorSensor.colorIDMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .map { value -> ColorId.colorId(value) }

        if (distinctUntilChanged) {
            colorId = colorId.distinctUntilChanged()
        }
    }
}