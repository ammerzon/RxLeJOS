package lejos.sensors

import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.EV3ColorSensor
import lejos.robotics.Color

class RxEV3ColorSensor(port: Port) {

    private val colorId: Observable<ColorId>
    private val color: Observable<Color>

    init {
        colorId = Observable.using(
                { EV3ColorSensor(port) },
                { sensor: EV3ColorSensor -> Sampler(sensor.colorIDMode).sample },
                { it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .map { value -> ColorId.colorId(value) }
                .distinctUntilChanged()

        color = Observable.using(
                { EV3ColorSensor(port) },
                { sensor: EV3ColorSensor -> Sampler(sensor.redMode).sample },
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