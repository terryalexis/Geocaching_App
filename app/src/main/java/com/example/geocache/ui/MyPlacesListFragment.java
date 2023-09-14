package com.example.geocache.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.geocache.databinding.FragmentMyPlacesListBinding;
import com.example.geocache.models.Geocache;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class MyPlacesListFragment extends Fragment {
    FragmentMyPlacesListBinding binding;
    NavController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyPlacesListBinding.inflate(inflater, container, false);
        controller = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("geocaches")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener((task) -> {
                    if(task.isSuccessful()) {
                        QuerySnapshot collection = task.getResult();
                        List<Geocache> geocaches = (List<Geocache>) collection.toObjects(Geocache.class);
                        recyclerView.setLayoutManager(new LinearLayoutManager(super.getContext()));
                        recyclerView.setAdapter(new MyItemRecyclerViewAdapter(geocaches));
                    }
                });
    }
}