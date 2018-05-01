package lejos.sensors

import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.EV3IRSensor

class RxEV3IRSensor {

    private var port: Port? = null
    private var sensor: EV3IRSensor? = null
    var distance: Observable<Float> private set

    constructor(port: Port, autoClose: Boolean = true) {
        this.port = port
        distance = Observable.using(
                { EV3IRSensor(port) },
                { irSensor: EV3IRSensor -> Sampler(irSensor.distanceMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .distinctUntilChanged()
    }

    constructor(sensor: EV3IRSensor, autoClose: Boolean = true) {
        this.sensor = sensor
        distance = Observable.using(
                { sensor },
                { irSensor: EV3IRSensor -> Sampler(irSensor.distanceMode).sample },
                { if (autoClose) it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .distinctUntilChanged()
    }
}
