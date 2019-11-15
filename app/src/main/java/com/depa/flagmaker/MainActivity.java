package com.depa.flagmaker;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.depa.flagmaker.Constants.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

public class MainActivity extends AppCompatActivity implements Callback {

    ImageView imageView, prevIV;
    FloatingActionButton colorfab;
    List<Drawable> flagDrawables;
    List<Inset> insets;
    SparseArray<PartType> idToPt;
    TinyDB db;
    BillingManager bm;
    boolean whichselfad;

    FlagPart flagPart;
    ColorPicker cp;

    SeekBar seekBar;

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        MobileAds.initialize(this, "ca-app-pub-7231500745390063~4703920823");

        imageView = findViewById(R.id.imageView);
        prevIV = findViewById(R.id.imageView2);
        colorfab = findViewById(R.id.fab2);
        flagDrawables = new ArrayList<>();
        insets = new ArrayList<>();
        idToPt = new SparseArray<>();
        cp = new ColorPicker(this, 200, 50, 100);
        db = new TinyDB(this);
        bm = new BillingManager(this, this);

        idToPt.append(R.id.vertical, PartType.band_v);
        idToPt.append(R.id.horizontal, PartType.band_h);
        idToPt.append(R.id.bend, PartType.bend);
        idToPt.append(R.id.nordic, PartType.nordic);
        idToPt.append(R.id.chevron, PartType.chevron);
        idToPt.append(R.id.backgr, PartType.fill);
        idToPt.append(R.id.circle, PartType.circle);
        idToPt.append(R.id.shield, PartType.shield);
        idToPt.append(R.id.canton, PartType.canton);
        idToPt.append(R.id.star, PartType.star);
        idToPt.append(R.id.rhombus, PartType.rhombus);

        flagPart = new FlagPart(Pos.start, PartType.band_h, cp.getColor(), 1);

        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                flagPart.setSize(progress);
                prevIV.setImageDrawable(flagPart.getSmallDrawable());
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        seekBar.setProgress(3);
        flagPart.setSize(3);

        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(int color) {
                flagPart.setColor(color);
                colorfab.setBackgroundTintList(ColorStateList.valueOf(color));
                prevIV.setImageDrawable(flagPart.getSmallDrawable());
            }
        });
        cp.enableAutoClose();
        colorfab.setBackgroundTintList(ColorStateList.valueOf(cp.getColor()));

        if(!BuildConfig.DEBUG && db.getBoolean("ads", true)) {
            adView = findViewById(R.id.adview);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        if (db.getBoolean("first", true)){
            startActivityForResult(new Intent(this, What.class), 0);
            db.putBoolean("first", false);
        }
        whichselfad = Calendar.getInstance().getTimeInMillis() % 2 == 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.selfad).setTitle(whichselfad ? R.string.tb : R.string.lb);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_clear:
                flagDrawables.clear();
                insets.clear();

                seekBar.setProgress(11);
                cp.setColors(200, 50, 100);
                RadioGroup sR = findViewById(R.id.posRadio);
                sR.check(R.id.start);
                sR = findViewById(R.id.shapeRadio);
                sR.check(R.id.horizontal);

                flagPart = new FlagPart(Pos.start, PartType.band_h, Color.RED, 1f);
                imageView.setImageDrawable(null);
                return true;
            case R.id.action_undo:
                return undo();
            case R.id.action_save:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.REQ_SAVE_IMAGE);
                else saveImage();
                return true;
            case R.id.action_what:
                startActivity(new Intent(this, What.class));
                return true;
            case R.id.action_noad:
                bm.purchase("noads");
                return true;
            case R.id.selfad:
                String appPackageName = whichselfad ?"com.depa.thinkbank":"com.depa.amazonlootbox";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

        return super.onOptionsItemSelected(item);
    }

    boolean undo(){
        try {
            flagDrawables.remove(flagDrawables.size() - 1);
            insets.remove(insets.size() - 1);
            imageView.setImageDrawable(makeWholeDrawable(null));
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public void addLayer(View view) {
        imageView.setImageDrawable(makeWholeDrawable(flagPart));
    }

    LayerDrawable makeWholeDrawable (FlagPart fp){

        if (fp != null) {
            flagDrawables.add(fp.makeDrawable().mutate());
            insets.add(fp.inset);
        }

        LayerDrawable fld = new LayerDrawable(flagDrawables.toArray(new Drawable[0]));

        for (int i = 0; i < insets.size(); i++)
            fld.setLayerInset(i, insets.get(i).left, insets.get(i).top,
                    insets.get(i).right, insets.get(i).bottom);

        return fld;
    }

    public void setShape(View view) {
        RadioGroup sR = findViewById(R.id.shapeRadio);
        flagPart.setType(idToPt.get(sR.getCheckedRadioButtonId()));
        prevIV.setImageDrawable(flagPart.getSmallDrawable());
    }

    public void setPos(View view) {
        RadioGroup sR = findViewById(R.id.posRadio);
        switch (sR.getCheckedRadioButtonId()) {
            case R.id.start:
                flagPart.setPos(Pos.start);
                break;
            case R.id.end:
                flagPart.setPos(Pos.end);
                break;
            case R.id.center:
                flagPart.setPos(Pos.center);
                break;
        }
        prevIV.setImageDrawable(flagPart.getSmallDrawable());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQ_SAVE_IMAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                    Snackbar.make(imageView, R.string.flag_saved,
                            Snackbar.LENGTH_LONG).show();

                    if (!db.getBoolean("reviewed", false)) {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.flag_saved)
                                .setMessage(R.string.liked)
                                .setPositiveButton(R.string.yesrev, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                                                "https://play.google.com/store/apps/details?id=com.depa.flagmaker")));
                                    }
                                }).setNegativeButton(R.string.nothank, null)
                                .show();
                        db.putBoolean("reviewed", true);
                    }
                } else {
                    Snackbar.make(imageView, R.string.permission,
                            Snackbar.LENGTH_LONG).show();
                }
        }
    }

    public void saveImage (){
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getPath() +
                "/Flags/");
        if(!wallpaperDirectory.isDirectory())
            wallpaperDirectory.mkdirs();
        File outputFile = new File(wallpaperDirectory,
                "flag" + Integer.toString((new Random(Calendar.getInstance().getTimeInMillis())).nextInt())+".png");
        // now attach the OutputStream to the file object, instead of a String representation
        // create bitmap screen capture
        Bitmap bitmap;

        imageView.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(imageView.getDrawingCache(),0,0,imageView.getWidth(),imageView.getHeight());
        imageView.setDrawingCacheEnabled(false);

        OutputStream fout;

        try {
            fout = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            addImageGallery(outputFile);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openColorDialog(View view) {
        cp.show();
    }

    @Override
    public void onSuccess(String result) {
        Snackbar.make(imageView, R.string.removed, Snackbar.LENGTH_LONG).show();
        db.putBoolean("ads", false);
        adView.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        Snackbar.make(imageView, R.string.aint_work, Snackbar.LENGTH_LONG).show();
    }

    private void addImageGallery( File file ) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
