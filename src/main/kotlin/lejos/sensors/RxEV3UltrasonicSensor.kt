package lejos.sensors

import io.reactivex.Observable
import lejos.hardware.port.Port
import lejos.hardware.sensor.EV3UltrasonicSensor

class RxEV3UltrasonicSensor(port: Port) {

    var distance: Observable<Float> private set

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