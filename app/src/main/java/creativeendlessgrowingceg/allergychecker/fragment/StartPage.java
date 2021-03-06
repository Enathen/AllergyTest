package creativeendlessgrowingceg.allergychecker.fragment;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import creativeendlessgrowingceg.allergychecker.AlgorithmAllergies;
import creativeendlessgrowingceg.allergychecker.AllergiesClass;
import creativeendlessgrowingceg.allergychecker.AllergyList;
import creativeendlessgrowingceg.allergychecker.DateAndHistory;
import creativeendlessgrowingceg.allergychecker.LanguagesAccepted;
import creativeendlessgrowingceg.allergychecker.R;
import creativeendlessgrowingceg.allergychecker.StartPageTip;
import creativeendlessgrowingceg.allergychecker.TextHandler;
import creativeendlessgrowingceg.allergychecker.billingmodule.billing.BillingManager;
import creativeendlessgrowingceg.allergychecker.billingmodule.billing.BillingProvider;
import creativeendlessgrowingceg.allergychecker.camera.OcrCaptureActivity;
import creativeendlessgrowingceg.allergychecker.design.activity.OnboardingPagerActivity;
import creativeendlessgrowingceg.allergychecker.subscription.SubscriptionsViewController;

public class StartPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , HistoryFragment.OnFragmentInteractionListener
        , LanguageFragment.OnFragmentInteractionListener
        , AboutFragment.OnFragmentInteractionListener
        , TranslateHelp.OnFragmentInteractionListener
        , E_Numbers.OnFragmentInteractionListener
        , BillingProvider {
    private static final String TAG = "StartPage";
    private static final String SHARED_PREFS_NAME = "StartPage";
    FloatingActionMenu camera;
    private FloatingActionButton flash;
    private FloatingActionButton timeSleep;
    private FloatingActionButton write;
    private FloatingActionButton focus;
    private TextView suggestions;
    private TextView allergic;
    private InterstitialAd interstitialAd;
    private BillingManager mBillingManager;
    private SubscriptionsViewController mViewController;
    private boolean Unfiltered = true;
    private AdView mAdView;
    private AdView mAdViewRectangle;
    private StartPage startPage;
    private boolean startPageBoolean = false;


    public StartPage() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBillingManager != null){
            mBillingManager.destroy();

        }

    }

    /**
     * handles too much now
     * take care of actavity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        //findViewById(R.id.textViewtip).setVisibility(View.GONE);
        startPage = this;
        startPageBoolean = false;

        mAdView = findViewById(R.id.adView);
        mAdViewRectangle = findViewById(R.id.adViewRectangle);

        new LanguageFragment().setGetLanguage(StartPage.this, Locale.getDefault().getLanguage());


        Intent intent = getIntent();
        suggestions = findViewById(R.id.ingredientsTextView);
        allergic = findViewById(R.id.textViewFoundAllergies);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#00000000"));
        setSupportActionBar(toolbar);
        for(int i = 0; i < toolbar.getChildCount(); i++)
        { View view = toolbar.getChildAt(i);

            if(view instanceof TextView) {
                TextView textView = (TextView) view;

                Typeface myCustomFont=Typeface.createFromAsset(getAssets(),"yatra.ttf");
                textView.setTypeface(myCustomFont); }


        }
        //loadInterstitial();
        ((TextView) findViewById(R.id.textViewtip)).setText(StartPageTip.getTip(getBaseContext()));
        write = findViewById(R.id.write);
        focus = findViewById(R.id.focusOn);
        camera = findViewById(R.id.menu);
        flash = findViewById(R.id.flashon);
        timeSleep = findViewById(R.id.timeSleep);
        final StartPage startPage = this;
        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(!sharedPreferences.getBoolean("FirstTimerLanguage", false)){
            new LanguageFragment().saveDefaultLanguage(this);
            SharedPreferences.Editor sharedPreferencesEditor =
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            sharedPreferencesEditor.putBoolean(
                    "FirstTimerLanguage", true);
            sharedPreferencesEditor.apply();
        }
        int timeSleepInt = sharedPreferences.getInt("timeSleep", 2);
        if (timeSleepInt == 0) {
            timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 0 + " s");
            timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep00));
        } else if (timeSleepInt == 1) {
            timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 0.25 + " s");
            timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep));

        } else if (timeSleepInt == 2) {
            timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 0.5 + " s");
            timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep05));

        } else if (timeSleepInt == 3) {
            timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 0.75 + " s");
            timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep075));

        } else if (timeSleepInt == 4) {
            timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 1 + " s");
            timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep10));

        }
        if (sharedPreferences.getBoolean("focus", true)) {
            focus.setLabelText(getString(R.string.useFocus));
            focus.setImageDrawable(getDrawable(R.drawable.focuson));

        } else {
            focus.setLabelText(getString(R.string.useNoFocus));
            focus.setImageDrawable(getDrawable(R.drawable.focusoff));

        }
        if (sharedPreferences.getBoolean("flash", false)) {
            flash.setLabelText(getString(R.string.useflash));
            flash.setImageDrawable(getDrawable(R.drawable.flashon));

        } else {
            flash.setLabelText(getString(R.string.useNoFlash));
            flash.setImageDrawable(getDrawable(R.drawable.flash));

        }
        timeSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor sharedPreferencesEditor =
                        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                int timeSleepInt = sharedPreferences.getInt("timeSleep", 1);
                Log.d(TAG, "onClick: "+ timeSleepInt);
                if (timeSleepInt == 4) {
                    timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 0 + " s");
                    timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep00));
                } else if (timeSleepInt == 0) {
                    timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 0.25 + " s");
                    timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep));

                } else if (timeSleepInt == 1) {
                    timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 0.5 + " s");
                    timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep05));

                } else if (timeSleepInt == 2) {
                    timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 0.75 + " s");
                    timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep075));

                } else if (timeSleepInt == 3) {
                    timeSleep.setLabelText(getString(R.string.timeSleep) + " " + 1 + " s");
                    timeSleep.setImageDrawable(getDrawable(R.drawable.timesleep10));

                }
                if(timeSleepInt+1> 4){
                    timeSleepInt = -1;
                }
                sharedPreferencesEditor.putInt(
                        "timeSleep", timeSleepInt+1);
                sharedPreferencesEditor.apply();
            }
        });
        focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor sharedPreferencesEditor =
                        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                if (sharedPreferences.getBoolean("focus", true)) {
                    sharedPreferencesEditor.putBoolean(
                            "focus", false);
                    sharedPreferencesEditor.apply();
                    focus.setImageDrawable(getDrawable(R.drawable.focusoff));
                    focus.setLabelText(getString(R.string.useNoFocus));

                } else {
                    focus.setImageDrawable(getDrawable(R.drawable.focuson));
                    sharedPreferencesEditor.putBoolean(
                            "focus", true);
                    sharedPreferencesEditor.apply();
                    focus.setLabelText(getString(R.string.useFocus));
                }
            }
        });
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences.Editor sharedPreferencesEditor =
                        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                if (sharedPreferences.getBoolean("flash", false)) {
                    sharedPreferencesEditor.putBoolean(
                            "flash", false);
                    sharedPreferencesEditor.apply();

                    flash.setImageDrawable(getDrawable(R.drawable.flash));
                    flash.setLabelText(getString(R.string.useNoFlash));
                } else {
                    flash.setImageDrawable(getDrawable(R.drawable.flashon));
                    sharedPreferencesEditor.putBoolean(
                            "flash", true);
                    sharedPreferencesEditor.apply();
                    flash.setLabelText(getString(R.string.useflash));
                }
            }
        });
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(startPage);
                builder.setTitle(R.string.inputIngredients);
                final EditText input = new EditText(startPage);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(startPage, StartPage.class);
                        intent.putExtra("location", input.getText().toString());
                        startActivity(intent);
                        finish();

                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                camera.close(true);
                builder.show();

            }
        });

        camera.setOnMenuButtonLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toggleFAB();
                return true;
            }

        });
        camera.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera.isOpened()) {
                    closeFAB();
                    return;
                }
                Intent intent = new Intent(startPage, OcrCaptureActivity.class);
                intent.putExtra("focus", sharedPreferences.getBoolean("focus", true));
                intent.putExtra("flash", sharedPreferences.getBoolean("flash", false));
                intent.putExtra("timeSleep", sharedPreferences.getInt("timeSleep", 2));
                startPage.startActivity(intent);

                if (!sharedPreferences.getBoolean("firstTimer", false)) {
                    startActivity(new Intent(StartPage.this, OnboardingPagerActivity.class));
                    SharedPreferences.Editor sharedPreferencesEditor =
                            PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    sharedPreferencesEditor.putBoolean(
                            "firstTimer", true);
                    sharedPreferencesEditor.apply();

                }
            }
        });

        intent = getIntent();
        String str = intent.getStringExtra("location");
        if (savedInstanceState != null) {
            findViewById(R.id.textViewtip).setVisibility(View.GONE);
            str = null;
        }
        if (str != null) {
            str = TextHandler.FixText(str);

            if (!str.equals("")) {
                DateAndHistory dateAndHistory = new DateAndHistory( startPage.getBaseContext(),str);
                dateAndHistory.saveArray();
            }
            if(!str.trim().equals("")){
                checkStringAgainstAllergies(str);

            }else{
                findViewById(R.id.adViewRectangle).setVisibility(View.VISIBLE);
            }

            //setDateStrings(dateStrings);



        } else {
            str = intent.getStringExtra("HistoryFragment");
            if (savedInstanceState != null) {
                str = null;
                findViewById(R.id.textViewtip).setVisibility(View.GONE);

            }
            if (str != null) {
                Unfiltered = intent.getBooleanExtra("Unfiltered", true);
                //suggestions.setText(str);
                checkStringAgainstAllergies(str);
            } else {

                //checkPremium();
                startPageBoolean = true;
                loadInter();


            }
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //checkNewVersion();
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    public void closeFAB() {
        write.setVisibility(View.GONE);
        flash.setVisibility(View.GONE);
        focus.setVisibility(View.GONE);
        timeSleep.setVisibility(View.GONE);

        camera.close(true);
    }

    public void toggleFAB() {
        if (write.getVisibility() != View.VISIBLE) {
            write.setVisibility(View.VISIBLE);
            flash.setVisibility(View.VISIBLE);
            focus.setVisibility(View.VISIBLE);
            timeSleep.setVisibility(View.VISIBLE);
            camera.open(true);

        } else {
            write.setVisibility(View.GONE);
            flash.setVisibility(View.GONE);
            focus.setVisibility(View.GONE);
            timeSleep.setVisibility(View.GONE);

            camera.close(true);
        }
    }

    /**
     * startup premium check
     */
    private void checkPremium() {
        Log.d(TAG, "checkPremium: ");
        mViewController = new SubscriptionsViewController();
        mBillingManager = new BillingManager(startPage, mViewController.getUpdateListener());
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBillingManager.startServiceFromStartPage();

            }
        }).start();

    }







    private void displayInterstitial() {
        interstitialAd.loadAd(new AdRequest.Builder().addTestDevice("79C8186833AA41CD2C967FE87614751A").build());
    }

    public void loadInterstitial() {
        interstitialAd = new InterstitialAd(StartPage.this);
        interstitialAd.setAdUnitId("ca-app-pub-3607354849437438~1697911164");
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    Log.d(TAG, "The interstitial wasn't loaded yet.");
                }

            }

            @Override
            public void onAdClosed() {
            }
        });

    }

    private void checkStringAgainstAllergies(String str) {
        startPageBoolean = false;
        mAdViewRectangle.setVisibility(View.GONE);
        mAdViewRectangle.pause();
        mAdView.setVisibility(View.VISIBLE);
        findViewById(R.id.textViewtip).setVisibility(View.GONE);


        (findViewById(R.id.progressBar3)).setVisibility(View.VISIBLE);
        //checkPremium();
        loadInter();
        Locale locale = new Locale(new LanguageFragment().getLanguageFromLFragment(this));
        final Locale newLocale = new Locale(locale.getLanguage());
        Locale.setDefault(newLocale);
        final Configuration config = new Configuration();
        config.locale = newLocale;

        final Resources res = this.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
        final StartPage startPage = this;
        findViewById(R.id.imageViewQuestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(startPage);
                if (Unfiltered) {
                    builder.setMessage(R.string.advancedSearchis).setPositiveButton(R.string.ok, dialogClickListener).show();
                } else {
                    builder.setMessage(R.string.unfilteredSearchIs).setPositiveButton(R.string.ok, dialogClickListener).show();

                }
            }
        });
        Log.d(TAG, "checkStringAgainstAllergies: "+ str);
        new CalcAllergy(this, allergic, (ProgressBar) findViewById(R.id.progressBar3)).execute(str);


    }

    /**
     * on back press handler
     * check if drawer is open
     * check if it is no fragment open
     * check if user is currently at analyze page.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START) || camera.isOpened()) {
            closeFAB();
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            Log.d(TAG, "onBackPressed COUNT: " + count);

            if (count == 0) {
                TextView viewById = findViewById(R.id.textViewFoundAllergies);
                CharSequence text = viewById.getText();
                if (text.length() > 0) {
                    Intent intent = new Intent(this, StartPage.class);
                    startActivity(intent);
                    finish();
                } else {

                    super.onBackPressed();
                }
                //additional code
            } else {
                if (count == 1) {
                    setTitle(R.string.app_name);
                    findViewById(R.id.textViewtip).setVisibility(View.VISIBLE);

                    TextView viewById = findViewById(R.id.textViewFoundAllergies);
                    CharSequence text = viewById.getText();
                    if (text.length() > 0) {
                        findViewById(R.id.adView).setVisibility(View.VISIBLE);
                        findViewById(R.id.textViewtip).setVisibility(View.GONE);
                        findViewById(R.id.linlaybtnadvanced).setVisibility(View.VISIBLE);
                       /* if (findViewById(R.id.linlayallergyFromWord) != null) {
                            findViewById(R.id.linlayallergyFromWord).setVisibility(View.VISIBLE);
                        }*/
                        findViewById(R.id.linLayHorizontalStartPage).setVisibility(View.VISIBLE);
                        findViewById(R.id.ingredientsTextView).setVisibility(View.VISIBLE);
                        findViewById(R.id.textViewFoundAllergies).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.adViewRectangle).setVisibility(View.VISIBLE);
                    }

                } else {
                    FragmentManager supportFragmentManager = getSupportFragmentManager();
                    String topOnStack = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.getBackStackEntryCount() - 2).getName();
                    Log.d(TAG, "onBackPressed: " + topOnStack);
                    setTitle(topOnStack);

                }
                getSupportFragmentManager().popBackStack();
            }
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String backtitle = "";

        if (id == R.id.history) {
            fragment = new HistoryFragment();
            setTitle(getString(R.string.history));
            backtitle = getString(R.string.history);
        } else if (id == R.id.languageMenu) {

            fragment = new LanguageFragment();
            setTitle(getString(R.string.language));
            backtitle = getString(R.string.language);
        } else if (id == R.id.tutorial) {
            startActivity(new Intent(this, OnboardingPagerActivity.class));
        } else if (id == R.id.about) {
            fragment = new AboutFragment();
            setTitle(getString(R.string.about));
            backtitle = getString(R.string.about);
        } else if (id == R.id.translate) {
            fragment = new TranslateHelp();
            setTitle(getString(R.string.helpTranslate));
            backtitle = getString(R.string.helpTranslate);
        } else if (id == R.id.nav_rate) {
            Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())));
            }
        } else if (id == R.id.nav_share) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = "\n" + getString(R.string.welcomeMessageInvite) + "\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + this.getPackageName() + "\n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } else if (id == R.id.nav_send) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "AllergyCheckerCEG@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.mustBeInEnglish));
            startActivity(Intent.createChooser(emailIntent, getResources().getText(R.string.sendTipsFrom)));

        }else if (id == R.id.instagram) {
            Uri uri = Uri.parse("http://instagram.com/allergychecker");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/allergychecker")));
            }

        }

        if (fragment != null) {
            fragment(fragment, backtitle);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(),"yatra.ttf");

        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("yatra.ttf" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    private void fragment(Fragment fragment, String backtitle) {
        if (fragment != null) {
            suggestions.setVisibility(View.GONE);
            allergic.setVisibility(View.GONE);
            findViewById(R.id.linlaybtnadvanced).setVisibility(View.GONE);
            closeFAB();
            mAdView.pause();
            mAdView.setVisibility(View.GONE);
            mAdViewRectangle.pause();
            mAdViewRectangle.setVisibility(View.GONE);
            findViewById(R.id.textViewtip).setVisibility(View.GONE);
        /*    if (findViewById(R.id.linlayallergyFromWord) != null) {
                findViewById(R.id.linlayallergyFromWord).setVisibility(View.INVISIBLE);
            }*/
            findViewById(R.id.linLayHorizontalStartPage).setVisibility(View.INVISIBLE);


            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.startPageFrame, fragment).addToBackStack(backtitle).commit();
            fragmentManager.executePendingTransactions();
        }
    }

    public HashMap<Integer, ImageView> getImageViewHashMap(StartPage startPage) {
        @SuppressLint("UseSparseArrays") HashMap<Integer, ImageView> imageViewHashMap = new HashMap<>();
        NavigationView navigationView = startPage.findViewById(R.id.nav_view);

        View parentView = navigationView.getHeaderView(0);
        imageViewHashMap.put(0, (ImageView) parentView.findViewById(R.id.imageViewNav1));
        imageViewHashMap.put(1, (ImageView) parentView.findViewById(R.id.imageViewNav2));
        imageViewHashMap.put(2, (ImageView) parentView.findViewById(R.id.imageViewNav3));
        imageViewHashMap.put(3, (ImageView) parentView.findViewById(R.id.imageViewNav4));
        imageViewHashMap.put(4, (ImageView) parentView.findViewById(R.id.imageViewNav5));
        imageViewHashMap.put(5, (ImageView) parentView.findViewById(R.id.imageViewNav6));
        imageViewHashMap.put(6, (ImageView) parentView.findViewById(R.id.imageViewNav7));
        imageViewHashMap.put(7, (ImageView) parentView.findViewById(R.id.imageViewNav8));

        return imageViewHashMap;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.textViewtip).setVisibility(View.GONE);
        mAdView.pause();
        mAdView.setVisibility(View.GONE);
        mAdViewRectangle.pause();
        mAdViewRectangle.setVisibility(View.GONE);


    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            TextView viewById = findViewById(R.id.textViewFoundAllergies);
            CharSequence text = viewById.getText();
            if (text.length() <= 0) {
                findViewById(R.id.textViewtip).setVisibility(View.VISIBLE);
                findViewById(R.id.adViewRectangle).setVisibility(View.VISIBLE);
                ((AdView) findViewById(R.id.adViewRectangle)).resume();
            } else {
                ((AdView) findViewById(R.id.adView)).resume();
                findViewById(R.id.adView).setVisibility(View.VISIBLE);

            }


        } else {
            findViewById(R.id.adViewRectangle).setVisibility(View.GONE);
            findViewById(R.id.adView).setVisibility(View.GONE);
        }
    }


    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isPremiumPurchased() {
        return mViewController.isPremiumPurchased();
    }


    @Override
    public boolean isGoldMonthlySubscribed() {
        return mViewController.isGoldMonthlySubscribed();
    }

    @Override
    public boolean isGoldYearlySubscribed() {
        return mViewController.isGoldYearlySubscribed();
    }

    @SuppressLint("WrongConstant")
    public void loadInter() {
        Log.d(TAG, "premiumBanner: " + startPageBoolean);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (startPageBoolean) {
                    findViewById(R.id.adView).setVisibility(View.GONE);
                    ((AdView) findViewById(R.id.adViewRectangle)).loadAd(new AdRequest.Builder()
                            .addTestDevice("85566EDEF434C46837B6373FFB555990").build());

                } else {
                    findViewById(R.id.adViewRectangle).setVisibility(View.GONE);
                    ((AdView) findViewById(R.id.adView)).loadAd(new AdRequest.Builder()
                            .addTestDevice("85566EDEF434C46837B6373FFB555990").build());
                }
            }
        });


    }

    public void premium() {
        startPage = this;

        if (!isPremiumPurchased() || true) {

            if (startPageBoolean) {

                findViewById(R.id.adView).setVisibility(View.GONE);
                ((AdView) findViewById(R.id.adViewRectangle)).loadAd(new AdRequest.Builder()
                        .addTestDevice("85566EDEF434C46837B6373FFB555990").build());

            } else {
                findViewById(R.id.adViewRectangle).setVisibility(View.GONE);
                ((AdView) findViewById(R.id.adView)).loadAd(new AdRequest.Builder()
                        .addTestDevice("85566EDEF434C46837B6373FFB555990").build());
            }


            Set<String> set = new HashSet<>();
            Set<Locale> setToDelete = new HashSet<>();
            for (Locale locale : new LanguageFragment().getCategories(startPage)) {
                if (locale == Locale.getDefault() || locale.getLanguage().equals("en")) {
                    set.add(locale.getLanguage());
                } else {
                    setToDelete.add(locale);
                }
            }
            new LanguageFragment().setCategories(startPage, set, setToDelete);

        } else {
            findViewById(R.id.adView).setVisibility(View.GONE);
            findViewById(R.id.adViewRectangle).setVisibility(View.GONE);
        }

    }

    public void clearFAB(View view) {
        if (camera.isOpened()) {
            closeFAB();

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("Unfiltered", true);
    }

    private class CalcAllergy extends AsyncTask<String, Integer, ArrayList<AllergiesClass>> {
        private final AlgorithmAllergies algorithmAllergies;
        private StartPage mContext;
        private TextView textView;
        private ProgressBar viewById;
        private String stringToCheck;
        private ArrayList<AllergyList.E_Numbers> allfoundENumbers = new ArrayList<>();
        private TreeMap<Integer, TreeSet<String>> hashSetAllStrings;
        private TreeSet<String> strings;
        private String outputString;


        CalcAllergy(StartPage context, TextView textView, ProgressBar viewById) {
            mContext = context;

            this.textView = textView;
            this.viewById = viewById;
            algorithmAllergies = new AlgorithmAllergies();

        }

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Do something like display a progress bar
        }

        // This is run in a background thread

        @Override
        protected ArrayList<AllergiesClass> doInBackground(String... params) {
      /*      // get the stringToCheck from params, which is an array
            stringToCheck = params[0];
            hashSetAllStrings = new TreeMap<>();

            hashSetAllStrings = algorithmAllergies.FixStringAllStrings(params[0].split("\\s+"));
            HashSet<String> hashSetToCheckLast = algorithmAllergies.FixStringAllStringsToCheckLast(params[0].split("\\s+"));
            String[] stringToCheckENumbers = stringToCheck.split("\\s+");
            ArrayList<Locale> listOfLanguages = new LanguageFragment().getCategories(mContext);
            //listOfLanguages.add(Locale.getDefault());
            outputString = "";
            if(listOfLanguages.isEmpty()){
                outputString = getString(R.string.noLanguageSelected) + "\n\n";
            }

            //HashSet<Integer> hashSetFromOtherClass = new LoadUIAllergies().getAllergies(mContext);

            @SuppressLint("UseSparseArrays") HashMap<String, AllergiesClass> allergies = new HashMap<>();

            int length = 0;
            int counter = 0;
            int i = 0;
            ArrayList<AllergiesClass> allFoundAllergies = new ArrayList<>();
            for (int id : hashSetFromOtherClass) {
                publishProgress(hashSetFromOtherClass.size(), counter);
                length = algorithmAllergies.translateAllAllergies(id, allergies, listOfLanguages, StartPage.this);

                if (i % 2 == 0) {
                    counter++;
                }
                i++;

            }

            if (allergies.isEmpty()){
                if(!listOfLanguages.isEmpty()){

                    outputString = outputString.concat(getString(R.string.noAllergies)+ "\n\n");
                }
            }
            allFoundAllergies.addAll(algorithmAllergies.bkTree(length, hashSetAllStrings, allergies));


            long start = System.currentTimeMillis();
            counter = stringToCheckENumbers.length / 2;
            i = 0;
            ArrayList<AllergyList.E_Numbers> eNumbersArrayList = new ArrayList<>();

            AllergyList allergyList = new AllergyList(getBaseContext());
            eNumbersArrayList = allergyList.getArrayListE_Numbers();

            for (int j = 0; j < stringToCheckENumbers.length; j++) {

                if (i % 2 == 0) {

                    publishProgress(stringToCheckENumbers.length, counter);
                    counter++;
                }
                i++;
                if (j + 1 < stringToCheckENumbers.length && stringToCheckENumbers.length != 1) {
                    String number = stringToCheckENumbers[j] + stringToCheckENumbers[j + 1].replaceAll("\\D+", "");
                    if (number.length() > 2 && stringToCheckENumbers[j].compareToIgnoreCase("e") == 0) {

                        algorithmAllergies.checkFullStringEnumbers(stringToCheckENumbers[j] + stringToCheckENumbers[j + 1], eNumbersArrayList, allfoundENumbers);
                    }
                }
                String number = stringToCheckENumbers[j].replaceAll("\\D+", "");
                if (number.length() > 2) {
                    algorithmAllergies.checkFullStringEnumbers(stringToCheckENumbers[j], eNumbersArrayList, allfoundENumbers);
                }


            }
            counter = 0;
            for (String s : hashSetToCheckLast) {

                if (Unfiltered) {
                    algorithmAllergies.checkFullString(s, allergies, allFoundAllergies);
                }
                publishProgress(hashSetToCheckLast.size(), counter);
                counter++;
            }

            long stop = System.currentTimeMillis();
            Log.d(TAG, "TIME: " + (stop - start));
            Collections.sort(allFoundAllergies);
            Collections.sort(allfoundENumbers);
            strings = new TreeSet<>();
            for (TreeSet<String> stringTreeSet : hashSetAllStrings.values()) {
                strings.addAll(stringTreeSet);
            }
            return allFoundAllergies;*/
        return null;
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            double i = ((double) values[1] / (double) values[0]) * 100;

            viewById.setProgress((int) i);
            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(ArrayList<AllergiesClass> allergiesClass) {
            viewById.setVisibility(View.INVISIBLE);
            super.onPostExecute(allergiesClass);

            final LinearLayout linearLayout = findViewById(R.id.linLayHorizontalStartPage);
            final HashMap<String, Lin> linearLayoutHashMap = new HashMap<>();
            for (final AllergiesClass allergiesForEachInteger : allergiesClass) {


                if (!linearLayoutHashMap.containsKey(allergiesForEachInteger.getMotherAllergy())) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout newlinearLayout = (LinearLayout) inflater.inflate(R.layout.linlayoutstartpagevertical2, null);
                    ((TextView) newlinearLayout.findViewById(R.id.textViewAllergy)).setText(TextHandler.cutFirstWord(getString(allergiesForEachInteger.getId())));
                    LinearLayout parentLin = (LinearLayout) inflater.inflate(R.layout.linlayoutstartpageverticaltrue, null);
                    parentLin.addView(newlinearLayout);
                    linearLayout.addView(parentLin);
                    ArrayList<LinearLayout> l = new ArrayList<>();
                    linearLayoutHashMap.put(allergiesForEachInteger.getMotherAllergy(), new Lin(newlinearLayout, l, parentLin));
                    //Log.d(TAG, "onPostExecute: size" + linearLayoutHashMap.get(allergiesForEachInteger.getMotherAllergy()).linearLayoutArrayList.size());


                }
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout newlinearLayout = (LinearLayout) inflater.inflate(R.layout.linlayoutstartpagevertical, null);
                ((ImageView) newlinearLayout.findViewById(R.id.imageViewHorStartPage)).setImageResource(LanguagesAccepted.getFlag(allergiesForEachInteger.getLanguage()));
                ((TextView) newlinearLayout.findViewById(R.id.textViewAllergy)).setText(TextHandler.cutFirstWord(allergiesForEachInteger.getNameOfIngredient()));
                ((TextView) newlinearLayout.findViewById(R.id.textViewFoundFromWord)).setText(TextHandler.capitalLetter(allergiesForEachInteger.getNameOfWordFound()));
                newlinearLayout.findViewById(R.id.arrowLeft).setVisibility(View.INVISIBLE);
                linearLayoutHashMap.get(allergiesForEachInteger.getMotherAllergy()).linearLayoutArrayList.add(newlinearLayout);

            }
            for (final String string : linearLayoutHashMap.keySet()) {
                if (linearLayoutHashMap.get(string).linearLayoutArrayList.isEmpty()) {
                    linearLayoutHashMap.get(string).parentLin.findViewById(R.id.arrowLeft).setVisibility(View.INVISIBLE);
                }
                ((TextView) linearLayoutHashMap.get(string).parentLin.findViewById(R.id.textViewAllergy)).
                        append(" " + (linearLayoutHashMap.get(string).linearLayoutArrayList.size()));

                linearLayoutHashMap.get(string).linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkIfAlreadyShown(linearLayoutHashMap.get(string).parentLin.findViewById(R.id.arrowLeft))) {
                            if (!linearLayoutHashMap.get(string).linearLayoutArrayList.isEmpty()) {
                                for (LinearLayout newLin : linearLayoutHashMap.get(string).linearLayoutArrayList) {
                                    linearLayoutHashMap.get(string).parentLin.addView(newLin);
                                }
                            }
                        } else {
                            if (!linearLayoutHashMap.get(string).linearLayoutArrayList.isEmpty()) {
                                for (LinearLayout newLin : linearLayoutHashMap.get(string).linearLayoutArrayList) {
                                    if (linearLayoutHashMap.get(string).parentLin != newLin) {
                                        linearLayoutHashMap.get(string).parentLin.removeView(newLin);
                                    }
                                }
                            }
                        }

                    }

                });
            }
            if (!allfoundENumbers.isEmpty()) {
                linearLayout.addView(new TextView(getBaseContext()));
                TextView textViewOverHead = new TextView(getBaseContext());
                textViewOverHead.setTextSize(24);
                textViewOverHead.setTextColor(Color.WHITE);
                textViewOverHead.setText("E numbers found:");
                linearLayout.addView(textViewOverHead);
                linearLayout.addView(new TextView(getBaseContext()));

                for (final AllergyList.E_Numbers allfoundENumber : allfoundENumbers) {
                    TextView textView = new TextView(getBaseContext());
                    textView.setText(Html.fromHtml("<u>" + allfoundENumber.getId() + " : " + allfoundENumber.getName() + "</u>"));
                    textView.setTextColor(Color.parseColor("#19b3ad"));
                    textView.setTextSize(20);
                    linearLayout.addView(textView);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Fragment fragment = new E_Numbers();
                            Bundle b = new Bundle();
                            b.putString("URL", allfoundENumber.getUrl());
                            b.putString("ENUMBER", allfoundENumber.getInformation());
                            b.putString("NAME", allfoundENumber.getName());
                            b.putInt("US", allfoundENumber.getUS());
                            b.putInt("EU", allfoundENumber.getEU());
                            fragment.setArguments(b);
                            fragment(fragment, getString(R.string.eNumbers));
                        }
                    });
                }
            }

            if (!allergiesClass.isEmpty()) {

                outputString = outputString.concat("\n" + getString(R.string.dontUse) + "\n");
                outputString = outputString.concat("\n" + getString(R.string.mightContainOther) + "\n");
                outputString = outputString.concat(getString(R.string.scannedTextBelow) + "\n");
                textView.setTextColor(Color.parseColor("#f23931"));
                textView.setText(outputString);
               // findViewById(R.id.linlayallergyFromWord).setVisibility(View.VISIBLE);
            } else {
                outputString = outputString.concat("\n" + getString(R.string.youCanUse) + "\n");
                outputString = outputString.concat("\n" + getString(R.string.mightContainAllergies) + "\n");
                outputString = outputString.concat(getString(R.string.scannedTextBelow) + "\n");
                textView.setTextColor(Color.GREEN);
                textView.setText(outputString);
                //((LinearLayout) findViewById(R.id.linLayStartPage)).removeView(findViewById(R.id.linlayallergyFromWord));

            }

            allergic.setText(outputString);


            if (Unfiltered) {
                ((Button) findViewById(R.id.buttonAdvancedSearch)).setText(getString(R.string.regularSearch));


            } else {
                ((Button) findViewById(R.id.buttonAdvancedSearch)).setText(getString(R.string.advancedSearch));

            }

            ((Button) findViewById(R.id.buttonAdvancedSearch)).getBackground().setColorFilter(0xFF19b3ad, PorterDuff.Mode.MULTIPLY);
            (findViewById(R.id.linlaybtnadvanced)).setVisibility(View.VISIBLE);

            findViewById(R.id.buttonAdvancedSearch).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(StartPage.this, StartPage.class);
                    if (Unfiltered) {
                        intent.putExtra("Unfiltered", false);
                    } else {
                        intent.putExtra("Unfiltered", true);
                    }
                    intent.putExtra("HistoryFragment", stringToCheck);
                    startActivity(intent);
                    finish();
                }
            });
            findViewById(R.id.textViewtip).setVisibility(View.GONE);
            //suggestions.setText("");

            char charCurrent = 'a';
            for (String string : strings) {
                //Log.d(TAG, "onPostExecute: "+ string);
                if (string.length()>1) {
                    if (!Character.isDigit(string.charAt(0))) {
                        if(string.charAt(0)!= charCurrent){
                            charCurrent = string.charAt(0);
                            suggestions.append("\n");
                        }
                        suggestions.append(string + "\n");
                    }
                }

            }
            findViewById(R.id.adViewRectangle).setVisibility(View.GONE);
            // Do things like hide the progress bar or change a TextView
        }

        private class Lin {
            private LinearLayout linearLayout;
            private ArrayList<LinearLayout> linearLayoutArrayList;
            private LinearLayout parentLin;

            public Lin(LinearLayout linearLayout, ArrayList<LinearLayout> linearLayoutArrayList, LinearLayout parentLin) {

                this.linearLayout = linearLayout;
                this.linearLayoutArrayList = linearLayoutArrayList;
                this.parentLin = parentLin;
            }


        }
    }

    /**
     *
     * @param v
     * @return
     */
    public boolean checkIfAlreadyShown(View v) {
        if (v.getRotation() == 0) {
            v.setRotation(180);
            return true;
        } else {
            v.setRotation(0);
            return false;


        }

    }
}
