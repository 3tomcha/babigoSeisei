package site.kobatomo.babigoseisei;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView text;
    String translatedword="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DownloadFilesTask().execute();

        text = findViewById(R.id.text);
//        text.setText(translatedword);
    }

    private class DownloadFilesTask extends AsyncTask<String,String,String> {

//        APIに接続し、レスポンスを得る
        public String doInBackground(String... params) {
            Log.d("TAG", "doInBackground: ");
            String urlStr = "http://girly.lolitapunk.jp/babigoapi/index.php?word=%E3%81%94%E3%81%AF%E3%82%93%E3%81%9F%E3%81%B9%E3%81%9F%E3%81%84";
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

