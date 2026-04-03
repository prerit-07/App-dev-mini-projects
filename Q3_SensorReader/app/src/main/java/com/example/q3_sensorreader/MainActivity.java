package com.example.q3_sensorreader;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// "implements SensorEventListener" zaroori hai sensors ko sunne ke liye
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Variables declare kiye
    SensorManager sm;
    TextView textAccel, textLight, textProx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Aapka Edge-to-Edge setup
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. UI TextBoxes ko link kiya
        textAccel = findViewById(R.id.accelDataText);
        textLight = findViewById(R.id.lightDataText);
        textProx = findViewById(R.id.proxDataText);

        // 2. System ke Sensor Manager ko bulaya
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    // Jab app samne ho, Sensors ko ON karo
    @Override
    protected void onResume() {
        super.onResume();
        Sensor accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor prox = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (accel != null) sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        if (light != null) sm.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        if (prox != null) sm.registerListener(this, prox, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Jab app background mein jaye, Sensors ko OFF karo (Battery save karne ke liye)
    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    // Jab bhi sensor ki reading change ho, screen par data dikhao
    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            textAccel.setText("X: " + event.values[0] + "\nY: " + event.values[1] + "\nZ: " + event.values[2]);
        }
        else if (sensorType == Sensor.TYPE_LIGHT) {
            textLight.setText(event.values[0] + " Lux");
        }
        else if (sensorType == Sensor.TYPE_PROXIMITY) {
            textProx.setText(event.values[0] + " cm");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Iski zarurat nahi hai
    }
}