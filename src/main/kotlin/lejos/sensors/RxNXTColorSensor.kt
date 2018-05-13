package lejos.sensors

import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.NXTColorSensor
import lejos.robotics.Color

class RxNXTColorSensor {

    private var port: Port? = null
    private var sensor: NXTColorSensor? = null
    var colorId: Observable<ColorId> private set
    var color: Observable<Color> private set

    constructor(port: Port, autoClose: Boolean = true) {
        colorId = Observable.using(
                { NXTColorSensor(port) },
                { sensor: NXTColorSensor -> Sampler(sensor.colorIDMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .map { value -> ColorId.colorId(value) }
                .distinctUntilChanged()

        color = Observable.using(
                { NXTColorSensor(port) },
                { sensor: NXTColorSensor -> Sampler(sensor.rgbMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample ->
                    Color(
                            Math.round(sample.values[sample.offset]),
                            Math.round(sample.values[sample.offset + 1]),
                            Math.round(sample.values[sample.offset + 2]))
                }
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

        color = Observable.using(
                { sensor },
                { colorSensor: NXTColorSensor -> Sampler(colorSensor.rgbMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample ->
                    Color(
                            Math.round(sample.values[sample.offset]),
                            Math.round(sample.values[sample.offset + 1]),
                            Math.round(sample.values[sample.offset + 2]))
                }
                .distinctUntilChanged()
    }
}