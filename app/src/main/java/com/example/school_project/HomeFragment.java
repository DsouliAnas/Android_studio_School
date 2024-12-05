package com.example.school_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;

    // Default constructor for the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ViewPager2
        viewPager = view.findViewById(R.id.viewPager);

        // Image URLs for the carousel
        String[] imageUrls = {
                "https://i.pinimg.com/736x/1a/28/c1/1a28c1c2234a048dbb224762cdfcccc0.jpg",
                "https://i.pinimg.com/736x/c2/6a/80/c26a80278d6a831fa2f256b508f7fb7c.jpg",
                "https://i.pinimg.com/736x/a4/fd/91/a4fd914a3b4e778f2702e3db41f1a92e.jpg",
                "https://i.pinimg.com/736x/20/8b/b9/208bb979fc5c07dc760b0cd812d6168a.jpg",
                "https://i.pinimg.com/736x/40/7f/bd/407fbdf41dc59750cc8a65d4d4424970.jpg",
                "https://i.pinimg.com/736x/08/89/a2/0889a2f4190df4edbb79bb5d31424657.jpg",
                "https://i.pinimg.com/736x/19/b5/28/19b528809f8daad3f64d06568f547b49.jpg",
                "https://i.pinimg.com/736x/85/76/20/857620842f8d1a494cf5fda74335c232.jpg"
        };

        // Set up the ImageAdapter with the image URLs
        ImageAdapter imageAdapter = new ImageAdapter(imageUrls);
        viewPager.setAdapter(imageAdapter);

        // Optional: Auto-scroll functionality every 3 seconds
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int nextItem = currentItem + 1 < imageUrls.length ? currentItem + 1 : 0;
                viewPager.setCurrentItem(nextItem, true);
                viewPager.postDelayed(this, 3000);  // Continue scrolling every 3 seconds
            }
        }, 3000);

        return view;
    }
}
