package ua.com.crooge.light.threads.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ua.com.crooge.light.threads.LightThreads;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LightThreads.runInForeground(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Enjoy using LightThreads!", Toast.LENGTH_SHORT).show();
            }
        }, 2000L);
    }
}
