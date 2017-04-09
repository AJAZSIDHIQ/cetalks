package techub.cet.cetalks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class web extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        WebView webView=(WebView) findViewById(R.id.webView);

        webView.loadUrl("https://cetalks.in");
    }
}
