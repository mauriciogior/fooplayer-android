package com.mauriciogiordano.fooplayer.view;

/**
 * Created by mauricio on 11/1/14.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.mauriciogiordano.fooplayer.R;

public class TextView extends android.widget.TextView
{
    private String SCHEMA = "http://schemas.android.com/apk/lib/com.mauriciogiordano.fooplayer.view";
    private Context context = null;
    private int textThickness = 4;
    private String textFor = "";

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        isInEditMode();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView);

        try {
            try
            {
                textThickness = Integer.parseInt(attrs.getAttributeValue(SCHEMA, "textThickness"));
            } catch(NumberFormatException e)
            {
                textThickness = 4;
            }

            textFor = attrs.getAttributeValue(SCHEMA, "textFor");

            if(textFor == null) textFor = "p";

            setThickness(textThickness);
            setTextUtility(textFor);

            invalidate();
            requestLayout();
        } finally {
            a.recycle();
        }
    }

    private void setThickness(int textThickness)
    {
        Typeface typeface = null;

        switch(textThickness)
        {
            case 0:
                typeface = Typeface.createFromAsset(context.getAssets(), "roboto_black.ttf");
                break;
            case 1:
                typeface = Typeface.createFromAsset(context.getAssets(), "roboto_bold.ttf");
                break;
            case 2:
                typeface = Typeface.createFromAsset(context.getAssets(), "roboto_medium.ttf");
                break;
            case 3:
                typeface = Typeface.createFromAsset(context.getAssets(), "roboto_regular.ttf");
                break;
            case 4:
                typeface = Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf");
                break;
            case 5:
                typeface = Typeface.createFromAsset(context.getAssets(), "roboto_thin.ttf");
                break;
            default:
                typeface = Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf");
                break;
        }

        setTypeface(typeface);
    }

    private void setTextUtility(String textFor)
    {
        if(textFor.equals("goku"))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40.0f);
        else if(textFor.equals("h1"))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.0f);
        else if(textFor.equals("h2"))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f);
        else if(textFor.equals("h3"))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);
        else if(textFor.equals("p"))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
        else if(textFor.equals("small"))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f);
        else
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
    }
}
