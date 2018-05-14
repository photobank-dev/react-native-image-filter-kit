package iyegoroff.ImageColorFilter;

import java.util.Arrays;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.util.Log;

import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.ReactConstants;

public class RNImageColorFilterView extends ReactViewGroup {

  private float[] mMatrix = {
    1, 0, 0, 0, 0,
    0, 1, 0, 0, 0,
    0, 0, 1, 0, 0,
    0, 0, 0, 1, 0
  };

  public RNImageColorFilterView(Context context) {
    super(context);
  }

  public void setMatrix(ReadableArray matrix) {
    mMatrix = new float[matrix.size()];

    for (int i = 0; i < mMatrix.length; i++) {
      mMatrix[i] = (float) matrix.getDouble(i);
    }

    invalidate();

    invalidateAllImageColorFilterChildren(this);
  }

  @Override
  public void draw(Canvas canvas) {
    useColorFilterOnAllChildren(
      this,
      new ColorMatrixColorFilter(calculateColorMatrix(this, new ColorMatrix(mMatrix)))
    );

    super.draw(canvas);
  }

  private ColorMatrix calculateColorMatrix(ViewGroup target, ColorMatrix currentMatrix) {
    ViewParent parent = target.getParent();

    if (parent instanceof ViewGroup) {
      if (parent instanceof RNImageColorFilterView) {
        currentMatrix.postConcat(new ColorMatrix(((RNImageColorFilterView) parent).mMatrix));
      }

      return calculateColorMatrix((ViewGroup) parent, currentMatrix);
    }

    Log.v(ReactConstants.TAG, Arrays.toString(currentMatrix.getArray()));

    return currentMatrix;
  }

  private void useColorFilterOnAllChildren(ViewGroup parent, ColorMatrixColorFilter filter) {
    for (int i = 0; i < parent.getChildCount(); i++) {
      View child = parent.getChildAt(i);

      if (child instanceof ImageView) {
        ((ImageView) child).setColorFilter(filter);

      } else if (child instanceof RNImageColorFilterView) {
        return;

      } else if (child instanceof ViewGroup) {
        useColorFilterOnAllChildren((ViewGroup) child, filter);
      }
    }
  }

  private void invalidateAllImageColorFilterChildren(ViewGroup parent) {
    for (int i = 0; i < parent.getChildCount(); i++) {
      View child = parent.getChildAt(i);

      if (child instanceof RNImageColorFilterView) {
        ((RNImageColorFilterView) child).invalidate();
      }

      if (child instanceof ViewGroup) {
        invalidateAllImageColorFilterChildren((ViewGroup) child);
      }
    }
  }
}