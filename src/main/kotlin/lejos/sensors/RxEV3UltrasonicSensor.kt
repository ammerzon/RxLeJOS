package lejos.sensors

import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.EV3UltrasonicSensor

class RxEV3UltrasonicSensor {

    private var port: Port? = null
    private var sensor: EV3UltrasonicSensor? = null
    var distance: Observable<Float> private set

    constructor(port: Port, autoClose: Boolean = true) {
        this.port = port
        distance = Observable.using(
                { EV3UltrasonicSensor(port) },
                { irSensor: EV3UltrasonicSensor -> Sampler(irSensor.distanceMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .distinctUntilChanged()
    }

    constructor(sensor: EV3UltrasonicSensor, autoClose: Boolean = true) {
        this.sensor = sensor
        distance = Observable.using(
                { sensor },
                { ultrasonicSensor: EV3UltrasonicSensor -> Sampler(ultrasonicSensor.distanceMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .distinctUntilChanged()
    }
}