package com.example.pomodoroapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_MINUTES = 120;

    private TextView timerText, titleText;
    private ImageButton btnIncrease, btnDecrease;
    private int minutes = 30; // Start time in minutes : 30
    private CountDownTimer countDownTimer;

    private CountDownTimer countPauseDownTimer;
    private long timeInMillis = minutes * 60 * 1000;

    private Button startButton;

    private Boolean isRunning = false;

    private long timeToChangeImage ; // time to change the picture is 20%

    private ShapeableImageView image ;

    private long timeInMillisForPause = (long) (5 * 60 * 1000); // 5 minutes pause time

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get the views from the layout
        titleText = findViewById(R.id.title);
        timerText = findViewById(R.id.timerText);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        startButton = findViewById(R.id.startButton);
        image = findViewById(R.id.image);
        toolbar = findViewById(R.id.toolbar);

        //set the toolbar as the action bar
        setSupportActionBar(toolbar);

        //hide the title in the toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // set the initial time
        updateTimerText(timerText, timeInMillis);

        // set the listeners for the buttons
        btnIncrease.setOnClickListener(v -> changeTime(5)); // 5
        btnDecrease.setOnClickListener(v -> changeTime(-5)); // -5

        // listener for the start button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning){
                    onButtonShowPopupWindowClick(v);
                }else{
                    start();
                }
            }
        });

    }

    // This method is called when the user clicks to add apps to the block list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // this method gets all the installed apps on the phone
    private List<String> getAllInstalledApps(){
        List<String> appNames = new ArrayList<>();
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        // Get the app names
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo packageInfo : packages) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            // Exclude system apps
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packageManager.getApplicationLabel(appInfo).toString();
                appNames.add(appName);
            }
        }

        return appNames;
    }

    // This method is called when the user clicks on the settings icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_menu) {
            // TODO: logic to block apps chosen by the user when the timer is running
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    // This method is called when the user wants to quit the app
    // It shows a dialog to confirm the exit
    @SuppressLint("ClickableViewAccessibility")
    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up, null);

        Button cancelButton = popupView.findViewById(R.id.cancelButton);
        Button giveUpButton = popupView.findViewById(R.id.giveUpButton);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation( view , Gravity.CENTER, 0, 0);

        // Set click listeners for the buttons
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());
        giveUpButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            reset();
        });

        // Dismiss the popup window when touched
        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });
    }

    // method to reset the timer, texts and buttons to their initial state
    private void reset(){

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        isRunning = false;
        //enable the increase/decrease buttons
        btnIncrease.setClickable(true);
        btnDecrease.setClickable(true);

        titleText.setText(R.string.title_start);
        startButton.setText(R.string.start);

        minutes = 0;
        timeInMillis = minutes * 60 * 1000;
        timeToChangeImage = timeInMillis;
        updateTimerText(timerText, timeInMillis);

    }

    // method to start the timer
    private void start(){

        // check if the user has set a time
        if(minutes != 0) {

            isRunning = true;

            //start the time
            countDownTimer = new CountDownTimer(timeInMillis, 1000) {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onTick(long millisUntilFinished) {
                    timeInMillis = millisUntilFinished;
                    long elapsedMillis = ((long) minutes * 60 * 1000) - millisUntilFinished;

                    // change the image when the time is almost up to simulate a growing flower
                    if (millisUntilFinished <= (0.75 * timeToChangeImage )){
                        image.setImageResource(R.drawable.torchflower0);
                    }

                    if(millisUntilFinished <= (0.5 * timeToChangeImage )){
                        image.setImageResource(R.drawable.torchflowe1);
                    }

                    if(millisUntilFinished <= (0.25 * timeToChangeImage )){
                        image.setImageResource(R.drawable.torchflower2);
                    }

                    //every 25 minutes, make a pop up for the pause
                    if(elapsedMillis >= (25 * 60 * 1000) && elapsedMillis % ( 25 * 60 * 1000 ) < 1000){
                        //make sound when is break time
                        Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pinterest_chime);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();

                        //make the pop up for the pause appear
                        LayoutInflater inflaterPause = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupViewPause = inflaterPause.inflate(R.layout.pause_pop_up, null);

                        Button cancelPauseButton = popupViewPause.findViewById(R.id.cancelPauseButton);
                        TextView timerPauseText = popupViewPause.findViewById(R.id.timerPauseText);

                        // create the popup window
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupViewPause, width, height, focusable);

                        // show the popup window
                        // which view you pass in doesn't matter, it is only used for the window tolken
                        popupWindow.showAtLocation( startButton , Gravity.CENTER, 0, 0);


                        // timer for the pause time
                        countPauseDownTimer = new CountDownTimer(timeInMillisForPause, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                timeInMillisForPause = millisUntilFinished;

                                updateTimerText(timerPauseText, timeInMillisForPause);
                            }

                            // show the time when the pause is over
                            @Override
                            public void onFinish() {
                                timerPauseText.setText("00:00");
                                // timeInMillisForPause = (long) (0.5 * 60 * 1000);
                                // updateTimerText(timerPauseText, timeInMillisForPause);
                                popupWindow.dismiss();
                            }
                        }.start();

                        // Set click listener for the cancel button
                        // if the user wants to cancel the pause
                        cancelPauseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timeInMillisForPause = (long) (0.5 * 60 * 1000);
                                // reset time pause for the next break time if exists
                                updateTimerText(timerPauseText, timeInMillisForPause);
                                countPauseDownTimer.cancel();
                                popupWindow.dismiss();
                            }
                        });

                        // Reset the onTouch listener each time the popup appears
                        popupViewPause.setOnTouchListener((v, event) -> {
                            // timeInMillisForPause = (long) (5 * 60 * 1000);
                            popupWindow.dismiss();
                            return true;
                        });

                    }

                    // change the title when the time is almost up
                    if (timeInMillis < 120000) {
                        titleText.setText(R.string.title_almost_done);
                    }

                    // update the timer text view with the remaining time every second
                    updateTimerText(timerText, timeInMillis);
                }

                // when the timer is finished
                @Override
                public void onFinish() {
                    timerText.setText("00:00");
                    reset();
                }
            }.start();

            // change the title and the start button text
            titleText.setText(R.string.title_countdown);
            startButton.setText(R.string.quit);

            //stop the increase.decrease buttons
            btnIncrease.setClickable(false);
            btnDecrease.setClickable(false);


            //pop up message if you want to exit the app and the flower is still growing
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Show the exit confirmation dialog
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Exit")
                            .setMessage("The timer is still running. Are you sure you want to exit?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Handle the exit action
                                finish();
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            };

            // Add the callback to the back button
            getOnBackPressedDispatcher().addCallback(this, callback);

        }
    }

    // method to change the time
    private void changeTime(int i) {
        minutes += i;
        if (minutes < 0) minutes = 30; // 30
        if(minutes > MAX_MINUTES) minutes = 120;
        timeInMillis = minutes * 60 * 1000;
        timeToChangeImage = timeInMillis;

        // update the timer text view with the new time
        updateTimerText(timerText, timeInMillis);
    }

    // method to update the timer text view with the remaining time
    // to be displayed in a format easy to read
    @SuppressLint("DefaultLocale")
    private void updateTimerText(TextView view, long timeInMillis) {
        int mins = (int) (timeInMillis / 1000 / 60);
        int secs = (int) ((timeInMillis / 1000) % 60);

        view.setText(String.format("%02d:%02d", mins, secs));
    }
}