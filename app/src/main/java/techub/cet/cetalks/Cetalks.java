package techub.cet.cetalks;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ohoussein.playpause.PlayPauseView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Cetalks extends AppCompatActivity{
    PlayPauseView view;
    TextView songName;
    IntentFilter intentFilter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cetalks);
        intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.ACTION.CHANGE_STATE);
        intentFilter.addAction(Constants.ACTION.LOADED);
        intentFilter.addAction(Constants.ACTION.SONG_CHANGE);
        progressDialog=new ProgressDialog(Cetalks.this);
        PlayPauseView view=(PlayPauseView)findViewById(R.id.play_pause_view);
        songName=(TextView)findViewById(R.id.marque_scrolling_text);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
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
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}

