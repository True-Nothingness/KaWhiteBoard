package com.whiteboard.kobo;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

// MyBottomSheetDialogFragment.java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.whiteboard.kobo.model.drawingView;

import java.util.Objects;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {
    drawingView drawing_view;
    Context context = getActivity();
    public MyBottomSheetDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof BoardActivity) {
            BoardActivity boardActivity = (BoardActivity) activity;
            drawing_view = boardActivity.findViewById(R.id.drawing_view);

            // Set up click listeners for each item
            //ImageButton itemButton1 = view.findViewById(R.id.itemButton1);
            ImageButton itemButton2 = view.findViewById(R.id.itemButton2);
            ImageButton itemButton3 = view.findViewById(R.id.itemButton3);
            ImageButton itemButton4 = view.findViewById(R.id.itemButton4);
            //ImageButton itemButton5 = view.findViewById(R.id.itemButton5);
            //ImageButton itemButton6 = view.findViewById(R.id.itemButton6);
            //ImageButton itemButton7 = view.findViewById(R.id.itemButton7);
            ImageButton itemButton8 = view.findViewById(R.id.itemButton8);

//            itemButton1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boardActivity.addNewTextBox();
//                    dismiss(); // Close the bottom sheet after the action
//                }
//            });
            itemButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boardActivity.getSeekbars();
                    dismiss();
                }
            });
            itemButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boardActivity.showColorPickerDialog();
                    dismiss(); // Close the bottom sheet after the action
                }
            });
            itemButton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawing_view.erase(16777215);
                    dismiss(); // Close the bottom sheet after the action
                }
            });
//            itemButton5.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Handle click for item 1
//                    dismiss(); // Close the bottom sheet after the action
//                }
//            });
//            itemButton6.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boardActivity.openImagePicker();
//                    dismiss(); // Close the bottom sheet after the action
//                }
//            });
//            itemButton7.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Handle click for item 1
//                    dismiss(); // Close the bottom sheet after the action
//                }
//            });
            itemButton8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promptForSave();
                    dismiss(); // Close the bottom sheet after the action
                }
            });

        }

    }
    public void promptForSave() {
        // Create an EditText to allow the user to input a file name
        final EditText input = new EditText(getContext());
        input.setHint("Enter file name");

        // Create a dialog to input the file name
        new AlertDialog.Builder(requireContext())
                .setTitle("Save Drawing")
                .setMessage("Enter the file name")
                .setView(input)
                .setPositiveButton("Save", (dialog, whichButton) -> {
                    String fileName = input.getText().toString();
                    // Ensure the file name is not empty
                    if (fileName.trim().isEmpty()) {
                        Toast.makeText(getContext(), "File name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    drawing_view.saveImageToGallery(fileName);
                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss())
                .show();
    }

}