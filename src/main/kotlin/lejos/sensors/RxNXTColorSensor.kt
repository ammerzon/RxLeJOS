package lejos.sensors

import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.NXTColorSensor

class RxNXTColorSensor {

    private var sensor: NXTColorSensor? = null
    var colorId: Observable<ColorId> private set

    constructor(port: Port, autoClose: Boolean = true) {
        colorId = Observable.using(
                { NXTColorSensor(port) },
                { sensor: NXTColorSensor -> Sampler(sensor.colorIDMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .map { value -> ColorId.colorId(value) }
                .distinctUntilChanged()
    }

    constructor(sensor: NXTColorSensor, autoClose: Boolean = true) {
        this.sensor = sensor
        colorId = Observable.using(
                { sensor },
                { colorSensor: NXTColorSensor -> Sampler(colorSensor.colorIDMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .map { value -> ColorId.colorId(value) }
                .distinctUntilChanged()
    }
}