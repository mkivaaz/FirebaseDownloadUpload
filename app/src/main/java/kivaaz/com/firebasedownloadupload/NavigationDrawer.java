package kivaaz.com.firebasedownloadupload;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kivaaz.com.firebasedownloadupload.Fragments.GalleryFragment;
import kivaaz.com.firebasedownloadupload.Fragments.UploadFragment;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class NavigationDrawer extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment current = new GalleryFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frame, current).commit();
        getSupportActionBar().setTitle("Home");

        final DuoDrawerLayout drawer = findViewById(R.id.drawer_layout);
        DuoDrawerToggle toggle = new DuoDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        final SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        drawer.setDrawerListener(toggle);
        View menuView = drawer.getMenuView();
        String storedEmail = sharedpreferences.getString("emailKey",null);

        LinearLayout nav_upload = menuView.findViewById(R.id.nav_upload);
        LinearLayout nav_gallery = menuView.findViewById(R.id.nav_gallery);
        LinearLayout nav_logout = menuView.findViewById(R.id.logoutBtn);
        TextView userEmail = menuView.findViewById(R.id.useremail);
        userEmail.setText(storedEmail);

        toggle.syncState();

        nav_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,  new UploadFragment()).commit();
                getSupportActionBar().setTitle("Upload File");
                drawer.closeDrawer();
            }
        });
        nav_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,  new GalleryFragment()).commit();
                getSupportActionBar().setTitle("Gallery");
                drawer.closeDrawer();
            }
        });
        nav_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer();

                sharedpreferences.edit().clear().commit();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
