package com.angrynerds.ev3.lejos.hardware.sensors

import com.angrynerds.ev3.lejos.robotics.ColorId
import com.angrynerds.ev3.lejos.robotics.Sampler
import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.NXTColorSensor

/**
 * Allows the reading of [ColorId]s. The sensor has a tri-color LED and this can
 * be set to output red/green/blue or off. It also has a full mode in which
 * four samples are read (off/red/green/blue) very quickly.
 */
class RxNXTColorSensor {

    private var sensor: NXTColorSensor? = null
    var colorId: Observable<ColorId> private set

    constructor(port: Port, autoClose: Boolean = true, distinctUntilChanged: Boolean = false) {
        colorId = Observable.using(
                { NXTColorSensor(port) },
                { sensor: NXTColorSensor -> Sampler(sensor.colorIDMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .map { value -> ColorId.colorId(value) }

        if (distinctUntilChanged) {
            colorId = colorId.distinctUntilChanged()
        }
    }

    constructor(sensor: NXTColorSensor, autoClose: Boolean = true, distinctUntilChanged: Boolean = false) {
        this.sensor = sensor
        colorId = Observable.using(
                { sensor },
                { colorSensor: NXTColorSensor -> Sampler(colorSensor.colorIDMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .map { value -> ColorId.colorId(value) }
                .distinctUntilChanged()

        if (distinctUntilChanged) {
            colorId = colorId.distinctUntilChanged()
        }
    }
}