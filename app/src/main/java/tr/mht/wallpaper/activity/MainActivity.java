package tr.mht.wallpaper.activity;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import tr.mht.wallpaper.R;
import tr.mht.wallpaper.fragment.WallpaperListFragment;
import tr.mht.wallpaper.model.Photo;

public class MainActivity extends AppCompatActivity implements WallpaperListFragment.OnFragmentInteractionListener {
    public static final String TAG = "Main Activity";
    private OnCategoryChangedListener onCategoryChangedListener;

    public void setOnCategoryChangedListener(OnCategoryChangedListener onCategoryChangedListener) {
        this.onCategoryChangedListener = onCategoryChangedListener;
    }

    public enum Category {
        TRENDING(1),
        RECENT(2),
        NEARME(3),
        SHUFFLE(256);

        public final int id;

        private Category(int id) {
            this.id = id;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedpreferences = getSharedPreferences("interval", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Intent mServiceIntent = new Intent(getBaseContext(), WallPaperRefresh.class);
        mServiceIntent.setData(Uri.parse("2"));
        this.getApplicationContext().startService(mServiceIntent);
        editor.putString("interval", "2");
        editor.commit();
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_shuffle).setIcon(
                new IconicsDrawable(this, GoogleMaterial.Icon.gmd_shuffle).color(Color.WHITE).actionBar()
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_shuffle:
                onCategoryChangedListener.onCategoryChanged(Category.SHUFFLE.id);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public interface OnCategoryChangedListener {
        void onCategoryChanged(int category);
    }

}
