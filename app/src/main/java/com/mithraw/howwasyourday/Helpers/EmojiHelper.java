package com.mithraw.howwasyourday.Helpers;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class EmojiHelper {
    static Map<String, String> emojis = createMap();

    static Map<String, String> createMap() {
        Map<String, String> lEmojis = new HashMap<String, String>();
        lEmojis.put(":-)", "\uD83D\uDE03");
        lEmojis.put(":)", "\uD83D\uDE03");
        lEmojis.put(";)", new String(Character.toChars(0x01F609)));
        lEmojis.put(";-)", new String(Character.toChars(0x01F609)));
        lEmojis.put("<3", new String(Character.toChars(0x2764)));
        lEmojis.put(":joy:", new String(Character.toChars(0x01F602)));
        lEmojis.put(":-D", new String(Character.toChars(0x01F604)));
        lEmojis.put(":D", new String(Character.toChars(0x01F604)));
        lEmojis.put(":p", "\uD83D\uDE0B");
        lEmojis.put(":-p", "\uD83D\uDE0B");
        lEmojis.put(";-p", new String(Character.toChars(0x01F61C)));
        lEmojis.put(";p", new String(Character.toChars(0x01F61C)));
        lEmojis.put(":(", new String(Character.toChars(0x01F61E)));
        lEmojis.put(":-(", new String(Character.toChars(0x01F61E)));
        lEmojis.put(":'(", new String(Character.toChars(0x01F62D)));
        lEmojis.put(":eggplant:", new String(Character.toChars(0x01F346)));
        lEmojis.put(":/", new String(Character.toChars( 0x01F614)));
        lEmojis.put(":-/", new String(Character.toChars( 0x01F614)));
        lEmojis.put(":kiss:", new String(Character.toChars(0x01F61A)));
        lEmojis.put("><", new String(Character.toChars(0x01F623)));
        lEmojis.put(">.<", new String(Character.toChars(0x01F623)));
        lEmojis.put("^^", new String(Character.toChars(0x01F60A)));



        return lEmojis;
    }

    public static String parseCurrentChange(EditText et, CharSequence s, int start) {
        String text = s.toString();
        for (Map.Entry<String, String> entry : emojis.entrySet()) {
            int startMin = (start - entry.getKey().length() >= 0) ? start - entry.getKey().length() : 0;
            int offset = text.indexOf(entry.getKey(), startMin);
            if (offset == -1)
                continue;
            et.getEditableText().replace(offset, offset + entry.getKey().length(), entry.getValue());
            SpannedString spannableString = new SpannedString(entry.getValue());
            et.getEditableText().setSpan(spannableString, offset, offset + entry.getValue().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return text;
    }

    public static void attachToEditText(final EditText et) {

        TextWatcher watcher = new TextWatcher() {
            private int spanLength = -1;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (start == 0) return;
                if (count > after) {
                    ImageSpan[] spans = et.getEditableText().getSpans(start + count, start + count, ImageSpan.class);
                    SpannedString[] spansText = et.getEditableText().getSpans(start + count, start + count, SpannedString.class);
                    if (spansText == null || spansText.length == 0) return;
                    for (int i = 0; i < spansText.length; i++) {
                        int end = et.getEditableText().getSpanEnd(spansText[i]);
                        if (end != start + count) continue;
                        String text = spansText[i].toString();
                        spanLength = text.length() - 1;
                        et.getEditableText().removeSpan(spansText[i]);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (spanLength > -1) {
                    int length = spanLength;
                    spanLength = -1;
                    et.getEditableText().replace(start - length, start, "");
                }
                EmojiHelper.parseCurrentChange(et, s, start);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        et.addTextChangedListener(watcher);
    }
}
