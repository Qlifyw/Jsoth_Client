package com.example.herem1t.rc_client.ui.screens.details;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.example.herem1t.rc_client.R;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("unused")
public class AvatarImageBehavior extends CoordinatorLayout.Behavior<CircleImageView>  {

    private final static float MIN_AVATAR_PERCENTAGE_SIZE   = 0.3f;
    private final static int EXTRA_FINAL_AVATAR_PADDING     = 80;

    private Context context;

    private float customFinalYPosition;
    private float customStartXPosition;
    private float customStartToolbarPosition;
    private float customStartHeight;
    private float customFinalHeight;

    private float avatarMaxSize;
    private float finalLeftAvatarPadding;
    private float startPosition;
    private int startXPosition;
    private float startToolbarPosition;
    private int startYPosition;
    private int finalYPosition;
    private int startHeight;
    private int finalXPosition;
    private float changeBehaviorPoint;

    public AvatarImageBehavior(Context context, AttributeSet attrs) {
        this.context = context;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageBehavior);
            customFinalYPosition = a.getDimension(R.styleable.AvatarImageBehavior_finalYPosition, 0);
            customStartXPosition = a.getDimension(R.styleable.AvatarImageBehavior_startXPosition, 0);
            customStartToolbarPosition = a.getDimension(R.styleable.AvatarImageBehavior_startToolbarPosition, 0);
            customStartHeight = a.getDimension(R.styleable.AvatarImageBehavior_startHeight, 0);
            customFinalHeight = a.getDimension(R.styleable.AvatarImageBehavior_finalHeight, 0);

            a.recycle();
        }

        init();

        finalLeftAvatarPadding = context.getResources().getDimension(
                R.dimen.spacing_normal);
    }

    private void init() {
        bindDimensions();
    }

    private void bindDimensions() {
        avatarMaxSize = context.getResources().getDimension(R.dimen.image_width);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircleImageView child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircleImageView child, View dependency) {
        maybeInitProperties(child, dependency);

        final int maxScrollDistance = (int) (startToolbarPosition);
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;

        if (expandedPercentageFactor < changeBehaviorPoint) {
            float heightFactor = (changeBehaviorPoint - expandedPercentageFactor) / changeBehaviorPoint;

            float distanceXToSubtract = ((startXPosition - finalXPosition)
                    * heightFactor) + (child.getHeight()/2);
            float distanceYToSubtract = ((startYPosition - finalYPosition)
                    * (1f - expandedPercentageFactor)) + (child.getHeight()/2);

            child.setX(startXPosition - distanceXToSubtract);
            child.setY(startYPosition - distanceYToSubtract);

            float heightToSubtract = ((startHeight - customFinalHeight) * heightFactor);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            lp.width = (int) (startHeight - heightToSubtract);
            lp.height = (int) (startHeight - heightToSubtract);
            child.setLayoutParams(lp);
        } else {
            float distanceYToSubtract = ((startYPosition - finalYPosition)
                    * (1f - expandedPercentageFactor)) + (startHeight /2);

            child.setX(startXPosition - child.getWidth()/2);
            child.setY(startYPosition - distanceYToSubtract);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            lp.width = (int) (startHeight);
            lp.height = (int) (startHeight);
            child.setLayoutParams(lp);
        }
        return true;
    }

    private void maybeInitProperties(CircleImageView child, View dependency) {
        if (startYPosition == 0)
            startYPosition = (int) (dependency.getY());

        if (finalYPosition == 0)
            finalYPosition = (dependency.getHeight() /2);

        if (startHeight == 0)
            startHeight = child.getHeight();

        if (startXPosition == 0)
            startXPosition = (int) (child.getX() + (child.getWidth() / 2));

        if (finalXPosition == 0)
            finalXPosition = context.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + ((int) customFinalHeight / 2);

        if (startToolbarPosition == 0)
            startToolbarPosition = dependency.getY();

        if (changeBehaviorPoint == 0) {
            changeBehaviorPoint = (child.getHeight() - customFinalHeight) / (2f * (startYPosition - finalYPosition));
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
