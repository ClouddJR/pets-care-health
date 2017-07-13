package com.clouddroid.pettypetscarehealth.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.clouddroid.pettypetscarehealth.R;
import com.clouddroid.pettypetscarehealth.dialogs.DialogAnimalPicker;
import com.clouddroid.pettypetscarehealth.fragments.InfoFragment;
import com.github.clans.fab.FloatingActionButton;

public class MainActivity extends AppCompatActivity {


  @BindView(R.id.layout_drawer)
  DrawerLayout mDrawerLayout;
  @BindView(R.id.toolbar)
  Toolbar mToolbar;
  @BindView(R.id.navigation_view)
  NavigationView mNavigationView;
  @BindView(R.id.spinner_animals)
  Spinner spinner;
  @BindView(R.id.fab_animal)
  FloatingActionButton fabAnimal;

  private FragmentManager fragmentManager;
  private FragmentTransaction fragmentTransaction;
  private Fragment activeFragment;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_drawer_main);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);

    setUpHamburgerIcon();
    setUpSpinner();
    setupDrawerLayout(mNavigationView);
    removeTitle();

    fabAnimal.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        DialogAnimalPicker dialog = new DialogAnimalPicker(MainActivity.this,R.style.AnimalDialog);
        dialog.show();
      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.

    //noinspection SimplifiableIfStatement

    return super.onOptionsItemSelected(item);
  }

  private void setUpHamburgerIcon() {
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    mDrawerLayout.setDrawerListener(toggle);
    toggle.syncState();
  }

  private void setupDrawerLayout(final NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (!item.isChecked()) {
          item.setChecked(true);
          switch (item.getItemId()) {
            case R.id.menu_nav_info:
              activeFragment = new InfoFragment();
              fragmentTransaction.add(R.id.fragment_container, activeFragment);
              fragmentTransaction.commit();
              break;
          }
        }
        mDrawerLayout.closeDrawers();
        return true;
      }
    });
  }

  private void removeTitle() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle("");
    }
  }

  private void setUpSpinner() {
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.animals_array, R.layout.spinner_animal_item);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TextView textView = (TextView) findViewById(R.id.textview_spinner);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(20);
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
  }


}
