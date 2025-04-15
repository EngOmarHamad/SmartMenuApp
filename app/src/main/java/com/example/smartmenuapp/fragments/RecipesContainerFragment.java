    package com.example.smartmenuapp.fragments;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.viewpager2.widget.ViewPager2;

    import com.example.smartmenuapp.R;
    import com.example.smartmenuapp.adapters.ViewPagerAdapter;
    import com.google.android.material.tabs.TabLayout;
    import com.google.android.material.tabs.TabLayoutMediator;

    import java.util.ArrayList;
    import java.util.List;

    public class RecipesContainerFragment extends Fragment {

        private final String[] tabTitles = {"Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "Beverage"};
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_recipes_container, container, false);

            TabLayout tabLayout = view.findViewById(R.id.tabLayout);
            ViewPager2 viewPager = view.findViewById(R.id.viewPager);

            List<Fragment> fragments = new ArrayList<>();
            for (String type : tabTitles) {
                fragments.add( RecipesFragment.newInstance(type));
            }

            ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), getLifecycle(), fragments);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();

            return view;
        }
    }
