package com.weexbox.core.util;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import com.weexbox.core.widget.FloatingDraftButton;

import java.util.ArrayList;


public class AnimationUtil {

    private static int mDurationMillis = 200;

    public static void slideview(final View view, final float p1, final float p2, final float p3, final float p4,
                                 long durationMillis, long delayMillis,
                                 final boolean startVisible, final boolean endVisible) {
        //如果处在动画阶段则不允许再次运行动画
        if (view.getTag() != null && "-1".equals(view.getTag().toString())) {
            return;
        }
        TranslateAnimation animation = new TranslateAnimation(p1, p2, p3, p4);
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (startVisible) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.INVISIBLE);
                }
                view.setTag("-1");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                int left = view.getLeft() + (int) (p2 - p1);
                int top = view.getTop() + (int) (p4 - p3);
                int width = view.getWidth();
                int height = view.getHeight();
                view.layout(left, top, left + width, top + height);//重新设置位置
                if (endVisible) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.INVISIBLE);
                }
                view.setTag("1");

            }
        });
        if (endVisible) {
            view.startAnimation(animation);
        } else {//如果关闭则加渐变效果
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setDuration(durationMillis);
            animationSet.setStartOffset(delayMillis);
            animationSet.addAnimation(animation);

            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            animationSet.addAnimation(alphaAnimation);

            view.startAnimation(animationSet);
        }

    }

    //移动Buttons展开或者关闭
    public static void slideButtons(Context context, final FloatingDraftButton button) {

        int size = button.getButtonSize();
        if (size == 0) {
            return;
        }
        int buttonLeft = button.getLeft();
        int screenWidth = AndroidUtil.getScreenWidth(context);
        int buttonRight = screenWidth - button.getRight();
        int buttonTop = button.getTop();
        int buttonBottom = AndroidUtil.getScreenHeight(context) - button.getBottom();
        int buttonWidth = button.getWidth();
        int radius = 7 * buttonWidth / 4;
        int gap = 5 * buttonWidth / 4;

        ArrayList<FloatingActionButton> buttons = button.getButtons();

        if (button.isDraftable()) {//可拖拽 展开Buttons
            showRotateAnimation(button, 0, 225);
            //可围成圆形
            if (buttonLeft >= radius && buttonRight >= radius
                    && buttonTop >= radius && buttonBottom >= radius) {
                double angle = 360.0 / size;
                int randomDegree = 160;
                for (int i = 0; i < size; i++) {
                    FloatingActionButton faButton = buttons.get(i);
                    slideview(faButton, 0, radius * (float) Math.cos(Math.toRadians(randomDegree + angle * i)), 0, radius * (float) Math.sin(Math.toRadians(randomDegree + angle * i)), mDurationMillis, 0, true, true);
                }

            } else if (size * gap < screenWidth && (buttonTop > 3 * buttonBottom || buttonBottom > 3 * buttonTop)) {//如果太靠上边或下边
                int leftNumber = buttonLeft / gap;
                int rightNumber = buttonRight / gap;
                if (buttonTop >= radius && buttonBottom >= radius) {
                    if (buttonTop > buttonBottom) {
                        FloatingActionButton faButton = buttons.get(0);
                        slideview(faButton, 0, 0, 0, -radius, mDurationMillis, 0, true, true);
                        for (int i = 1; i <= leftNumber && i < size; i++) {
                            faButton = buttons.get(i);
                            slideview(faButton, 0, -gap * i, 0, -radius, mDurationMillis, 0, true, true);
                        }
                        for (int i = 1; i <= rightNumber && i < size - leftNumber; i++) {
                            faButton = buttons.get(i + leftNumber);
                            slideview(faButton, 0, gap * i, 0, -radius, mDurationMillis, 0, true, true);
                        }
                    } else {
                        FloatingActionButton faButton = buttons.get(0);
                        slideview(faButton, 0, 0, 0, radius, mDurationMillis, 0, true, true);
                        for (int i = 1; i <= leftNumber && i < size; i++) {
                            faButton = buttons.get(i);
                            slideview(faButton, 0, -gap * i, 0, radius, mDurationMillis, 0, true, true);
                        }
                        for (int i = 1; i <= rightNumber && i < size - leftNumber; i++) {
                            faButton = buttons.get(i + leftNumber);
                            slideview(faButton, 0, gap * i, 0, radius, mDurationMillis, 0, true, true);
                        }
                    }
                } else if (buttonTop >= radius) {
                    FloatingActionButton faButton = buttons.get(0);
                    slideview(faButton, 0, 0, 0, -radius, mDurationMillis, 0, true, true);
                    for (int i = 1; i <= leftNumber && i < size; i++) {
                        faButton = buttons.get(i);
                        slideview(faButton, 0, -gap * i, 0, -radius, mDurationMillis, 0, true, true);
                    }
                    for (int i = 1; i <= rightNumber && i < size - leftNumber; i++) {
                        faButton = buttons.get(i + leftNumber);
                        slideview(faButton, 0, gap * i, 0, -radius, mDurationMillis, 0, true, true);
                    }
                } else if (buttonBottom >= radius) {
                    FloatingActionButton faButton = buttons.get(0);
                    slideview(faButton, 0, 0, 0, radius, mDurationMillis, 0, true, true);
                    for (int i = 1; i <= leftNumber && i < size; i++) {
                        faButton = buttons.get(i);
                        slideview(faButton, 0, -gap * i, 0, radius, mDurationMillis, 0, true, true);
                    }
                    for (int i = 1; i <= rightNumber && i < size - leftNumber; i++) {
                        faButton = buttons.get(i + leftNumber);
                        slideview(faButton, 0, gap * i, 0, radius, mDurationMillis, 0, true, true);
                    }
                }
            } else {
                int upNumber = buttonTop / gap;
                int belowNumber = buttonBottom / gap;
                if ((upNumber + belowNumber + 1) >= size) {
                    upNumber = upNumber * (size - 1) / (upNumber + belowNumber);
                    belowNumber = size - 1 - upNumber;
                    if (buttonLeft >= radius) {
                        FloatingActionButton faButton = buttons.get(0);
                        slideview(faButton, 0, -radius, 0, 0, mDurationMillis, 0, true, true);
                        for (int i = 1; i <= upNumber && i < size; i++) {
                            faButton = buttons.get(i);
                            slideview(faButton, 0, -radius, 0, -gap * i, mDurationMillis, 0, true, true);
                        }
                        for (int i = 1; i <= belowNumber && i < size - upNumber; i++) {
                            faButton = buttons.get(i + upNumber);
                            slideview(faButton, 0, -radius, 0, gap * i, mDurationMillis, 0, true, true);
                        }
                    } else if (buttonRight >= radius) {
                        FloatingActionButton faButton = buttons.get(0);
                        slideview(faButton, 0, radius, 0, 0, mDurationMillis, 0, true, true);
                        for (int i = 1; i <= upNumber && i < size; i++) {
                            faButton = buttons.get(i);
                            slideview(faButton, 0, radius, 0, -gap * i, mDurationMillis, 0, true, true);
                        }
                        for (int i = 1; i <= belowNumber && i < size - upNumber; i++) {
                            faButton = buttons.get(i + upNumber);
                            slideview(faButton, 0, radius, 0, gap * i, mDurationMillis, 0, true, true);
                        }
                    }
                }

            }


        } else { //关闭Buttons
            showRotateAnimation(button, 225, 0);
            for (FloatingActionButton faButton : buttons) {
                int faButtonLeft = faButton.getLeft();
                int faButtonTop = faButton.getTop();
                slideview(faButton, 0, buttonLeft - faButtonLeft, 0, buttonTop - faButtonTop, mDurationMillis, 0, true, false);
            }
        }

    }

    /**
     * 旋转的动画
     *
     * @param mView        需要选择的View
     * @param startDegress 初始的角度【从这个角度开始】
     * @param degrees      当前需要旋转的角度【转到这个角度来】
     */
    public static void showRotateAnimation(View mView, int startDegress, int degrees) {
        mView.clearAnimation();
        float centerX = mView.getWidth() / 2.0f;
        float centerY = mView.getHeight() / 2.0f;
        //这个是设置需要旋转的角度（也是初始化），我设置的是当前需要旋转的角度
        RotateAnimation rotateAnimation = new RotateAnimation(startDegress, degrees, centerX, centerY);//centerX和centerY是旋转View时候的锚点
        //这个是设置动画时间的
        rotateAnimation.setDuration(100);
        rotateAnimation.setInterpolator(new AccelerateInterpolator());
        //动画执行完毕后是否停在结束时的角度上
        rotateAnimation.setFillAfter(true);
        //启动动画
        mView.startAnimation(rotateAnimation);
    }
}
