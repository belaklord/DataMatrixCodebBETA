package com.example.pelu.qrcode;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telecom.Connection;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;


public class DataBase {



                                        /*
                            Selecciona los no enviados
                                        */


    public static String DatosTabla(SQLiteDatabase db) throws JSONException {



        String [] camposCabecera = new String[] {"albaran", "matricula_cam", "matricula_rem", "fecha"};
        String [] argsCabecera = new String[] {"no"};

        Cursor cabecerea = db.query(AyudaBD.DatosTabla.NOMBRE_TABLA, camposCabecera, AyudaBD.DatosTabla.NOMBRE_TABLA + ".enviado=?", argsCabecera, null, null, null );


        String [] camposLineas = new String[] {"id_albaran", "scaneo"};
        String [] argsLineas = new String[] {"no"};

        Cursor  linea = db.query(AyudaBD.Lineas.NOMBRE_TABLA, camposLineas, AyudaBD.Lineas.NOMBRE_TABLA +".enviado=?", argsLineas, null, null, null);




        JSONArray resultSet = new JSONArray();

        int columnasCabecera = cabecerea.getColumnCount();

        int columnasLinea = linea.getColumnCount();



            while(cabecerea.moveToNext()){
                JSONObject objetoCabecera = new JSONObject();

                for (int i = 0; i<columnasCabecera; i++){

                    objetoCabecera.put(cabecerea.getColumnName(i), cabecerea.getString(i));

                }


                if(objetoCabecera.length() >=2){

                    resultSet.put(objetoCabecera);
                }


            }

            cabecerea.close();



            while(linea.moveToNext()){
                JSONObject objetoLinea = new JSONObject();

                for (int i = 0; i<columnasLinea; i++){

                    objetoLinea.put(linea.getColumnName(i), linea.getString(i));

                }

                if(objetoLinea.length()>=2){
                    resultSet.put(objetoLinea);

                }


            }


            linea.close();


            return resultSet.toString();


    }

                            /*
    Actualiza el campo enviado a SI (cuando haya sido enviado)
                            */


    public static void Update(SQLiteDatabase db){

        ContentValues valoresCabecera = new ContentValues();
        ContentValues valoresLinea = new ContentValues();


        valoresCabecera.put(AyudaBD.DatosTabla.ENVIADO, "si" );
        valoresLinea.put(AyudaBD.Lineas.ENVIADO, "si");

        db.update(AyudaBD.DatosTabla.NOMBRE_TABLA, valoresCabecera, null, null);
        db.update(AyudaBD.Lineas.NOMBRE_TABLA, valoresLinea, null, null);



    }

                            /*
            Inserta los datos en la base de datos
                             */
    public  static void Insert(SQLiteDatabase db , String tabla, ContentValues valor){

        db.insert(tabla, null, valor);


}

    


}
