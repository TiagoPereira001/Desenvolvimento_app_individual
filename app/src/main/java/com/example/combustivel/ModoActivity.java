package com.example.combustivel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.List;

public class ModoActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    public static final String PREFS_NAME = "com.example.combustivel.PREFS";
    public static final String KEY_USER_NAME = "USER_NAME";
    public static final String KEY_IS_PRO_USER = "IS_PRO_USER";
    public static final String KEY_APP_MODE = "APP_MODE";

    private boolean isPro = false;

    private BillingClient billingClient;
    private ProductDetails productDetailsPro;
    private final String PRODUCT_ID_PRO = "pro_upgrade"; // ID do produto na Play Store

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String modoGuardado = prefs.getString(KEY_APP_MODE, null);
        if (modoGuardado != null) {
            goToWelcomeScreen();
            return;
        }

        setContentView(R.layout.activity_modo);
        setupBillingClient();
        isPro = prefs.getBoolean(KEY_IS_PRO_USER, false);

        Button btnCombustao = findViewById(R.id.btn_modo_combustao);
        Button btnEletrico = findViewById(R.id.btn_modo_eletrico);
        Button btnAmbos = findViewById(R.id.btn_modo_ambos);

        if (isPro) {
            btnAmbos.setText("Ambos");
            btnEletrico.setText("Só Elétrico");
        }

        btnCombustao.setOnClickListener(v -> selecionarModo("COMBUSTAO"));
        btnEletrico.setOnClickListener(v -> selecionarModo("ELETRICO"));

        btnAmbos.setOnClickListener(v -> {
            if (isPro) {
                selecionarModo("AMBOS");
            } else {
                mostrarPopupPro("A gestão de veículos de combustão e elétricos em simultâneo é uma funcionalidade PRO.\n\nDeseja comprar?");
            }
        });
    }

    private void selecionarModo(String modo) {
        prefs.edit().putString(KEY_APP_MODE, modo).apply();
        goToWelcomeScreen();
    }

    private void goToWelcomeScreen() {
        Intent intent = new Intent(ModoActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    // --- LOGICA DE PAGAMENTOS (BILLING) ---
    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(new com.android.billingclient.api.PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                            for (Purchase purchase : purchases) {
                                handlePurchase(purchase);
                            }
                        }
                    }
                })
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProduct(PRODUCT_ID_PRO);
                    checkExistingPurchases();
                }
            }
            @Override
            public void onBillingServiceDisconnected() { }
        });
    }

    void queryProduct(String productId) {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder().setProductList(productList).build();

        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> productDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                    productDetailsPro = productDetailsList.get(0);
                }
            }
        });
    }

    private void mostrarPopupPro(String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle("Funcionalidade PRO")
                .setMessage(mensagem)
                .setPositiveButton("Sim, comprar", (dialog, which) -> launchPurchaseFlow())
                .setNegativeButton("Agora Não", null)
                .show();
    }

    private void launchPurchaseFlow() {
        if (productDetailsPro == null) {
            Toast.makeText(this, "Não foi possível encontrar o produto. A simular compra...", Toast.LENGTH_SHORT).show();
            simularCompraPro();
            return;
        }
        List<BillingFlowParams.ProductDetailsParams> productParamsList = new ArrayList<>();
        productParamsList.add(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetailsPro)
                        .build()
        );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productParamsList).build();
        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                billingClient.acknowledgePurchase(acknowledgeParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            prefs.edit().putBoolean(KEY_IS_PRO_USER, true).apply();
                            runOnUiThread(() -> {
                                Toast.makeText(ModoActivity.this, "Compra bem-sucedida! Obrigado.", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());
                            });
                        }
                    }
                });
            }
        }
    }

    private void simularCompraPro() {
        prefs.edit().putBoolean(KEY_IS_PRO_USER, true).apply();
        Toast.makeText(this, "Compra PRO simulada com sucesso! Tente outra vez.", Toast.LENGTH_LONG).show();
        finish();
        startActivity(getIntent());
    }

    void checkExistingPurchases() {
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                            for (Purchase purchase : purchases) {
                                if (purchase.getProducts().contains(PRODUCT_ID_PRO) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    if (!prefs.getBoolean(KEY_IS_PRO_USER, false)) {
                                        prefs.edit().putBoolean(KEY_IS_PRO_USER, true).apply();
                                        runOnUiThread(() -> {
                                            isPro = true;
                                            ((Button) findViewById(R.id.btn_modo_ambos)).setText("Ambos");
                                            ((Button) findViewById(R.id.btn_modo_eletrico)).setText("Só Elétrico");
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }
}