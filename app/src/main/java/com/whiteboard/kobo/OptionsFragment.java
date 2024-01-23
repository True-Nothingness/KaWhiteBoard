package com.whiteboard.kobo;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.CurrentBoard;
import com.whiteboard.kobo.model.Deletion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OptionsFragment extends Fragment {
        public OptionsFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_options, container, false);
            TextView textView = view.findViewById(R.id.boardName);
            textView.setText(CurrentBoard.getInstance().getBoardName());
            ImageButton back = view.findViewById(R.id.backButton);
            ConstraintLayout itemContainer1 = view.findViewById(R.id.itemContainer1);
            ConstraintLayout itemContainer2 = view.findViewById(R.id.itemContainer2);
            ConstraintLayout itemContainer3 = view.findViewById(R.id.itemContainer3);
            itemContainer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Join the board with this code: "+CurrentBoard.getInstance().getId());
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, "Share your board code for others to join.");
                    startActivity(shareIntent);

                }
            });
            itemContainer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            itemContainer3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Deletion deletion = new Deletion();
                    deletion.setBoardId(CurrentBoard.getInstance().getId());
                    apiService.apiService.deleteBoard(deletion).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getActivity(), "Board deleted successfully", Toast.LENGTH_SHORT).show();
                                Intent deletion = new Intent(getActivity(), HomeActivity.class);
                                startActivity(deletion);
                                getActivity().finish();
                            }else{
                                Toast.makeText(getActivity(), "Board deletion failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getActivity(), "Server Error!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getParentFragmentManager().popBackStack();
                }
            });
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Consume touch events to prevent them from being passed to views below
                    return true;
                }
            });
            // Inflate the layout for this fragment
            return view;
        }

    }
