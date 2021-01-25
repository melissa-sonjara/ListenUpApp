package com.sonjara.listenup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.github.appintro.AppIntroPageTransformerType.Depth;
import com.github.appintro.model.SliderPage;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ListenUpIntro extends AppIntro
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        int color1 = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        int color2 = ContextCompat.getColor(this, R.color.colorPrimary);

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("Welcome");
        sliderPage1.setDescription(getString(R.string.about_listenup));
        sliderPage1.setImageDrawable(R.drawable.logo_listen_up_white);
        sliderPage1.setBackgroundColor(color1);
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("About this App");
        sliderPage2.setDescription(getString(R.string.about_listenup_app));
        sliderPage2.setImageDrawable(R.drawable.intro_screenshot);
        sliderPage2.setBackgroundColor(color1);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("The Toolbar");
        sliderPage3.setDescription(getString(R.string.intro_toolbar));
        sliderPage3.setImageDrawable(R.drawable.intro_icons);
        sliderPage3.setBackgroundColor(color1);
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Getting The Latest Information");
        sliderPage4.setDescription(getString(R.string.intro_sync));
        sliderPage4.setImageDrawable(R.drawable.intro_icons_sync);
        sliderPage4.setBackgroundColor(color1);
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle("Go To Your Location");
        sliderPage5.setDescription(getString(R.string.intro_location));
        sliderPage5.setImageDrawable(R.drawable.intro_icons_location);
        sliderPage5.setBackgroundColor(color1);
        addSlide(AppIntroFragment.newInstance(sliderPage5));

        SliderPage sliderPage6 = new SliderPage();
        sliderPage6.setTitle("See a List of Service Locations");
        sliderPage6.setDescription(getString(R.string.intro_list));
        sliderPage6.setImageDrawable(R.drawable.intro_icons_list);
        sliderPage6.setBackgroundColor(color1);
        addSlide(AppIntroFragment.newInstance(sliderPage6));

        SliderPage sliderPage7 = new SliderPage();
        sliderPage7.setTitle("Go back to the Map");
        sliderPage7.setDescription(getString(R.string.intro_map));
        sliderPage7.setImageDrawable(R.drawable.intro_icons_map);
        sliderPage7.setBackgroundColor(color1);
        addSlide(AppIntroFragment.newInstance(sliderPage7));

        SliderPage sliderPage8 = new SliderPage();
        sliderPage8.setTitle("Search for Service Locations");
        sliderPage8.setDescription(getString(R.string.intro_search));
        sliderPage8.setImageDrawable(R.drawable.intro_icons_search);
        sliderPage8.setBackgroundColor(color1);
        addSlide(AppIntroFragment.newInstance(sliderPage8));

        SliderPage sliderPage9 = new SliderPage();
        sliderPage8.setTitle("Report Issues With Equitable & Safe Access");
        sliderPage8.setDescription(getString(R.string.intro_issue));
        sliderPage8.setImageDrawable(R.drawable.intro_report_issue);
        sliderPage8.setBackgroundColor(color1);
        addSlide(AppIntroFragment.newInstance(sliderPage8));
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment)
    {
        super.onSkipPressed(currentFragment);
        clearFirstRun();
        finish();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment)
    {
        super.onDonePressed(currentFragment);
        clearFirstRun();
        finish();
    }

    public void clearFirstRun()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("intro_run", true);
        editor.apply();
    }
}
