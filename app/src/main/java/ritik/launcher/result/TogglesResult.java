package ritik.launcher.result;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPreference;

import ritik.launcher.R;
import ritik.launcher.pojo.TogglesPojo;
import ritik.launcher.toggles.TogglesHandler;

public class TogglesResult extends Result {
    private final TogglesPojo togglePojo;

    /**
     * Handler for all toggle-related queries
     */
    private TogglesHandler togglesHandler = null;

    public TogglesResult(TogglesPojo togglePojo) {
        super();
        this.pojo = this.togglePojo = togglePojo;
    }

    @SuppressWarnings({"ResourceType", "deprecation"})
    @Override
    public View display(final Context context, int position,View v,Boolean highlight) {
        // On first run, initialize handler
        if (togglesHandler == null)
            togglesHandler = new TogglesHandler(context);

        if (v == null)
            v = inflateFromId(context, R.layout.item_toggle);

        String togglePrefix = "<small><small>" + context.getString(R.string.toggles_prefix) + "</small></small>";

        TextView toggleName = (TextView) v.findViewById(R.id.item_toggle_name);
        toggleName.setText(TextUtils.concat(Html.fromHtml(togglePrefix), enrichText(togglePojo.displayName, context)));

        final ImageView toggleIcon = (ImageView) v.findViewById(R.id.item_toggle_icon);
        toggleIcon.setImageDrawable(context.getResources().getDrawable(togglePojo.icon));
        toggleIcon.setColorFilter(getThemeFillColor(context), Mode.SRC_IN);


        // Use the handler to check or un-check button
        final CompoundButton toggleButton = (CompoundButton) v
                .findViewById(R.id.item_toggle_action_toggle);

        //set listener to null to avoid calling the listener of the older toggle item
        //(due to recycling)
        toggleButton.setOnCheckedChangeListener(null);

        Boolean state = togglesHandler.getState(togglePojo);
        if (state != null) {
            Log.d("Ritik", "display: state recievd is"+state);
            toggleButton.setChecked(togglesHandler.getState(togglePojo));
           //TODO change toggle enabled color
            if(togglesHandler.getState(togglePojo))
                toggleIcon.setColorFilter(Color.GREEN, Mode.SRC_IN);
            else
                toggleIcon.setColorFilter(getThemeFillColor(context), Mode.SRC_IN);
        }

        else{
            toggleButton.setEnabled(false);
            toggleIcon.setColorFilter(getThemeFillColor(context), Mode.SRC_IN);
        }

        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!togglesHandler.getState(togglePojo).equals(toggleButton.isChecked())) {

                    // record launch manually
                    recordLaunch(buttonView.getContext());

                    Log.d("Ritik", "onCheckedChanged: state changed "+isChecked);
                    if (isChecked) {
                        Log.d("Ritik", "onCheckedChanged: greening it now");

                        toggleIcon.setColorFilter(Color.GREEN, Mode.SRC_IN);


                    } else {
                        toggleIcon.setColorFilter(getThemeFillColor(context), Mode.SRC_IN);
                    }
                    toggleIcon.invalidate();

                    togglesHandler.setState(togglePojo, toggleButton.isChecked());

 //                   toggleButton.setEnabled(false);
//                    new AsyncTask<Void, Void, Void>() {
//
//                        @Override
//                        protected Void doInBackground(Void... params) {
//                            try {
//                                Thread.sleep(1500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            return null;
//                        }
//
//                        @Override
//                        protected void onPostExecute(Void result) {
//                            super.onPostExecute(result);
//                            toggleButton.setEnabled(true);
//                        }
//
//                    }.execute();
                }
            }
        });
        return v;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Drawable getDrawable(Context context) {
        return context.getResources().getDrawable(togglePojo.icon);
    }

    @Override
    public void doLaunch(Context context, View v) {
        CompoundButton toggleButton = null;
        if (v != null) {
            toggleButton = (CompoundButton) v.findViewById(R.id.item_toggle_action_toggle);
        }

        if (toggleButton != null) {
            // Use the handler to check or un-check button
            if (toggleButton.isEnabled()) {
                toggleButton.performClick();
            }
        } else {
            //in case it is pinned on bar
            if (togglesHandler == null) {
                togglesHandler = new TogglesHandler(context);
            }

            //get message based on current state of toggle
            String msg = context.getResources().getString(togglesHandler.getState(togglePojo) ? R.string.toggles_off : R.string.toggles_on);

            //toggle state
            togglesHandler.setState(togglePojo, !togglesHandler.getState(togglePojo));

            //show toast to inform user what the state is
            Toast.makeText(context, String.format(msg, " " + this.pojo.displayName), Toast.LENGTH_SHORT).show();

        }
    }

}