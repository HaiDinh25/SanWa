package com.sanwashoseki.bookskozuchi.books.base

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseActivity
import com.sanwashoseki.bookskozuchi.utilities.SWApplication.Companion.context
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import kotlinx.android.synthetic.main.tutorial.*

enum class AnimationStatus {
    ANIMATION_NOT_RUNNING,
    ANIMATION_RUNNING,
    ANIMATION_CANCELED
}

open class SWBaseBookActivity : SWBaseActivity() {

    var isVerticalBook = false
    private val SWIPE_DURATION = 1200L
    private val TAB_DURATION = 800L
    private val DELAY_START_TIME = 1500L
    private val GUIDE_LINE_OPEN_MENU = 0
    private val SWIPE_TO_LEFT = 1
    private val SWIPE_TO_RIGHT = 2
    private val ANIMATION_REPEAT_NUM = 4

    private var swipeRepeatCount = 0
    private var animation_status = AnimationStatus.ANIMATION_NOT_RUNNING
    override fun onStart() {
        super.onStart()
        tutorialAnimationLayout?.setOnClickListener(View.OnClickListener {
            stopAllAnim()
        })
        runTutorialAnim()
    }

    private fun runHorizontalReadingTutorial() {
        tutorialDescriptionRight.text = getString(R.string.swipe_right_to_left_to_curl_next_page)
        tutorialDescriptionLeft.text = getString(R.string.swipe_left_to_right_to_curl_previous_page)
        runTabThenSwipeRightToLeftAnim()
    }

    private fun runVerticalReadingTutorial() {
        tutorialDescriptionRight.text =
            getString(R.string.swipe_right_to_left_to_curl_previous_page)
        tutorialDescriptionLeft.text = getString(R.string.swipe_left_to_right_to_curl_next_page)
        runTabThenSwipeLeftToRightAnim()
    }

    fun runTutorialAnim() {
        val isFirstTime: Boolean
        if (isVerticalBook) isFirstTime = Sharepref.isVerticalFirstTime(context)
        else isFirstTime = Sharepref.isHorizontalFirstTime(context)
        if (isFirstTime && animation_status != AnimationStatus.ANIMATION_RUNNING) {
            Log.d(TAG, "runTutorialAnim")
            tutorialAnimationLayout.visibility = View.VISIBLE
            animation_status = AnimationStatus.ANIMATION_RUNNING
            Handler(Looper.getMainLooper()).postDelayed({
                swipeRepeatCount = 0
                if (isVerticalBook) {
                    runVerticalReadingTutorial()
                    Sharepref.setVerticalFirstTime(context)
                } else {
                    runHorizontalReadingTutorial()
                    Sharepref.setHorizontalFirstTime(context)
                }
            }, DELAY_START_TIME)
        }
    }

    fun stopAllAnim() {
        Log.d(TAG, "stopAllAnim: ")
        swipeToRightToNextPage.clearAnimation()
        swipeToLeftToNextPage.clearAnimation()
        guideLineOpenMenuLayout.clearAnimation()
        tabLeft.clearAnimation()
        tabRight.clearAnimation()
        animation_status = AnimationStatus.ANIMATION_CANCELED
        goneALlView()
    }

    private fun runTabThenSwipeLeftToRightAnim() {
        if (animation_status == AnimationStatus.ANIMATION_CANCELED) return
        swipeToRightToNextPage.visibility = View.VISIBLE
        runTabAnimation(tabLeft, 0, SWIPE_TO_RIGHT)
    }

    private fun runTabThenSwipeRightToLeftAnim() {
        if (animation_status == AnimationStatus.ANIMATION_CANCELED) return
        swipeRepeatCount++
        swipeToLeftToNextPage.visibility = View.VISIBLE
        runTabAnimation(tabRight, 0, SWIPE_TO_LEFT)
    }

    private fun runSwipeLeftToRightAnimation() {
        if (animation_status == AnimationStatus.ANIMATION_CANCELED) return
        Log.d(TAG, "runSwipeLeftToRightAnimation: ")
        imgSwipeToRight.visibility = View.VISIBLE
        //rotate image
        runRotateAnim(imgSwipeToRight, -90f, 0f)
        //move to right
        val translateAnimation = TranslateAnimation(0f, 250f, 0f, -700f)
            .apply {
                duration = SWIPE_DURATION
                fillAfter = false
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        if (swipeRepeatCount < ANIMATION_REPEAT_NUM) {
                            swipeToRightToNextPage.visibility = View.GONE
                            imgSwipeToRight.visibility = View.GONE
                            runTabThenSwipeRightToLeftAnim()
                        } else {
                            swipeToRightToNextPage.visibility = View.GONE
                            imgSwipeToRight.visibility = View.GONE
                            if (isVerticalBook) runTabThenSwipeRightToLeftAnim()
                            else runTabAnimation(guideLineOpenMenuLayout, 3, GUIDE_LINE_OPEN_MENU)
                        }
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }
                })
            }
        swipeToRightToNextPage.startAnimation(translateAnimation)
    }

    private fun runSwipeRightToLeftAnimation() {
        if (animation_status == AnimationStatus.ANIMATION_CANCELED) return
        Log.d(TAG, "runSwipeRightToLeftAnimation: ")
        swipeToLeftToNextPage.clearAnimation()
        imgSwipeToLeft.visibility = View.VISIBLE
        //rotate image
        runRotateAnim(imgSwipeToLeft, 90f, 0f)
        //move to right
        val translateAnimation = TranslateAnimation(0f, -250f, 0f, -700f)
            .apply {
                duration = SWIPE_DURATION
                fillAfter = false
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        if (swipeRepeatCount < ANIMATION_REPEAT_NUM) {
                            imgSwipeToLeft.visibility = View.GONE
                            swipeToLeftToNextPage.visibility = View.GONE
                            runTabThenSwipeLeftToRightAnim()
                        } else {
                            swipeToLeftToNextPage.visibility = View.GONE
                            imgSwipeToLeft.visibility = View.GONE
                            if (!isVerticalBook) runTabThenSwipeLeftToRightAnim()
                            else runTabAnimation(guideLineOpenMenuLayout, 3, GUIDE_LINE_OPEN_MENU)
                        }
                    }

                    override fun onAnimationStart(animation: Animation?) {}
                })
            }
        swipeToLeftToNextPage.startAnimation(translateAnimation)
    }

    private fun runRotateAnim(view: View, fromDegrees: Float, toDegrees: Float) {
        view.clearAnimation()
        val rotateAnim = RotateAnimation(
            fromDegrees,
            toDegrees,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnim.duration = SWIPE_DURATION
        view.startAnimation(rotateAnim)
    }

    private fun runTabAnimation(view: View, repeat: Int, mode: Int) {
        if (animation_status == AnimationStatus.ANIMATION_CANCELED) return
        view.clearAnimation()
        Log.d(TAG, "runTabAnimation: " + repeat)
        val anim = ValueAnimator.ofFloat(1f, 0.85f)
        anim.duration = TAB_DURATION
        anim.addUpdateListener { animation ->
            view.setScaleX(animation.animatedValue as Float)
            view.setScaleY(animation.animatedValue as Float)
        }

        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                when (mode) {
                    SWIPE_TO_LEFT -> {
                        tabRight.visibility = View.VISIBLE
                        tutorialDescriptionRight.visibility = View.VISIBLE
                    }
                    SWIPE_TO_RIGHT -> {
                        tutorialDescriptionLeft.visibility = View.VISIBLE
                        tabLeft.visibility = View.VISIBLE
                    }
                    GUIDE_LINE_OPEN_MENU -> {
                        guideLineOpenMenuLayout.visibility = View.VISIBLE
                        guideLineOpenMenu.visibility = View.VISIBLE
                        guideLineOpenMenuDescription.visibility = View.VISIBLE
                    }
                }
            }

            override fun onAnimationEnd(animation: Animator?) {
                when (mode) {
                    SWIPE_TO_LEFT -> {
                        tabRight.visibility = View.GONE
                        runSwipeRightToLeftAnimation()
                    }
                    SWIPE_TO_RIGHT -> {
                        tabLeft.visibility = View.GONE
                        runSwipeLeftToRightAnimation()
                    }
                    GUIDE_LINE_OPEN_MENU -> {
                        animation_status =
                            AnimationStatus.ANIMATION_NOT_RUNNING //set tutorial has finished
                        goneALlView()
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        anim.repeatCount = repeat
        anim.repeatMode = ValueAnimator.RESTART
        anim.start()
    }

    private fun goneALlView() {
        tutorialAnimationLayout.visibility = View.GONE
        guideLineOpenMenuLayout.visibility = View.GONE
        guideLineOpenMenu.visibility = View.GONE
        guideLineOpenMenuDescription.visibility = View.GONE

        swipeToRightToNextPage.visibility = View.GONE
        tabLeft.visibility = View.GONE
        imgSwipeToRight.visibility = View.GONE
        tutorialDescriptionLeft.visibility = View.GONE

        swipeToLeftToNextPage.visibility = View.GONE
        tabRight.visibility = View.GONE
        imgSwipeToLeft.visibility = View.GONE
        tutorialDescriptionRight.visibility = View.GONE
    }
}