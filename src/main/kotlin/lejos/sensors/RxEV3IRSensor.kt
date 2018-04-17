package lejos.sensors

import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.EV3IRSensor

class RxEV3IRSensor(port: Port) {

    private val distance: Observable<Float>

    init {
        distance = Observable.using(
                { EV3IRSensor(port) },
                { sensor: EV3IRSensor -> Sampler(sensor.distanceMode).sample },
                { it.close() })
                .share()
                .map { sample -> sample.values[sample.offset] }
                .distinctUntilChanged()
    }
}
