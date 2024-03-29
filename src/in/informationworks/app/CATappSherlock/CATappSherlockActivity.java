package in.informationworks.app.CATappSherlock;




import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ActionBar.OnNavigationListener;
import android.support.v4.app.ActionBar.Tab;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;

public class CATappSherlockActivity extends FragmentActivity implements ActionBar.TabListener {
	static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

	private final Handler handler = new Handler();
	private RoundedColourFragment leftFrag;
	private RoundedColourFragment rightFrag;
	private boolean useLogo = false;
	private boolean showHomeUp = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final ActionBar ab = getSupportActionBar();

		// set defaults for logo & home up
		ab.setDisplayHomeAsUpEnabled(showHomeUp);
		ab.setDisplayUseLogoEnabled(useLogo);

		// set up tabs nav
		for (int i = 1; i < 4; i++) {
			ab.addTab(ab.newTab().setText("Tab " + i).setTabListener(this));
		}

		// set up list nav
		ab.setListNavigationCallbacks(ArrayAdapter
				.createFromResource(this, R.array.sections,
						android.R.layout.simple_spinner_dropdown_item),
				new OnNavigationListener() {
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						// FIXME add proper implementation
						rotateLeftFrag();
						return false;
					}
				});

		// default to tab navigation
		showTabsNav();

		// create a couple of simple fragments as placeholders
		final int MARGIN = 16;
		leftFrag = new RoundedColourFragment(getResources().getColor(
				R.color.android_green), 1f, MARGIN, MARGIN / 2, MARGIN, MARGIN);
		rightFrag = new RoundedColourFragment(getResources().getColor(
				R.color.honeycombish_blue), 2f, MARGIN / 2, MARGIN, MARGIN,
				MARGIN);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.root, leftFrag);
		ft.add(R.id.root, rightFrag);
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);

		// set up a listener for the refresh item
		final MenuItem refresh = menu.findItem(R.id.menu_refresh);
		refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			// on selecting show progress spinner for 1s
			public boolean onMenuItemClick(MenuItem item) {
				// item.setActionView(R.layout.progress_action);
				handler.postDelayed(new Runnable() {
					public void run() {
						refresh.setActionView(null);
					}
				}, 1000);
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// TODO handle clicking the app icon/logo
			return false;
		case R.id.menu_refresh:
			// switch to a progress animation
			item.setActionView(R.layout.indeterminate_progress_action);
			return true;
		case R.id.menu_both:
			// rotation animation of green fragment
			rotateLeftFrag();
		case R.id.menu_text:
			// alpha animation of blue fragment
			if (IS_HONEYCOMB) {
				ObjectAnimatorAlpha.invoke(rightFrag.getView());
			} else {
				AlphaAnimation alpha = new AlphaAnimation(1f, 0f);
				alpha.setRepeatMode(Animation.REVERSE);
				alpha.setRepeatCount(1);
				alpha.setDuration(800);
				rightFrag.getView().startAnimation(alpha);
			}
			return true;
		case R.id.menu_logo:
			useLogo = !useLogo;
			item.setChecked(useLogo);
			getSupportActionBar().setDisplayUseLogoEnabled(useLogo);
			return true;
		case R.id.menu_up:
			showHomeUp = !showHomeUp;
			item.setChecked(showHomeUp);
			getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeUp);
			return true;
		case R.id.menu_nav_tabs:
			item.setChecked(true);
			showTabsNav();
			return true;
		case R.id.menu_nav_label:
			item.setChecked(true);
			showStandardNav();
			return true;
		case R.id.menu_nav_drop_down:
			item.setChecked(true);
			showDropDownNav();
			return true;
		case R.id.menu_bak_none:
			item.setChecked(true);
			getSupportActionBar().setBackgroundDrawable(null);
			return true;
		case R.id.menu_bak_gradient:
			item.setChecked(true);
			getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ad_action_bar_gradient_bak));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private static final class ObjectAnimatorAlpha {
		static void invoke(View view) {
				ObjectAnimator alpha = ObjectAnimator.ofFloat(view,
					"alpha", 1f, 0f);
				alpha.setRepeatMode(ObjectAnimator.REVERSE);
				alpha.setRepeatCount(1);
				alpha.setDuration(800);
				alpha.start();
		}
	}

	private void rotateLeftFrag() {
		if (leftFrag != null) {
			if (IS_HONEYCOMB) {
				ObjectAnimatorRotate.invoke(leftFrag.getView());
			} else {
				RotateAnimation rotate = new RotateAnimation(0, 180, leftFrag.getView().getWidth() / 2.0f, leftFrag.getView().getHeight() / 2.0f);
				rotate.setDuration(500);
				leftFrag.getView().startAnimation(rotate);
			}
		}
	}
	
	private static final class ObjectAnimatorRotate {
		static void invoke(View view) {
			ObjectAnimator.ofFloat(view, "rotationY", 0, 180)
				.setDuration(500).start();
		}
	}

	private void showStandardNav() {
		ActionBar ab = getSupportActionBar();
		if (ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_STANDARD) {
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
	}

	private void showDropDownNav() {
		ActionBar ab = getSupportActionBar();
		if (ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_LIST) {
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		}
	}

	private void showTabsNav() {
		ActionBar ab = getSupportActionBar();
		if (ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
	}

	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		// FIXME add a proper implementation, for now just rotate the left
		// fragment
		rotateLeftFrag();
	}

	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// FIXME implement this
	}

	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// FIXME implement this
	}
}