package kivaaz.com.firebasedownloadupload;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import kivaaz.com.firebasedownloadupload.Fragments.FileviewerFragment;
import kivaaz.com.firebasedownloadupload.Fragments.UploadFragment;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class NavigationDrawer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment current = new UploadFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frame, current).commit();

        final DuoDrawerLayout drawer = findViewById(R.id.drawer_layout);
        DuoDrawerToggle toggle = new DuoDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        View menuView = drawer.getMenuView();

        LinearLayout nav_upload = menuView.findViewById(R.id.nav_upload);
        LinearLayout nav_gallery = menuView.findViewById(R.id.nav_gallery);

        toggle.syncState();

        nav_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,  new UploadFragment()).commit();
                drawer.closeDrawer();
            }
        });
        nav_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,  new FileviewerFragment()).commit();
                drawer.closeDrawer();
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
