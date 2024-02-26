package net.info420.yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import social.bigbone.api.entity.Status;

public class StatusData {
    private static final String TAG = StatusData.class.getSimpleName();
    Context context;
    DBHelper dbHelper;
    private SQLiteDatabase db;
    public static final String DB_NAME = "timeline.db";
    public static final String TABLE_NAME = "statuses";
    public static int DB_VERSION = 1;
    public static final String C_ID = BaseColumns._ID;
    public static final String C_CREATED_AT = "c_createdAt";
    public static final String C_USER = "c_user";
    public static final String C_TEXT = "c_text";

    // Attributs necessaures pour extraire les donnes desirees d'un toot.
    String dateIOS8601;
    SimpleDateFormat formatDateISO8601;
    Date dateJava;
    String nomUsager;
    String texteFinal;

    public StatusData(Context context) {
        this.context = context;
        dbHelper = new DBHelper();
    }

    public void insert(Status status) throws ParseException {
        ContentValues fieldsValues = new ContentValues();
        db = dbHelper.getWritableDatabase();

        fieldsValues.put(C_ID, Long.parseLong(status.getId()));

        // Extraction de la date sous la norme ISO 8601.
        // La date extraite de type PrecisionDateTime. Elle est convertie en chaîne.
        dateIOS8601 = String.valueOf(status.getCreatedAt());

        // Création du profil/modèle de la date extraite sous forme texte, afin de pouvoir la
        // convertir en Date Java.
        formatDateISO8601 = new SimpleDateFormat(
                "'ExactTime(instant='yyyy-MM-dd'T'HH:mm:ss.SSS'Z')");

        // Création d'une date Java, à partir de la date extraite sous forme de texte et de son
        // profil/modèle.
        try {
            dateJava = formatDateISO8601.parse(dateIOS8601);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        fieldsValues.put(C_CREATED_AT, dateJava.getTime());

        // Extraction du nom de l'usager tel qu'affiché dans chaque toot.
        nomUsager = status.getAccount().getDisplayName();
        fieldsValues.put(C_USER, nomUsager);

        // Élimination des tags HTML du texte brut du toot.
        texteFinal = status.getContent().replaceAll("<[^>]*>", "");
        fieldsValues.put(C_TEXT, texteFinal);

        db.insert(TABLE_NAME, null, fieldsValues);
        Log.d(
                TAG,
                String.format("insert() : Insertion du Status/toot \"%s: %s\" dans la BD", nomUsager, texteFinal)
        );
        db.close();
    }

    private class DBHelper extends SQLiteOpenHelper {
        private static final String TAG = "DBHelper";

        public DBHelper() {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format("create table statuses (%s int primary key, %s int, %s text, %s text)", C_ID,
                                       C_CREATED_AT, C_USER, C_TEXT
            );
            db.execSQL(sql);
            Log.d(TAG, "onCreate(): Commande SQL \"" + sql + "\"exécutée");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            Log.d(TAG, "onUpgrade(): Table \"" + TABLE_NAME + "\"détruite");
            onCreate(db);
            Log.d(TAG, "onUpgrade(): Mise à jour de la BD de la version " + oldVersion + " à la version " + newVersion);
        }
    }

}
