package com.example.myapplication.ui.gallery;
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
    private String filename = "BSC.csv";
    private String filepath = "BSC";
    private static long timeInMillis;
    private boolean start = false;
    ArrayList<AccelData> RawData_New = new ArrayList<AccelData>();
    File myInternalFile;
    private RadioGroup radioGroup;
    private float[] acc_3 = new float[4];
    private RadioButton radioButton;
    EditText mEdit;
    private Button btnDisplay;
    private int count = 0;
    private static int fre = 0;
    private static int tim = 0;

    private String ten = "";
    private String tuoi = "";
    private String gioitinh = "";
    private String chieucao = "";
    private String congviec = "";



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

        sensorManager.registerListener(GalleryFragment.this, accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG,"on Create: Registered accelerometer listener");
        //onViewCreated(v,null);
        return root;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        radioGroup = (RadioGroup) view.findViewById(R.id.activityGroup);
        //get infor from user information view

        //int selectedId = radioGroup.getCheckedRadioButtonId();
        //radioButton = (RadioButton) getView().findViewById(selectedId);
        //String sex = radioButton.getText().toString();



        //Tạo file
        Date currentTime = Calendar.getInstance().getTime();
        ContextWrapper contextWrapper = new ContextWrapper(getActivity().getApplicationContext());
        System.out.println("Date" + currentTime);
        //Tạo (Hoặc là mở file nếu nó đã tồn tại) Trong bộ nhớ trong có thư mục là ThuMucCuaToi.


        Button buttonStart = (Button) view.findViewById(R.id.start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) view.findViewById(selectedId);
                if (selectedId == -1) {
                    System.out.println("No activity is chosen");
                } else {
                    String activity = radioButton.getText().toString();
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
                    EditText name = (EditText) view.findViewById(R.id.name);
                    EditText job = (EditText) view.findViewById(R.id.job);
                    EditText age = (EditText) view.findViewById(R.id.age);
                    EditText height = (EditText) view.findViewById(R.id.height);

//                ten = name.getText().toString();
//                congviec = job.getText().toString();
//                tuoi = age.getText().toString();
//                chieucao = height.getText().toString() + " " + "cm";
                    ten = "Nguyen Vu Dong";
                    congviec = "IT";
                    tuoi = "23";
                    chieucao = "170" + " " + "cm";
                    buttonStart.setText("Đang thu dữ liệu");
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




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // kiem tra trang thai thu du lieu
        if (start) {
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
                try {
                    String lineSeparator = System.getProperty("line.separator");
                    //Mở file
                    FileOutputStream fos = new FileOutputStream(myInternalFile);
                    fos.write(ten.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(tuoi.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(chieucao.getBytes(StandardCharsets.UTF_8));
                    fos.write(lineSeparator.getBytes());
                    fos.write(congviec.getBytes(StandardCharsets.UTF_8));
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
                start = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}