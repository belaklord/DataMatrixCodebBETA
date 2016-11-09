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



        Cursor cabecerea = db.rawQuery("SELECT albaran, matricula_cam, matricula_rem, fecha from Cabecera WHERE  Cabecera.enviado = 'no'"
                , null);




        Cursor linea =  db.rawQuery("SELECT id_albaran, scaneo from lineas WHERE  lineas.enviado = 'no'", null);


        JSONArray resultSet = new JSONArray();

        int columnasCabecera = cabecerea.getColumnCount();

        int columnasLinea = linea.getColumnCount();



            while(cabecerea.moveToNext()){
                JSONObject objetoCabecera = new JSONObject();

                for (int i = 0; i<columnasCabecera; i++){

                    objetoCabecera.put(cabecerea.getColumnName(i), cabecerea.getString(i));

                }

                    resultSet.put(objetoCabecera);


            }

            cabecerea.close();



            while(linea.moveToNext()){
                JSONObject objetoLinea = new JSONObject();

                for (int i = 0; i<columnasLinea; i++){

                    objetoLinea.put(linea.getColumnName(i), linea.getString(i));

                }

                if(objetoLinea.length() == 2){
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

            db.execSQL("UPDATE Cabecera SET enviado='si'");
            db.execSQL("UPDATE lineas SET enviado='si'");

    }

                            /*
            Inserta los datos en la base de datos
                             */
    public  static void Insert(SQLiteDatabase db , String tabla, String campo, ContentValues valor){

        db.insert(tabla, campo, valor);

        db.insert(tabla, campo, valor);

}


}
