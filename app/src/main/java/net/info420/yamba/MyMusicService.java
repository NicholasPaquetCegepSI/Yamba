// Un service est une composante essentielle d'une application. Il est nécessaire lorsque votre application souhaite effectuer
// des opérations ou des calculs en dehors de l'interaction utilisateur. Un service ne dispose donc pas d'interface graphique.

// Un service est une tâche de longue durée qui s'exécute en arrière-plan sans interface utilisateur. Par exemple, la lecture
// d'un fichier audio ou le téléchargement de fichiers depuis Internet. Une demande de service peut également être utilisée pour
// étendre les fonctionnalités d'une application normale. Par exemple, un gestionnaire de téléchargement peut accepter une demande
// de téléchargement de l'utilisateur, et l'envoyer à un service qui assure effectivement le téléchargement. Une application peut
// utiliser Context.startService() et Context.stopService() pour démarrer et arrêter un service.

package net.info420.yamba;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyMusicService extends Service {
    private static final String TAG = "MyMusicService";
    MediaPlayer player;
    boolean estDejaDemarre = false;
    public static final String YAMBA_CHANNEL_ID = "net.info420.yamba";
    public static final String YAMBA_CHANNEL_NAME = "Yamba";

    // Appelé par le système lorsque le service est créé pour la première fois, suite à l'appel à startService().
    // ***Si le service est déjà en exécution, le système n'appellera pas cette méthode de nouveau. ***
    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate(): objet MediaPlayer créé");
        player = MediaPlayer.create(this, R.raw.a_love_eternal_joe_satriani);
        player.setLooping(true); // Lecture en boucle.
        player.start();

        // Création d'un objet NotificationCHannel avec un identificateur unique.
        NotificationChannel yambaChannel = new NotificationChannel(YAMBA_CHANNEL_ID, YAMBA_CHANNEL_NAME,
                                                                   NotificationManager.IMPORTANCE_DEFAULT
        );
        // Soumettre l'objet yambaChannel au NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(yambaChannel);

        // Préparation de l'intent que la notification enverra lorsque l'usager appuiera sur la notification.
        Intent notificationIntent = new Intent(this, MyMusicService.class);
        // Création d'un PendingIntent pour arrêter le service MyMusicService, à partir de la notification.
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, notificationIntent,
                                                               PendingIntent.FLAG_IMMUTABLE
        );

        // Création et affichage d'une notification, avec icône, titre et texte.
        Notification notification = new Notification.Builder(this, YAMBA_CHANNEL_ID).setSmallIcon(
                R.drawable.note_de_musique).setContentTitle(getString(R.string.myMusicServiceStarted)).setContentText(
                getString(R.string.myMusicServiceNotification)).setContentIntent(pendingIntent).build();

        startForeground(1, notification);

        Toast.makeText(this, R.string.myMusicServiceStarted, Toast.LENGTH_LONG).show();
        Log.d(TAG, getString(R.string.myMusicServiceStarted));
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        if (!estDejaDemarre) {
            estDejaDemarre = true;
        } else {
            stopSelf();
        }
        // Nous voulons que ce service demeure en exécution tant qu'il n'est pas explicitement stoppé.
        return START_STICKY;
    }


    // Appelée par le système pour notifier un service qu'il n'est plus utilisé, et sur le point d'être détruit.
    // Le service devrait nettoyer ici les ressources qu'il détient (threads, registered receivers, etc.).
    // Au retour de cette méthode, le service est considéré mort.
    // Ne pas appeler cette méthose directement.
    // Appelée avec stopService().
    // Pour que le service s'arrête lui-même, nous pouvons utiliser la méthode Service.stopSelf().
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        player.stop();
        player = null;
        Toast.makeText(this, R.string.myMusicServiceStopped, Toast.LENGTH_LONG).show();
    }

    // Retourne "null" car le binding n'est pas utilisé dans ce projet.
    public IBinder onBind(Intent intent) {
        return null;
    }
}

