package ritik.launcher.result;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ritik.launcher.R;
import ritik.launcher.pojo.SettingsPojo;

public class SettingsResult extends Result {
    private final SettingsPojo settingPojo;

    public SettingsResult(SettingsPojo settingPojo) {
        super();
        this.pojo = this.settingPojo = settingPojo;
    }

    @Override
    public View display(Context context, int position, View v,Boolean highlight) {
        if (v == null)
            v = inflateFromId(context, R.layout.item_setting);

        String settingPrefix = "<small><small>" + context.getString(R.string.settings_prefix) + "</small></small>";
        TextView settingName = (TextView) v.findViewById(R.id.item_setting_name);
        settingName.setText(TextUtils.concat(Html.fromHtml(settingPrefix), enrichText(settingPojo.displayName, context)));

        ImageView settingIcon = (ImageView) v.findViewById(R.id.item_setting_icon);
        settingIcon.setImageDrawable(getDrawable(context));
        settingIcon.setColorFilter(getThemeFillColor(context), Mode.SRC_IN);

        return v;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Drawable getDrawable(Context context) {
        if (settingPojo.icon != -1) {
            return context.getResources().getDrawable(settingPojo.icon);
        }

        return null;
    }

    @Override
    public void doLaunch(Context context, View v) {
        Intent intent = new Intent(settingPojo.settingName);
        if (!settingPojo.packageName.isEmpty()) {
            intent.setClassName(settingPojo.packageName, settingPojo.settingName);
        }
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.setSourceBounds(v.getClipBounds());
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
