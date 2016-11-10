package com.example.pelu.qrcode;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import static android.widget.Toast.LENGTH_SHORT;
import static com.example.pelu.qrcode.R.id.albaran;
import static com.example.pelu.qrcode.R.id.camion;
import static com.example.pelu.qrcode.R.id.conexion;
import static com.example.pelu.qrcode.R.id.matricula_remolque;



public class ReaderActivity extends AppCompatActivity {


    private Button scan_btn, borrar;

    private TextView albaranes, matriculaCamion, matriculaRemolque, tvIsConnected;

    final Activity activity = this;

    private GoogleApiClient client;


                /*
    variables para la conexion
             */

    private IntentFilter mNetworkStateChangedFilter;
    private BroadcastReceiver mNetworkStateIntentReceiver;
    private String mTypeName = "Unknown";
    private String mSubtypeName = "Unknown";
    private boolean mAvailable = false;



                    /*
                VALIDACIONES
                    */

    public boolean  Validaciones(){



        if (    albaranes.getText().toString().equals("") ||
                matriculaCamion.getText().toString().equals("") ||
                matriculaRemolque.getText().toString().equals("")){

            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show();

            return false;

        }

        else{
            return true;
        }

    }



                            /*
    comprueba si hay conexion y los muestra en pantalla
                        */



    private void updateScreen() {




        if(mAvailable == true){

            tvIsConnected.setBackgroundColor(Color.GREEN);
            tvIsConnected.setText("Conectado");

        }

        else{
            tvIsConnected.setBackgroundColor(Color.RED);
            tvIsConnected.setText("No hay conexion");
        }


    }

                                /*
                    Vuelve a ejecutar lo que
                    paramos en el metodo Pause
                    */

    @Override
    protected void onResume() {
        Log.d("resume", "onResume");
        super.onResume();
        registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);
        unregisterReceiver(mNetworkStateIntentReceiver);
        registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);
    }

                            /*
            Para los procesos que nosostros queramos
            como texturas etc.
            Se ejecuta cuando cambiamos de actividad
                            */

    @Override
    protected void onPause() {
        Log.d("pause", "onPause");
        super.onPause();
        unregisterReceiver(mNetworkStateIntentReceiver);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_reader); // --> IMPORTANTE QUE ESTE AL INICIO DEL MÉTODO



        albaranes = (TextView) findViewById(albaran);
        matriculaCamion = (TextView) findViewById(camion);
        matriculaRemolque = (TextView) findViewById(matricula_remolque);
        tvIsConnected = (TextView) findViewById(conexion);




                        /*
        Parte de la gestion de la conexion
                        */

        mNetworkStateChangedFilter = new IntentFilter();
        mNetworkStateChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);


        mNetworkStateIntentReceiver = new BroadcastReceiver() {


                    /*
                    Escucha ......
                     */
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

                    mTypeName = info.getTypeName();
                    mSubtypeName = info.getSubtypeName();
                    mAvailable = info.isAvailable();

                    Log.i("tipod de conexion", "Network Type: " + mTypeName
                            + ", subtype: " + mSubtypeName
                            + ", available: " + mAvailable);

                    updateScreen();

                    if(mAvailable == true){


                        new PostAsincrono().execute("http://192.168.1.171:8084/gsRest/sincro/escaneoCarga?idAndroid=34");
                    }



                }
            }
        };


        /*
                Llamada al metodo asíncrono por GET



        new GetAsincrono().execute("http://192.168.1.171:8084/gsRest/sincro/escaneoCarga?idAndroid=34");
         */


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        scan_btn = (Button) findViewById(R.id.scan_btn); // obtiene el noton de scan para mostrarlo //


        scan_btn.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) // version minima de android //
            @Override
            public void onClick(View v) {

                             /*
                valida los datos y ejecuta el scanner
                             */
                if(Validaciones()){

                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.DATA_MATRIX_TYPES); // tipod de codigos, se puede cambiar //
                    integrator.setPrompt("Scan");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.initiateScan();


                }

                }
    });

        borrar = (Button) findViewById(R.id.borrar_btn);

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                albaranes.setText("");
                matriculaRemolque.setText("");
                matriculaCamion.setText("");


            }
        });


    }



    @RequiresApi(api = Build.VERSION_CODES.N) // requerido para la fecha y hora //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        final AyudaBD ayudabd = new AyudaBD(getApplicationContext());


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        String scanContent = result.getContents();



                                            /*
                            variable de escritura de la base de datos
                                            */

        SQLiteDatabase db = ayudabd.getWritableDatabase();


        String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new Date()).toString());


                                                /*

                               INSERCION DATOS TABLA  CABECERA (CAMBIAR A LA CLASE DataBase)

                                                 */

        ContentValues valores = new ContentValues();

        valores.put(AyudaBD.DatosTabla.COLUMNA_ID, albaranes.getText().toString());
        valores.put(AyudaBD.DatosTabla.COLUMNA_MATRICULA1, matriculaCamion.getText().toString());
        valores.put(AyudaBD.DatosTabla.COLUMNA_MATRICULA2, matriculaRemolque.getText().toString());
        valores.put(AyudaBD.DatosTabla.FECHA, date);
        valores.put(AyudaBD.DatosTabla.ENVIADO, "no");


        ContentValues valores2 = new ContentValues();


        valores2.put(AyudaBD.Lineas.LINEA_PEDIDO, scanContent);
        valores2.put(AyudaBD.Lineas.ENVIADO, "no");
        valores2.put(AyudaBD.Lineas.ID_ALBARAN, albaranes.getText().toString());


                            /*
        Lamma al metodo de la clase DataBase para insertar los datos
                             */

        DataBase.Insert(db, AyudaBD.DatosTabla.NOMBRE_TABLA, valores);
        DataBase.Insert(db, AyudaBD.Lineas.NOMBRE_TABLA, valores2);


        Toast.makeText(getApplicationContext(), "Se guardo el dato: ", Toast.LENGTH_LONG).show();


        db.close();


                                                /*
                                TRATAMIENTO DEL RESULTADO DE LOS ESCANEOS
                                                */


        if (result == null) {
            Toast.makeText(this, "no hay datos", Toast.LENGTH_LONG).show();
        }
        if (result != null) {

            if (result.getContents() == null) {

                /*
                cuando volvemos de un scaneo a la pantalla principal
                 */

                Toast.makeText(this, "Has cancelado el scanner", LENGTH_SHORT).show();
            } else {

                /*
                FECHA Y HORA DEL ESCANEO --> eliminar para que no salga en la pantalla
                 */


            }
        } else {


            super.onActivityResult(requestCode, resultCode, data);

        }

    }


                                /*
    recibe el objeto jsoon resultado de la query a la base de datos
                                 */

    public void postData(String jota) throws JSONException {



            JSONObject json = new JSONObject();

            try {
                json.put("id", 22);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                json.put("texto", jota);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        String text = "";
        BufferedReader reader = null;


        final AyudaBD ayudabd = new AyudaBD(getApplicationContext());

        SQLiteDatabase db2 = ayudabd.getReadableDatabase();
        //envio de datos

        try {


                    URL url = new URL("http://192.168.1.171:8084/gsRest/sincro/escaneoCarga?idAndroid=34");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");


                        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                        wr.write(json.toString());
                        wr.flush();
                        wr.close();


            // respuesta del servidor

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));



            // update a SI de los datos enviados

            DataBase.Update(db2);


        } catch (Exception e) {




        } finally {


            try {

                reader.close();

            } catch (Exception e) {


            }
        }

    }

                        /*
     CLASE PARA LA CONEXION CON EL SERVICIO REST POST
                        */


    public class PostAsincrono extends AsyncTask<String, Void, String> {

        final AyudaBD ayudabd = new AyudaBD(getApplicationContext());


        @Override
        protected String doInBackground(String... urls) {

            SQLiteDatabase db2 = ayudabd.getReadableDatabase();


            try {


                if(mAvailable == true){

                    // introducir el objeto json de la base de datos

                    postData(DataBase.DatosTabla(db2));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


    }


                        /*
     CLASE PARA LA CONEXION CON EL SERVICIO REST GET (NO se esta usando)
                        */

    public class GetAsincrono extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject json = new JSONObject(result);
                tvIsConnected.setText(json.toString(1));
                Toast.makeText(getBaseContext(), "exito!", LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();


            }

        }

        @Override
        protected String doInBackground(String... urls) {


            return GET(urls[0]);


        }

    }


                    /*
    OBTIENE LOS DATOS EL SERVIDOR GET
                    */

        public String GET(String urls) {



            InputStream inputStream = null;
            String result = "";

            try {

                // creamos el cliente hhtp //

                URL url = new URL(urls);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("GET"); // metodo de la peticion //
                connection.setDoInput(true);
                connection.connect();


                if (connection.getResponseCode() == 200) {

                    tvIsConnected.setBackgroundColor(Color.GREEN);
                    tvIsConnected.setText("Conectado");

                } else {

                    tvIsConnected.setBackgroundColor(Color.RED);
                    tvIsConnected.setText("No Hay Conexion");
                    tvIsConnected.setText("No conectado");

                }


                inputStream = connection.getInputStream();

                if (inputStream != null) {

                    result = convertInputStreamToString(inputStream);
                } else {
                    result = "no funciona";
                }


            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            return result;
        }


        public static String convertInputStreamToString(InputStream inputStream) throws IOException {


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {

                result += line;

            }

            inputStream.close();
            return result;

        }


    }


