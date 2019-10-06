package com.jins_jp.memelib_realtime;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jins_jp.meme.MemeStandardData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MemeStandardDataItemAdapter extends BaseAdapter {

    List<String[]> items;
    Context context;

    public MemeStandardDataItemAdapter(Context context) {
        this.context = context;

        updateMemeData(new MemeStandardData());
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context, R.layout.meme_data_item, null);
        }

        TextView labelTextView = (TextView)view.findViewById(R.id.item_label);
        TextView valueTextView = (TextView)view.findViewById(R.id.item_value);

        String[] item = items.get(i);
        labelTextView.setText(item[0]);
        valueTextView.setText(item[1]);

        return view;
    }

    public void updateMemeData(MemeStandardData d) {
        items = new ArrayList<>();
        addItem(R.string.focus_level, d.getFocus());
        addItem(R.string.is_eog_valid, d.isEOGValid());
        addItem(R.string.blink_strength, d.getBlinkStrength());
        addItem(R.string.blink_speed, d.getBlinkSpeed());
        addItem(R.string.num_of_blink, d.getNumBlinks());
        addItem(R.string.num_of_blink_burst, d.getNumBlinkBurst());
        addItem(R.string.foot_fold_left, d.getFootholdLeft());
        addItem(R.string.foot_fold_right, d.getFootholdRight());
        addItem(R.string.pitch_diff, d.getPitchDiff());
        addItem(R.string.pitch_average, d.getPitchAvg());
        addItem(R.string.roll_diff, d.getRollDiff());
        addItem(R.string.roll_average, d.getRollAvg());
        addItem(R.string.num_of_steps_280, d.getNumStep280());
        addItem(R.string.num_of_steps_310, d.getNumStep310());
        addItem(R.string.num_of_steps_340, d.getNumStep340());
        addItem(R.string.num_of_steps_370, d.getNumStep370());
        addItem(R.string.num_of_steps_400, d.getNumStep400());
        addItem(R.string.num_of_steps_430, d.getNumStep430());
        addItem(R.string.num_of_steps_460, d.getNumStep460());
        addItem(R.string.num_of_steps_500, d.getNumStep500());
        addItem(R.string.num_of_steps_530, d.getNumStep530());
        addItem(R.string.num_of_steps_560, d.getNumStep560());
        addItem(R.string.num_of_steps_590, d.getNumStep590());
        addItem(R.string.num_of_steps_620, d.getNumStep620());
        addItem(R.string.num_of_steps_650, d.getNumStep650());
        addItem(R.string.num_of_steps_680, d.getNumStep680());
        addItem(R.string.num_of_steps_710, d.getNumStep710());
        addItem(R.string.num_of_steps_750, d.getNumStep750());
        addItem(R.string.num_of_steps_780, d.getNumStep780());
        addItem(R.string.num_of_steps_810, d.getNumStep810());
        addItem(R.string.num_of_steps_840, d.getNumStep840());
        addItem(R.string.num_of_steps_870, d.getNumStep870());
        addItem(R.string.num_of_steps_900, d.getNumStep900());
        addItem(R.string.num_of_steps_930, d.getNumStep930());
        addItem(R.string.num_of_steps_960, d.getNumStep960());
        addItem(R.string.num_of_steps_1000, d.getNumStep1000());
        addItem(R.string.head_move_big_vertical_count, d.getHeadMoveBigVerticalCount());
        addItem(R.string.head_move_big_horizontal_count, d.getHeadMoveBigHorizontalCount());
        addItem(R.string.head_move_vertical_count, d.getHeadMoveVerticalCount());
        addItem(R.string.head_move_horizontal_count, d.getHeadMoveHorizontalCount());
        addItem(R.string.cadence, d.getCadence());
        addItem(R.string.vertical_eye_move, d.getEyeMoveVertical());
        addItem(R.string.horizontal_eye_move, d.getEyeMoveHorizontal());
        addItem(R.string.big_vertical_eye_move, d.getEyeMoveBigVertical());
        addItem(R.string.big_horizontal_eye_move, d.getEyeMoveBigHorizontal());
        addItem(R.string.power_left, d.getPowerLeft());
        addItem(R.string.fit_status, d.getFitError());
        addItem(R.string.eog_noise_duration, d.getNoiseDuration());
        if (d.getCapturedAt() != null) {
            addItem(R.string.timestamp, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN).format(d.getCapturedAt()));
        } else {
            addItem(R.string.timestamp, "0");
        }
    }

    private String getLabel(int resourceId) {
        return context.getResources().getString(resourceId);
    }

    private void addItem(int resourceId, Object value) {
        items.add(new String[] {getLabel(resourceId), String.valueOf(value)});
    }
}
