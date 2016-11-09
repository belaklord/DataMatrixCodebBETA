package com.example.pelu.qrcode;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;


public class AyudaBD extends SQLiteOpenHelper {


    public static abstract class DatosTabla implements BaseColumns {


        public static final String NOMBRE_TABLA = "Cabecera";
        public static final String COLUMNA_ID = "albaran";
        public static final String COLUMNA_MATRICULA1 = "matricula_cam";
        public static final String COLUMNA_MATRICULA2 = "matricula_rem";
        public static final String ENVIADO = "enviado";
        public static final String FECHA = "fecha";

        private static final String TEXT_TYPE = " TEXT";
        private static final String DATE_TIPE = "DATE";
        private static final String COMMA_SEP = ",";
        private static final String CREAR_TABLA_1 =
                "CREATE TABLE " + DatosTabla.NOMBRE_TABLA + " (" +
                        DatosTabla.COLUMNA_ID + " INTEGER PRIMARY KEY," +
                        DatosTabla.COLUMNA_MATRICULA1 + TEXT_TYPE + COMMA_SEP +
                        DatosTabla.FECHA + COMMA_SEP +
                        DatosTabla.ENVIADO + COMMA_SEP +
                        DatosTabla.COLUMNA_MATRICULA2 + TEXT_TYPE +
                        " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + DatosTabla.NOMBRE_TABLA;

    }


    public static abstract class Lineas implements BaseColumns {

                                /*
                       TABLA LINEAS ESCANEADAS
                                */


        public static final String NOMBRE_TABLA = "lineas";

        public static final String LINEA_PEDIDO = "scaneo";
        public static final String ID_ALBARAN = "id_albaran";
        public  static final String ENVIADO = "enviado";

        private static final String TIPE_TEXT = " TEXT";
        private static final String COMA_SEP = ",";
        private static final String CREAR_TABLA = "CREATE TABLE " + Lineas.NOMBRE_TABLA + " (" +

                Lineas.ID_ALBARAN + TIPE_TEXT + COMA_SEP +
                Lineas.ENVIADO + COMA_SEP +
                Lineas.LINEA_PEDIDO + ")";


        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Lineas.NOMBRE_TABLA;

    }





    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MiBasedeDatos.db";

    public AyudaBD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(DatosTabla.CREAR_TABLA_1);
      db.execSQL(Lineas.CREAR_TABLA);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatosTabla.SQL_DELETE_ENTRIES);
         db.execSQL(Lineas.SQL_DELETE_ENTRIES);


        onCreate(db);

    }


}
