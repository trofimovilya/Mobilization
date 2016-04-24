package ru.ilyatrofimov.mobilization.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import ru.ilyatrofimov.mobilization.MobilizationApp;
import ru.ilyatrofimov.mobilization.R;
import ru.ilyatrofimov.mobilization.model.Artist;


/**
 * Artist's detail activity.
 * Holds everything that consider UI of toolbar, animations and transitions;
 *
 * @author Ilya Trofimov
 */
public class DetailActivity extends AppCompatActivity {
    private static final int GRADIENT_ANIM_DURATION = 200;
    private static final int CONTENT_ANIM_DURATION = 500;
    private static final int CONTENT_ANIM_START_DELAY = 200;
    private static final int ANIM_BACKUP_START_DELAY = 2000;
    private static final int LANDSCAPE_APPBAR_HEIGHT_DP = 200;
    private static final String YANDEX_MUSIC_URI = "yandexmusic://artist/";

    private boolean mDisplayLocked = true;
    private Artist mArtist;
    private RequestCreator mPicassoRequest;

    @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.toolbar_details) Toolbar mToolbar;
    @Bind(R.id.gradient_top) View mGradientTop;
    @Bind(R.id.gradient_bottom) View mGradientBottom;
    @Bind(R.id.img_artist_photo) ImageView mCoverImageView;
    @Bind(R.id.fab) FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        Intent intent = getIntent();

        if (intent != null && intent.getExtras() != null) {
            mArtist = intent.getParcelableExtra(Artist.TAG);
        }

        if (mArtist != null) {
            // Use small cover while transition goes
            Picasso.with(this).load(mArtist.getCoverSmall()).into(mCoverImageView);
            // Prepare loading of big cover
            mPicassoRequest = Picasso.with(this).load(mArtist.getCoverBig());

            initToolbar();
            if (MobilizationApp.IS_LOLLIPOP_OR_HIGHER) {
                initTransitions();
            }
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCollapsingToolbarLayout.setTitle(mArtist.getName());
        ViewGroup.LayoutParams appBarParams = mAppBarLayout.getLayoutParams();
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT
                && appBarParams != null) {
            appBarParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                    , LANDSCAPE_APPBAR_HEIGHT_DP, getResources().getDisplayMetrics());
            mAppBarLayout.setLayoutParams(appBarParams);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initTransitions() {
        Window window = getWindow();

        postponeEnterTransition();
        final View decor = window.getDecorView();
        decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onPreDraw() {
                decor.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });

        Slide returnTransition = new Slide();
        returnTransition.excludeTarget(mFab, true);
        returnTransition.excludeTarget(mAppBarLayout, true);

        Slide enterTransition = new Slide();
        enterTransition.setStartDelay(CONTENT_ANIM_START_DELAY);
        enterTransition.setDuration(CONTENT_ANIM_DURATION);
        enterTransition.setInterpolator(new DecelerateInterpolator());
        enterTransition.excludeTarget(mFab, true);
        enterTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                startEnterAnimation();
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });

        window.setReenterTransition(null);
        window.setExitTransition(null);
        window.setReturnTransition(returnTransition);
        window.setEnterTransition(enterTransition);
        window.getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                setHighQualityCover();
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    private void startEnterAnimation() {
        mDisplayLocked = false;

        mFab.show();

        if (mGradientTop.getVisibility() == View.INVISIBLE) {
            mGradientTop.setVisibility(View.VISIBLE);
            mGradientBottom.setVisibility(View.VISIBLE);

            ObjectAnimator top = ObjectAnimator.ofInt(mGradientTop, "bottom", mGradientTop
                    .getTop(), mGradientTop.getTop() + mGradientTop.getHeight());
            ObjectAnimator bottom = ObjectAnimator.ofInt(mGradientBottom, "top", mGradientBottom
                    .getBottom(), mGradientBottom.getBottom() - mGradientBottom.getHeight());

            AnimatorSet animationSet = new AnimatorSet();
            animationSet.setInterpolator(new DecelerateInterpolator());
            animationSet.playTogether(top, bottom);
            animationSet.setDuration(GRADIENT_ANIM_DURATION);
            animationSet.start();
        }
    }

    private void startReturnAnimation(final ReturnAnimationListener listener) {
        mFab.hide();

        if (mGradientTop.getVisibility() == View.VISIBLE) {
            ObjectAnimator top = ObjectAnimator.ofInt(mGradientTop, "bottom", mGradientTop
                    .getBottom(), mGradientTop.getBottom() - mGradientTop.getHeight());
            ObjectAnimator bottom = ObjectAnimator.ofInt(mGradientBottom, "top", mGradientBottom
                    .getTop(), mGradientBottom.getTop() + mGradientBottom.getHeight());

            AnimatorSet animationSet = new AnimatorSet();
            animationSet.playTogether(top, bottom);
            animationSet.setInterpolator(new DecelerateInterpolator());
            animationSet.setDuration(GRADIENT_ANIM_DURATION);
            animationSet.addListener(new Animator.AnimatorListener() {
                private void makeInvisible(View... views) {
                    for (View view : views) {
                        view.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    makeInvisible(mGradientTop, mGradientBottom, mFab, mToolbar);
                    mCollapsingToolbarLayout.setTitleEnabled(false);
                    listener.onReturnAnimationEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });

            animationSet.start();
        }
    }

    /**
     * Sets high-quality cover into ImageView and uses low-quality cover as placeholder
     */
    private void setHighQualityCover() {
        if (mPicassoRequest != null) {
            try {
                if (mCoverImageView.getDrawable() != null) { // Use low-quality cover as placeholder
                    mPicassoRequest.placeholder(mCoverImageView.getDrawable());
                } else { // Use default cover as placeholder if even low-quality hasn't loaded yet
                    mPicassoRequest.placeholder(R.drawable.default_cover_artist);
                }
            } catch (IllegalStateException e) {
                //  Bypass "Placeholder image already set" exception
            }

            mPicassoRequest.noFade().into(mCoverImageView);
        }
    }

    /**
     * FAB click handler
     * Opens Yandex.Music if it's installed, show toast otherwise
     */
    @OnClick(R.id.fab)
    public void openYandexMusic() {
        if (mArtist != null) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW
                        , Uri.parse(YANDEX_MUSIC_URI + mArtist.getId())));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.status_no_yandex_music_app, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        if (mArtist != null) {
            String link = mArtist.getLink(); // Display menu button if link is exists
            menu.findItem(R.id.action_open_link).setVisible(link != null && !link.isEmpty());
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_link: // Open artist's website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mArtist.getLink()));
                startActivity(Intent.createChooser(intent, getString(R.string.open_artist_link)));
                return true;
            case android.R.id.home:
                // onBackPressed() is not best practice here, but we have only two level
                // of transitions depth: MainActivity -> DetailActivity, so I think it is ok
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        // Postpone transitions and back press event until the return animation is completed
        if (MobilizationApp.IS_LOLLIPOP_OR_HIGHER) {
            postponeEnterTransition();
            startReturnAnimation(new ReturnAnimationListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onReturnAnimationEnd() {
                    DetailActivity.super.onBackPressed();
                    startPostponedEnterTransition();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        // In case transition animation wasn't played ¯\_(ツ)_/¯
        if (MobilizationApp.IS_LOLLIPOP_OR_HIGHER && getResources().getConfiguration()
                .orientation == Configuration.ORIENTATION_PORTRAIT) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startEnterAnimation();
                    setHighQualityCover();
                }
            }, ANIM_BACKUP_START_DELAY);
        } else {
            startEnterAnimation();
            setHighQualityCover();
        }

        super.onResume();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent pEvent) {
        // Block display until the animation is completed
        if (!mDisplayLocked) {
            return super.dispatchTouchEvent(pEvent);
        }

        return true;
    }

    /**
     * Listener with callback that calls when return animation ends
     */
    private interface ReturnAnimationListener {
        void onReturnAnimationEnd();
    }
}