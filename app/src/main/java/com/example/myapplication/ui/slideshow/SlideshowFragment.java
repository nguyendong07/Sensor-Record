package com.example.myapplication.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        Intent intent = new Intent();
       // radioGroup = (RadioGroup) getView().findViewById(R.id.Sex);
        Button buttonStart = (Button) getView().findViewById(R.id.buttonCreate);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // get infor of user
                EditText name = (EditText) getView().findViewById(R.id.name);
                EditText job = (EditText) getView().findViewById(R.id.job);
                EditText age = (EditText) getView().findViewById(R.id.age);
                EditText height = (EditText) getView().findViewById(R.id.height);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) getView().findViewById(selectedId);
                String sex = radioButton.getText().toString();

                // put infor to intent for transfer parameter between activities
                intent.putExtra("name", name.toString());
                intent.putExtra("job", job.toString());
                intent.putExtra("age", age.toString());
                intent.putExtra("height", height.toString());
                intent.putExtra("sex", sex);
                // find the radiobutton by returned id
            }
        });
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}