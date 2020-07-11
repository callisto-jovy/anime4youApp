/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:39
 */

package net.bplaced.abzzezz.animeapp.activities.main;

import android.os.Bundle;
import android.view.ViewParent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;

public class DrawerMainMenu extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(AnimeAppMain.getInstance().getThemeId());

        setContentView(R.layout.drawer_layout);
        Toolbar toolbar = findViewById(R.id.fragmenthost_toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_anime_list).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            //Stackoverflow https://stackoverflow.com/questions/36604826/onnavigationitemselected-not-getting-called/40312398
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStackImmediate();
            boolean handled = NavigationUI.onNavDestinationSelected(menuItem, navController);
            if (handled) {
                ViewParent parent = navigationView.getParent();
                if (parent instanceof DrawerLayout) {
                    ((DrawerLayout) parent).closeDrawer(navigationView);
                }
            }
            return handled;
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
