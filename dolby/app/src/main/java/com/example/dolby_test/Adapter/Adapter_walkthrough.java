package com.example.dolby_test.Adapter;

        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;
        import androidx.fragment.app.FragmentStatePagerAdapter;

        import com.example.app.fragment.Fragment_walkthrough_one;
        import com.example.app.fragment.Fragment_walkthrough_two;
        import com.example.app.fragment.Fragment_walkthrough_three;

        public class Adapter_walkthrough extends FragmentStatePagerAdapter {
    public Adapter_walkthrough(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment_walkthrough_one tab1 = new Fragment_walkthrough_one();
                return tab1;
            case 1:
                Fragment_walkthrough_two tab2 = new Fragment_walkthrough_two();
                return tab2;
            case 2:
                Fragment_walkthrough_three tab3 = new Fragment_walkthrough_three();
                return tab3;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return 3;
    }
}