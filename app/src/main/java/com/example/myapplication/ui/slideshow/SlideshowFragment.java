package com.example.myapplication.ui.slideshow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentGalleryBinding;
import com.example.myapplication.databinding.FragmentSlideshowBinding;
import com.example.myapplication.ui.gallery.GalleryViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class SlideshowFragment extends Fragment {
    private RadioGroup radioGroupGender;
    private FragmentSlideshowBinding binding;
    private RadioButton radioButton;
    private static Context context;
    public int width_screen;
    public int height_screen;
    File myInternalFile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        width_screen = Resources.getSystem().getDisplayMetrics().widthPixels;
        height_screen = Resources.getSystem().getDisplayMetrics().heightPixels;
        ContextWrapper contextWrapper = new ContextWrapper(getActivity().getApplicationContext());
        File directory = contextWrapper.getDir("User" , Context.MODE_PRIVATE);
        myInternalFile = new File(directory,  "user_profile.csv");



        Button buttonStart = (Button) view.findViewById(R.id.buttonCreate);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                radioGroupGender = (RadioGroup) view.findViewById(R.id.sexgroup);
                int selectedGender = radioGroupGender.getCheckedRadioButtonId();
                EditText name = (EditText) view.findViewById(R.id.name);
                EditText weight = (EditText) view.findViewById(R.id.weight);
                EditText height = (EditText) view.findViewById(R.id.height);
                EditText age = (EditText) view.findViewById(R.id.age);
                radioButton = (RadioButton)  view.findViewById(selectedGender);
                if (selectedGender == -1) {
                    Toast toast = Toast.makeText(getContext(), "Chưa chọn giới tính", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.LEFT | Gravity.TOP, 3*width_screen/10 , 7*height_screen/10);
                    toast.show();
                }
                else {
                    try {

                        String lineSeparator = System.getProperty("line.separator");
                        FileOutputStream fos = new FileOutputStream(myInternalFile);
                        fos.write(name.getText().toString().getBytes(StandardCharsets.UTF_8));
                        fos.write(lineSeparator.getBytes());
                        fos.write(age.getText().toString().getBytes(StandardCharsets.UTF_8));
                        fos.write(lineSeparator.getBytes());
                        fos.write(weight.getText().toString().getBytes(StandardCharsets.UTF_8));
                        fos.write(lineSeparator.getBytes());
                        fos.write(height.getText().toString().getBytes(StandardCharsets.UTF_8));
                        fos.write(lineSeparator.getBytes());
                        fos.write(radioButton.getText().toString().getBytes(StandardCharsets.UTF_8));
                        fos.write(lineSeparator.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast toast = Toast.makeText(getContext(), "Lưu thành công", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.LEFT | Gravity.TOP, 3*width_screen/10 , 7*height_screen/10);
                    toast.show();
                    buttonStart.setText("Saved");
                }
            }
        });
    };

    public String read_file(Context context, String filename) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}