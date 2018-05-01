package lejos.sensors

import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.NXTColorSensor
import lejos.robotics.Color

class RxNXTColorSensor(port: Port) {

    var colorId: Observable<ColorId> private set
    var color: Observable<Color> private set

    init {
        colorId = Observable.using(
                { NXTColorSensor(port) },
                { sensor: NXTColorSensor -> Sampler(sensor.colorIDMode).sample },
                { it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .map { value -> ColorId.colorId(value) }
                .distinctUntilChanged()

        color = Observable.using(
                { NXTColorSensor(port) },
                { sensor: NXTColorSensor -> Sampler(sensor.rgbMode).sample },
                { it.close() })
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