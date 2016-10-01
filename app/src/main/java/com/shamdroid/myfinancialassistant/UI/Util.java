package com.shamdroid.myfinancialassistant.UI;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.Utils.Utils;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mohammad on 21/09/16.
 */

public class Util {

    public static int getColor(Context context, int res){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(res,context.getTheme());
        }else{
            return context.getResources().getColor(res);
        }
    }



    public static void initNavigationView(final AppCompatActivity activity) {


        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        final DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawerLayout);
        NavigationView navView = (NavigationView) activity.findViewById(R.id.navView);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();



        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.home:

                        break;
                    case R.id.history:
                        Intent intent = new Intent(activity,HistoryActivity.class);
                        activity.startActivity(intent);
                        break;
                    case R.id.manage_cats:
                        Intent intent1 = new Intent(activity,EditCategoriesActivity.class);
                        activity.startActivity(intent1);
                        break;

                }

                drawerLayout.closeDrawer(GravityCompat.START);

                if(!(activity instanceof MainActivity))
                    activity.finish();



                return true;
            }
        });

        View headerView = navView.getHeaderView(0);

        LinearLayout accountInfoContainer = (LinearLayout) headerView.findViewById(R.id.accountInfoContainer);
        TextView txtEmail = (TextView) headerView.findViewById(R.id.txtEmail);
        TextView txtAccountName = (TextView) headerView.findViewById(R.id.txtAccountName);
        CircleImageView imgProfile = (CircleImageView) headerView.findViewById(R.id.profile_image);

        accountInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(activity).setMessage(R.string.logoutMessage)
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.logOut(activity);
                                Intent intent = new Intent(activity, SignInActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        }).create();
                alertDialog.show();

            }
        });

        txtEmail.setText(SharedPreferencesManager.getEmail(activity));
        txtAccountName.setText(SharedPreferencesManager.getName(activity));


        Picasso.with(activity).load(SharedPreferencesManager.getProfileImage(activity)).into(imgProfile);


    }




}
