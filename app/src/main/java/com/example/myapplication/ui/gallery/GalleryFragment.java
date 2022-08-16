package com.example.myapplication.ui.gallery;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Calendar;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentGalleryBinding;
import com.example.myapplication.ui.slideshow.SlideshowFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

public class GalleryFragment extends Fragment implements SensorEventListener {

    final Handler handler = new Handler();
    private FragmentGalleryBinding binding;
    private SensorManager sensorManager;
    private static final String TAG = "GalleryFragment";
    private static long timeInMillis;
    private boolean start = false;
    ArrayList<AccelData> RawData_New = new ArrayList<AccelData>();
    File myInternalFile;

    File myInforInternalFile;
    private RadioGroup radioGroupActivity;
    private RadioGroup radioGroupGender;
    private float[] acc_3 = new float[4];
    private RadioButton radioButtonActivity;
    private RadioButton radioButtonGender;
    EditText mEdit;
    private Button btnDisplay;
    private int count = 0;
    private static int fre = 0;
    private static int tim = 0;
    private static String activity = "";


    private Button buttonStart;
    private String [] information;

    Sensor accelerometer;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //mEdit   = (EditText) findViewById(R.id.frequency);
        Log.d(TAG,"on Create: Initializing Sensor Service");
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(GalleryFragment.this, accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
        Log.d(TAG,"on Create: Registered accelerometer listener");
        //onViewCreated(v,null);
        return root;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        radioGroupActivity = (RadioGroup) view.findViewById(R.id.activityGroup);
        radioGroupGender = (RadioGroup) view.findViewById(R.id.sexgroup);
        //Tạo file
        Date currentTime = Calendar.getInstance().getTime();
        ContextWrapper contextWrapper = new ContextWrapper(getActivity().getApplicationContext());
        System.out.println("Date" + currentTime);
        StringBuilder sb = new StringBuilder();

        try {
            File directory = contextWrapper.getDir("User" , Context.MODE_PRIVATE);
            myInternalFile = new File(directory, "user_profile.csv");
            FileInputStream fis = new FileInputStream(myInternalFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            try {
                String line;
                for( int k = 0; k < 5 ; k++)
                {
                    line = bufferedReader.readLine();
                    sb.append(line).append(" ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("xin chao" + sb);
            information = sb.toString().split(" ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        buttonStart = (Button) view.findViewById(R.id.start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int selectedId = radioGroupActivity.getCheckedRadioButtonId();
//                int selectedGender = radioGroupGender.getCheckedRadioButtonId();
                radioButtonActivity = (RadioButton) view.findViewById(selectedId);
              //  radioButtonGender = (RadioButton) view.findViewById(selectedGender);
                if (selectedId == -1) {
                    System.out.println("No activity is chosen");
                } else {
                    activity = radioButtonActivity.getText().toString();
                    System.out.println("Activities" + activity);

                    File directory = contextWrapper.getDir(activity , Context.MODE_PRIVATE);
                    myInternalFile = new File(directory, activity + ".csv");
                    EditText frequency = (EditText) view.findViewById(R.id.frequencyValue);
                    EditText time = (EditText) view.findViewById(R.id.timeValue);
                    if (frequency.toString().length() > 0) {
                        fre = Integer.parseInt(frequency.getText().toString());
                    }
                    if (time.toString().length() > 0) {
                        tim = Integer.parseInt(time.getText().toString());
                        System.out.println("duration" + tim);
                    }
                    buttonStart.setText("Đang thu dữ liệu");
                    buttonStart.setEnabled(false);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start = true;
//                        UpdateActivity task = new UpdateActivity();
//                        task.execute();
                        }
                    }, 5000);
                }
            }
        });
    }


    public void ReceiveData(String name) {
        System.out.println("Test1" + name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // kiem tra trang thai thu du lieu
        if (start==true) {
            //TestList.add((double)sensorEvent.values[0])
            acc_3 = sensorEvent.values.clone();
            if (acc_3 != null) {
                timeInMillis = System.currentTimeMillis();
                AccelData accel = new AccelData(timeInMillis,
                        acc_3[0], acc_3[1],
                        acc_3[2]);

                RawData_New.add(accel);
            }

            if (timeInMillis - RawData_New.get(0).getTimestamp() >= tim*1000) {
                String header1 = "#Acceleration force along the x y z axes (including gravity)";
                String header2 = "#timestamp(ns),x,y,z(m/s^2)";
                Date date = new Date();
                String header3 = "Datetime: " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date);
                String header4 = "##########################################";
                String number_activity = NumberActivity(activity);
                String header5 = "#Activity:" + " " + number_activity + "-" +  activity + "-" + tim + "s";
                String header6 = "#Subject ID: ";
                String header7 = "#Last name: " + information[0];
                String header8 = "#First name: " + information[0];
                String header9 = "#Age: " + information[1];
                String header10 = "#Height(cm): " + information[3]; //+ height_edit.getText();
                String header11 = "#Weight(kg): " + information[2]; //+ weight_edit.getText();
                String header12 = "#Gender: " + information[4];// + sex;
                String header13 = "##########################################";
                String header14 = "@DATA";
                try {
                    String lineSeparator = System.getProperty("line.separator");
                    //Mở file
                    FileOutputStream fos = new FileOutputStream(myInternalFile);
                    fos.write(header1.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header2.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header3.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header4.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header5.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header6.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header7.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header8.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header9.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header10.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header11.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(header13.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(lineSeparator.getBytes());
                    fos.write(lineSeparator.getBytes());
                    fos.write(header14.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    for (int i = 0; i < RawData_New.size(); i++) {
                        String inp = String.valueOf(RawData_New.get(i).getTimestamp()) + "," + String.valueOf(RawData_New.get(i).getX()) + "," + String.valueOf(RawData_New.get(i).getY()) + "," + String.valueOf(RawData_New.get(i).getZ());
                        //Ghi dữ liệu vào file
                        fos.write(inp.getBytes(StandardCharsets.UTF_8));
                        fos.write(lineSeparator.getBytes());
                        count++;
                    }
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buttonStart.setEnabled(true);
                buttonStart.setText("Thu dữ liệu");
                start = false;
            }

        }
    }

    public String NumberActivity(String activity) {
        switch (activity) {
            case "BSC" :
                return "0";
            case "CHU" :
                return "1";
            case "CSI" :
                return "2";
            case "CSO" :
                return "3";
            case "FKL" :
                return "4";
            case "FOL" :
                return "5";
            case "JOG" :
                return "6";
            case "JUM" :
                return "7";
            case "SCH" :
                return "8";
            case "SDL" :
                return "9";
            case "SIT" :
                return "10";
            case "STD" :
                return "11";
            case "STN" :
                return "12";
            case "STU" :
                return "13";
            case "WAL" :
                return "14";
            default : return "No activity is chosen";

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}