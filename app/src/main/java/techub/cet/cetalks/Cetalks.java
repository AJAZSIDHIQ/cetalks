package techub.cet.cetalks;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import techub.cet.cetalks.CircularSeekBar.OnCircularSeekBarChangeListener;
import com.squareup.picasso.Picasso;
import com.ohoussein.playpause.PlayPauseView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Cetalks extends AppCompatActivity{
    PlayPauseView view;
    TextView songName;
    IntentFilter intentFilter;
    ProgressDialog progressDialog;
    CircularSeekBar volumeControl;
    AudioManager audioManager;
    ImageView songImage;
    Button aboutview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_cetalks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.ACTION.CHANGE_STATE);
        intentFilter.addAction(Constants.ACTION.LOADED);
        intentFilter.addAction(Constants.ACTION.SONG_CHANGE);

        progressDialog=new ProgressDialog(Cetalks.this);
        view=(PlayPauseView)findViewById(R.id.play_pause_view);
        aboutview=(Button)findViewById(R.id.about);
        songName=(TextView)findViewById(R.id.marque_scrolling_text);
        songImage=(ImageView)findViewById(R.id.songimage);
        volumeControl = (CircularSeekBar) findViewById(R.id.circularSeekBar);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        volumeControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        songName.setSelected(true);
        songName.setHorizontallyScrolling(true);
        if(RadioService.iSRunning)
            view.toggle();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkUtil.isNetworkAvailable(Cetalks.this)){
                    Snackbar.make(findViewById(R.id.content_cetalks),"Network Unavailable",Snackbar.LENGTH_LONG).show();
                    return;
                }
                Intent service = new Intent(Cetalks.this, RadioService.class);
                if (!RadioService.iSRunning) {
                    service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    progressDialog.show();
                    RadioService.iSRunning = true;
                } else {
                    service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                    RadioService.iSRunning = false;

                }
                startService(service);
            }
        });



        volumeControl.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int i, boolean b) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar circularSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar circularSeekBar) {

            }
        });
        aboutview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkUtil.isNetworkAvailable(Cetalks.this)){
                    Snackbar.make(findViewById(R.id.content_cetalks),"Network Unavailable",Snackbar.LENGTH_LONG).show();
                    return;
                }
                Intent service = new Intent(Cetalks.this, web.class);
                startActivity(service);
            }
        });


    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
//        Intent service = new Intent(Cetalks.this, RadioService.class);
//        service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
//        startService(service);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }
    private BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Constants.ACTION.LOADED))
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            if(intent.getAction().equals(Constants.ACTION.CHANGE_STATE)){
                view.toggle();
            }
            if(intent.getAction().equals(Constants.ACTION.SONG_CHANGE)){
                songName.setText(intent.getStringExtra("song"));
                //Picasso.with(context).load(intent.getStringExtra("songimage")).into(songImage);
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==R.id.about){
            if(!NetworkUtil.isNetworkAvailable(Cetalks.this)){
                Snackbar.make(findViewById(R.id.content_cetalks),"Network Unavailable",Snackbar.LENGTH_LONG).show();
                return true;
            }
            Intent startWeb = new Intent(this,web.class);
            startActivity(startWeb);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

