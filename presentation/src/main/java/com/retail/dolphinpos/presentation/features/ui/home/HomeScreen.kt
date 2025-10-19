package com.retail.dolphinpos.presentation.features.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.retail.dolphinpos.common.components.BaseText
import com.retail.dolphinpos.common.components.HeaderAppBarAuth
import com.retail.dolphinpos.common.utils.GeneralSans
import com.retail.dolphinpos.domain.model.home.cart.CartItem
import com.retail.dolphinpos.domain.model.home.catrgories_products.CategoryData
import com.retail.dolphinpos.domain.model.home.catrgories_products.Products
import com.retail.dolphinpos.presentation.R
import com.retail.dolphinpos.presentation.util.ErrorDialogHandler
import com.retail.dolphinpos.presentation.util.Loader
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val subtotal by viewModel.subtotal.collectAsStateWithLifecycle()
    val tax by viewModel.tax.collectAsStateWithLifecycle()
    val totalAmount by viewModel.totalAmount.collectAsStateWithLifecycle()
    val cashDiscountTotal by viewModel.cashDiscountTotal.collectAsStateWithLifecycle()
    val orderDiscountTotal by viewModel.orderDiscountTotal.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchProductResults.collectAsStateWithLifecycle()

    var selectedCategory by remember { mutableStateOf<CategoryData?>(null) }
    var paymentAmount by remember { mutableStateOf("0.00") }
    var searchQuery by remember { mutableStateOf("") }

    // Handle UI events
    LaunchedEffect(Unit) {
        viewModel.homeUiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.ShowLoading -> Loader.show("Loading...")
                is HomeUiEvent.HideLoading -> Loader.hide()
                is HomeUiEvent.ShowError -> {
                    ErrorDialogHandler.showError(
                        message = event.message,
                        buttonText = "OK"
                    ) {}
                }

                is HomeUiEvent.PopulateCategoryList -> {
                    if (event.categoryList.isNotEmpty()) {
                        selectedCategory = event.categoryList[0]
                        viewModel.loadProducts(event.categoryList[0].id)
                    }
                }

                is HomeUiEvent.PopulateProductsList -> {
                    // Products are already updated in ViewModel
                }
            }
        }
    }

    // Auto-select first category
    LaunchedEffect(categories) {
        if (categories.isNotEmpty() && selectedCategory == null) {
            selectedCategory = categories[0]
            viewModel.loadProducts(categories[0].id)
        }
    }

    // Update payment amount when total changes
    LaunchedEffect(totalAmount) {
        paymentAmount = viewModel.formatAmount(totalAmount)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.light_grey))
    ) {
        // Header App Bar
        HeaderAppBarAuth()

        Row(
            modifier = Modifier.weight(1f)
        ) {
            // Column 1 - Cart (25% width, full height)
            CartPanel(
                modifier = Modifier.weight(0.3f),
                cartItems = cartItems,
                onRemoveFromCart = { viewModel.removeFromCart(it) },
                onUpdateCartItem = { viewModel.updateCartItem(it) },
                onAddCustomer = {
                    // TODO: Show add customer dialog
                }
            )

            // Column 2 - Pricing/Payment/Keypad + Action Buttons (25% width, full height)
            Column(
                modifier = Modifier
                    .weight(0.25f)
                    .background(colorResource(id = R.color.light_grey))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pricing Summary
                PricingSummary(
                    subtotal = subtotal,
                    cashDiscountTotal = cashDiscountTotal,
                    orderDiscountTotal = orderDiscountTotal,
                    tax = tax,
                    totalAmount = totalAmount
                )

                // Cart Action Buttons
                CartActionButtons(
                    onClearCart = { viewModel.clearCart() }
                )

                // Payment Input
                PaymentInput(
                    paymentAmount = paymentAmount,
                    onPaymentAmountChange = { paymentAmount = it },
                    onRemoveDigit = {
                        val current = paymentAmount.replace("$", "").toDoubleOrNull() ?: 0.0
                        val newAmount = viewModel.removeLastDigit(current)
                        paymentAmount = viewModel.formatAmount(newAmount)
                    }
                )

                // Keypad
                Keypad(
                    onDigitClick = { digit ->
                        val current = paymentAmount.replace("$", "").toDoubleOrNull() ?: 0.0
                        val newAmount = viewModel.appendDigitToAmount(current, digit)
                        paymentAmount = viewModel.formatAmount(newAmount)
                    },
                    onAmountSet = { amount ->
                        paymentAmount = viewModel.formatAmount(amount)
                    },
                    onExactAmount = {
                        paymentAmount = viewModel.formatAmount(totalAmount)
                    }
                )

                // Payment Methods
                PaymentMethods(
                    onCashSelected = {
                        viewModel.isCashSelected = true
                        viewModel.updateCartPrices()
                    },
                    onCardSelected = {
                        viewModel.isCashSelected = false
                        viewModel.updateCartPrices()
                    }
                )
            }

            // Column 3 - Categories (25% width, full height)
            CategoriesPanel(
                modifier = Modifier.weight(0.15f),
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    viewModel.loadProducts(category.id)
                }
            )

            // Column 4 - Products (25% width)
            ProductsPanel(
                modifier = Modifier.weight(0.3f),
                products = if (searchQuery.isNotEmpty()) searchResults else products,
                onProductClick = { product ->
                    viewModel.addToCart(product)
                }
            )
        }
    }
}

@Composable
fun CartPanel(
    modifier: Modifier = Modifier,
    cartItems: List<CartItem>,
    onRemoveFromCart: (Int) -> Unit,
    onUpdateCartItem: (CartItem) -> Unit,
    onAddCustomer: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Order Header
            CartHeader(
                cartItemsCount = cartItems.size,
                onAddCustomer = onAddCustomer
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cart Items or Empty State
            if (cartItems.isEmpty()) {
                EmptyCartState()
            } else {
                CartItemsList(
                    cartItems = cartItems,
                    onRemoveFromCart = onRemoveFromCart,
                    onUpdateCartItem = onUpdateCartItem
                )
            }
        }
    }
}

@Composable
fun CartHeader(
    cartItemsCount: Int,
    onAddCustomer: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.primary),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseText(
            text = "Order: #524184",
            color = Color.White,
            fontSize = 12f,
            fontFamily = GeneralSans
        )

        BaseText(
            text = "Cart Item: $cartItemsCount",
            color = Color.White,
            fontSize = 12f,
            fontFamily = GeneralSans
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onAddCustomer() }
        ) {
            BaseText(
                text = "Non Member",
                color = Color.White,
                fontSize = 12f,
                fontFamily = GeneralSans
            )
        }
    }
}

@Composable
fun EmptyCartState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.cart_icon),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = colorResource(id = R.color.light_grey)
        )

        Spacer(modifier = Modifier.height(8.dp))

        BaseText(
            text = stringResource(id = R.string.empty),
            color = Color.Black,
            fontSize = 14f,
            fontFamily = GeneralSans,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CartItemsList(
    cartItems: List<CartItem>,
    onRemoveFromCart: (Int) -> Unit,
    onUpdateCartItem: (CartItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(cartItems) { item ->
            CartItemRow(
                item = item,
                onRemove = { item.productId?.let { id -> onRemoveFromCart(id) } },
                onUpdate = onUpdateCartItem
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit,
    onUpdate: (CartItem) -> Unit
) {
    val finalPrice = run {
        val discount = when (item.discountType) {
            com.retail.dolphinpos.domain.model.home.cart.DiscountType.PERCENTAGE ->
                (item.selectedPrice * (item.discountValue ?: 0.0) / 100.0)

            com.retail.dolphinpos.domain.model.home.cart.DiscountType.AMOUNT ->
                item.discountValue ?: 0.0

            else -> 0.0
        }
        (item.selectedPrice - discount).coerceAtLeast(0.0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Show edit dialog */ },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            BaseText(
                text = item.name,
                color = Color.Black,
                fontSize = 12f,
                fontFamily = GeneralSans
            )

            BaseText(
                text = "Qty: ${item.quantity}",
                color = Color.Gray,
                fontSize = 10f,
                fontFamily = GeneralSans
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            if (finalPrice < item.selectedPrice) {
                // Show discount
                BaseText(
                    text = "$${String.format("%.2f", item.selectedPrice)}",
                    color = Color.Gray,
                    fontSize = 10f,
                    textDecoration = TextDecoration.LineThrough
                )
                BaseText(
                    text = "$${String.format("%.2f", finalPrice)}",
                    color = Color.Black,
                    fontSize = 12f,
                    fontFamily = GeneralSans
                )
            } else {
                BaseText(
                    text = "$${String.format("%.2f", finalPrice)}",
                    color = Color.Black,
                    fontSize = 12f,
                    fontFamily = GeneralSans
                )
            }
        }
    }
}

@Composable
fun PricingSummary(
    subtotal: Double,
    cashDiscountTotal: Double,
    orderDiscountTotal: Double,
    tax: Double,
    totalAmount: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(12.dp)
    ) {
        // Primary color top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(colorResource(id = R.color.primary))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Cash Discount (if applicable)
        if (cashDiscountTotal > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BaseText(
                    text = "Cash Discount",
                    color = Color.Black,
                    fontSize = 12f,
                    fontFamily = GeneralSans
                )
                BaseText(
                    text = "-$${String.format("%.2f", cashDiscountTotal)}",
                    color = Color.Black,
                    fontSize = 12f,
                    fontFamily = GeneralSans
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Subtotal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BaseText(
                text = stringResource(id = R.string.subtotal),
                color = Color.Black,
                fontSize = 12f,
                fontFamily = GeneralSans,
                fontWeight = FontWeight.SemiBold
            )
            BaseText(
                text = "$${String.format("%.2f", subtotal)}",
                color = Color.Black,
                fontSize = 12f,
                fontFamily = GeneralSans,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Discount
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BaseText(
                text = stringResource(id = R.string.discount),
                color = Color.Black,
                fontSize = 12f,
                fontFamily = GeneralSans
            )
            BaseText(
                text = "-$${String.format("%.2f", orderDiscountTotal)}",
                color = Color.Black,
                fontSize = 12f,
                fontFamily = GeneralSans
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tax
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BaseText(
                text = stringResource(id = R.string.tax),
                color = Color.Black,
                fontSize = 12f,
                fontFamily = GeneralSans
            )
            BaseText(
                text = "$${String.format("%.2f", tax)}",
                color = Color.Black,
                fontSize = 12f,
                fontFamily = GeneralSans
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black.copy(alpha = 0.1f))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BaseText(
                text = stringResource(id = R.string.total),
                color = colorResource(id = R.color.primary),
                fontSize = 16f,
                fontFamily = GeneralSans,
                fontWeight = FontWeight.SemiBold
            )
            BaseText(
                text = "$${String.format("%.2f", totalAmount)}",
                color = colorResource(id = R.color.primary),
                fontSize = 16f,
                fontFamily = GeneralSans,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CartActionButtons(
    onClearCart: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Clear Cart
        Card(
            onClick = onClearCart,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_grey))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.clear_cart_btn),
                    contentDescription = "Clear Cart",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Hold Cart
        Card(
            onClick = { /* TODO */ },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_grey))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hold_cart_btn),
                    contentDescription = "Hold Cart",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Price Check
        Card(
            onClick = { /* TODO */ },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_grey))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.price_check_btn),
                    contentDescription = "Price Check",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Print Last Receipt
        Card(
            onClick = { /* TODO */ },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_grey))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.print_last_receipt_btn),
                    contentDescription = "Print Last Receipt",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun PaymentInput(
    paymentAmount: String,
    onPaymentAmountChange: (String) -> Unit,
    onRemoveDigit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        BaseText(
            text = stringResource(id = R.string.payment_amount),
            color = Color.Black,
            fontSize = 12f,
            fontFamily = GeneralSans,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.borderOutline),
                    shape = RoundedCornerShape(4.dp)
                )
                .height(35.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = paymentAmount,
                onValueChange = onPaymentAmountChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    BaseText(
                        text = "0.00",
                        color = Color.Gray,
                        fontSize = 12f,
                        fontFamily = GeneralSans
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = GeneralSans,
                    fontSize = 12.sp
                )
            )

            IconButton(
                onClick = onRemoveDigit,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun Keypad(
    onDigitClick: (String) -> Unit,
    onAmountSet: (Double) -> Unit,
    onExactAmount: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1: 7, 8, 9, $1, $5
        KeypadRow(
            buttons = listOf("7", "8", "9", "$1", "$5"),
            onDigitClick = onDigitClick,
            onAmountSet = onAmountSet
        )

        // Row 2: 4, 5, 6, $10, $20
        KeypadRow(
            buttons = listOf("4", "5", "6", "$10", "$20"),
            onDigitClick = onDigitClick,
            onAmountSet = onAmountSet
        )

        // Row 3: 1, 2, 3, $50, $100
        KeypadRow(
            buttons = listOf("1", "2", "3", "$50", "$100"),
            onDigitClick = onDigitClick,
            onAmountSet = onAmountSet
        )

        // Row 4: Exact, 0, Next, Cash (2 cols)
        KeypadRow(
            buttons = listOf(
                stringResource(id = R.string.exact),
                stringResource(id = R.string._0),
                stringResource(id = R.string.next)
            ),
            onDigitClick = onDigitClick,
            onAmountSet = onAmountSet,
            onExactAmount = onExactAmount,
            isLastRow = true
        )

        // Row 5: Empty, 00, Clear, Card (2 cols)
        KeypadRow(
            buttons = listOf(
                "",
                stringResource(id = R.string._00),
                stringResource(id = R.string.clear)
            ),
            onDigitClick = onDigitClick,
            onAmountSet = onAmountSet
        )
    }
}

@Composable
fun KeypadRow(
    buttons: List<String>,
    onDigitClick: (String) -> Unit,
    onAmountSet: (Double) -> Unit,
    onExactAmount: (() -> Unit)? = null,
    isLastRow: Boolean = false
) {
    val strExact = stringResource(id = R.string.exact)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        buttons.forEach { button ->
            KeypadButton(
                text = button,
                modifier = Modifier.weight(
                    if (button.contains("$")) 1f else if (isLastRow && button == stringResource(
                            id = R.string.cash
                        )
                    ) 2f else 1f
                ),
                onClick = {
                    when {
                        button.contains("$") -> {
                            val amount = button.replace("$", "").toDoubleOrNull() ?: 0.0
                            onAmountSet(amount)
                        }

                        button == strExact -> {
                            onExactAmount?.invoke()
                        }

                        button.isNotEmpty() -> {
                            onDigitClick(button)
                        }
                    }
                },
                isActionButton = button.contains("$") || button == stringResource(id = R.string.exact) ||
                        button == stringResource(id = R.string.next) || button == stringResource(id = R.string.clear),
                isPaymentButton = button == stringResource(id = R.string.cash) || button == stringResource(
                    id = R.string.card
                )
            )
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isActionButton: Boolean = false,
    isPaymentButton: Boolean = false
) {
    if (text.isEmpty()) {
        Spacer(modifier = modifier.height(36.dp))
        return
    }

    val backgroundColor = when {
        isPaymentButton -> colorResource(id = if (text == stringResource(id = R.string.cash)) R.color.primary else R.color.green_success)
        isActionButton -> colorResource(id = R.color.primary)
        else -> colorResource(id = R.color.bgColorEditText)
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(4.dp)
    ) {
        BaseText(
            text = text,
            color = Color.White,
            fontSize = 12f,
            fontFamily = GeneralSans,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PaymentMethods(
    onCashSelected: () -> Unit,
    onCardSelected: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Button(
            onClick = onCashSelected,
            modifier = Modifier
                .weight(2f)
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.primary)),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cash_icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                BaseText(
                    text = stringResource(id = R.string.cash),
                    color = Color.White,
                    fontSize = 12f,
                    fontFamily = GeneralSans
                )
            }
        }

        Button(
            onClick = onCardSelected,
            modifier = Modifier
                .weight(2f)
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green_success)),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.card_icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                BaseText(
                    text = stringResource(id = R.string.card),
                    color = Color.White,
                    fontSize = 12f,
                    fontFamily = GeneralSans
                )
            }
        }
    }
}

@Composable
fun ActionBar(
    userName: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.primary))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseText(
            text = "Welcome ${userName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}!",
            color = Color.White,
            fontSize = 14f,
            fontFamily = GeneralSans,
            fontWeight = FontWeight.Medium
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                BaseText(
                    text = stringResource(id = R.string.search_items),
                    color = Color.Gray,
                    fontSize = 12f,
                    fontFamily = GeneralSans
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = GeneralSans,
                fontSize = 12.sp
            )
        )

        TextButton(onClick = onLogout) {
            BaseText(
                text = "Logout",
                color = Color.White,
                fontSize = 12f,
                fontFamily = GeneralSans
            )
        }
    }
}

@Composable
fun CategoriesPanel(
    modifier: Modifier = Modifier,
    categories: List<CategoryData>,
    selectedCategory: CategoryData?,
    onCategorySelected: (CategoryData) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .background(colorResource(id = R.color.light_grey))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                isSelected = selectedCategory?.id == category.id,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: CategoryData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) colorResource(id = R.color.primary) else Color.White
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        BaseText(
            text = category.title,
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 12f,
            fontFamily = GeneralSans,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProductsPanel(
    modifier: Modifier = Modifier,
    products: List<Products>,
    onProductClick: (Products) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(colorResource(id = R.color.light_grey))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Products Grid - 60% of height
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(0.65f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductItem(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }

        // Action Buttons - 40% of height
        ActionButtonsPanel(
            modifier = Modifier.weight(0.35f)
        )
    }
}

@Composable
fun ProductItem(
    product: Products,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (product.images.isNotEmpty()) {
                AsyncImage(
                    model = product.images.first().fileURL,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            BaseText(
                text = product.name,
                color = Color.Black,
                fontSize = 10f,
                fontFamily = GeneralSans,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActionButtonsPanel(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(5.dp),
    ) {
        // Row 1
        ActionButtonRow(
            buttons = listOf(
                ActionButton("Split", R.drawable.split_btn),
                ActionButton("Gift Card", R.drawable.gift_card_btn),
                ActionButton("Pending Orders", R.drawable.pending_orders_button),
                ActionButton("Refund", R.drawable.refund_btn)
            )
        )

        // Row 2
        ActionButtonRow(
            buttons = listOf(
                ActionButton("EBT", R.drawable.ebt_btn),
                ActionButton("Rewards", R.drawable.rewards_btn),
                ActionButton("Online Order", R.drawable.online_order_btn),
                ActionButton("Tax Exempt", R.drawable.tax_exempt_btn)
            )
        )

        // Row 3
        ActionButtonRow(
            buttons = listOf(
                ActionButton("Custom Sales", R.drawable.custom_sales),
                ActionButton("Last Receipt", R.drawable.last_receipt),
                ActionButton("Pay In/Out", R.drawable.pay_in_out_btn),
                ActionButton("Void", R.drawable.void_btn)
            )
        )

        // Row 4
        ActionButtonRow(
            buttons = listOf(
                ActionButton("Promotions", R.drawable.promotions_btn),
                ActionButton("Weight Scale", R.drawable.weight_scale_btn),
                ActionButton("Clock In/Out", R.drawable.clock_in_out_btn),
                ActionButton("Order Discount", R.drawable.discount_btn)
            )
        )
    }
}

data class ActionButton(
    val label: String,
    val iconRes: Int
)

@Composable
fun ActionButtonRow(
    buttons: List<ActionButton>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        buttons.forEach { button ->
            Card(
                onClick = { /* TODO: Handle action */ },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_grey))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = button.iconRes),
                        contentDescription = button.label,
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}