package com.elmundobinario.lacomidadelcanco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public String cerealElegido;
    public String legumbreElegida;
    public String hortalizaTuberculoElegido;
    public String condimento_hidratosElegido;

    public String verduraElegida;
    public String proteinaElegida;
    public String condimentoCenaElegido;

    SharedPreferences cerealSharedPreferences;
    SharedPreferences legumbreSharedPreferences;
    SharedPreferences hortalizasTuberculosSharedPreferences;
    SharedPreferences condimentoHidratosSharedPreferences;

    SharedPreferences verduraSharedPreferences;
    SharedPreferences proteinaSharedPreferences;
    SharedPreferences condimentoCenaSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cerealSharedPreferences = getSharedPreferences("cereales", Context.MODE_PRIVATE);
        legumbreSharedPreferences = getSharedPreferences("legumbres", Context.MODE_PRIVATE);
        hortalizasTuberculosSharedPreferences = getSharedPreferences("hortalizas_tuberculos", Context.MODE_PRIVATE);
        condimentoHidratosSharedPreferences = getSharedPreferences("condimento_hidratos", Context.MODE_PRIVATE);

        verduraSharedPreferences = getSharedPreferences("verduras_cena", Context.MODE_PRIVATE);
        proteinaSharedPreferences = getSharedPreferences("tipo_proteina", Context.MODE_PRIVATE);
        condimentoCenaSharedPreferences = getSharedPreferences("condimentos_cena", Context.MODE_PRIVATE);

    }

    public void intentAlmuerzo(View view) {
        Intent intencion = new Intent(this, Almuerzo.class);
        startActivity(intencion);
    }

    public void intentCena(View view) {
        Intent intencion = new Intent(this, Cena.class);
        startActivity(intencion);
    }

    public void consultaUltimoAlmuerzo(View view) {
        if (cerealSharedPreferences.contains("ingrediente_usado_cereal")) {
            cerealElegido = cerealSharedPreferences.getString("ingrediente_usado_cereal", "");
            legumbreElegida = legumbreSharedPreferences.getString("ingrediente_usado_legumbre", "");
            hortalizaTuberculoElegido = hortalizasTuberculosSharedPreferences.getString("ingrediente_usado_hortaliza", "");
            condimento_hidratosElegido = condimentoHidratosSharedPreferences.getString("ingrediente_usado_condimento", "");
            Intent intencion = new Intent(this, MenuAlmuerzoFinal.class);
            intencion.putExtra("cerealElegidoExtra", cerealElegido);
            intencion.putExtra("legumbreElegidaExtra", legumbreElegida);
            intencion.putExtra("hortalizaTuberculoElegidoExtra", hortalizaTuberculoElegido);
            intencion.putExtra("condimentoHidratosElegidoExtra", condimento_hidratosElegido);
            startActivity(intencion);
        } else {
            Toast.makeText(this, "aun no hay último almuerzo...", Toast.LENGTH_LONG).show();

        }
    }

    public void consultaUltimaCena(View view) {
        if (verduraSharedPreferences.contains("ingrediente_usado_verdura")) {
            verduraElegida = verduraSharedPreferences.getString("ingrediente_usado_verdura", "");
            proteinaElegida = proteinaSharedPreferences.getString("ingrediente_usado_proteina", "");
            condimentoCenaElegido = condimentoCenaSharedPreferences.getString("ingrediente_usado_condimento", "");
            Intent intencion = new Intent(this, MenuCenaFinal.class);
            intencion.putExtra("verduraElegidaExtra", verduraElegida);
            intencion.putExtra("alimentoProteinas", proteinaElegida);
            intencion.putExtra("condimentoCena", condimentoCenaElegido);
            startActivity(intencion);
        } else {
            Toast.makeText(this, "aun no hay última cena...", Toast.LENGTH_LONG).show();
        }
    }
}