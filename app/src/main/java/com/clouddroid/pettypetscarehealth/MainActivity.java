package com.clouddroid.pettypetscarehealth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


  @BindView(R.id.layout_drawer)
  DrawerLayout mDrawerLayout;
  @BindView(R.id.toolbar)
  Toolbar mToolbar;
  @BindView(R.id.navigation_view)
  NavigationView mNavigationView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_drawer_main);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);

    setUpHamburgerIcon();
    setupDrawerLayout(mNavigationView);

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
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

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
        item.setChecked(true);
        mDrawerLayout.closeDrawers();
        return true;
      }
    });
  }
}
