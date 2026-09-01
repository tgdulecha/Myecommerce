package org.course.mobile.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.course.mobile.data.network.NetworkModule
import org.course.mobile.data.network.dto.PaymentDto
import org.course.mobile.data.repository.CategoryRepository
import org.course.mobile.data.repository.EmployeeRepository
import org.course.mobile.data.repository.OrderDetailsRepository
import org.course.mobile.data.repository.OrderRepository
import org.course.mobile.data.repository.PaymentRepository
import org.course.mobile.data.repository.ProductRepository
import org.course.mobile.ui.screens.SignInScreen
import org.course.mobile.ui.screens.SignUpScreen
import org.course.mobile.ui.screens.category.CategoryFormScreen
import org.course.mobile.ui.screens.category.CategoryListScreen
import org.course.mobile.ui.screens.employee.EmployeeFormScreen
import org.course.mobile.ui.screens.employee.EmployeeListScreen
import org.course.mobile.ui.screens.order.OrderDetailScreen
import org.course.mobile.ui.screens.order.OrderFormScreen
import org.course.mobile.ui.screens.order.OrderLineFormScreen
import org.course.mobile.ui.screens.order.OrderListScreen
import org.course.mobile.ui.screens.orderdetails.OrderDetailsListScreen
import org.course.mobile.ui.screens.payment.PaymentFormScreen
import org.course.mobile.ui.screens.payment.PaymentListScreen
import org.course.mobile.ui.screens.product.ProductFormScreen
import org.course.mobile.ui.screens.product.ProductListScreen
import org.course.mobile.ui.viewmodel.AuthViewModel
import org.course.mobile.ui.viewmodel.CategoryViewModel
import org.course.mobile.ui.viewmodel.EmployeeViewModel
import org.course.mobile.ui.viewmodel.OrderDetailsListViewModel
import org.course.mobile.ui.viewmodel.OrderViewModel
import org.course.mobile.ui.viewmodel.PaymentViewModel
import org.course.mobile.ui.viewmodel.ProductViewModel
import org.course.mobile.ui.viewmodel.Session

private inline fun <reified VM : ViewModel> vmFactory(crossinline create: () -> VM) =
    viewModelFactory { initializer { create() } }

// Drives navigation purely off AuthViewModel.session: Checking shows a spinner while
// the stored token (if any) is validated via GET /me, then the graph switches
// between the signin/signup pair and the whole authenticated app (dashboard + all
// six feature areas) whenever session flips between SignedOut/SignedIn.
@Composable
fun AppNav(authViewModel: AuthViewModel, networkModule: NetworkModule) {
    val session by authViewModel.session.collectAsState()

    when (val current = session) {
        Session.Checking -> LoadingScreen()
        else -> {
            val navController = rememberNavController()
            val formState by authViewModel.form.collectAsState()

            LaunchedEffect(current) {
                val destination = if (current is Session.SignedIn) "home" else "signin"
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            NavHost(navController = navController, startDestination = "signin") {
                composable("signin") {
                    SignInScreen(
                        formState = formState,
                        onSignIn = authViewModel::signIn,
                        onNavigateToSignUp = { navController.navigate("signup") },
                    )
                }
                composable("signup") {
                    SignUpScreen(
                        formState = formState,
                        onSignUp = authViewModel::signUp,
                        onClearError = authViewModel::clearError,
                        onNavigateToSignIn = { navController.navigate("signin") },
                    )
                }
                composable("home") {
                    val signedIn = current as? Session.SignedIn
                    if (signedIn != null) {
                        val viewModel: OrderViewModel = viewModel(
                            factory = vmFactory {
                                OrderViewModel(OrderRepository(networkModule.orderApi), OrderDetailsRepository(networkModule.orderDetailsApi))
                            },
                        )
                        OrderListScreen(
                            viewModel = viewModel,
                            onCreate = { navController.navigate("orders/new") },
                            onOpen = { navController.navigate("orders/${it.orderId}") },
                            onNavigateCategories = { navController.navigate("categories") },
                            onNavigateProducts = { navController.navigate("products") },
                            onNavigateEmployees = { navController.navigate("employees") },
                            onNavigateOrderDetails = { navController.navigate("orderdetails") },
                            onNavigatePayments = { navController.navigate("payments") },
                            onLogout = authViewModel::logout,
                        )
                    }
                }

                categoryGraph(navController, networkModule)
                productGraph(navController, networkModule)
                employeeGraph(navController, networkModule)
                orderGraph(navController, networkModule)
                orderDetailsGraph(navController, networkModule)
                paymentGraph(navController, networkModule)
            }
        }
    }
}

private fun androidx.navigation.NavGraphBuilder.categoryGraph(nav: NavHostController, networkModule: NetworkModule) {
    composable("categories") { backStackEntry ->
        val viewModel: CategoryViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = vmFactory { CategoryViewModel(CategoryRepository(networkModule.categoryApi)) },
        )
        CategoryListScreen(
            viewModel = viewModel,
            onBack = { nav.popBackStack() },
            onCreate = { nav.navigate("categories/new") },
            onEdit = { nav.navigate("categories/${it.categoryId}/edit") },
        )
    }
    composable("categories/new") {
        val parent = nav.getBackStackEntry("categories")
        val viewModel: CategoryViewModel = viewModel(
            viewModelStoreOwner = parent,
            factory = vmFactory { CategoryViewModel(CategoryRepository(networkModule.categoryApi)) },
        )
        CategoryFormScreen(existing = null, onBack = { nav.popBackStack() }, onSave = { viewModel.save(it) { nav.popBackStack() } })
    }
    composable("categories/{id}/edit") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
        val parent = nav.getBackStackEntry("categories")
        val viewModel: CategoryViewModel = viewModel(
            viewModelStoreOwner = parent,
            factory = vmFactory { CategoryViewModel(CategoryRepository(networkModule.categoryApi)) },
        )
        val listState by viewModel.state.collectAsState()
        val existing = listState.items.find { it.categoryId == id }
        CategoryFormScreen(existing = existing, onBack = { nav.popBackStack() }, onSave = { viewModel.save(it) { nav.popBackStack() } })
    }
}

private fun androidx.navigation.NavGraphBuilder.productGraph(nav: NavHostController, networkModule: NetworkModule) {
    composable("products") {
        val viewModel: ProductViewModel = viewModel(factory = vmFactory { ProductViewModel(ProductRepository(networkModule.productApi)) })
        ProductListScreen(
            viewModel = viewModel,
            onBack = { nav.popBackStack() },
            onCreate = { nav.navigate("products/new") },
            onEdit = { nav.navigate("products/${it.productId}/edit") },
        )
    }
    composable("products/new") {
        val parent = nav.getBackStackEntry("products")
        val viewModel: ProductViewModel = viewModel(
            viewModelStoreOwner = parent,
            factory = vmFactory { ProductViewModel(ProductRepository(networkModule.productApi)) },
        )
        ProductFormScreen(existing = null, onBack = { nav.popBackStack() }, onSave = { viewModel.save(it) { nav.popBackStack() } })
    }
    composable("products/{id}/edit") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
        val parent = nav.getBackStackEntry("products")
        val viewModel: ProductViewModel = viewModel(
            viewModelStoreOwner = parent,
            factory = vmFactory { ProductViewModel(ProductRepository(networkModule.productApi)) },
        )
        val listState by viewModel.state.collectAsState()
        val existing = listState.items.find { it.productId == id }
        ProductFormScreen(existing = existing, onBack = { nav.popBackStack() }, onSave = { viewModel.save(it) { nav.popBackStack() } })
    }
}

private fun androidx.navigation.NavGraphBuilder.employeeGraph(nav: NavHostController, networkModule: NetworkModule) {
    composable("employees") {
        val viewModel: EmployeeViewModel = viewModel(factory = vmFactory { EmployeeViewModel(EmployeeRepository(networkModule.employeeApi)) })
        EmployeeListScreen(
            viewModel = viewModel,
            onBack = { nav.popBackStack() },
            onCreate = { nav.navigate("employees/new") },
            onEdit = { nav.navigate("employees/${it.employeeId}/edit") },
        )
    }
    composable("employees/new") {
        val parent = nav.getBackStackEntry("employees")
        val viewModel: EmployeeViewModel = viewModel(
            viewModelStoreOwner = parent,
            factory = vmFactory { EmployeeViewModel(EmployeeRepository(networkModule.employeeApi)) },
        )
        EmployeeFormScreen(existing = null, onBack = { nav.popBackStack() }, onSave = { viewModel.save(it) { nav.popBackStack() } })
    }
    composable("employees/{id}/edit") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
        val parent = nav.getBackStackEntry("employees")
        val viewModel: EmployeeViewModel = viewModel(
            viewModelStoreOwner = parent,
            factory = vmFactory { EmployeeViewModel(EmployeeRepository(networkModule.employeeApi)) },
        )
        val listState by viewModel.state.collectAsState()
        val existing = listState.items.find { it.employeeId == id }
        EmployeeFormScreen(existing = existing, onBack = { nav.popBackStack() }, onSave = { viewModel.save(it) { nav.popBackStack() } })
    }
}

// Orders no longer has its own top-level list route - "home" (OrderListScreen) IS
// that list now (see the mobile-app plan's landing-screen redesign). Every route
// here scopes its OrderViewModel to the "home" back-stack entry so state (list,
// selected order, its lines) is shared and survives navigating in and out of forms.
private fun androidx.navigation.NavGraphBuilder.orderGraph(nav: NavHostController, networkModule: NetworkModule) {
    fun orderViewModelFactory() = vmFactory {
        OrderViewModel(OrderRepository(networkModule.orderApi), OrderDetailsRepository(networkModule.orderDetailsApi))
    }

    composable("orders/new") {
        val parent = nav.getBackStackEntry("home")
        val viewModel: OrderViewModel = viewModel(viewModelStoreOwner = parent, factory = orderViewModelFactory())
        OrderFormScreen(existing = null, onBack = { nav.popBackStack() }, onSave = { viewModel.save(it) { nav.popBackStack() } })
    }
    composable("orders/{id}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
        val parent = nav.getBackStackEntry("home")
        val viewModel: OrderViewModel = viewModel(viewModelStoreOwner = parent, factory = orderViewModelFactory())
        OrderDetailScreen(
            viewModel = viewModel,
            orderId = id,
            onBack = { nav.popBackStack() },
            onEditOrder = { nav.navigate("orders/$id/edit") },
            onAddLine = { nav.navigate("orders/$id/lines/new") },
            onEditLine = { nav.navigate("orders/$id/lines/${it.productId}/edit") },
        )
    }
    composable("orders/{id}/edit") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
        val parent = nav.getBackStackEntry("home")
        val viewModel: OrderViewModel = viewModel(viewModelStoreOwner = parent, factory = orderViewModelFactory())
        val listState by viewModel.listState.collectAsState()
        val existing = listState.items.find { it.orderId == id }
        OrderFormScreen(existing = existing, onBack = { nav.popBackStack() }, onSave = { viewModel.save(it) { nav.popBackStack() } })
    }
    composable("orders/{id}/lines/new") { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
        val parent = nav.getBackStackEntry("home")
        val viewModel: OrderViewModel = viewModel(viewModelStoreOwner = parent, factory = orderViewModelFactory())
        OrderLineFormScreen(
            orderId = orderId,
            existing = null,
            onBack = { nav.popBackStack() },
            onSave = { line, isNew -> viewModel.saveLine(line, isNew) { nav.popBackStack() } },
        )
    }
    composable("orders/{id}/lines/{productId}/edit") { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
        val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
        val parent = nav.getBackStackEntry("home")
        val viewModel: OrderViewModel = viewModel(viewModelStoreOwner = parent, factory = orderViewModelFactory())
        val lines by viewModel.orderLines.collectAsState()
        val existing = lines.find { it.orderId == orderId && it.productId == productId }
        OrderLineFormScreen(
            orderId = orderId,
            existing = existing,
            onBack = { nav.popBackStack() },
            onSave = { line, isNew -> viewModel.saveLine(line, isNew) { nav.popBackStack() } },
        )
    }
}

private fun androidx.navigation.NavGraphBuilder.orderDetailsGraph(nav: NavHostController, networkModule: NetworkModule) {
    composable("orderdetails") {
        val viewModel: OrderDetailsListViewModel = viewModel(
            factory = vmFactory { OrderDetailsListViewModel(OrderDetailsRepository(networkModule.orderDetailsApi)) },
        )
        OrderDetailsListScreen(viewModel = viewModel, onBack = { nav.popBackStack() })
    }
}

private fun androidx.navigation.NavGraphBuilder.paymentGraph(nav: NavHostController, networkModule: NetworkModule) {
    composable("payments") {
        val viewModel: PaymentViewModel = viewModel(factory = vmFactory { PaymentViewModel(PaymentRepository(networkModule.paymentApi)) })
        PaymentListScreen(
            viewModel = viewModel,
            onBack = { nav.popBackStack() },
            onCreate = { nav.navigate("payments/new") },
        )
    }
    composable("payments/new") {
        val parent = nav.getBackStackEntry("payments")
        val viewModel: PaymentViewModel = viewModel(
            viewModelStoreOwner = parent,
            factory = vmFactory { PaymentViewModel(PaymentRepository(networkModule.paymentApi)) },
        )
        PaymentFormScreen(
            defaultOrderId = null,
            onBack = { nav.popBackStack() },
            onSave = { payment: PaymentDto -> viewModel.create(payment) { nav.popBackStack() } },
        )
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
