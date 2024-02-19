package net.info420.yamba;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import social.bigbone.MastodonClient;
import social.bigbone.api.Pageable;
import social.bigbone.api.Range;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

public class UpdaterService extends Service {
    private static final String TAG = "UpdaterService";
    boolean updaterServiceDejaDemarre = false;
    Handler handler;
    final String[] message = new String[1];
    String id;
    String dateIOS8601;
    SimpleDateFormat formatDateISO8601;
    Date dateJava;
    long tempsMillisecondes;
    CharSequence tempsRelatif;
    String idUsager;
    String nomUsager;
    String texteBrut;
    String texteFinal;
    //    private static final long DELAY = 30000;
    boolean updaterServiceIsRequesting;

    public UpdaterService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate()");
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!updaterServiceDejaDemarre) {
            updaterServiceDejaDemarre = true;
            Log.d(TAG, "onStartCommand() : " + getString(R.string.updaterServiceStarted));
            Toast.makeText(this, R.string.updaterServiceStarted, Toast.LENGTH_LONG).show();

            new Thread(() -> {
                updaterServiceIsRequesting = true;

                while (updaterServiceIsRequesting) {
                    try {
                        int limit = Integer.parseInt(((YambaApplication) getApplication()).getNumberOfToots());

                        // Extraction des 5 derniers toots du timeline de l'usager.
                        Pageable<Status> timeline = ((YambaApplication) getApplication()).getClient().timelines()
                                .getHomeTimeline(new Range(null, null, null, limit))
                                .execute();
                        Log.d(TAG, "Thread run(): " + getString(R.string.timelineReceived));
                        message[0] = getString(R.string.timelineReceived);

                        // Extraction de diverses données de chaque toot du timeline.
                        timeline.getPart().forEach(status -> {
                            try {
                                // Extraction de l'identifiant du toot.
                                id = status.getId();

                                // Extraction de la date sous la norme ISO 8601.
                                // La date extraite de type PrecisionDateTime. Elle est convertie en chaîne.
                                dateIOS8601 = String.valueOf(status.getCreatedAt());
                                // Création du profil/modèle de la date extraite sous forme texte, afin de pouvoir la
                                // convertir en Date Java.
                                formatDateISO8601 = new SimpleDateFormat(
                                        "'ExactTime(instant='yyyy-MM-dd'T'HH:mm:ss.SSS'Z')");

                                // Création d'une date Java, à partir de la date extraite sous forme de texte et de son
                                // profil/modèle.
                                dateJava = formatDateISO8601.parse(dateIOS8601);

                                // Calcul du nombre de millisecondes écoulées depuis le 1 janvier 1970, à partir de la
                                // Date Java.
                                tempsMillisecondes = dateJava.getTime();

                                // Création du temps relatif, à partir du nombre de secondes écoulées depuis le 1 janvier
                                // 1970.
                                tempsRelatif = DateUtils.getRelativeTimeSpanString(tempsMillisecondes);

                                // Extraction de l'identifiant de l'usager.
                                idUsager = status.getAccount().getUsername();

                                // Extraction du nom de l'usager tel qu'affiché dans chaque toot.
                                nomUsager = status.getAccount().getDisplayName();

                                // Extraction du texte brut du toot (avec les tags HTML)
                                texteBrut = status.getContent();

                                // Élimination des tags HTML du texte brut du toot.
                                // La méthode replaceAll() retourne une nouvelle chaîne de caractères dans laquelle
                                // toutes les occurrences d'un motif donné ont été remplacées par une chaîne de
                                // remplacement.
                                // Le 1er argument (description d'un motif) peut être une chaîne de caractères ou une
                                // expression régulière (regex).
                                // Le 2e argument (remplacement) peut être une chaîne de caractères ou une fonction qui
                                // sera appelée pour chaque correspondance.
                                // La chaîne de caractères initiale reste inchangée.
                                // < : Correspond au symbole débutant le tag.
                                // [^>*] : Correspond à tout caractère n'étant pas le symbole >.
                                // > : Correspond au symbole terminant le tag.
                                // "" : Remplace les correspondances avec une chaîne vide.
                                texteFinal = status.getContent().replaceAll("<[^>]*>", "");

                                // Insertion au Logcat des diverses données extraites du toot.
                                Log.d(TAG, "Id du toot : " + id);
                                Log.d(TAG, "Date ISO 8601 : " + dateIOS8601);
                                Log.d(TAG, "Date Java : " + dateJava);
                                Log.d(TAG, "Temps en millisecondes : " + tempsMillisecondes);
                                Log.d(TAG, "Temps relatif : " + tempsRelatif);
                                Log.d(TAG, "Texte avec tags HTML : " + texteBrut);
                                Log.d(TAG, "Texte sans tags HTML : " + texteFinal);
                                Log.d(TAG, "Identifiant de l'usager : " + idUsager);
                                Log.d(TAG, "Nom de l'usager : " + nomUsager);


                            } catch (ParseException e) {
                                Log.d(TAG, "Thread run(): " + getString(R.string.exception), e);
                                message[0] = getString(R.string.exception);
                                updaterServiceIsRequesting = false;
                            }

                        });
                        long delay = Long.parseLong(((YambaApplication) getApplication()).getDelay()) * 1000;
                        Thread.sleep(delay);
                    } catch (BigBoneRequestException e) {
                        Log.d(TAG, "Thread run(): " + getString(R.string.bigBoneException), e);
                        message[0] = getString(R.string.bigBoneException);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Thread run(): " + getString(R.string.exception), e);
                        message[0] = getString(R.string.exception);
                        updaterServiceIsRequesting = false;
                    }
                    handler.post(() -> {
                        Toast.makeText(UpdaterService.this, message[0], Toast.LENGTH_LONG).show();
                    });
                }
            }).start();

        } else {
            Log.d(TAG, "onStartCommand() : " + getString(R.string.updaterServiceAlreadyStarted));
            Toast.makeText(this, R.string.updaterServiceAlreadyStarted, Toast.LENGTH_LONG).show();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy() : " + getString(R.string.updaterServiceStopped));
        Toast.makeText(this, R.string.updaterServiceStopped, Toast.LENGTH_LONG).show();
        updaterServiceIsRequesting = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}