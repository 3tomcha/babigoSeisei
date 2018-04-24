package site.kobatomo.babigoseisei;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.SUCCESS;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private EditText text;
    String translatedword="";
    String targetword="";
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("バビ語メーカー");
        tts = new TextToSpeech(this,this);


        setContentView(R.layout.activity_main);

        TextView button = findViewById(R.id.button);
//        new DownloadFilesTask().execute();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetword = text.getText().toString();
                new DownloadFilesTask().execute();
            }
        });

        ImageView twitter = findViewById(R.id.twitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("twitterbutton","twitterbutton");
                new TwitterShareTask();
            }
        });

        ImageView text2speeech = findViewById(R.id.text2speeech);
        text2speeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    txt2speech();
            }
        });

//        齊藤京子の画像をセットする
        WebView webview = findViewById(R.id.memberphoto);
        String memberphotourl = "http://cdn.keyakizaka46.com/images/14/eec/34a579fcf71c9d239038b2f18ff30/400_320_102400.jpg";
        webview.loadUrl(memberphotourl);

        text = findViewById(R.id.text);
        Log.d("translatedword", translatedword);
    }




    @Override
    public void onInit(int status) {
        if (SUCCESS == status) {
            //言語選択
            Locale locale = Locale.JAPAN;
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                Log.d("Error", "Locale");
            }
        } else {
            Log.d("Error", "Init");
        }
        this.txt2speech();
    }

    private void txt2speech() {
        if (0 < translatedword.length()) {
            if (tts.isSpeaking()) {
                // 読み上げ中なら停止
                tts.stop();
            }
            //読み上げられているテキストを確認
            System.out.println(translatedword);
            //読み上げ開始
            tts.speak(translatedword, TextToSpeech.QUEUE_FLUSH, null);
        }
    }



    //twitter投稿機能
    private class TwitterShareTask {
        TwitterShareTask() {
            ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(MainActivity.this);
            builder.setChooserTitle("Choose App");
            if(translatedword!="") {
                builder.setText(translatedword);
            }else{
                builder.setText(targetword);
            }
            builder.setType("text/plain");
            builder.startChooser();
        }
    }




//バビ語変換機能
    private class DownloadFilesTask extends AsyncTask<String,String,String> {

//        APIに接続し、レスポンスを得る
        public String doInBackground(String... params) {
            Log.d("TAG", "doInBackground: ");
//            String targetword = "ごはんたべたい";
            String urlStr = "http://girly.lolitapunk.jp/babigoapi/index.php?word="+targetword;
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try{
                URL url = new URL(urlStr);
                Log.d("URL",url.toString());
                con = (HttpURLConnection) url.openConnection();
                Log.d("con",con.toString());
                con.connect();
                is = con.getInputStream();
                Log.d("is",is.toString());
                result = is2String(is);
                Log.d("result",result.toString());
            }catch (MalformedURLException ex){
            }
            catch(IOException ex) {
            }

            finally {
                if(con != null) {
                    con.disconnect();  // （10）
                }
                if(is != null) {
                    try {
                        is.close();  // （11）
                    }
                    catch(IOException ex) {
                    }
                }
            }
            return result;
        }

        public void onProgressUpdate(String... progress) {
        }

//        受けとったjsonを加工する
        public void onPostExecute(String result) {
            Log.d("onPostExecute","onPostExecute");
            try{
                Log.d("try","try");
                Log.d("result",result);

                try {
                    JSONArray jsarr = new JSONArray(result);
                    translatedword = jsarr.getString(1);
                    Log.d("translatedword", translatedword);
                    text.setText(translatedword);

//                    JSONObject rootJSON = new JSONObject(result);
//                    Log.d("rootJSON", rootJSON.toString());
                }catch (Exception ex){
                    Log.d("エラー","エラー");

                }
//                JSONObject JSON1 = rootJSON.getJSONObject("translated");

//                translatedword = rootJSON.getString("translated");
//                Log.d("translatedword", translatedword);

            }catch (Exception ex){

            }

        }



    }

    /**
     * InputStreamオブジェクトを文字列に変換するメソッド。変換文字コードはUTF-8。
     *
     * @param is 変換対象のInputStreamオブジェクト。
     * @return 変換された文字列。
     * @throws IOException 変換に失敗した時に発生。
     */
    private String is2String(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        char[] b = new char[1024];
        int line;
        while(0 <= (line = reader.read(b))) {
            sb.append(b, 0, line);
        }
        return sb.toString();
    }









}

