package pl.avaris.manager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.io.InputStream;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;
    private FrameLayout root;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        root = new FrameLayout(this);
        WebView web = new WebView(this);
        web.setBackgroundColor(0xFF111713);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        web.addJavascriptInterface(new AndroidBridge(this), "AvarisAndroid");
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        root.addView(web, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
        NotificationHelper.createChannel(this);
        web.loadUrl("file:///android_asset/index.html");
        showIntro();
    }

    private void showIntro() {
        final SplashIntroView intro = new SplashIntroView();
        root.addView(intro, new FrameLayout.LayoutParams(-1, -1));
        intro.start(() -> root.removeView(intro));
    }

    public void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
        }
    }

    public static class AndroidBridge {
        private final MainActivity activity;
        AndroidBridge(MainActivity activity) { this.activity = activity; }
        @JavascriptInterface public void requestNotifications() { activity.runOnUiThread(activity::requestNotificationPermission); }
        @JavascriptInterface public void scheduleReminder(long id, long triggerAtMillis, String title, String message) {
            if (triggerAtMillis <= System.currentTimeMillis()) return;
            ReminderScheduler.schedule(activity, id, triggerAtMillis, title, message);
        }
        @JavascriptInterface public void cancelReminder(long id) { ReminderScheduler.cancel(activity, id); }
    }

    private class SplashIntroView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        private Bitmap logo;
        private long started;
        private ValueAnimator animator;
        private Runnable finished;
        private float progress = 0f;
        private final int red = Color.rgb(145, 0, 0);
        private final int darkRed = Color.rgb(55, 0, 0);

        SplashIntroView() {
            super(MainActivity.this);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            try (InputStream in = getAssets().open("avaris_logo.png")) { logo = BitmapFactory.decodeStream(in); }
            catch (Exception ignored) { logo = null; }
        }

        void start(Runnable onFinished) {
            finished = onFinished;
            started = android.os.SystemClock.uptimeMillis();
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(3700);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(a -> { progress = (float)a.getAnimatedValue(); invalidate(); });
            animator.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(android.animation.Animator animation) { if (finished != null) finished.run(); }
            });
            animator.start();
        }

        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float w=getWidth(), h=getHeight();
            c.drawColor(Color.BLACK);
            // Slightly visible red ambience behind the logo.
            p.setShader(new RadialGradient(w/2f,h/2f,Math.max(w,h)*.58f,new int[]{0x553d0000,0x00000000},null,Shader.TileMode.CLAMP));
            c.drawRect(0,0,w,h,p); p.setShader(null);
            if (logo != null) {
                float maxW=w*.94f, maxH=h*.55f;
                float scale=Math.min(maxW/logo.getWidth(),maxH/logo.getHeight());
                float dw=logo.getWidth()*scale, dh=logo.getHeight()*scale;
                RectF dst=new RectF((w-dw)/2f,(h-dh)/2f- h*.025f,(w+dw)/2f,(h+dh)/2f-h*.025f);
                p.setAlpha(255); c.drawBitmap(logo,null,dst,p);
            }
            // FNAF-inspired security shutters: open from the middle, hold, then slam shut.
            float open;
            if (progress < .48f) open = ease(progress/.48f);
            else if (progress < .70f) open = 1f;
            else open = 1f-ease((progress-.70f)/.30f);
            float doorW=w*.51f;
            float travel=doorW*open;
            drawDoor(c, -travel, 0, doorW, h, true);
            drawDoor(c, w-doorW+travel, 0, doorW, h, false);
            // Brief black beat once the doors meet.
            if (progress > .96f) { p.setColor(Color.BLACK); p.setAlpha((int)(255*((progress-.96f)/.04f))); c.drawRect(0,0,w,h,p); p.setAlpha(255); }
        }

        private float ease(float x) { x=Math.max(0,Math.min(1,x)); return x*x*(3-2*x); }

        private void drawDoor(Canvas c,float x,float y,float width,float height,boolean left) {
            RectF r=new RectF(x,y,x+width,height);
            p.setShader(new LinearGradient(x,0,x+width,0,new int[]{darkRed,red,0xFF260000},null,Shader.TileMode.CLAMP));
            c.drawRect(r,p); p.setShader(null);
            p.setColor(0xFF160000); p.setStrokeWidth(Math.max(3,width*.018f));
            float edge=left?x+width-5:x+5; c.drawLine(edge,0,edge,height,p);
            p.setColor(0xFF350000); p.setStrokeWidth(3);
            for(int i=1;i<5;i++){float yy=height*i/5f;c.drawLine(x+width*.12f,yy,x+width*.88f,yy,p);}
            // Heavy horizontal metal braces and rivets give the shutters a mechanical feel.
            p.setColor(0xFF210000); p.setStrokeWidth(Math.max(5,width*.035f));
            c.drawLine(x+width*.08f,height*.16f,x+width*.92f,height*.16f,p);
            c.drawLine(x+width*.08f,height*.84f,x+width*.92f,height*.84f,p);
            p.setColor(0xFF090909);
            float rr=Math.max(3,width*.018f);
            for(int i=0;i<4;i++){float yy=height*(.12f+i*.24f);c.drawCircle(x+width*.13f,yy,rr,p);c.drawCircle(x+width*.87f,yy,rr,p);}
            // Narrow seam shadow at the closing edge.
            p.setShader(new LinearGradient(left?x+width-22:x,0,left?x+width:x+22,0,0xAA000000,0x00000000,Shader.TileMode.CLAMP));
            c.drawRect(r,p);p.setShader(null);
        }
    }
}
