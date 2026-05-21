package com.ria4.odoo.presentation.screen.home

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.ria4.odoo.R
import com.ria4.odoo.domain.entity.User
import com.ria4.odoo.presentation.base_mvp.base.BaseActivity
import com.ria4.odoo.presentation.screen.auth.AuthActivity
import com.ria4.odoo.presentation.utils.extensions.*
import com.ria4.odoo.presentation.utils.glide.TransformationType
import com.ria4.odoo.presentation.utils.glide.load
import com.ria4.odoo.presentation.widget.navigation_view.NavigationItem
import com.ria4.odoo.presentation.widget.navigation_view.NavigationItemSelectedListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import javax.inject.Inject
import com.ria4.odoo.presentation.widget.navigation_view.NavigationId as Id

/**
 * Main home screen with navigation drawer — manages toolbar state, fragment navigation, user info and permissions.
 * Ecran d'accueil principal avec drawer de navigation — gere l'etat de la toolbar, la navigation entre fragments, les infos utilisateur et les permissions.
 */
class HomeActivity : BaseActivity<HomeContract.View, HomeContract.Presenter>(), HomeContract.View,
        NavigationItemSelectedListener {

    private val TRANSLATION_X_KEY = "TRANSLATION_X_KEY"
    private val CARD_ELEVATION_KEY = "CARD_ELEVATION_KEY"
    private val SCALE_KEY = "SCALE_KEY"

    private var exitTime: Long = 0L

    @Inject
    protected lateinit var homePresenter: HomePresenter

    override fun initPresenter() = homePresenter

    /** Inflates layout, initializes toolbar/drawer, restores navigator state. / Gonfle le layout, initialise toolbar/drawer, restaure l'etat du navigateur. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.ria4.odoo.R.layout.activity_home)
        initViews()

        presenter.getNavigatorState()?.let {
            navigator.restore(it)
        }
    }

    /** Saves drawer animation state (translation, scale, elevation) to bundle. / Sauvegarde l'etat d'animation du drawer (translation, echelle, elevation) dans le bundle. */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fun put(key: String, value: Float) = outState.putFloat(key, value)
        with(mainView) {
            put(TRANSLATION_X_KEY, translationX)
            put(CARD_ELEVATION_KEY, scale)
            put(SCALE_KEY, cardElevation)
        }
    }

    /** Restores drawer animation state from saved bundle. / Restaure l'etat d'animation du drawer depuis le bundle sauvegarde. */
    override fun onRestoreInstanceState(savedState: Bundle) {
        super.onRestoreInstanceState(savedState)
        savedState?.let {
            with(mainView) {
                translationX = it.getFloat(TRANSLATION_X_KEY)
                scale = it.getFloat(CARD_ELEVATION_KEY)
                cardElevation = it.getFloat(SCALE_KEY)
            }
        }
    }

    /** Saves navigator state before destroying. / Sauvegarde l'etat du navigateur avant la destruction. */
    override fun onDestroy() {
        presenter.saveNavigatorState(navigator.getState())
        super.onDestroy()
    }

    /** Injects HomePresenter via Dagger activity component. / Injecte le HomePresenter via le composant Dagger d'activite. */
    override fun injectDependencies() {
        activityComponent.inject(this)
    }

    /** Wires toolbar, navigation drawer listener, and drawer slide animation. / Connecte la toolbar, le listener du drawer de navigation et l'animation de glissement du drawer. */
    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        navView.navigationItemSelectListener = this
        navView.header.userName

        drawerLayout.drawerElevation = 0F
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val moveFactor = navView.width * slideOffset
                mainView.translationX = moveFactor
                mainView.scale = 1 - slideOffset / 4
                mainView.cardElevation = slideOffset * 10.toPx(this@HomeActivity)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                presenter.handleDrawerOpen()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                presenter.handleDrawerClose()
            }
        })
        drawerLayout.setScrimColor(Color.TRANSPARENT)

//        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
//            v.addTopMargin(insets.systemWindowInsetTop)
//            navView.addBottomMargin(insets.systemWindowInsetBottom)
//            insets
//        }
    }

    /** Switches arc icon to back-arrow and sets back-press behavior. / Change l'icone arc en fleche retour et definit le comportement back-press. */
    override fun setArcArrowState() {
        arcView.onClick {
            super.onBackPressed()
        }
        arcImage.setAnimatedImage(com.ria4.odoo.R.drawable.arrow_left)
    }

    /** Switches arc icon to hamburger menu and opens drawer on click. / Change l'icone arc en menu hamburger et ouvre le drawer au clic. */
    override fun setArcHamburgerIconState() {
        drawerLayout?.let {
            arcView.onClick {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            //  汉堡格式菜单
            arcImage.setAnimatedImage(com.ria4.odoo.R.drawable.hamb)
        }
    }

    override fun openHomeFragment() {
//        goTo<>()
    }

    /** Navigates to login screen, shows logged-out toast, and finishes current activity. / Navigue vers l'ecran de connexion, affiche un toast de deconnexion et termine l'activite. */
    override fun openLoginActivity() {
        start<AuthActivity>()
        showToast("Logged out")
        finish()
    }

    /** Updates the toolbar animated title. / Met a jour le titre anime de la toolbar. */
    override fun setToolBarTitle(title: String) {
        toolbarTitle?.setAnimatedText(title)
    }

    /** Delegates fragment change handling to the presenter. / Delegue la gestion du changement de fragment au presentateur. */
    override fun onFragmentChanged(currentTag: String, currentFragment: Fragment) {
        presenter.handleFragmentChanges(currentTag, currentFragment)
    }

    /** Populates the navigation drawer header with user name, info, and avatar. / Remplit l'en-tete du drawer de navigation avec le nom, les infos et l'avatar utilisateur. */
    override fun updateDrawerInfo(user: User) {
        val header = navView.header
        with(header) {
            userName.text = user.userName
            userInfo.text = user.userName
            userAvatar.load(user.userIcon, TransformationType.CIRCLE)
        }
    }

    /** Checks the given navigation item position in the drawer. / Coche l'element de navigation a la position donnee dans le drawer. */
    override fun checkNavigationItem(position: Int) {
        navView?.let {
            navView.setChecked(position)
        }
    }

    /** Handles back-press: closes drawer or shows exit toast on second press within 2 seconds. / Gere le retour : ferme le drawer ou affiche un toast de sortie au deuxieme appui dans les 2 secondes. */
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            else -> {
                if (arcImage.tag == R.drawable.hamb) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - this.exitTime > 2000) {
                        showToast("再按一次退出")
                        this.exitTime = currentTime
                        return
                    }
                }
                super.onBackPressed()
            }
        }
    }

    /** Routes navigation drawer item selections (Home, About, Logout) to appropriate action. / Route les selections du drawer (Accueil, A propos, Deconnexion) vers l'action appropriee. */
    override fun onNavigationItemSelected(item: NavigationItem) {
        when (item.id) {
            Id.HOME -> {
//                goTo<>()
            }
            Id.ABOUT -> {
//                goTo<AboutFragment>()
            }
            Id.LOG_OUT -> {
                presenter.logOut()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.any { it == PackageManager.PERMISSION_DENIED }) {
                showToast("Permissions refusees")
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        val perms = arrayOf(
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val missing = perms.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
