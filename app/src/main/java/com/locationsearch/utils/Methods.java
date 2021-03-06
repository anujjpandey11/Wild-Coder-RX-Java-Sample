package com.locationsearch.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wild Coder on 17-07-2013.
 */
public class Methods {

    public static final String PROPERTY_APP_VERSION = "appVersion";

    static final String TAG = "GCMDemo";

    /**
     * @param str
     * @return
     */
    public static String fetchInitial(String str) {
        if (valid(str))
            return Character.toString(str.charAt(0)).toUpperCase();
        else
            return "";
    }

    /**
     * @param strValue
     * @return
     */
    public static float getFloat(String strValue) {
        if (strValue == null || strValue.length() == 0)
            return 0;
        float value = 0;
        try {
            value = Float.parseFloat(strValue);
        } catch (Exception e) {

        }
        return value;
    }

    /**
     * @param cal
     * @param hourOfDay
     * @param minute
     */
    public static void setCalendar(Calendar cal, int hourOfDay, int minute) {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
    }

    /**
     * @param format
     * @return
     */
    public static SimpleDateFormat getDateFormat(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    /**
     * @param target
     * @return
     */
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                .matches();
    }

    /**
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * @param ctx
     * @return
     */
    public static boolean checkInternetConnection(Context ctx) {
        ConnectivityManager mManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mManager.getActiveNetworkInfo();
        return (mNetworkInfo != null) && (mNetworkInfo.isConnected());
    }

    /**
     * @param ctx
     * @param message
     */
    public static void openShortToast(final Context ctx, final String message) {
        if (ctx == null)
            return;
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public static void openOKPopup(final Context ctx, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(message);
        builder.setTitle(null);
        builder.setCancelable(false);
        builder.setNeutralButton(ctx.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * @param ctx
     */
    public static void noInternetDialog(Context ctx) {
        openShortToast(ctx, "No internet connection");
    }

    /**
     * @param is
     * @param os
     */
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /**
     * @param pathToImage
     * @return
     */
    public static String getBase64String(String pathToImage) {
        if (valid(pathToImage)) {
            Bitmap bm = BitmapFactory.decodeFile(pathToImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the
            // bitmap
            // object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            return encodedImage;
        }
        return null;
    }

    /**
     * @param text
     * @return
     */
    public static boolean valid(String text) {
        return !(text == null || text.trim().equals("") || text.equals("null") || isOnlyWhiteSpaces(text));
    }

    /**
     * @param text
     * @return
     */
    private static boolean isOnlyWhiteSpaces(String text) {
        return text.trim().length() == 0;
    }

    /**
     * @param ctx
     * @param contentURI
     * @return
     */
    public static String getRealPathFromURI(Context ctx, Uri contentURI) {
        if (contentURI != null) {
            // OI FILE Manager
            String filemanagerstring = contentURI.getPath();

            // MEDIA GALLERY
            String filename = getImagePath(ctx, contentURI);

            String chosenPath;

            if (filename != null) {
                chosenPath = filename;
            } else {
                chosenPath = filemanagerstring;
            }
            return chosenPath;
        } else
            return "";
    }

    /**
     * @param ctx
     * @param uri
     * @return
     */
    public static String getImagePath(Context ctx, Uri uri) {
        String selectedImagePath;
        // 1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = ((Activity) ctx).managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            selectedImagePath = cursor.getString(column_index);
        } else {
            selectedImagePath = null;
        }

        if (selectedImagePath == null) {
            // 2:OI FILE Manager --- call method: uri.getPath()
            selectedImagePath = uri.getPath();
        }
        return selectedImagePath;
    }

    /**
     * @param response
     * @param w
     * @param h
     * @return
     */
    public static Bitmap doParse(String response, int w, int h) {
        // byte[] data = response.data;
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap = null;

        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(response, decodeOptions);
        // BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        // Then compute the dimensions we would ideally like to decode to.
        int desiredWidth = getResizedDimension(w, h, actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(w, h, actualHeight, actualWidth);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        // TODO(ficus): Do we need this or is it okay since API 8 doesn't
        // support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
                actualHeight, desiredWidth, desiredHeight);
        Bitmap tempBitmap = BitmapFactory.decodeFile(response, decodeOptions);

        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null
                && (tempBitmap.getWidth() > desiredWidth || tempBitmap
                .getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
                    desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }

        return bitmap;
    }

    /**
     * @param maxPrimary
     * @param maxSecondary
     * @param actualPrimary
     * @param actualSecondary
     * @return
     */
    public static int getResizedDimension(int maxPrimary, int maxSecondary,
                                          int actualPrimary, int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling
        // ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * @param actualWidth
     * @param actualHeight
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static int findBestSampleSize(int actualWidth, int actualHeight,
                                         int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    /**
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            IBinder windowToken = view.getWindowToken();
            boolean b = inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            AppLoger.e("boolean", String.valueOf(b));
        }
    }

    /**
     * @param activity
     * @param view
     */
    public static void setupUI(final Activity activity, View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }

            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(activity, innerView);
            }
        }
    }

    /**
     * @param unixTimeStamp
     * @param dateFormat
     * @return
     */
    public static String getDateFromUnixTimeStamp(String unixTimeStamp,
                                                  String dateFormat) {
        if (!unixTimeStamp.isEmpty()) {
            Date date = new Date(Long.parseLong(unixTimeStamp) * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            String formattedDate = sdf.format(date);
            return formattedDate;
        } else
            return "";
    }


    public static void openCallIntent(Context context,
                                      String phoneNumber) {
        try {
            String uri = "tel:" + phoneNumber.trim();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        } catch (ParseException e) {
            openShortToast(context, "Unable to call");
        }
    }

    /**
     * @param context
     * @param toEmail
     * @param subject
     * @param message
     */
    public static void openEmailIntent(Context context, String toEmail,
                                       String subject, String message) {

        final Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * @param zipCode
     * @return
     */
    public static boolean isValidZipCode(String zipCode) {
        boolean isValid = false;
        if (zipCode.startsWith("11"))
            isValid = true;
        return isValid;
    }


    public static boolean validIp(String ip) {
        Pattern IP_ADDRESS
                = Pattern.compile(
                "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                        + "|[1-9][0-9]|[0-9]))");
        Matcher matcher = IP_ADDRESS.matcher(ip);
        return matcher.matches();
    }
}
