package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Clase que obtiene la orientación del dispositivo usando los sensores.
 * Con los datos del Magnetómetro y el Acelerómetro se calcula el azimut.
 */
class SensorDataSource
    @Inject
    constructor(
        private val sensorManager: SensorManager,
    ) {
        // Almacenar las lecturas de los sensores
        private val accelerometerReading = FloatArray(3)
        private val magnetometerReading = FloatArray(3)

        // Matrices de rotación y orientación
        private val rotationMatrix = FloatArray(9)
        private val orientationAngles = FloatArray(3)

        // Banderas para saber si ya recibimos datos de cada sensor
        private var hasAccelerometerData = false
        private var hasMagnetometerData = false

        /**
         * Función que obtiene un Flow continuo del azimut en grados.
         * Se retorna el Flow que emite el azimut cada vez que cambia.
         */
        fun getOrientation(): Flow<Float> =
            callbackFlow {
                // Listener que escucha cambios en los sensores
                val listener =
                    object : SensorEventListener {
                        override fun onSensorChanged(event: SensorEvent) {
                            when (event.sensor.type) {
                                Sensor.TYPE_ACCELEROMETER -> {
                                    // Copia los valores del acelerómetro
                                    System.arraycopy(
                                        event.values,
                                        0,
                                        accelerometerReading,
                                        0,
                                        accelerometerReading.size,
                                    )
                                    // Marcar que ya tenemos datos del acelerómetro
                                    hasAccelerometerData = true
                                }
                                Sensor.TYPE_MAGNETIC_FIELD -> {
                                    // Copia los valores del magnetómetro
                                    System.arraycopy(
                                        event.values,
                                        0,
                                        magnetometerReading,
                                        0,
                                        magnetometerReading.size,
                                    )
                                    // Marcar que ya tenemos datos del magnetómetro
                                    hasMagnetometerData = true
                                }
                            }

                            // Calcula la orientación solo si tenemos datos REALES de ambos sensores
                            if (hasAccelerometerData && hasMagnetometerData) {
                                // Calcula la matriz de rotación
                                val success =
                                    SensorManager.getRotationMatrix(
                                        rotationMatrix,
                                        null,
                                        accelerometerReading,
                                        magnetometerReading,
                                    )
                                if (success) {
                                    // Obtiene los ángulos de orientación
                                    SensorManager.getOrientation(rotationMatrix, orientationAngles)

                                    // orientationAngles[0] es al azimut en radianes (-π a π)
                                    // Lo convertimos a grados (0-360)
                                    val azimuthRadians = orientationAngles[0]
                                    val azimuthDegrees = Math.toDegrees(azimuthRadians.toDouble()).toFloat()

                                    // Normalizar a 0-360
                                    val normalizedAzimuth =
                                        if (azimuthDegrees < 0) {
                                            azimuthDegrees + 360
                                        } else {
                                            azimuthDegrees
                                        }

                                    // Emitir el valor
                                    trySend(normalizedAzimuth)
                                }
                            }
                        }

                        override fun onAccuracyChanged(
                            sensor: Sensor?,
                            accuracy: Int,
                        ) {
                            // No necesitamos manejar cambios de precisión por ahora
                        }
                    }

                // Registrar listeners para ambos sensores
                val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

                sensorManager.registerListener(
                    listener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI, // Actualización optimizada para UI (60 Hz aprox. - 16ms)
                )

                sensorManager.registerListener(
                    listener,
                    magnetometer,
                    SensorManager.SENSOR_DELAY_UI,
                )

                // Cuando el Flow se cancela, desregistrar los listeners
                awaitClose {
                    sensorManager.unregisterListener(listener)
                    // Resetear banderas para la próxima vez que alguien se suscriba
                    hasAccelerometerData = false
                    hasMagnetometerData = false
                }
            }
    }
